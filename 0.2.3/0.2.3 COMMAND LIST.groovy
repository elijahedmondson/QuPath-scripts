def imageData = getCurrentImageData()
def labelServer = new LabeledImageServer.Builder(imageData)
println describe(labelServer)

def imageData = getCurrentImageData()
def labelServer = new LabeledImageServer.Builder(imageData)
println describe(RegionRequest.createInstance)
