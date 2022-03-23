setChannelNames(
      'DAPI',
     'GFP-488',
     'NeuN-594')
     
selectAnnotations();

runPlugin('qupath.imagej.detect.cells.WatershedCellDetection', '{"detectionImage": "DAPI",  "requestedPixelSizeMicrons": 0.5,  "backgroundRadiusMicrons": 8.0,  "medianRadiusMicrons": 0.0,  "sigmaMicrons": 1.5,  "minAreaMicrons": 10.0,  "maxAreaMicrons": 400.0,  "threshold": 10000.0,  "watershedPostProcess": true,  "cellExpansionMicrons": 5.0,  "includeNuclei": true,  "smoothBoundaries": true,  "makeMeasurements": true}');

//Create "single measurement classifier"

runClassifier('C://Users//edmondsonef//Desktop//QuPath//Venditti//MHL 200422B//classifiers//object_classifiers//GFP.json');
runClassifier('C://Users//edmondsonef//Desktop//QuPath//Venditti//MHL 200422B//classifiers//object_classifiers//NeuN-594.json');
runClassifier('C://Users//edmondsonef//Desktop//QuPath//Venditti//MHL 200422B//classifiers//object_classifiers//NeuN.GFP.json');


// Load and run a trained classifier
def pathClassifier = buildFilePath(PROJECT_BASE_DIR , 'classifiers', 'NeuN.GFP.json')
runClassifier(pathClassifier);


def name = getProjectEntry().getImageName() + '.txt'
def path = buildFilePath(PROJECT_BASE_DIR, 'Colocalization')
mkdirs(path)
path = buildFilePath(path, name)
saveAnnotationMeasurements(path)