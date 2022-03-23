setImageType('BRIGHTFIELD_H_E');
setColorDeconvolutionStains('{"Name" : "H&E default", "Stain 1" : "Hematoxylin", "Values 1" : "0.65111 0.70119 0.29049 ", "Stain 2" : "Eosin", "Values 2" : "0.2159 0.8012 0.5581 ", "Background" : " 242 242 242 "}');
runPlugin('qupath.imagej.detect.tissue.SimpleTissueDetection2', '{"threshold": 200,  "requestedPixelSizeMicrons": 5.0,  "minAreaMicrons": 1000000.0,  "maxHoleAreaMicrons": 10000.0,  "darkBackground": false,  "smoothImage": true,  "medianCleanup": true,  "dilateBoundaries": true,  "smoothCoordinates": true,  "excludeOnBoundary": false,  "singleAnnotation": true}');
selectAnnotations();
classifyDetectionsByCentroid("LungMet")
selectAnnotations();
addPixelClassifierMeasurements("LungMet", "LungMet")
