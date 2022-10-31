import qupath.lib.gui.measure.ObservableMeasurementTableData
def ob = new ObservableMeasurementTableData();
ob.setImageData( getCurrentImageData(),  getAnnotationObjects() );

getAnnotationObjects().each{
    onecells = ob.getNumericValue(it, "Num 1+")
    twocells = ob.getNumericValue(it, "Num 2+")
    threecells = ob.getNumericValue(it, "Num 3+")
    twothreecells = twocells + threecells
    onetwothreecells = onecells + twocells + threecells
    area = ob.getNumericValue(it, "Area µm^2")
    
    cells3perarea = threecells/area*1000000
    it.getMeasurementList().putMeasurement("Num 3+ per mm^2", cells3perarea)
    
    cellstwothreeperarea = twothreecells/area*1000000
    it.getMeasurementList().putMeasurement("Num 2+ & 3+ per mm^2", cellstwothreeperarea)
    
    onetwothreecellsperarea = onetwothreecells/area*1000000
    it.getMeasurementList().putMeasurement("Num 1+ & 2+ & 3+ per mm^2", onetwothreecellsperarea)
}

