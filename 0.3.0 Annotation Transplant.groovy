//Save annotations//
def path = buildFilePath(PROJECT_BASE_DIR, 'annotations')
def annotations = getAnnotationObjects()
new File(path).withObjectOutputStream {
    it.writeObject(annotations)
}
print 'Done!'


//Paste annotations//

def path = buildFilePath(PROJECT_BASE_DIR, 'annotations')
def annotations = null
new File(path).withObjectInputStream {
    annotations = it.readObject()
}
addObjects(annotations)
print 'Added ' + annotations

//Realign objects

double dx = 0
double dy = -10
for (pathObject in getAllObjects()) {
    if (pathObject.hasROI())
        pathObject.setROI(pathObject.getROI().translate(dx, dy))
}
fireHierarchyUpdate()