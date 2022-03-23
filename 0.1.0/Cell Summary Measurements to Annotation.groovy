//Sometimes you may want to add a summary measurement from cells within each annotation to the annotation itself.
//This will allow you to see that measurement in the "Show Annotation Measurements" list.

//In this case, it will add the total area taken up by Positive class cells within each annotation to their parent
//annotation as "Positive Area"

import qupath.lib.objects.PathCellObject

hierarchy = getCurrentHierarchy()

for (annotation in getAnnotationObjects()){
    //Block 1
    def positiveCells = hierarchy.getDescendantObjects(annotation,null, PathCellObject).findAll{it.getPathClass() == getPathClass("Positive")}
    double totalArea = 0
    for (def cell in positiveCells){
        totalArea += cell.getMeasurementList().getMeasurementValue("Cell: Area")
    }
    //Comment the following in or out depending on whether you want to see the output
    //println("Mean area for Positive is: " + totalArea/positiveCells.size)
    //println("Total Positive Area is: " + totalArea)
    
    //Add the total as "Positive Area" to each annotation.
    annotation.getMeasurementList().putMeasurement("Positive Area", totalArea)
    //Block 2 - add as many blocks as you have classes
    //...
}


//SAVE ANNOTATIONS //

def name = getProjectEntry().getImageName() + '.txt'
//def path = buildFilePath('C:/Users/edmondsonef/Desktop/QuPath/', 'annotation results')
mkdirs(path)
path = buildFilePath(path, name)
saveAnnotationMeasurements(path)