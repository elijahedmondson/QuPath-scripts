/**
 * Script to help with annotating tumor regions, separating the tumor margin from the center.
 *
 * Here, each of the margin regions is approximately 500 microns in width.
 *
 * @author Pete Bankhead
 */


import qupath.lib.common.GeneralTools
import qupath.lib.objects.PathAnnotationObject
import qupath.lib.objects.PathObject
import qupath.lib.roi.PathROIToolsAwt

import java.awt.Rectangle
import java.awt.geom.Area

import static qupath.lib.scripting.QPEx.*

//-----
// Some things you might want to change

// How much to expand each region
double expandMarginMicrons = 250.0

// Define the colors
def coloInnerMargin = getColorRGB(0, 0, 200)
def colorOuterMargin = getColorRGB(0, 200, 0)
def colorCentral = getColorRGB(0, 0, 0)

// Choose whether to lock the annotations or not (it's generally a good idea to avoid accidentally moving them)
def lockAnnotations = true

//-----

// Extract the main info we need
def imageData = getCurrentImageData()
def hierarchy = imageData.getHierarchy()
def server = imageData.getServer()

// We need the pixel size
if (!server.hasPixelSizeMicrons()) {
    print 'We need the pixel size information here!'
    return
}
if (!GeneralTools.almostTheSame(server.getPixelWidthMicrons(), server.getPixelHeightMicrons(), 0.0001)) {
    print 'Warning! The pixel width & height are different; the average of both will be used'
}

// Get annotation & detections
def annotations = getAnnotationObjects()
def selected = getSelectedObject()
if (selected == null || !selected.isAnnotation()) {
    print 'Please select an annotation object!'
    return
}

// We need one selected annotation as a starting point; if we have other annotations, they will constrain the output
annotations.remove(selected)

// If we have at most one other annotation, it represents the tissue
Area areaTissue
PathObject tissueAnnotation
if (annotations.isEmpty()) {
    areaTissue = new Area(new Rectangle(0, 0, server.getWidth(), server.getHeight()))
} else if (annotations.size() == 1) {
    tissueAnnotation = annotations.get(0)
    areaTissue = PathROIToolsAwt.getArea(tissueAnnotation.getROI())
} else {
    print 'Sorry, this script only support one selected annotation for the tumor region, and at most one other annotation to constrain the expansion'
    return
}

// Calculate how much to expand
double expandPixels = expandMarginMicrons / server.getAveragedPixelSizeMicrons()
def roiOriginal = selected.getROI()
def areaTumor = PathROIToolsAwt.getArea(roiOriginal)

// Get the outer margin area
def areaOuter = PathROIToolsAwt.shapeMorphology(areaTumor, expandPixels)
areaOuter.subtract(areaTumor)
areaOuter.intersect(areaTissue)
def roiOuter = PathROIToolsAwt.getShapeROI(areaOuter, roiOriginal.getC(), roiOriginal.getZ(), roiOriginal.getT())
def annotationOuter = new PathAnnotationObject(roiOuter)
annotationOuter.setName("Outer margin")
annotationOuter.setColorRGB(colorOuterMargin)

// Get the central area
def areaCentral = PathROIToolsAwt.shapeMorphology(areaTumor, -expandPixels)
areaCentral.intersect(areaTissue)
def roiCentral = PathROIToolsAwt.getShapeROI(areaCentral, roiOriginal.getC(), roiOriginal.getZ(), roiOriginal.getT())
def annotationCentral = new PathAnnotationObject(roiCentral)
annotationCentral.setName("Center")
annotationCentral.setColorRGB(colorCentral)

// Get the inner margin area
areaInner = areaTumor
areaInner.subtract(areaCentral)
areaInner.intersect(areaTissue)
def roiInner = PathROIToolsAwt.getShapeROI(areaInner, roiOriginal.getC(), roiOriginal.getZ(), roiOriginal.getT())
def annotationInner = new PathAnnotationObject(roiInner)
annotationInner.setName("Inner margin")
annotationInner.setColorRGB(coloInnerMargin)

// Add the annotations
hierarchy.getSelectionModel().clearSelection()
hierarchy.removeObject(selected, true)
def annotationsToAdd = [annotationOuter, annotationInner, annotationCentral];
annotationsToAdd.each {it.setLocked(lockAnnotations)}
if (tissueAnnotation == null) {
    hierarchy.addPathObjects(annotationsToAdd, false)
} else {
    tissueAnnotation.addPathObjects(annotationsToAdd)
    hierarchy.fireHierarchyChangedEvent(this, tissueAnnotation)
    if (lockAnnotations)
        tissueAnnotation.setLocked(true)
}


//RUN CELL DETECTIONS for CD206 //

setImageType('BRIGHTFIELD_H_DAB');
setColorDeconvolutionStains('{"Name" : "H-DAB default", "Stain 1" : "Hematoxylin", "Values 1" : "0.65111 0.70119 0.29049 ", "Stain 2" : "DAB", "Values 2" : "0.26917 0.56824 0.77759 ", "Background" : " 255 255 255 "}');
selectAnnotations();
runPlugin('qupath.imagej.detect.nuclei.PositiveCellDetection', '{"detectionImageBrightfield": "Hematoxylin OD",  "requestedPixelSizeMicrons": 0.5,  "backgroundRadiusMicrons": 8.0,  "medianRadiusMicrons": 0.0,  "sigmaMicrons": 1.5,  "minAreaMicrons": 10.0,  "maxAreaMicrons": 400.0,  "threshold": 0.1,  "maxBackground": 2.0,  "watershedPostProcess": true,  "excludeDAB": false,  "cellExpansionMicrons": 5.0,  "includeNuclei": true,  "smoothBoundaries": true,  "makeMeasurements": true,  "thresholdCompartment": "Cytoplasm: DAB OD mean",  "thresholdPositive1": 0.6,  "thresholdPositive2": 0.4,  "thresholdPositive3": 0.6,  "singleThreshold": true}');


//SAVE ANNOTATIONS //

def name = getProjectEntry().getImageName() + '.txt'
def path = buildFilePath('C:/Users/edmondsonef/Desktop/QuPath/NCL/Tox 171/', 'annotation results')
mkdirs(path)
path = buildFilePath(path, name)
saveAnnotationMeasurements(path)
print 'Results exported to ' + path
