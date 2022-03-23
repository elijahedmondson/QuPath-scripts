
setImageType('BRIGHTFIELD_H_DAB');
setColorDeconvolutionStains('{"Name" : "H-DAB default", "Stain 1" : "Hematoxylin", "Values 1" : "0.65111 0.70119 0.29049 ", "Stain 2" : "DAB", "Values 2" : "0.26917 0.56824 0.77759 ", "Background" : " 255 255 255 "}');


//runPlugin('qupath.imagej.detect.tissue.SimpleTissueDetection2', '{"threshold": 210,  "requestedPixelSizeMicrons": 20.0,  "minAreaMicrons": 10000.0,  "maxHoleAreaMicrons": 1000000.0,  "darkBackground": false,  "smoothImage": true,  "medianCleanup": true,  "dilateBoundaries": false,  "smoothCoordinates": true,  "excludeOnBoundary": false,  "singleAnnotation": true}');
selectAnnotations();
runPlugin('qupath.imagej.detect.cells.WatershedCellDetection', '{"detectionImageBrightfield": "Hematoxylin OD",  "requestedPixelSizeMicrons": 0.5,  "backgroundRadiusMicrons": 8,  "medianRadiusMicrons": 0.0,  "sigmaMicrons": 1.5,  "minAreaMicrons": 20.0,  "maxAreaMicrons": 400.0,  "threshold": 0.05,  "maxBackground": 2.0,  "watershedPostProcess": true,  "excludeDAB": false,  "cellExpansionMicrons": 10.0,  "includeNuclei": true,  "smoothBoundaries": true,  "makeMeasurements": true}');

runPlugin('qupath.imagej.detect.cells.SubcellularDetection', '{"detection[DAB]": 0.5,  "doSmoothing": false,  "splitByIntensity": true,  "splitByShape": false,  "spotSizeMicrons": .3,  "minSpotSizeMicrons": 0.01,  "maxSpotSizeMicrons": 1.5,  "includeClusters": false}');
setCellIntensityClassifications("Subcellular: DAB: Num spots estimated", 1, 4, 10)
/**
 * Script to set cell intensity sub-classifications based on an arbitrary number
 * of thresholds.
 *
 * Note: Because QuPath's dynamic measurements only care about negative/positive or
 *       negative/1+/2+/3+, any further classifications (e.g. 4+, 5+) won't be part of the
 *       summary scores (e.g. H-score or even Positive %)!
 *       Be warned and be careful!
 *
 * For 3 thresholds or fewer, the built-in setCellIntensityClassifications method is preferable.
 * See https://github.com/qupath/qupath/wiki/Spot-detection#binning-cells-according-to-spot-counts
 *
 * @author Pete Bankhead
 */

import qupath.lib.objects.PathObject
import qupath.lib.objects.classes.PathClass
import qupath.lib.objects.classes.PathClassFactory
import qupath.lib.scripting.QPEx

// Choose measurement & thresholds
def measurement = "Subcellular: DAB: Num spots estimated"
def thresholds = [1, 4, 10, 15] as double[]
setManyCellIntensityClassifications(measurement, thresholds)


/**
 * Apply multiple thresholds to all cells in the current image hierarchy
 *
 * @param cells
 * @param measurementName
 * @param thresholds
 */
static void setManyCellIntensityClassifications(final String measurementName, final double... thresholds) {
    def cells = QPEx.getCellObjects()
    cells.each {setManyCellIntensityClassifications(it, measurementName, thresholds)}
    QPEx.fireHierarchyUpdate()
}

/**
 * Apply multiple threshold to a single PathObject to set its intensity classification
 *
 * @param pathObject
 * @param measurementName
 * @param thresholds
 * @return
 */
static PathClass setManyCellIntensityClassifications(final PathObject pathObject, final String measurementName, final double... thresholds) {
    if (thresholds.length == 0)
        throw new IllegalArgumentException("At least 1 intensity thresholds required!")

    PathClass baseClass = PathClassFactory.getNonIntensityAncestorClass(pathObject.getPathClass())
    // Need to handle possible 4+, 5+ etc.
    if (baseClass != null && baseClass.getName().endsWith('+'))
        baseClass = baseClass.getParentClass()

    double estimatedSpots = pathObject.getMeasurementList().getMeasurementValue(measurementName)

    boolean singleThreshold = thresholds.length == 1

    if (estimatedSpots < thresholds[0]) {
        pathObject.setPathClass(PathClassFactory.getNegative(baseClass, null));
    } else {
        if (singleThreshold)
            pathObject.setPathClass(PathClassFactory.getPositive(baseClass, null));
        else {
            int n = thresholds.length
            while (n > 0) {
                if (estimatedSpots >= thresholds[n-1]) {
                    String name = n + '+'
                    pathObject.setPathClass(
                            PathClassFactory.getDerivedPathClass(baseClass, name, null)
                    )
                    break
                }
                n--
            }
        }
    }
    return pathObject.getPathClass()
}

//SAVE ANNOTATIONS //

def name = getProjectEntry().getImageName() + '.txt'
//def path = buildFilePath('C:/Users/edmondsonef/Desktop/QuPath/Venditti/', 'RNAscope quantification')
def path = buildFilePath(PROJECT_BASE_DIR, 'RNAscope quantification')
mkdirs(path)
path = buildFilePath(path, name)
saveAnnotationMeasurements(path)
