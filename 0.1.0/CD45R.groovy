setImageType('BRIGHTFIELD_H_DAB');
selectAnnotations();
runPlugin('qupath.imagej.detect.nuclei.PositiveCellDetection', '{"detectionImageBrightfield": "Hematoxylin OD",  "requestedPixelSizeMicrons": 0.5,  "backgroundRadiusMicrons": 0.0,  "medianRadiusMicrons": 0.0,  "sigmaMicrons": 1.5,  "minAreaMicrons": 10.0,  "maxAreaMicrons": 400.0,  "threshold": 0.1,  "maxBackground": 2.0,  "watershedPostProcess": true,  "excludeDAB": false,  "cellExpansionMicrons": 5.0,  "includeNuclei": true,  "smoothBoundaries": true,  "makeMeasurements": true,  "thresholdCompartment": "Cell: DAB OD mean",  "thresholdPositive1": 0.5,  "thresholdPositive2": 0.4,  "thresholdPositive3": 0.6,  "singleThreshold": true}');
saveAnnotationMeasurements('C:/Users/edmondsonef/Desktop/QuPath/Sayers/2018.3.2/CD45R/');


//SAVE ANNOTATIONS //

def name = getProjectEntry().getImageName() + '.txt'
//def path = buildFilePath('C:/Users/edmondsonef/Desktop/QuPath/Dong Zhang/ER CD45/', 'CD45')
//def path = buildFilePath('P:/archive/PHL/Edmondson/QuPath/Young/PHL 190350/', 'CD45')
def path = buildFilePath(PROJECT_BASE_DIR, 'CD45')
mkdirs(path)
path = buildFilePath(path, name)
saveAnnotationMeasurements(path)