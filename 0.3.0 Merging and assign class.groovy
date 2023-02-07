
selectObjects {it.isAnnotation() && it.getPathClass() == null};
mergeSelectedAnnotations()
resultingClass = getPathClass("Metastasis")
toChange = getAnnotationObjects().findAll{it.getPathClass() == null}
toChange.each{ it.setPathClass(resultingClass)}


//selectAnnotations();
//addPixelClassifierMeasurements("DAB_0.9", "DAB_0.9")


//import qupath.lib.gui.measure.ObservableMeasurementTableData
//def ob = new ObservableMeasurementTableData();
//ob.setImageData( getCurrentImageData(),  getAnnotationObjects() );

//getAnnotationObjects().each{
//    pos_pixel = ob.getNumericValue(it, "DAB_0.9: Positive area µm^2")
//    total_pixel = ob.getNumericValue(it, "Area µm^2")
    
//    perc_pos_pixel = pos_pixel/total_pixel*100
//    it.getMeasurementList().putMeasurement("% Positive Pixel", perc_pos_pixel)
//    print perc_pos_pixel
// }

