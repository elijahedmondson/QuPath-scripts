setImageType('BRIGHTFIELD_H_DAB');
setColorDeconvolutionStains('{"Name" : "H-DAB default", "Stain 1" : "Hematoxylin", "Values 1" : "0.65111 0.70119 0.29049 ", "Stain 2" : "DAB", "Values 2" : "0.26917 0.56824 0.77759 ", "Background" : " 255 255 255 "}');

//runPlugin('qupath.imagej.detect.tissue.SimpleTissueDetection2', '{"threshold": 230,  "requestedPixelSizeMicrons": 20.0,  "minAreaMicrons": 1000000.0,  "maxHoleAreaMicrons": 1000000.0,  "darkBackground": false,  "smoothImage": true,  "medianCleanup": true,  "dilateBoundaries": false,  "smoothCoordinates": true,  "excludeOnBoundary": false,  "singleAnnotation": true}');
//selectAnnotations();
//mergeSelectedAnnotations();
selectAnnotations();

runPlugin('qupath.imagej.detect.nuclei.WatershedCellDetection', '{"detectionImageBrightfield": "Hematoxylin OD",  "requestedPixelSizeMicrons": 0.5,  "backgroundRadiusMicrons": 8,  "medianRadiusMicrons": 0.0,  "sigmaMicrons": 1.5,  "minAreaMicrons": 20.0,  "maxAreaMicrons": 400.0,  "threshold": 0.05,  "maxBackground": 2.0,  "watershedPostProcess": true,  "excludeDAB": false,  "cellExpansionMicrons": 10.0,  "includeNuclei": true,  "smoothBoundaries": true,  "makeMeasurements": true}');
selectAnnotations();
runPlugin('qupath.imagej.detect.cells.SubcellularDetection', '{"detection[DAB]": 0.4,  "doSmoothing": false,  "splitByIntensity": true,  "splitByShape": false,  "spotSizeMicrons": .3,  "minSpotSizeMicrons": 0.01,  "maxSpotSizeMicrons": 1.5,  "includeClusters": false}');
setCellIntensityClassifications("Subcellular: DAB: Num spots estimated", 1, 4, 10)

