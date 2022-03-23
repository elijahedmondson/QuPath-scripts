setImageType('FLUORESCENCE');
setChannelNames('DAPI','NUMA1','Siglec-15')
selectAnnotations();
mergeSelectedAnnotations();     
selectAnnotations();
//selectTMACores();

runPlugin('qupath.imagej.detect.cells.WatershedCellDetection', '{"detectionImage": "DAPI",  "requestedPixelSizeMicrons": 0.5,  "backgroundRadiusMicrons": 0.0,  "medianRadiusMicrons": 0.0,  "sigmaMicrons": 1.5,  "minAreaMicrons": 10.0,  "maxAreaMicrons": 400.0,  "threshold": 10000.0,  "watershedPostProcess": true,  "cellExpansionMicrons": 5.0,  "includeNuclei": true,  "smoothBoundaries": true,  "makeMeasurements": true}');

//runObjectClassifier("NUMA1.Siglec-15")
runObjectClassifier("NUMA1.Siglec-15.05")
//runObjectClassifier("NUMA1.Siglec-15.1")
//runObjectClassifier("CD4.CD8")