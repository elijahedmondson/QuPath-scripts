setImageType('BRIGHTFIELD_H_E');
setColorDeconvolutionStains('{"Name" : "H&E default", "Stain 1" : "Hematoxylin", "Values 1" : "0.65111 0.70119 0.29049 ", "Stain 2" : "Eosin", "Values 2" : "0.2159 0.8012 0.5581 ", "Background" : " 255 255 255 "}');
runPlugin('qupath.imagej.detect.tissue.SimpleTissueDetection2', '{"threshold": 197,  "requestedPixelSizeMicrons": 5.0,  "minAreaMicrons": 50000.0,  "maxHoleAreaMicrons": 1000000.0,  "darkBackground": false,  "smoothImage": true,  "medianCleanup": true,  "dilateBoundaries": true,  "smoothCoordinates": true,  "excludeOnBoundary": false,  "singleAnnotation": true}');


//SAVE ANNOTATIONS //

def name = getProjectEntry().getImageName() + '.txt'
def path = buildFilePath(PROJECT_BASE_DIR, 'Area')
mkdirs(path)
path = buildFilePath(path, name)
saveAnnotationMeasurements(path)