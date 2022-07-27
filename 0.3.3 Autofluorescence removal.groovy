import qupath.lib.gui.scripting.QPEx

toRemove = []
getAnnotationObjects().each{ s->
if (measurement(s, “FITC: Cell: Mean”) > 3000 & measurement(s, “TRITC: Cell: Mean”) > 3000 ) {
toRemove << s
}
}

removeObjects(toRemove,false)