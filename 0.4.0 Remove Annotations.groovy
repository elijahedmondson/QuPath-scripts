def toDelete = getAnnotationObjects().findAll{it.getPathClass() == getPathClass("Ignore")}
removeObjects(toDelete, true)
