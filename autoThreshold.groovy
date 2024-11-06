/*
QuPath script to apply various auto-threshold methods from ImageJ on annotations.

This script has been optimised to apply auto-thresholding on the histogram from pixels strictly within the annotation ROI, instead of the bounding box.

Note that there are two different downsample parameters to set.
 * "thresholdDownsample" determines the downsample factor for the image and pixels that are used to calculate the threshold value from the histogram. This is useful to adjust if you have a very large annotation, which you should use a larger downsample factor.
 * "classifierDownsample" determines the downsample factor for the objects that are to be created. This is useful to adjust depending on the size/complexity of the resulting object ROI.

Specify the threshold method to use by setting the "threshold" variable.
 * For fixed threshold, set "threshold" with the desired threshold value.
 * For auto threshold, set "threshold" with the desired threshold method. The following are available: "Default", "Huang", "Intermodes", "IsoData", "IJ_IsoData", "Li", "MaxEntropy", "Mean", "MinError", "Minimum", "Moments", "Otsu", "Percentile", "RenyiEntropy", "Shanbhag", "Triangle", "Yen

You can choose the desired output:
 * "threshold value" for just saving the threshold value to manually check the results without creating objects.
 * "preview" to show the thresholded area as an overlay (experimental feature).
 * "measurement" to save the thresholded area as a measurement in the parent annotation.
 * "annotation" to save the thresholded area as an annotation, making use of the classifier object options.
 * "detection" to save the thresholded area as a detection, making use of the classifier object options.

@author Yau Mun Lim @yau-lim (2024)
*/

/* PARAMETERS */
String channel = "DAB" // "HTX", "DAB", "Residual" for BF ; use channel name for FL ; "Average":Mean of all channels for BF/FL
double thresholdDownsample = 8 // 1:Full, 2:Very high, 4:High, 8:Moderate, 16:Low, 32:Very low, 64:Extremely low
def threshold = 0.25 // Input threshold value for fixed threshold. Use the following for auto threshold: "Default", "Huang", "Intermodes", "IsoData", "IJ_IsoData", "Li", "MaxEntropy", "Mean", "MinError", "Minimum", "Moments", "Otsu", "Percentile", "RenyiEntropy", "Shanbhag", "Triangle", "Yen"
boolean darkBackground = false // Adapt threshold method for dark backgrounds
def thresholdFloor = null // Set a threshold floor value in case auto threshold is too low. Set null to disable
String output = "preview" // "annotation", "detection", "measurement", "preview", "threshold value"
// Reset preview overlay with "getQuPath().getViewer().resetCustomPixelLayerOverlay()"

double classifierDownsample = 8 // 1:Full, 2:Very high, 4:High, 8:Moderate, 16:Low, 32:Very low, 64:Extremely low
double classifierGaussianSigma = 0.5 // Strength of gaussian blurring for pixel classifier (not used in calculation of threshold)
String classBelow = null // null or "Class Name"; use this for positive "Average" channel on brightfield
String classAbove = "Positive" // null or "Class Name"; use this for positive deconvoluted or fluorescence channels

/* Create object parameters */
double minArea = 0 // Minimum area for annotations to be created
double minHoleArea = 0 // Minimum area for holes in annotations to be created
String classifierObjectOptions = "" // "SPLIT,DELETE_EXISTING,INCLUDE_IGNORED,SELECT_NEW"


def annotations = getSelectedObjects().findAll{it.getPathClass() != getPathClass("Ignore*")}

if (annotations) {
    annotations.forEach{ anno ->
        autoThreshold(anno, channel, thresholdDownsample, threshold, darkBackground, thresholdFloor, output, classifierDownsample, classifierGaussianSigma, classBelow, classAbove, minArea, minHoleArea, classifierObjectOptions)
    }
} else {
    logger.warn("No annotations selected.")
}

/* IMPORTS */
import qupath.lib.images.servers.TransformedServerBuilder
import qupath.lib.roi.interfaces.ROI
import qupath.imagej.tools.IJTools
import qupath.lib.images.PathImage
import qupath.lib.regions.RegionRequest
import ij.ImagePlus
import ij.process.ImageProcessor
import qupath.opencv.ml.pixel.PixelClassifiers
import qupath.lib.gui.viewer.OverlayOptions
import qupath.lib.gui.viewer.RegionFilter
import qupath.lib.gui.viewer.overlays.PixelClassificationOverlay
import qupath.lib.images.servers.ColorTransforms.ColorTransform
import qupath.opencv.ops.ImageOp
import qupath.opencv.ops.ImageOps

