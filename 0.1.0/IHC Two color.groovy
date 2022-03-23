setImageType('BRIGHTFIELD_OTHER');
setColorDeconvolutionStains('{"Name" : "H-DAB-AP", "Stain 1" : "Hematoxylin", "Values 1" : "0.54936 0.71051 0.43975 ", "Stain 2" : "DAB", "Values 2" : "0.3976 0.6146 0.68131 ", "Stain 3" : "AP", "Values 3" : "0.12439 0.7547 0.64417 ", "Background" : " 255 255 255 "}');

runPlugin('qupath.opencv.CellCountsCV', '{"stainChannel": "Hematoxylin + DAB",  "gaussianSigmaMicrons": 2.5,  "backgroundRadiusMicrons": 0.0,  "doDoG": false,  "threshold": 0.2,  "thresholdDAB": 0.2,  "detectionDiameter": 10.0}');

//Also, because the default ?positive? and ?negative? detection colors (red and blue) are not very clear with this staining, I changed them with the following script:

// Change colors
positiveColor = getColorRGB(20, 255, 20)
getPathClass("Positive").setColor(positiveColor)
negativeColor = getColorRGB(20, 200, 255)
getPathClass("Negative").setColor(negativeColor)
fireHierarchyUpdate()


//SAVE ANNOTATIONS //

def name = getProjectEntry().getImageName() + '.txt'
//def path = buildFilePath('C:/Users/edmondsonef/Desktop/QuPath/', 'annotation results')
mkdirs(path)
path = buildFilePath(path, name)
saveAnnotationMeasurements(path)