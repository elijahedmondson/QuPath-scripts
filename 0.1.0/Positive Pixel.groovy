setImageType('BRIGHTFIELD_H_DAB');
setColorDeconvolutionStains('{"Name" : "H-DAB default", "Stain 1" : "Hematoxylin", "Values 1" : "0.65111 0.70119 0.29049 ", "Stain 2" : "DAB", "Values 2" : "0.26917 0.56824 0.77759 ", "Background" : " 241 241 241 "}');
selectAnnotations();
mergeSelectedAnnotations();
//runPlugin('qupath.imagej.detect.tissue.SimpleTissueDetection2', '{"threshold": 230,  "requestedPixelSizeMicrons": 20.0,  "minAreaMicrons": 100000.0,  "maxHoleAreaMicrons": 1000000.0,  "darkBackground": false,  "smoothImage": true,  "medianCleanup": true,  "dilateBoundaries": false,  "smoothCoordinates": true,  "excludeOnBoundary": true,  "singleAnnotation": false}');
selectAnnotations();
runPlugin('qupath.imagej.detect.tissue.PositivePixelCounterIJ', '{"downsampleFactor": 10,  "gaussianSigmaMicrons": 5.0,  "thresholdStain1": 1.0,  "thresholdStain2": 0.15,  "addSummaryMeasurements": true,  "clearParentMeasurements": true,  "appendDetectionParameters": false,  "legacyMeasurements0.1.2": false}');


def name = getProjectEntry().getImageName() + '.txt'
def path = buildFilePath(PROJECT_BASE_DIR, 'Pixel')
mkdirs(path)
path = buildFilePath(path, name)
saveAnnotationMeasurements(path)
print 'Results exported to ' + path