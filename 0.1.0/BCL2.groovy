setImageType('BRIGHTFIELD_H_DAB');
setColorDeconvolutionStains('{"Name" : "H-DAB default", "Stain 1" : "Hematoxylin", "Values 1" : "0.65111 0.70119 0.29049 ", "Stain 2" : "DAB", "Values 2" : "0.26917 0.56824 0.77759 ", "Background" : " 205 206 217 "}');
selectAnnotations();
runPlugin('qupath.imagej.detect.nuclei.WatershedCellDetection', '{"detectionImageBrightfield": "Hematoxylin OD",  "requestedPixelSizeMicrons": 0.5,  "backgroundRadiusMicrons": 0.0,  "medianRadiusMicrons": 0.0,  "sigmaMicrons": 2.0,  "minAreaMicrons": 20.0,  "maxAreaMicrons": 100.0,  "threshold": 0.1,  "maxBackground": 2.0,  "watershedPostProcess": true,  "excludeDAB": false,  "cellExpansionMicrons": 6.0,  "includeNuclei": true,  "smoothBoundaries": true,  "makeMeasurements": true}');
runClassifier('C:\\Users\\edmondsonef\\Desktop\\QuPath\\Hewes\\182976\\classifiers\\Bcl2.qpclassifier');


//SAVE ANNOTATIONS //

def name = getProjectEntry().getImageName() + '.txt'
def path = buildFilePath('C:/Users/edmondsonef/Desktop/QuPath/Hewes/182976/', 'Bcl2')
mkdirs(path)
path = buildFilePath(path, name)
saveAnnotationMeasurements(path)