/* FUNCTIONS */
def autoThreshold(annotation, channel, thresholdDownsample, threshold, darkBackground, thresholdFloor, output, classifierDownsample, classifierGaussianSigma, classBelow, classAbove, minArea, minHoleArea, classifierObjectOptions) {
    def qupath = getQuPath()
    def imageData = getCurrentImageData()
    def imageType = imageData.getImageType()
    def server = imageData.getServer()
    def cal = server.getPixelCalibration()
    def resolution = cal.createScaledInstance(classifierDownsample, classifierDownsample)
    def classifierChannel

    if (imageType.toString().contains("Brightfield")) {
        def stains = imageData.getColorDeconvolutionStains()

        if (channel == "HTX") {
            server = new TransformedServerBuilder(server).deconvolveStains(stains, 1).build()
            classifierChannel = ColorTransforms.createColorDeconvolvedChannel(stains, 1)
        } else if (channel == "DAB") {
            server = new TransformedServerBuilder(server).deconvolveStains(stains, 2).build()
            classifierChannel = ColorTransforms.createColorDeconvolvedChannel(stains, 2)
        } else if (channel == "Residual") {
            server = new TransformedServerBuilder(server).deconvolveStains(stains, 3).build()
            classifierChannel = ColorTransforms.createColorDeconvolvedChannel(stains, 3)
        } else if (channel == "Average") {
            server = new TransformedServerBuilder(server).averageChannelProject().build()
            classifierChannel = ColorTransforms.createMeanChannelTransform()
        }
    } else if (imageType.toString() == "Fluorescence") {
        if (channel == "Average") {
            server = new TransformedServerBuilder(server).averageChannelProject().build()
            classifierChannel = ColorTransforms.createMeanChannelTransform()
        } else {
            server = new TransformedServerBuilder(server).extractChannels(channel).build()
            classifierChannel = ColorTransforms.createChannelExtractor(channel)
        }
    } else {
        logger.error("Current image type not compatible with auto threshold.")
        return
    }

    // Check if threshold is Double (for fixed threshold) or String (for auto threshold)
    String thresholdMethod
    if (threshold instanceof String) {
        thresholdMethod = threshold
    } else {
        thresholdMethod = "Fixed"
    }

    // Apply the selected algorithm
    def validThresholds = ["Fixed", "Default", "Huang", "Intermodes", "IsoData", "IJ_IsoData", "Li", "MaxEntropy", "Mean", "MinError", "Minimum", "Moments", "Otsu", "Percentile", "RenyiEntropy", "Shanbhag", "Triangle", "Yen"]

    double thresholdValue
    if (thresholdMethod in validThresholds){
        if (thresholdMethod == "Fixed") {
            thresholdValue = threshold
        } else {
            // Determine threshold value by auto threshold method
            ROI pathROI = annotation.getROI() // Get QuPath ROI
            PathImage pathImage = IJTools.convertToImagePlus(server, RegionRequest.createInstance(server.getPath(), thresholdDownsample, pathROI)) // Get PathImage within bounding box of annotation
            def ijRoi = IJTools.convertToIJRoi(pathROI, pathImage) // Convert QuPath ROI into ImageJ ROI
            ImagePlus imagePlus = pathImage.getImage() // Convert PathImage into ImagePlus
            ImageProcessor ip = imagePlus.getProcessor() // Get ImageProcessor from ImagePlus
            ip.setRoi(ijRoi) // Add ImageJ ROI to the ImageProcessor to limit the histogram to within the ROI only

            if (darkBackground) {
                ip.setAutoThreshold("${thresholdMethod} dark")
            } else {
                ip.setAutoThreshold("${thresholdMethod}")
            }

            thresholdValue = ip.getMaxThreshold()
            if (thresholdValue != null && thresholdValue < thresholdFloor) {
                thresholdValue = thresholdFloor
            }
        }
    } else {
        logger.error("Invalid auto-threshold method")
        return
    }

    // If specified output is "threshold value, return threshold value in annotation measurements
    if (output == "threshold value") {
        logger.info("${thresholdMethod} threshold value: ${thresholdValue}")
        annotation.measurements.put("${thresholdMethod} threshold value", thresholdValue)
        return
    }

    // Assign classification
    def classificationBelow
    if (classBelow instanceof PathClass) {
        classificationBelow = classBelow
    } else if (classBelow instanceof String) {
        classificationBelow = getPathClass(classBelow)
    } else if (classBelow == null) {
        classificationBelow = classBelow
    }
    
    def classificationAbove
    if (classAbove instanceof PathClass) {
        classificationAbove = classAbove
    } else if (classAbove instanceof String) {
        classificationAbove = getPathClass(classAbove)
    } else if (classAbove == null) {
        classificationAbove = classAbove
    }

    Map<Integer, PathClass> classifications = new LinkedHashMap<>()
    classifications.put(0, classificationBelow)
    classifications.put(1, classificationAbove)

    // Define parameters for pixel classifier
    List<ImageOp> ops = new ArrayList<>()
    ops.add(ImageOps.Filters.gaussianBlur(classifierGaussianSigma))
    ops.add(ImageOps.Threshold.threshold(thresholdValue))

    // Create pixel classifier
    def op = ImageOps.Core.sequential(ops)
    def transformer = ImageOps.buildImageDataOp(classifierChannel).appendOps(op)
    def classifier = PixelClassifiers.createClassifier(
        transformer,
        resolution,
        classifications
    )

    // Apply classifier
    selectObjects(annotation)
    if (output == "annotation") {
        logger.info("Creating annotations in ${annotation} from ${thresholdMethod}: ${thresholdValue}")
        
        if (classifierObjectOptions) {
            classifierObjectOptions = classifierObjectOptions.split(',')
            def allowedOptions = ["SPLIT", "DELETE_EXISTING", "INCLUDE_IGNORED", "SELECT_NEW"]
            boolean checkValid = classifierObjectOptions.every{allowedOptions.contains(it)}

            if (checkValid) {
                createAnnotationsFromPixelClassifier(classifier, minArea, minHoleArea, classifierObjectOptions)
            } else {
                logger.warn("Invalid create annotation options")
                return
            }
        } else {
            createAnnotationsFromPixelClassifier(classifier, minArea, minHoleArea)
        }
    }
    if (output == "detection") {
        logger.info("Creating detections in ${annotation} from ${thresholdMethod}: ${thresholdValue}")

        if (classifierObjectOptions) {
            classifierObjectOptions = classifierObjectOptions.split(',')
            def allowedOptions = ["SPLIT", "DELETE_EXISTING", "INCLUDE_IGNORED", "SELECT_NEW"]
            boolean checkValid = classifierObjectOptions.every{allowedOptions.contains(it)}

            if (checkValid) {
                createDetectionsFromPixelClassifier(classifier, minArea, minHoleArea, classifierObjectOptions)
            } else {
                logger.warn("Invalid create detection options")
                return
            }
        } else {
            createDetectionsFromPixelClassifier(classifier, minArea, minHoleArea)
        }
    }
    if (output == "measurement") {
        logger.info("Measuring thresholded area in ${annotation} from ${thresholdMethod}: ${thresholdValue}")
        def measurementID = "${thresholdMethod} threshold"
        addPixelClassifierMeasurements(classifier, measurementID)
    }
    if (output == "preview") {
        logger.info("Showing preview of ${annotation} with ${thresholdMethod}: ${thresholdValue}")
        OverlayOptions overlayOption = qupath.getOverlayOptions()
        overlayOption.setPixelClassificationRegionFilter(RegionFilter.StandardRegionFilters.ANY_ANNOTATIONS) // RegionFilter.StandardRegionFilters.ANY_ANNOTATIONS
        PixelClassificationOverlay previewOverlay = PixelClassificationOverlay.create(overlayOption, classifier)
        previewOverlay.setLivePrediction(true)
        qupath.getViewer().setCustomPixelLayerOverlay(previewOverlay)
    }
    
    if (classificationBelow == null) {
        annotation.measurements.put("${thresholdMethod}: ${classificationAbove.toString()} threshold value", thresholdValue)
    }
    if (classificationAbove == null) {
        annotation.measurements.put("${thresholdMethod}: ${classificationBelow.toString()} threshold value", thresholdValue)
    }
    if (classificationBelow != null && classificationAbove != null) {
        annotation.measurements.put("${thresholdMethod} threshold value", thresholdValue)
    }
}