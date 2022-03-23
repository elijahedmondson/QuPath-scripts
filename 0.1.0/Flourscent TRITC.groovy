setImageType('FLUORESCENCE');
selectAnnotations();
runPlugin('qupath.imagej.detect.cells.PositiveCellDetection', '{"detectionImage": "DAPI",  "requestedPixelSizeMicrons": 0.5,  "backgroundRadiusMicrons": 8.0,  "medianRadiusMicrons": 0.0,  "sigmaMicrons": 1.5,  "minAreaMicrons": 10.0,  "maxAreaMicrons": 400.0,  "threshold": 100.0,  "watershedPostProcess": true,  "cellExpansionMicrons": 5.0,  "includeNuclei": true,  "smoothBoundaries": true,  "makeMeasurements": true,  "thresholdCompartment": "Cell: TRITC mean",  "thresholdPositive1": 10000.0,  "thresholdPositive2": 20000.0,  "thresholdPositive3": 30000.0,  "singleThreshold": false}');


//SAVE ANNOTATIONS //
def name = getProjectEntry().getImageName() + '.txt'
def path = buildFilePath(PROJECT_BASE_DIR, 'MSLN')
mkdirs(path)
path = buildFilePath(path, name)
saveAnnotationMeasurements(path)