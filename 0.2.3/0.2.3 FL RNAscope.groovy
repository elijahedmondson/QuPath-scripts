setChannelNames(
      'DAPI',
     'Csf2',
     'Cebpb')
     
selectAnnotations();
//mergeSelectedAnnotations();
//selectAnnotations();
runPlugin('qupath.imagej.detect.cells.WatershedCellDetection', '{"detectionImage": "DAPI",  "requestedPixelSizeMicrons": 0.1,  "backgroundRadiusMicrons": 8.0,  "medianRadiusMicrons": 0.0,  "sigmaMicrons": 1.5,  "minAreaMicrons": 10.0,  "maxAreaMicrons": 400.0,  "threshold": 10.0,  "watershedPostProcess": true,  "cellExpansionMicrons": 7.0,  "includeNuclei": true,  "smoothBoundaries": true,  "makeMeasurements": true}');
selectAnnotations();
runPlugin('qupath.imagej.detect.cells.SubcellularDetection', '{"detection[Channel 1]": -1.0,  "detection[Channel 2]": 60000.0,  "detection[Channel 3]": 17000.0,  "doSmoothing": false,  "splitByIntensity": false,  "splitByShape": false,  "spotSizeMicrons": 1.0,  "minSpotSizeMicrons": 0.2,  "maxSpotSizeMicrons": 2.0,  "includeClusters": true}');


def cells = getCellObjects()
resetDetectionClassifications()

cells.each {it.setPathClass(getPathClass('Negative'))}

// Get channel 1 & 2 positives
def ch2Pos = cells.findAll {measurement(it, "Subcellular: Channel 2: Num spots estimated") > 2}
ch2Pos.each {it.setPathClass(getPathClass('Csf2 positive'))}

def ch3Pos = cells.findAll {measurement(it, "Subcellular: Channel 3: Num spots estimated") > 2}
ch3Pos.each {it.setPathClass(getPathClass('Cebpb positive'))}

// Overwrite classifications for double positives
def doublePos = ch2Pos.intersect(ch3Pos)
doublePos.each {it.setPathClass(getPathClass('Double positive'))}

fireHierarchyUpdate()