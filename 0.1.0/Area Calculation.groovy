def tumorCells = getCellObjects().findAll{it.getPathClass() == getPathClass("Tumor")}
def totalArea = 0
for (cell in tumorCells){
    totalArea = totalArea + cell.getMeasurementList().getMeasurementValue("Cell: Area")
}
println("Total area of your class of cells in square micrometers is: " + totalArea)

//If you want to sum up any other type of object, swap out Tumor for your class of choice. 
//You could also then add this value to an annotation rather than printing it out, or if you 
//knew you only had one annotation, calculate the percentage of that annotation's area that 
//the totalArea represents. Your choice!