setImageType('BRIGHTFIELD_H_DAB');
setColorDeconvolutionStains('{"Name" : "H-DAB default", "Stain 1" : "Hematoxylin", "Values 1" : "0.65111 0.70119 0.29049 ", "Stain 2" : "DAB", "Values 2" : "0.26917 0.56824 0.77759 ", "Background" : " 244 244 244 "}');
/////////////////////////////////////////////////////
import qupath.imagej.tools.IJTools
import qupath.lib.gui.images.servers.RenderedImageServer
import qupath.lib.gui.viewer.overlays.HierarchyOverlay
import qupath.lib.regions.RegionRequest
import static qupath.lib.gui.scripting.QPEx.*
double downsample = 20
String path = buildFilePath(PROJECT_BASE_DIR, 'ImageQC', getProjectEntry().getImageName() + '.png')
def viewer = getCurrentViewer()
def imageData = getCurrentImageData()
def server = new RenderedImageServer.Builder(imageData)
    .downsamples(downsample)
    .layers(new HierarchyOverlay(viewer.getImageRegionStore(), viewer.getOverlayOptions(), imageData))
    .build()
if (path != null) {
    mkdirs(new File(path).getParent())
    writeImage(server, path)
} else
    IJTools.convertToImagePlus(server, RegionRequest.createInstance(server)).getImage().show()
print "Brightfield image exported" 

runPlugin('qupath.imagej.detect.tissue.SimpleTissueDetection2', '{"threshold": 201,  "requestedPixelSizeMicrons": 20.0,  "minAreaMicrons": 1000000.0,  "maxHoleAreaMicrons": 1000000.0,  "darkBackground": false,  "smoothImage": true,  "medianCleanup": true,  "dilateBoundaries": false,  "smoothCoordinates": true,  "excludeOnBoundary": true,  "singleAnnotation": true}');
selectAnnotations()
def minArea = 100.0 // To change
def minHoleArea = 100.0 // To change
def classifierName = "DAB" // To change
createDetectionsFromPixelClassifier(classifierName, minArea, minHoleArea)
print "Detections created from Pixel Classifier"
addPixelClassifierMeasurements("DAB", "DAB")
print "Measurement added"


// Add the output file path here
String path1 = buildFilePath(PROJECT_BASE_DIR, 'ImageQC', getProjectEntry().getImageName() + '-mask.png')

// Request the current viewer for settings, and current image (which may be used in batch processing)
def viewer1 = getCurrentViewer()
def imageData1 = getCurrentImageData()

// Create a rendered server that includes a hierarchy overlay using the current display settings
def server1 = new RenderedImageServer.Builder(imageData1)
    .downsamples(downsample)
    .layers(new HierarchyOverlay(viewer.getImageRegionStore(), viewer.getOverlayOptions(), imageData1))
    .build()

// Write or display the rendered image
if (path1 != null) {
    mkdirs(new File(path).getParent())
    writeImage(server1, path1)
} else
    IJTools.convertToImagePlus(server1, RegionRequest.createInstance(server1)).getImage().show()
    
    print "Image exported!"