/**
 * Create a 'counts' image in QuPath that can be used to compute the local density of specific objects.
 *
 * This implementation uses ImageJ to create and display the image, which can then be filtered as required.
 * 
 * Written for QuPath v0.2.0.
 *
 * @author Pete Bankhead
 */

import ij.ImagePlus
import ij.process.FloatProcessor
import qupath.imagej.gui.IJExtension
import qupath.lib.objects.PathObjectTools
import qupath.lib.regions.RegionRequest

import static qupath.lib.gui.scripting.QPEx.*
import qupath.imagej.tools.IJTools

// Define the resolution at which the image should be generated
double requestedPixelSizeMicrons = 20

// Get the current image
def imageData = getCurrentImageData()
def server = imageData.getServer()
// Set the downsample directly (without using the requestedPixelSize) if you want; 1.0 indicates the full resolution
double downsample = requestedPixelSizeMicrons / server.getPixelCalibration().getAveragedPixelSizeMicrons()
def request = RegionRequest.createInstance(server, downsample)
def imp = IJTools.convertToImagePlus(server, request).getImage()

// Get the objects you want to count
// Potentially you can add filters for specific objects, e.g. to get only those with a 'Positive' classification
def detections = getDetectionObjects()
//detections = detections.findAll {it.getPathClass() == getPathClass('Positive')}

// Create a counts image in ImageJ, where each pixel corresponds to the number of centroids at that pixel
int width = imp.getWidth()
int height = imp.getHeight()
def fp = new FloatProcessor(width, height)
for (detection in detections) {
    // Get ROI for a detection; this method gets the nucleus if we have a cell object (and the only ROI for anything else)
    def roi = PathObjectTools.getROI(detection, true)
    int x = (int)(roi.getCentroidX() / downsample)
    int y = (int)(roi.getCentroidY() / downsample)
    fp.setf(x, y, fp.getf(x, y) + 1 as float)
}

// Show the images
IJExtension.getImageJInstance()
imp.show()
new ImagePlus(imp.getTitle() + "-counts", fp).show()