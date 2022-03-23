setImageType('BRIGHTFIELD_H_DAB');
selectAnnotations();
//mergeSelectedAnnotations();
//selectAnnotations();
runPlugin('qupath.imagej.detect.nuclei.PositiveCellDetection', '{"detectionImageBrightfield": "Hematoxylin OD", "requestedPixelSizeMicrons": 0.5, "backgroundRadiusMicrons": 0.0,  "medianRadiusMicrons": 0.0,  "sigmaMicrons": 1.5,  "minAreaMicrons": 20.0,  "maxAreaMicrons": 400.0,  "threshold": 0.06,  "maxBackground": 2.0,  "watershedPostProcess": true,  "excludeDAB": false,  "cellExpansionMicrons": 7.0,  "includeNuclei": true,  "smoothBoundaries": true,  "makeMeasurements": true,  "thresholdCompartment": "Cell: DAB OD mean",  "thresholdPositive1": 0.3,  "thresholdPositive2": 0.4,  "thresholdPositive3": 0.6,  "singleThreshold": true}');

//SAVE ANNOTATIONS //

def name = getProjectEntry().getImageName() + '.txt'
def path = buildFilePath(PROJECT_BASE_DIR, 'F480')
mkdirs(path)
path = buildFilePath(path, name)
saveAnnotationMeasurements(path)
