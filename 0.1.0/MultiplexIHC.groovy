## After cell detection, I did Analyze ? Calculate features ? Add intensity features (experimental)., 
## with a pixel size of 1 um. I chose Red, Green, and Blue, and Mean. Then the following code

import qupath.lib.objects.classes.PathClass
import qupath.lib.objects.classes.PathClassFactory

def Brown = PathClassFactory.getPathClass("Brown")
def Red = PathClassFactory.getPathClass("Red")
def Purple = PathClassFactory.getPathClass("Purple")
def Teal = PathClassFactory.getPathClass("Teal")

def rmean = "ROI: 1.00 µm per pixel: Red:  Mean"
def gmean = "ROI: 1.00 µm per pixel: Green:  Mean"
def bmean = "ROI: 1.00 µm per pixel: Blue:  Mean"

for (def cell :getCellObjects()) {
        
        double r = cell.getMeasurementList().getMeasurementValue(rmean)
        double g = cell.getMeasurementList().getMeasurementValue(gmean)
        double b = cell.getMeasurementList().getMeasurementValue(bmean)
        
        if (isBrown(r,g,b))
            cell.setPathClass(Brown)

        else if (isPurple(r,g,b)) 
            cell.setPathClass(Purple)

        else if (isTeal(r,g,b))
            cell.setPathClass(Teal)

        else if (isRed(r,g,b)) 
            cell.setPathClass(Red)

}