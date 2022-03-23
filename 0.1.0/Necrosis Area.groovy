//setImageType('BRIGHTFIELD_H_DAB');
//setColorDeconvolutionStains('{"Name" : "H-DAB default", "Stain 1" : "Hematoxylin", "Values 1" : "0.65111 0.70119 0.29049 ", "Stain 2" : "DAB", "Values 2" : "0.26917 0.56824 0.77759 ", "Background" : " 255 255 255 "}');
setImageType('BRIGHTFIELD_H_E');
//runPlugin('qupath.imagej.detect.tissue.SimpleTissueDetection2', '{"threshold": 215,  "requestedPixelSizeMicrons": 20.0,  "minAreaMicrons": 10000.0,  "maxHoleAreaMicrons": 1000000.0,  "darkBackground": false,  "smoothImage": true,  "medianCleanup": true,  "dilateBoundaries": false,  "smoothCoordinates": true,  "excludeOnBoundary": false,  "singleAnnotation": true}');
selectAnnotations();
runPlugin('qupath.imagej.detect.nuclei.WatershedCellDetection', '{"detectionImageBrightfield": "Hematoxylin OD",  "requestedPixelSizeMicrons": 0.5,  "backgroundRadiusMicrons": 0,  "medianRadiusMicrons": 0.0,  "sigmaMicrons": 1.5,  "minAreaMicrons": 1.0,  "maxAreaMicrons": 400.0,  "threshold": 0.06,  "maxBackground": 2,  "watershedPostProcess": true,  "cellExpansionMicrons": 15,  "includeNuclei": true,  "smoothBoundaries": true,  "makeMeasurements": true}');

//runClassifier('C:\\Users\\edmondsonef\\Desktop\\QuPath\\CODEX\\190482\\classifiers\\new.qpclassifier');
runClassifier('C:\\Users\\edmondsonef\\Desktop\\QuPath\\Lockett\\PHL 192324\\classifiers\\necrosis area.qpclassifier');

//In this case, it will add the total area taken up by Positive class cells within each annotation to their parent
//annotation as "Positive Area"

import qupath.lib.objects.PathCellObject

hierarchy = getCurrentHierarchy()

for (annotation in getAnnotationObjects()){
    //Block 1
    def positiveCells = hierarchy.getDescendantObjects(annotation,null, PathCellObject).findAll{it.getPathClass() == getPathClass("Necrosis")}
    double totalArea = 0
    for (def cell in positiveCells){
        totalArea += cell.getMeasurementList().getMeasurementValue("Cell: Area")
    }
    //Comment the following in or out depending on whether you want to see the output
    println("Mean area for Positive is: " + totalArea/positiveCells.size)
    println("Total Positive Area is: " + totalArea)
    
    //Add the total as "Positive Area" to each annotation.
    annotation.getMeasurementList().putMeasurement("Necrosis Area", totalArea)
    def annotationArea = annotation.getROI().getArea()
    //annotation.getMeasurementList().putMeasurement("Necrosis Area %", totalArea/annotationArea*100)
    //Block 2 - add as many blocks as you have classes
    //repeat above
}

for (annotation in getAnnotationObjects()){
    //Block 1
    def positiveCells = hierarchy.getDescendantObjects(annotation,null, PathCellObject).findAll{it.getPathClass() == getPathClass("Tumor")}
    double totalArea = 0
    for (def cell in positiveCells){
        totalArea += cell.getMeasurementList().getMeasurementValue("Cell: Area")
    }
    //Comment the following in or out depending on whether you want to see the output
    println("Mean area for Positive is: " + totalArea/positiveCells.size)
    println("Total Positive Area is: " + totalArea)
    
    //Add the total as "Positive Area" to each annotation.
    annotation.getMeasurementList().putMeasurement("Viable Area", totalArea)
    def annotationArea = annotation.getROI().getArea()
    //annotation.getMeasurementList().putMeasurement("Viable Area %", totalArea/annotationArea*100)
    //Block 2 - add as many blocks as you have classes
    //repeat above
    }

//SAVE ANNOTATIONS //
def name = getProjectEntry().getImageName() + '.txt'
def path = buildFilePath(PROJECT_BASE_DIR, 'results')
mkdirs(path)
path = buildFilePath(path, name)
saveAnnotationMeasurements(path)

