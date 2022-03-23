//****************VALUES TO EDIT***********//

setChannelNames(
      'DAPI',
     'GFP',
     'NeuN'
)

//Channel numbers are based on cell measurement/channel order in Brightness/contrast menu, starting with 1
int FIRST_CHANNEL = 2
int SECOND_CHANNEL = 3



//CHOOSE ONE: "cell", "nucleus", "cytoplasm", "tile", "detection", "subcell"
//"detection" should be the equivalent of everything
String objectType = "cell"

//These should be figured out for a given sample to eliminate background signal
//Pixels below this value will not be considered for a given channel.
//Used for Manders coefficients only.
ch1Background = 1000
ch2Background = 10000

//***************No touchee past here************//

import qupath.lib.regions.RegionRequest
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.awt.image.BufferedImage
import qupath.imagej.tools.IJTools
import ij.process.ImageProcessor
import qupath.lib.images.servers.ImageServer
import qupath.lib.objects.PathObject
import qupath.lib.images.PathImage
import qupath.imagej.tools.PathImagePlus

def imageData = getCurrentImageData()
def hierarchy = imageData.getHierarchy()

def serverOriginal = imageData.getServer()
String path = serverOriginal.getPath()
double downsample = 1.0
ImageServer<BufferedImage> server = serverOriginal



println("Running, please wait...")
//target the objects you want to analyze
if(objectType == "cell" || objectType == "nucleus" || objectType == "cytoplasm" ){detections = getCellObjects()}
if(objectType == "tile"){detections = getDetectionObjects().findAll{it.isTile()}}
if(objectType == "detection"){detections = getDetectionObjects()}
if(objectType == "subcell") {detections = getObjects({p-> p.class == qupath.lib.objects.PathDetectionObject.class})}



println("Count = "+ detections.size())

detections.each{
    //Get the bounding box region around the target detection
    roi = it.getROI()
    request = RegionRequest.createInstance(path, downsample, roi)
    pathImage = IJTools.convertToImagePlus(server, request)
    imp = pathImage.getImage()
    //pathImage = PathImagePlus.createPathImage(imp, request)
    //imp.show()
    //imps = ij.plugin.ChannelSplitter.split(imp)
    
    //println(imp.getClass())
    //Extract the first channel as a list of pixel values
    //firstChanImage = imps[FIRST_CHANNEL-1]
    firstChanImage = imp.getProcessor(FIRST_CHANNEL)
    firstChanImage = firstChanImage.convertToFloatProcessor()  //Needed to handle big numbers
    ch1Pixels = firstChanImage.getPixels()
    //Create a mask so that only the pixels we want from the bounding box area are used in calculations
    bpSLICs = createObjectMask(pathImage, it, objectType).getPixels()
    
    //println(bpSLICs)
    //println(bpSLICs.getPixels())
    //println("ch1 size"+ch1.size())
    size = ch1Pixels.size()
    secondChanImage= imp.getProcessor(SECOND_CHANNEL)
    secondChanImage=secondChanImage.convertToFloatProcessor()
    ch2Pixels = secondChanImage.getPixels()
    //use mask to extract only the useful pixels into new lists
    //Maybe it would be faster to remove undesirable pixels instead?
    ch1 = []
    ch2 = []
    for (i=0; i<size; i++){
        if(bpSLICs[i]){
            ch1<<ch1Pixels[i]
            ch2<<ch2Pixels[i]
        }
    }
    /*
    println(ch1)
    println(ch2)
    println("ch1 size"+ch1.size())
    println("ch2 size"+ch2.size())
    println("ch1mean "+ch1Mean)
    println("ch2sum "+ch2.sum())
    println("ch2mean "+ch2Mean)
    */
    
    //Calculating the mean for Pearson's
    double ch1Mean = ch1.sum()/ch1.size()
    double ch2Mean = ch2.sum()/ch2.size()
    //get the new number of pixels to be analyzed
    size = ch1.size()
    
    //Create the sum for the top half of the pearson's correlation coefficient
    top = []
    for (i=0; i<size;i++){top << (ch1[i]-ch1Mean)*(ch2[i]-ch2Mean)}
    pearsonTop = top.sum()
    
    //Sums for the two bottom parts
    botCh1 = []
    for (i=0; i<size;i++){botCh1<< (ch1[i]-ch1Mean)*(ch1[i]-ch1Mean)}
    rootCh1 = Math.sqrt(botCh1.sum())
    
    botCh2 = []
    for (i=0; i<size;i++){botCh2 << (ch2[i]-ch2Mean)*(ch2[i]-ch2Mean)}
    rootCh2 = Math.sqrt(botCh2.sum())



    pearsonBot = rootCh2*rootCh1

    double pearson = pearsonTop/pearsonBot
    String name = "Pearson Corr "+objectType+":"+FIRST_CHANNEL+"+"+SECOND_CHANNEL
    it.getMeasurementList().putMeasurement(name, pearson)

    //Start Manders calculations
    double m1Top = 0
    for (i=0; i<size;i++){if (ch2[i] > ch2Background){m1Top += Math.max(ch1[i]-ch1Background,0)}}
    double m1Bottom = 0
    for (i=0; i<size;i++){m1Bottom += Math.max(ch1[i]-ch1Background,0)}
    double m2Top = 0
    for (i=0; i<size;i++){if (ch1[i] > ch1Background){m2Top += Math.max(ch2[i]-ch2Background,0)}}
    double m2Bottom = 0
    for (i=0; i<size;i++){m2Bottom += Math.max(ch2[i]-ch2Background,0)}
    
    //Check for divide by zero and add measurements
    name = "M1 "+objectType+": ratio of Ch"+FIRST_CHANNEL+" intensity in Ch"+SECOND_CHANNEL+" areas"
    double M1 = m1Top/m1Bottom
    if (M1.isNaN()){M1 = 0}
    it.getMeasurementList().putMeasurement(name, M1)
    double M2 = m2Top/m2Bottom
    if (M2.isNaN()){M2 = 0}
    name = "M2 "+objectType+": ratio of Ch"+SECOND_CHANNEL+" intensity in Ch"+FIRST_CHANNEL+" areas"
    it.getMeasurementList().putMeasurement(name, M2)
}    

    
println("Done!")    

//Making a mask. Phantom of the Opera style.

def createObjectMask(PathImage pathImage, PathObject object, String objectType) {
    //create a byteprocessor that is the same size as the region we are analyzing
    def bp = new ByteProcessor(pathImage.getImage().getWidth(), pathImage.getImage().getHeight())
    //create a value to fill into the "good" area
    bp.setValue(1.0)

    if (objectType == "nucleus"){
        def roi = object.getNucleusROI()
        def roiIJ = IJTools.convertToIJRoi(roi, pathImage)
        bp.fill(roiIJ)
        
    }else if (objectType == "cytoplasm"){
        def nucleus = object.getNucleusROI()
        roiIJNuc = IJTools.convertToIJRoi(nucleus, pathImage)
        def roi = object.getROI()
        //fill in the whole cell area
        def roiIJ = IJTools.convertToIJRoi(roi, pathImage)
        bp.fill(roiIJ)
        //remove the nucleus
        bp.setValue(0)
        bp.fill(roiIJNuc)
        
    } else { 
        def roi = object.getROI()
        roiIJ = IJTools.convertToIJRoi(roi, pathImage)
        bp.fill(roiIJ)
    }

    
    //fill the ROI with the setValue to create the mask, the other values should be 0
    
    return bp
}