
selectObjects {it.isAnnotation() && it.getPathClass() == null};
mergeSelectedAnnotations()
resultingClass = getPathClass("Glomeruli")
toChange = getAnnotationObjects().findAll{it.getPathClass() == null}
toChange.each{ it.setPathClass(resultingClass)}

 
 Thread.sleep(100)
// Try to reclaim whatever memory we can, including emptying the tile cache
javafx.application.Platform.runLater {
    getCurrentViewer().getImageRegionStore().cache.clear()
    System.gc()
}
Thread.sleep(100)
