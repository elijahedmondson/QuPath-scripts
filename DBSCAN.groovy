// DBScan implementation for QuPath 0.2.0 - Michael Nelson, May 2020
// Based heavily on code from https://bhanuchander210.github.io/Tutorial-Machine-Learning-With-Groovy/
// With major suggestion from Sara Mcardle
// Instigated by Colt Egelston
// Version 2.0 Added cluster size as a measurement

//Probably should read this stuff the second time after it doesn't work quite right the first time.
////////////////////////////////////////////////////////////////////////////////
//micronsBetweenCentroids (which is converted into "eps" and minPts to adjust the behavior of the clustering.
//eps and minPts are as described in the DBSCAN wiki
//baseClasses to true if you want to ignore complex classes and use subclasses from multiplexing classifications

/////////////////////////////////////////////////////////////////////////////////

import org.apache.commons.math3.ml.clustering.DBSCANClusterer
import org.apache.commons.math3.ml.clustering.DoublePoint

//distance to search around an object for another centroid.
double micronsBetweenCentroids = 30.0
//Minimum number of objects needed to be considered a cluster
int minPts = 5

boolean baseClasses = false


double eps = micronsBetweenCentroids/getCurrentServer().getPixelCalibration().getPixelWidthMicrons()
print eps

//Get the classes you want to analyze. Avoids Negative and no class by default.
Set classSet = []
List classList = []
if (!baseClasses){
    
    for (object in getCellObjects()) {
        c = object.getPathClass()
        if (c != getPathClass("Negative")){
            classSet << c
        }
    }
    
    classList.addAll(classSet.findAll{
        //If you only want one class, use it == getPathClass("MyClassHere") instead
       it != null 
    })
    print classList
}else{
    for (object in getCellObjects()) {
        parts = PathClassTools.splitNames(object.getPathClass())
        parts.each{
            if (it != "Negative"){
                classSet << it
            }
        }
    }
    classList.addAll(classSet.findAll{
        //If you only want one sub-class, use it == getPathClass("MyClassHere") instead
       it != null 
    })


}

classList.each{ c->
    //Storage for stuff we do later. points will hold the XY coordinates as DoublePoint objects
    List<DoublePoint> points = new ArrayList<DoublePoint>()
    //The Map allows us to use the DoublePoint to match the list of coordinates output by DBScan to the QuPath object
    Map< DoublePoint, double> pointMap = [:]
    
    //Get the objects of interest for this class or sub-class
    if(baseClasses){
        batch = getDetectionObjects().findAll{it.getPathClass().toString().contains(c)}
        text = c
    }else{
        batch = getDetectionObjects().findAll{it.getPathClass() == c}
        text = c.getName()
    }
    
    //print batch.size()
    //Prep each object being analyzed for clustering.
    batch.eachWithIndex{d,x->
        //create the unique identifier, if you want to look back at details
        //d.getMeasurementList().putMeasurement("ID",(double)x)
        
        //Reset previous cluster analyses for the given cell
        d?.getMeasurementList().removeMeasurements("Cluster "+text)
        
        //create the linker between the ID and the centroid
        double [] point = [d.getROI().getCentroidX(), d.getROI().getCentroidY()]
        DoublePoint dpoint = new DoublePoint(point)
        //print dpoint
        points[x] = dpoint
        
        //Key point here, each index (cell in most cases) is tracked and matched to it's XY coordinates
        pointMap[dpoint]= (double)x
    }
    
    //print points if you want to see them all
    def showClosure = {detail ->
        //println "Cluster : " + detail.cluster + " Point : " + detail.point + " Label : "+ detail.labels
        //print "labels "+(int)detail.labels
        //print "cluster"+detail.cluster
        
        //this uses the label (the index from the "batch") to access the correct cell, and apply a measurement with the correct cluster number
        batch[detail.labels]?.getMeasurementList()?.putMeasurement("Cluster "+text,detail.cluster )
        batch[detail.labels]?.getMeasurementList()?.putMeasurement("Cluster Size "+text,detail.clusterSize )
    }
    
    //Main run statements
    DBSCANClusterer DBScan = new DBSCANClusterer(eps, minPts)
    
    collectDetails(DBScan.cluster(points), pointMap).each(showClosure)
    
}
print "Done!"


//Things from the website linked at the top that I messed with very little.
//Used to extract information from the result of DBScan, might be useful if I play with other kinds of clustering in the future.
List<ClusterDetail> collectDetails(def clusters, pointMap)
{
    List<ClusterDetail> ret = []
    clusters.eachWithIndex{ c, ci ->
        c.getPoints().each { pnt ->
            DoublePoint pt = pnt as DoublePoint
            ret.add new ClusterDetail (ci +1 as Integer, pt, pointMap[pnt], c.getPoints().size())
        }
    }
    ret
}

class ClusterDetail
{
    int cluster
    DoublePoint point
    double labels
    int clusterSize
    ClusterDetail(int no, DoublePoint pt, double labs, int size)
    {
        cluster = no; point= pt; labels = labs; clusterSize = size
    }
}