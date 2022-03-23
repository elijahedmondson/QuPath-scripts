setImageType('BRIGHTFIELD_H_DAB');
setColorDeconvolutionStains('{"Name" : "H-DAB default", "Stain 1" : "Hematoxylin", "Values 1" : "0.65111 0.70119 0.29049 ", "Stain 2" : "DAB", "Values 2" : "0.26917 0.56824 0.77759 ", "Background" : " 255 255 255 "}');
selectAnnotations();
//mergeSelectedAnnotations();
//runPlugin('qupath.imagej.detect.tissue.SimpleTissueDetection2', '{"threshold": 230,  "requestedPixelSizeMicrons": 20.0,  "minAreaMicrons": 1000000.0,  "maxHoleAreaMicrons": 10000.0,  "darkBackground": false,  "smoothImage": true,  "medianCleanup": true,  "dilateBoundaries": false,  "smoothCoordinates": true,  "excludeOnBoundary": true,  "singleAnnotation": true}');
selectAnnotations();

runPlugin('qupath.imagej.detect.cells.PositiveCellDetection', '{"detectionImageBrightfield": "Hematoxylin OD",  "requestedPixelSizeMicrons": 0.5,  "backgroundRadiusMicrons": 0.0,  "medianRadiusMicrons": 0.0,  "sigmaMicrons": 2.0,  "minAreaMicrons": 10.0,  "maxAreaMicrons": 400.0,  "threshold": 0.01,  "maxBackground": 1.0,  "watershedPostProcess": true,  "excludeDAB": false,  "cellExpansionMicrons": 0.0,  "includeNuclei": true,  "smoothBoundaries": true,  "makeMeasurements": true,  "thresholdCompartment": "Nucleus: DAB OD mean",  "thresholdPositive1": 0.5,  "thresholdPositive2": 0.4,  "thresholdPositive3": 0.6000000000000001,  "singleThreshold": true}');

//runClassifier('C:\\Users\\edmondsonef\\Desktop\\QuPath\\');
