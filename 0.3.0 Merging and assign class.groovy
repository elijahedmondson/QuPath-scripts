
selectObjects {it.isAnnotation() && it.getPathClass() == null};
mergeSelectedAnnotations()
resultingClass = getPathClass("Glomeruli")
toChange = getAnnotationObjects().findAll{it.getPathClass() == null}
toChange.each{ it.setPathClass(resultingClass)}

addPixelClassifierMeasurements("DAB_0.3", "DAB_0.3")

import qupath.lib.gui.measure.ObservableMeasurementTableData
def ob = new ObservableMeasurementTableData();
ob.setImageData( getCurrentImageData(),  getAnnotationObjects() );

getAnnotationObjects().each{
    pos_pixel = ob.getNumericValue(it, "DAB_0.3: Positive area µm^2")
    total_pixel = ob.getNumericValue(it, "Area µm^2")
    
    perc_pos_pixel = pos_pixel/total_pixel*100
    it.getMeasurementList().putMeasurement("% Positive Pixel", perc_pos_pixel)
    print perc_pos_pixel
 }
 
 
 Thread.sleep(100)
// Try to reclaim whatever memory we can, including emptying the tile cache
javafx.application.Platform.runLater {
    getCurrentViewer().getImageRegionStore().cache.clear()
    System.gc()
}
Thread.sleep(100)
