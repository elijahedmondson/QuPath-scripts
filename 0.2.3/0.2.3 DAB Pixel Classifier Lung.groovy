setImageType('BRIGHTFIELD_H_DAB');
setColorDeconvolutionStains('{"Name" : "H-DAB default", "Stain 1" : "Hematoxylin", "Values 1" : "0.65111 0.70119 0.29049 ", "Stain 2" : "DAB", "Values 2" : "0.26917 0.56824 0.77759 ", "Background" : " 255 255 255 "}');
selectAnnotations();
mergeSelectedAnnotations();

def minArea = 100.0 // To change
def minHoleArea = 100.0 // To change
def classifierName = "RT.lung.glass.tumor" // To change

// Select all annotations
selectAnnotations()

// Apply pixel classifier inside them
//createAnnotationsFromPixelClassifier(classifierName, minArea, minHoleArea)
createDetectionsFromPixelClassifier(classifierName, minArea, minHoleArea)
addPixelClassifierMeasurements("RT.lung.glass.tumor", "RT.lung.glass.tumor")

print "Done!"



import qupath.imagej.tools.IJTools
import qupath.lib.gui.images.servers.RenderedImageServer
import qupath.lib.gui.viewer.overlays.HierarchyOverlay
import qupath.lib.regions.RegionRequest

import static qupath.lib.gui.scripting.QPEx.*

// It is important to define the downsample!
// This is required to determine annotation line thicknesses
double downsample = 20

// Add the output file path here
String path = buildFilePath(PROJECT_BASE_DIR, 'ImageQC2', getProjectEntry().getImageName() + '-mask.png')

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