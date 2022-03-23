/**
 * Export a thumbnail image, with and without an overlay, using QuPath.
 *
 * For tissue microarrays, the scripting code written by the 'File -> Export TMA data'
 * command is probably more appropriate.
 *
 * However, for all other kinds of images where batch export is needed this script can be used.
 *
 * @author Pete Bankhead
 */


import qupath.lib.gui.ImageWriterTools
import qupath.lib.gui.QuPathGUI
import qupath.lib.gui.viewer.OverlayOptions
import qupath.lib.regions.RegionRequest
import qupath.lib.scripting.QPEx

// Aim for an output resolution of approx 20 ?m/pixel
double requestedPixelSize = 10

// Create the output directory, if required
def path = QPEx.buildFilePath(QPEx.PROJECT_BASE_DIR, "Image masks")
QPEx.mkdirs(path)

// Get the imageData & server
def imageData = QPEx.getCurrentImageData()
def server = imageData.getServer()

// Get the file name from the current server
def name = server.getShortServerName()

// We need to get the display settings (colors, line thicknesses, opacity etc.) from the current viewer, if available
def overlayOptions = QuPathGUI.getInstance() == null ? new OverlayOptions() : QuPathGUI.getInstance().getViewer().getOverlayOptions()

// Calculate downsample factor depending on the requested pixel size
double downsample = requestedPixelSize / server.getAveragedPixelSizeMicrons()
def request = RegionRequest.createInstance(imageData.getServerPath(), downsample, 0, 0, server.getWidth(), server.getHeight())

// Write output image, with and without overlay
def dir = new File(path)
def fileImage = new File(dir, name + ".tif")
def img = ImageWriterTools.writeImageRegion(server, request, fileImage.getAbsolutePath())
def fileImageWithOverlay = new File(dir, name + "-overlay.tif")
ImageWriterTools.writeImageRegionWithOverlay(img, imageData, overlayOptions, request, fileImageWithOverlay.getAbsolutePath())
print("Done")