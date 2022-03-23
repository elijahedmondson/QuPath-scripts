setChannelNames('DAPI', 'GFP','CD45R')
     
selectAnnotations();

runPlugin('qupath.imagej.detect.cells.PositiveCellDetection', '{"detectionImage": "DAPI",  "requestedPixelSizeMicrons": 0.5,  "backgroundRadiusMicrons": 8.0,  "medianRadiusMicrons": 0.0,  "sigmaMicrons": 1.5,  "minAreaMicrons": 10.0,  "maxAreaMicrons": 400.0,  "threshold": 500.0,  "watershedPostProcess": true,  "cellExpansionMicrons": 5.0,  "includeNuclei": true,  "smoothBoundaries": true,  "makeMeasurements": true,  "thresholdCompartment": "Cytoplasm: NI820 mean",  "thresholdPositive1": 1100.0,  "thresholdPositive2": 1600.0,  "thresholdPositive3": 2100.0,  "singleThreshold": false}');
