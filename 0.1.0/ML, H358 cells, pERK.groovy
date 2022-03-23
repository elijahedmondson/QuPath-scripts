setImageType('BRIGHTFIELD_H_DAB');


selectAnnotations();
runPlugin('qupath.imagej.detect.nuclei.WatershedCellDetection', '{"detectionImageBrightfield": "Hematoxylin OD",  "requestedPixelSizeMicrons": 0.5,  "backgroundRadiusMicrons": 0.0,  "medianRadiusMicrons": 0.0,  "sigmaMicrons": 2.0,  "minAreaMicrons": 10.0,  "maxAreaMicrons": 100.0,  "threshold": 0.08,  "maxBackground": 2.0,  "watershedPostProcess": true,  "excludeDAB": false,  "cellExpansionMicrons": 5.0,  "includeNuclei": true,  "smoothBoundaries": true,  "makeMeasurements": true}');
runClassifier('C:/Users/edmondsonef/Desktop/QuPath/Tarasova/PHL182995/classifiers/pERK.qpclassifier');


def name = getProjectEntry().getImageName() + '.txt'
def path = buildFilePath('C:/Users/edmondsonef/Desktop/QuPath/Tarasova/PHL182995/', 'pERK')
mkdirs(path)
path = buildFilePath(path, name)
saveAnnotationMeasurements(path)
