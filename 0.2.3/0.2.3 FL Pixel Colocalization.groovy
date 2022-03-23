setImageType('FLUORESCENCE');
selectAnnotations();
//mergeSelectedAnnotations();
selectAnnotations();
createAnnotationsFromPixelClassifier("GFP.px", 5.0, 10.0, "SELECT_NEW")
addPixelClassifierMeasurements("CD11c", "CD11c")
addPixelClassifierMeasurements("CD3.px", "CD3.px")
