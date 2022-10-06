import qupath.lib.gui.measure.ObservableMeasurementTableData
def ob = new ObservableMeasurementTableData();
ob.setImageData( getCurrentImageData(),  getAnnotationObjects() );

getAnnotationObjects().each{
    area = ob.getNumericValue(it, "Area µm^2")
    positivePix = ob.getNumericValue(it, "DAB: Positive area µm^2")
    percentPosPix = positivePix/area*100
    it.getMeasurementList().putMeasurement("% DAB Positive Pixel", percentPosPix)
}


