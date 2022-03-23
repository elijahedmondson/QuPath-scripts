setImageType('BRIGHTFIELD_H_DAB');
setColorDeconvolutionStains('{"Name" : "H-DAB default", "Stain 1" : "Hematoxylin", "Values 1" : "0.65111 0.70119 0.29049 ", "Stain 2" : "DAB", "Values 2" : "0.26917 0.56824 0.77759 ", "Background" : " 243 243 243 "}');
selectAnnotations();

runPlugin('qupath.imagej.detect.tissue.PositivePixelCounterIJ', '{"downsampleFactor": 2,  "gaussianSigmaMicrons": 2.0,  "thresholdStain1": 0.0,  "thresholdStain2": 0.2,  "addSummaryMeasurements": true,  "clearParentMeasurements": true,  "appendDetectionParameters": false,  "legacyMeasurements0.1.2": false}');


//SAVE ANNOTATIONS //
def name = getProjectEntry().getImageName() + '.txt'
def path = buildFilePath(PROJECT_BASE_DIR, 'COL1')
mkdirs(path)
path = buildFilePath(path, name)
saveAnnotationMeasurements(path)
print 'Results exported to ' + path