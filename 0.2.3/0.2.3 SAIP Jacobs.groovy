setImageType('BRIGHTFIELD_H_E');
setColorDeconvolutionStains('{"Name" : "H&E default", "Stain 1" : "Hematoxylin", "Values 1" : "0.65111 0.70119 0.29049 ", "Stain 2" : "Eosin", "Values 2" : "0.2159 0.8012 0.5581 ", "Background" : " 242 243 242 "}');
//runPlugin('qupath.imagej.detect.tissue.SimpleTissueDetection2', '{"threshold": 210,  "requestedPixelSizeMicrons": 20.0,  "minAreaMicrons": 10000.0,  "maxHoleAreaMicrons": 1000000.0,  "darkBackground": false,  "smoothImage": true,  "medianCleanup": true,  "dilateBoundaries": false,  "smoothCoordinates": true,  "excludeOnBoundary": false,  "singleAnnotation": true}');
selectAnnotations();
mergeSelectedAnnotations();
selectAnnotations();

def minArea = 100.0 // To change
def minHoleArea = 100.0 // To change
def classifierName = "Necrosis2" // To change

// Select all annotations
selectAnnotations();

addPixelClassifierMeasurements(classifierName, classifierName);

// Apply pixel classifier inside them
//createAnnotationsFromPixelClassifier(classifierName, minArea, minHoleArea)
createDetectionsFromPixelClassifier(classifierName, minArea, minHoleArea);
print "Done!"
