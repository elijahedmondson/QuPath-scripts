setImageType('BRIGHTFIELD_H_DAB');
setColorDeconvolutionStains('{"Name" : "H-DAB default", "Stain 1" : "Hematoxylin", "Values 1" : "0.65111 0.70119 0.29049 ", "Stain 2" : "DAB", "Values 2" : "0.26917 0.56824 0.77759 ", "Background" : " 255 255 255 "}');
selectAnnotations();
mergeSelectedAnnotations();
selectAnnotations();
runPlugin('qupath.imagej.detect.cells.WatershedCellDetection', '{"detectionImageBrightfield": "Hematoxylin OD",  "requestedPixelSizeMicrons": 0.5,  "backgroundRadiusMicrons": 0.0,  "medianRadiusMicrons": 0.0,  "sigmaMicrons": 1.5,  "minAreaMicrons": 10.0,  "maxAreaMicrons": 400.0,  "threshold": 0.1,  "maxBackground": 2.0,  "watershedPostProcess": true,  "excludeDAB": false,  "cellExpansionMicrons": 8.01,  "includeNuclei": true,  "smoothBoundaries": true,  "makeMeasurements": true}');

runPlugin('qupath.imagej.detect.cells.SubcellularDetection', '{"detection[DAB]": 0.3,  "doSmoothing": true,  "splitByIntensity": true,  "splitByShape": true,  "spotSizeMicrons": 2.0,  "minSpotSizeMicrons": 0.1,  "maxSpotSizeMicrons": 6.0,  "includeClusters": true}');

setCellIntensityClassifications("Subcellular: DAB: Num spots estimated", 1, 10, 20)