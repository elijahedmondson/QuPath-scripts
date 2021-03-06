
//selectAnnotations()
//mergeSelectedAnnotations()
//clearSelectedObjects();
//runPlugin('qupath.imagej.detect.tissue.SimpleTissueDetection2', '{"threshold": 230,  "requestedPixelSizeMicrons": 5.0,  "minAreaMicrons": 1000000.0,  "maxHoleAreaMicrons": 10000.0,  "darkBackground": false,  "smoothImage": true,  "medianCleanup": true,  "dilateBoundaries": false,  "smoothCoordinates": true,  "excludeOnBoundary": true,  "singleAnnotation": true}');



print "One..."
// Select all annotations
selectAnnotations()

// Apply pixel classifier inside them
//createAnnotationsFromPixelClassifier(classifierName, minArea, minHoleArea)
def minArea = 100.0 // To change
def minHoleArea = 100.0 // To change
def classifierName = "Trichrome" // To change
createDetectionsFromPixelClassifier(classifierName, minArea, minHoleArea)
print "Two..."

addPixelClassifierMeasurements(classifierName, classifierName)
print "Three!"


import qupath.imagej.tools.IJTools
import qupath.lib.gui.images.servers.RenderedImageServer
import qupath.lib.gui.viewer.overlays.HierarchyOverlay
import qupath.lib.regions.RegionRequest

import static qupath.lib.gui.scripting.QPEx.*

// It is important to define the downsample!
// This is required to determine annotation line thicknesses
double downsample = 20

// Add the output file path here
String path = buildFilePath(PROJECT_BASE_DIR, 'ImageQC', getProjectEntry().getImageName() + '-mask1.png')

// Request the current viewer for settings, and current image (which may be used in batch processing)
def viewer = getCurrentViewer()
def imageData = getCurrentImageData()

// Create a rendered server that includes a hierarchy overlay using the current display settings
def server = new RenderedImageServer.Builder(imageData)
    .downsamples(downsample)
    .layers(new HierarchyOverlay(viewer.getImageRegionStore(), viewer.getOverlayOptions(), imageData))
    .build()

// Write or display the rendered image
if (path != null) {
    mkdirs(new File(path).getParent())
    writeImage(server, path)
} else
    IJTools.convertToImagePlus(server, RegionRequest.createInstance(server)).getImage().show()
    
    print "Image exported!"