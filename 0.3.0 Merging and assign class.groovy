
selectObjects {it.isAnnotation() && it.getPathClass() == null};
mergeSelectedAnnotations()
resultingClass = getPathClass("Glomeruli")
toChange = getAnnotationObjects().findAll{it.getPathClass() == null}
toChange.each{ it.setPathClass(resultingClass)}