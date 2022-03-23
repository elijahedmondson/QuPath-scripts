setImageType('BRIGHTFIELD_H_DAB');
setColorDeconvolutionStains('{"Name" : "H-DAB default", "Stain 1" : "Hematoxylin", "Values 1" : "0.65111 0.70119 0.29049 ", "Stain 2" : "DAB", "Values 2" : "0.26917 0.56824 0.77759 ", "Background" : " 255 255 255 "}');
runPlugin('qupath.imagej.detect.tissue.SimpleTissueDetection2', '{"threshold": 222,  "requestedPixelSizeMicrons": 20.0,  "minAreaMicrons": 1000000.0,  "maxHoleAreaMicrons": 1000000.0,  "darkBackground": false,  "smoothImage": true,  "medianCleanup": true,  "dilateBoundaries": false,  "smoothCoordinates": true,  "excludeOnBoundary": true,  "singleAnnotation": true}');
selectAnnotations();
runPlugin('qupath.lib.algorithms.TilerPlugin', '{"tileSizeMicrons": 500.0,  "trimToROI": true,  "makeAnnotations": true,  "removeParentAnnotation": false}');
selectAnnotations();
runPlugin('qupath.lib.algorithms.IntensityFeaturesPlugin', '{"pixelSizeMicrons": 5.0,  "region": "ROI",  "tileSizeMicrons": 500.0,  "colorOD": false,  "colorStain1": false,  "colorStain2": true,  "colorStain3": false,  "colorRed": false,  "colorGreen": false,  "colorBlue": false,  "colorHue": false,  "colorSaturation": false,  "colorBrightness": false,  "doMean": false,  "doStdDev": false,  "doMinMax": true,  "doMedian": false,  "doHaralick": false,  "haralickDistance": 1,  "haralickBins": 32}');

//SAVE ANNOTATIONS //

def name = getProjectEntry().getImageName() + '.txt'
def path = buildFilePath(PROJECT_BASE_DIR, 'TILE')
mkdirs(path)
path = buildFilePath(path, name)
saveAnnotationMeasurements(path)