import qupath.ext.stardist.StarDist2D
//setColorDeconvolutionStains('{"Name" : "H-DAB default", "Stain 1" : "Hematoxylin", "Values 1" : "0.65111 0.70119 0.29049", "Stain 2" : "DAB", "Values 2" : "0.26917 0.56824 0.77759", "Background" : " 228 223 226"}');

// Specify the model file (you will need to change this!)
var pathModel = 'F:/QuPath/Stardist/he_heavy_augment.pb'
//REQUIRED CONVERSION FOR GPU PROCESSING
def dnn = DnnTools.builder(pathModel).build();
// Get current image - assumed to have color deconvolution stains set
println '1'
//runPlugin('qupath.imagej.detect.tissue.SimpleTissueDetection2', '{"threshold": 230,  "requestedPixelSizeMicrons": 20.0,  "minAreaMicrons": 1000000.0,  "maxHoleAreaMicrons": 5000.0,  "darkBackground": false,  "smoothImage": true,  "medianCleanup": true,  "dilateBoundaries": false,  "smoothCoordinates": true,  "excludeOnBoundary": false,  "singleAnnotation": true}');

//selectAnnotations();
//selectTMACores();
var stardist = StarDist2D.builder(pathModel)
      .ignoreCellOverlaps(false)   // Set to true if you don't care if cells expand into one another
      .threshold(0.5)              // Prediction threshold
      .normalizePercentiles(2, 99) // Percentile normalization
      //.pixelSize(0)              // Resolution for detection
      //.includeProbability(true)    // Include prediction probability as measurement
      .cellExpansion(15.0)          // Approximate cells based upon nucleus expansion
      .cellConstrainScale(4)       // Constrain cell expansion using nucleus size
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
//setDetectionIntensityClassifications("DAB: Nucleus: Mean", 0.1, 0.3, 0.5)
//setDetectionIntensityClassifications("DAB: Cytoplasm: Mean", 0.1, 0.3, 0.5)
//setDetectionIntensityClassifications("DAB: Membrane: Mean", 0.1, 0.3, 0.5)
println '7'



//RNASCOPE//
//RNASCOPE//
//RNASCOPE//
//RNASCOPE//
//selectAnnotations();
runPlugin('qupath.imagej.detect.cells.SubcellularDetection', '{"detection[DAB]": 0.3,  "doSmoothing": false,  "splitByIntensity": true,  "splitByShape": false,  "spotSizeMicrons": .2,  "minSpotSizeMicrons": 0.01,  "maxSpotSizeMicrons": 1.5,  "includeClusters": false}');
setCellIntensityClassifications("Subcellular: DAB: Num spots estimated", 1, 4, 10)
println '8'

Thread.sleep(100)
// Try to reclaim whatever memory we can, including emptying the tile cache
javafx.application.Platform.runLater {
    getCurrentViewer().getImageRegionStore().cache.clear()
    System.gc()
}
Thread.sleep(100)
