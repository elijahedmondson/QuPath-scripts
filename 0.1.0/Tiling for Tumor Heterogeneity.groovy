setImageType('BRIGHTFIELD_H_E');
setColorDeconvolutionStains('{"Name" : "H&E default", "Stain 1" : "Hematoxylin", "Values 1" : "0.65111 0.70119 0.29049 ", "Stain 2" : "Eosin", "Values 2" : "0.2159 0.8012 0.5581 ", "Background" : " 242 242 242 "}');
runPlugin('qupath.lib.algorithms.TilerPlugin', '{"tileSizeMicrons": 300.0,  "trimToROI": true,  "makeAnnotations": true,  "removeParentAnnotation": true}');
selectAnnotations();
runPlugin('qupath.imagej.detect.nuclei.WatershedCellDetection', '{"detectionImageBrightfield": "Hematoxylin OD",  "requestedPixelSizeMicrons": 0.5,  "backgroundRadiusMicrons": 8.0,  "medianRadiusMicrons": 0.0,  "sigmaMicrons": 1.5,  "minAreaMicrons": 10.0,  "maxAreaMicrons": 400.0,  "threshold": 0.1,  "maxBackground": 2.0,  "watershedPostProcess": true,  "excludeDAB": false,  "cellExpansionMicrons": 5.0,  "includeNuclei": true,  "smoothBoundaries": true,  "makeMeasurements": true}');
selectDetections();
runPlugin('qupath.lib.algorithms.HaralickFeaturesPlugin', '{"magnification": 5.0,  "stainChoice": "Optical density",  "tileSizeMicrons": 25.0,  "includeStats": true,  "doCircular": false,  "useNucleusROIs": true,  "haralickDistance": 1,  "haralickBins": 32}');
