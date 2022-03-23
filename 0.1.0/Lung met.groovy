setImageType('BRIGHTFIELD_H_DAB');
runPlugin('qupath.imagej.detect.tissue.SimpleTissueDetection2', '{"threshold": 210,  "requestedPixelSizeMicrons": 20.0,  "minAreaMicrons": 1000000.0,  "maxHoleAreaMicrons": 1000000.0,  "darkBackground": false,  "smoothImage": true,  "medianCleanup": true,  "dilateBoundaries": false,  "smoothCoordinates": true,  "excludeOnBoundary": true,  "singleAnnotation": true}');
selectAnnotations();
runPlugin('qupath.imagej.detect.cells.WatershedCellDetection', '{"detectionImageBrightfield": "Hematoxylin OD",  "requestedPixelSizeMicrons": 0.5,  "backgroundRadiusMicrons": 0.0,  "medianRadiusMicrons": 0.0,  "sigmaMicrons": 1.5,  "minAreaMicrons": 10.0,  "maxAreaMicrons": 400.0,  "threshold": 0.1,  "maxBackground": 2.0,  "watershedPostProcess": true,  "excludeDAB": false,  "cellExpansionMicrons": 5.0,  "includeNuclei": true,  "smoothBoundaries": true,  "makeMeasurements": true}');
runClassifier('C:\\Users\\edmondsonef\\Desktop\\QuPath\\Harris\\MHL 200536\\classifiers\\lung.met2.qpclassifier');


//SAVE ANNOTATIONS //
def name = getProjectEntry().getImageName() + '.txt'
def path = buildFilePath(PROJECT_BASE_DIR, 'results')
mkdirs(path)
path = buildFilePath(path, name)
saveAnnotationMeasurements(path)
