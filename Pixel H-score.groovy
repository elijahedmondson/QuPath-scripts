import ij.IJ
import qupath.lib.images.servers.ImageServer
import qupath.lib.images.servers.TransformedServerBuilder
import qupath.lib.regions.RegionRequest
import qupath.lib.scripting.QP
import java.awt.image.BufferedImage
import qupath.imagej.tools.IJTools
import ij.ImageJ
import ij.ImagePlus
import ij.plugin.ImageCalculator

double[] DABthresholds=[0.15,0.5,1] //1+, 2+, 3+ DAB
double Hthreshold=0.08 //Nuclei in Hematoxylin
def downsample = 1

ImageServer<BufferedImage> myserver = QP.getCurrentServer() as ImageServer<BufferedImage>
def originalPixelSize = myserver.getPixelCalibration().getAveragedPixelSizeMicrons()
def imageData = QP.getCurrentImageData()
def tsb = new TransformedServerBuilder(myserver).deconvolveStains(imageData.getColorDeconvolutionStains()).build()
def annotations = QP.getSelectedObjects()

/*
def ij = new ImageJ()
ij.setVisible(true)
 */
IJ.setForegroundColor(255,255,255)
IJ.setBackgroundColor(0,0,0)
annotations.forEach({annot ->
    def roi = annot.getROI()
    def region = RegionRequest.createInstance(myserver.getPath(), downsample, roi)
    def pthimgplus = IJTools.convertToImagePlus(tsb, region)
    def imgplus = pthimgplus.getImage()
    def ijroi = IJTools.convertToIJRoi(roi, pthimgplus)
    imgplus.setRoi(ijroi, true)
    //imgplus.show()

    def imstack = imgplus.getStack()
    def heam_proc = imstack.getProcessor(1)
    heam_proc.fillOutside(ijroi)
    heam_proc.setThreshold(Hthreshold, 1e3)
    def mask_h = heam_proc.createMask()
    def dab_proc = imstack.getProcessor(2)
    dab_proc.fillOutside(ijroi)
    dab_proc.setThreshold(DABthresholds[0], 1e3)
    def mask_d0 = dab_proc.createMask()
    dab_proc.setThreshold(DABthresholds[1], 1e3)
    def mask_d1 = dab_proc.createMask()
    dab_proc.setThreshold(DABthresholds[2], 1e3)
    def mask_d2 = dab_proc.createMask()

    // create combined mask
    def himageplus = new ImagePlus("hmask", mask_h)
    def dimageplus = new ImagePlus("dmask", mask_d0)
    def ormask = ImageCalculator.run(himageplus, dimageplus, "or")
    def totpix_stat =  ormask.getStatistics()
    def dab0_stat = mask_d0.getStatistics()
    def dab1_stat = mask_d1.getStatistics()
    def dab2_stat = mask_d2.getStatistics()

    def pix_tot = totpix_stat.mean * totpix_stat.area / 255
    def pix_d0 = dab0_stat.mean * dab0_stat.area / 255
    def pix_d1 = dab1_stat.mean * dab1_stat.area / 255
    def pix_d2 = dab2_stat.mean * dab2_stat.area / 255

    print("tot " + pix_tot)
    print("d0 " + pix_d0)
    print("d1 " + pix_d1)
    print("d2 " + pix_d2)
    def pixhscore = 100 * (pix_d0+pix_d1+pix_d2 )/pix_tot
    annot.measurements['Pixelwise H-score IJ']=pixhscore
    annot.measurements["1: 1+ area µm^2 IJ"] = pix_d0 * originalPixelSize * originalPixelSize
    annot.measurements["2: 2+ area µm^2 IJ"] = pix_d1 * originalPixelSize * originalPixelSize
    annot.measurements["3: 3+ area µm^2 IJ"] = pix_d2 * originalPixelSize * originalPixelSize
    annot.measurements["HDAB: HDAB area µm^2 IJ"] = pix_tot * originalPixelSize * originalPixelSize
})