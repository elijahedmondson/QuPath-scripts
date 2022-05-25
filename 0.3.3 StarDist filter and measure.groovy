//Variables to set *************************************************************
//def model_trained_on_single_channel=1 //Set to 1 if the pretrained model you're using was trained on IF sections, set to 0 if trained on brightfield
//param_channel=1 //channel to use for nucleus detection. First channel in image is channel 1. If working with H&E or HDAB, channel 1 is hematoxylin.
//param_median=0 //median preprocessing: Requires an int value corresponding to the radius of the median filter kernel. For radii larger than 2, the image must be in uint8 bit depth. Default 0
//param_divide=1 //division preprocessing: int or floating point, divides selected channel intensity by value before segmenting. Useful when normalization is disabled. Default 1
//param_add=0 //addition preprocessing: int or floating point, add value to selected channel intensity before segmenting. Useful when normalization is disabled. Default 0
//param_threshold = 0.5//threshold for detection. All cells segmented by StarDist will have a detection probability associated with it, where higher values indicate more certain detections. Floating point, range is 0 to 1. Default 0.5
//param_pixelsize=0 //Pixel scale to perform segmentation at. Set to 0 for image resolution (default). Int values accepted, greater values  will be faster but may yield poorer segmentations.
//param_tilesize=1024 //size of tile in pixels for processing. Must be a multiple of 16. Lower values may solve any memory-related errors, but can take longer to process. Default is 1024.
//param_expansion=10 //size of cell expansion in pixels. Default is 10.
def min_nuc_area=30 //remove any nuclei with an area less than or equal to this value
nuc_area_measurement='Nucleus: Area µm^2'
def min_nuc_intensity=0.0 //remove any detections with an intensity less than or equal to this value
nuc_intensity_measurement='Hematoxylin: Nucleus: Mean'
normalize_low_pct=1 //lower limit for normalization. Set to 0 to disable
normalize_high_pct=99 // upper limit for normalization. Set to 100 to disable.


import groovy.time.*
import qupath.ext.stardist.StarDist2D
//setColorDeconvolutionStains('{"Name" : "H-DAB default", "Stain 1" : "Hematoxylin", "Values 1" : "0.65111 0.70119 0.29049", "Stain 2" : "DAB", "Values 2" : "0.26917 0.56824 0.77759", "Background" : " 228 223 226"}');

// Specify the model file (you will need to change this!)
var pathModel = 'F:/QuPath/Stardist/he_heavy_augment.pb'
//REQUIRED CONVERSION FOR GPU PROCESSING
def dnn = DnnTools.builder(pathModel).build();

println '1'

selectAnnotations();
//selectTMACores();
var stardist = StarDist2D.builder(pathModel)
      .ignoreCellOverlaps(false)   // Set to true if you don't care if cells expand into one another
      .threshold(0.05)              // Prediction threshold
      .normalizePercentiles(1, 99) // Percentile normalization
      .pixelSize(0.5)              // Resolution for detection
      //.includeProbability(true)    // Include prediction probability as measurement
      .cellExpansion(5.0)          // Approximate cells based upon nucleus expansion
      //.cellConstrainScale(4)       // Constrain cell expansion using nucleus size
      .measureShape()              // Add shape measurements
      .measureIntensity()          // Add cell measurements (in all compartments)
      .doLog()                     // Use this to log a bit more information while running the script
      .build()
println '2'
// Run detection for the selected objects
var imageData = getCurrentImageData()
println '3'
var pathObjects = getSelectedObjects()
println '4'
if (pathObjects.isEmpty()) {
    Dialogs.showErrorMessage("StarDist", "Please select a parent object!")
    return
}
println '5'
stardist.detectObjects(imageData, pathObjects)
println '6'


clearDetections()
def timeStart_CellDetection = new Date()
stardist.detectObjects(imageData, pathObjects)
TimeDuration CellDetection_duration = TimeCategory.minus(new Date(), timeStart_CellDetection)





Thread.sleep(100)
// Try to reclaim whatever memory we can, including emptying the tile cache
javafx.application.Platform.runLater {
    getCurrentViewer().getImageRegionStore().cache.clear()
    System.gc()
}
Thread.sleep(100)

//IHC//
//IHC//
//IHC//
//IHC//
//IHC//
//setDetectionIntensityClassifications("DAB: Cell: Mean", 0.1, 0.3, 0.5)
setDetectionIntensityClassifications("DAB: Nucleus: Mean", 0.4, 0.6, 0.8)
//setDetectionIntensityClassifications("DAB: Cytoplasm: Mean", 0.1, 0.3, 0.5)
//setDetectionIntensityClassifications("DAB: Membrane: Mean", 0.1, 0.3, 0.5)
println '7 - IHC'



//RNASCOPE//
//RNASCOPE//
//RNASCOPE//
//RNASCOPE//
//selectAnnotations();
//runPlugin('qupath.imagej.detect.cells.SubcellularDetection', '{"detection[DAB]": 0.3,  "doSmoothing": false,  "splitByIntensity": true,  "splitByShape": false,  "spotSizeMicrons": .2,  "minSpotSizeMicrons": 0.01,  "maxSpotSizeMicrons": 1.5,  "includeClusters": false}');
//setCellIntensityClassifications("Subcellular: DAB: Num spots estimated", 1, 4, 10)
//println '8 - RNAscope'

//Thread.sleep(100)
// Try to reclaim whatever memory we can, including emptying the tile cache
//javafx.application.Platform.runLater {
//    getCurrentViewer().getImageRegionStore().cache.clear()
//    System.gc()
//}
//Thread.sleep(100)


//filter out small and low intensity nuclei


def toDelete = getDetectionObjects().findAll {measurement(it, nuc_area_measurement) <= min_nuc_area}
removeObjects(toDelete, true)
def toDelete2 = getDetectionObjects().findAll {measurement(it, nuc_intensity_measurement) <= min_nuc_intensity}
removeObjects(toDelete2, true)
//CLOSE DNN TO FREE UP VRAM
dnn.getPredictionFunction().net.close()
//Free up normal RAM. Commented this out, as v0.3 should have better memory management anways
//Alledgely, putting this at the end of a batch script will clear memory between each processed image
//Thread.sleep(100)
// Try to reclaim whatever memory we can, including emptying the tile cache
//javafx.application.Platform.runLater {
//    getCurrentViewer().getImageRegionStore().cache.clear()
//    System.gc()
//}
//Thread.sleep(100)

println ('Done in ' + CellDetection_duration)
