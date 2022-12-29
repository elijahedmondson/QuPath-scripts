import qupath.imagej.tools.IJTools
import qupath.lib.gui.images.servers.RenderedImageServer
import qupath.lib.gui.viewer.overlays.HierarchyOverlay
import qupath.lib.regions.RegionRequest
import static qupath.lib.gui.scripting.QPEx.*


def viewer = getCurrentViewer()
def imageData = getCurrentImageData()


// Define output path (relative to project)
def outputDir = buildFilePath(PROJECT_BASE_DIR, 'export')
mkdirs(outputDir)
def name = GeneralTools.getNameWithoutExtension(imageData.getServer().getMetadata().getName())
def path = buildFilePath(outputDir, name + ".png")
def pathMask = buildFilePath(outputDir, name + "-mask.png")

// Define how much to downsample during export (may be required for large images)
double downsample = 40

def server = new RenderedImageServer.Builder(imageData)
    .downsamples(downsample)
    //.layers(new HierarchyOverlay(viewer.getImageRegionStore(), viewer.getOverlayOptions(), imageData))
    .build()
    
    
// Create an ImageServer where the pixels are derived from annotations
def labelServer = new LabeledImageServer.Builder(imageData)
  .backgroundLabel(255, ColorTools.WHITE) // Specify background label (usually 0 or 255)
  .downsample(downsample)    // Choose server resolution; this should match the resolution at which tiles are exported
  .addLabel('Lung', 1)      // Choose output labels (the order matters!)
  .addLabel('Metastasis', 2)
  .multichannelOutput(false) // If true, each label refers to the channel of a multichannel binary image (required for multiclass probability)
  .build()

// Write the image
writeImage(server, path)
writeImage(labelServer, pathMask)
print("Done")
