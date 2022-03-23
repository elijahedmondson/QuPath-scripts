setImageType('BRIGHTFIELD_OTHER');
setColorDeconvolutionStains('{"Name" : "H-DAB default", "Stain 1" : "Hematoxylin", "Values 1" : "0.65111 0.70119 0.29049 ", "Stain 2" : "DAB", "Values 2" : "0.26917 0.56824 0.77759 ", "Background" : " 242 243 242 "}');
//runPlugin('qupath.imagej.detect.tissue.SimpleTissueDetection2', '{"threshold": 210,  "requestedPixelSizeMicrons": 5.0,  "minAreaMicrons": 50000.0,  "maxHoleAreaMicrons": 1000000.0,  "darkBackground": false,  "smoothImage": true,  "medianCleanup": true,  "dilateBoundaries": true,  "smoothCoordinates": true,  "excludeOnBoundary": false,  "singleAnnotation": true}');
selectAnnotations();
runPlugin('qupath.imagej.detect.tissue.PositivePixelCounterIJ', '{"downsampleFactor": 4,  "gaussianSigmaMicrons": 2.0,  "thresholdStain1": 0.001,  "thresholdStain2": 0.3,  "addSummaryMeasurements": true}');

//SAVE ANNOTATIONS //

def name = getProjectEntry().getImageName() + '.txt'
def path = buildFilePath(PROJECT_BASE_DIR, 'Von Kossa')
mkdirs(path)
path = buildFilePath(path, name)
saveAnnotationMeasurements(path)