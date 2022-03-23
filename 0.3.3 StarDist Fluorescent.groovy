
//setChannelNames('DAPI', 'GFP','CD45R')
import qupath.ext.stardist.StarDist2D

// Specify the model file (you will need to change this!)
println '1'
selectAnnotations();
var pathModel = 'F:/QuPath/Stardist/dsb2018_heavy_augment.pb'
def dnn = DnnTools.builder(pathModel).build();
var stardist = StarDist2D.builder(pathModel)
        .threshold(0.5)              // Probability (detection) threshold
        .channels('DAPI')            // Specify detection channel
        .normalizePercentiles(1, 99) // Percentile normalization
        .pixelSize(0.7)              // Resolution for detection
        .cellExpansion(10.0)          // Approximate cells based upon nucleus expansion
        .cellConstrainScale(3)     // Constrain cell expansion using nucleus size
        .measureShape()              // Add shape measurements
        .measureIntensity()          // Add cell measurements (in all compartments)
        .includeProbability(true)    // Include prediction probability as measurement
        .build()

// Run detection for the selected objects
var imageData = getCurrentImageData()
var pathObjects = getSelectedObjects()
if (pathObjects.isEmpty()) {
    Dialogs.showErrorMessage("StarDist", "Please select a parent object!")
    return
}
stardist.detectObjects(imageData, pathObjects)
println 'Done!'


//runObjectClassifier("GFP.CD45R")
setDetectionIntensityClassifications("Cy7: Cell: Max", 9000, 11000, 13000)