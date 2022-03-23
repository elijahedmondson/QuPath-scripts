setImageType('FLUORESCENCE');
selectAnnotations();

runPlugin('qupath.imagej.detect.cells.PositiveCellDetection', '{"detectionImage": "DAPI",  "requestedPixelSizeMicrons": 0.5,  "backgroundRadiusMicrons": 0.0,  "medianRadiusMicrons": 0.0,  "sigmaMicrons": 1.5,  "minAreaMicrons": 50.0,  "maxAreaMicrons": 400.0,  "threshold": 2000.0,  "watershedPostProcess": true,  "cellExpansionMicrons": 15.0,  "includeNuclei": true,  "smoothBoundaries": true,  "makeMeasurements": true,  "thresholdCompartment": "Cell: FITC mean",  "thresholdPositive1": 35000.0,  "thresholdPositive2": 40000.0,  "thresholdPositive3": 50000.0,  "singleThreshold": false}');

//SAVE ANNOTATIONS //

def name = getProjectEntry().getImageName() + '.txt'
def path = buildFilePath(PROJECT_BASE_DIR, 'GFP')
mkdirs(path)
path = buildFilePath(path, name)
saveAnnotationMeasurements(path)