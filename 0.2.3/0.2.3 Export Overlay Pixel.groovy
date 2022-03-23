/**
 * Script to export a rendered (RGB) image in QuPath v0.2.0.
 *
 * This is much easier if the image is currently open in the viewer,
 * then see https://qupath.readthedocs.io/en/latest/docs/advanced/exporting_images.html
 *
 * The purpose of this script is to support batch processing (Run -> Run for project (without save)),
 * while using the current viewer settings.
 *
 * Note: This was written for v0.2.0 only. The process may change in later versions.
 *
 * @author Pete Bankhead
 */

import qupath.imagej.tools.IJTools
import qupath.lib.gui.images.servers.RenderedImageServer
import qupath.lib.gui.viewer.overlays.HierarchyOverlay
import qupath.lib.regions.RegionRequest

import static qupath.lib.gui.scripting.QPEx.*

// It is important to define the downsample!
// This is required to determine annotation line thicknesses
double downsample = 20

// Add the output file path here
String pathImage = buildFilePath(PROJECT_BASE_DIR, 'export', getProjectEntry().getImageName() + '.png')

print '01. pathImage = ' + (pathImage)

// Request the current viewer for settings, and current image (which may be used in batch processing)
def viewer = getCurrentViewer()
def imageData = getCurrentImageData()

// Create a rendered server that includes a hierarchy overlay using the current display settings
def server = new RenderedImageServer.Builder(imageData)
    .downsamples(downsample)
    //.layers(new HierarchyOverlay(viewer.getImageRegionStore(), viewer.getOverlayOptions(), imageData))
    .build()
    
print '02. server = ' + (server)

// Write or display the rendered image
if (pathImage != null) {
    mkdirs(new File(pathImage).getParent())
    writeImage(server, pathImage)
} else
    IJTools.convertToImagePlus(server, RegionRequest.createInstance(server)).getImage().show()
    

//////////////
  

//def imageData = getCurrentImageData()
def classifier = loadPixelClassifier('Necrosis')
def predictionServer = PixelClassifierTools.createPixelClassificationServer(imageData, classifier)
def path = buildFilePath(PROJECT_BASE_DIR, 'export', getProjectEntry().getImageName() + 'mask.tif')
//
print '03. path = ' + (path)
//def downsample = predictionServer.getDownsampleForResolution(0)
def request = RegionRequest.createInstance(predictionServer, downsample)
writeImageRegion(predictionServer, request, path)