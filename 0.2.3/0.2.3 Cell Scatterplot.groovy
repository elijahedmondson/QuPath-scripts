/**
 * Example showing how to create an interactive scatterplot for the cells in the current viewer
 * in QuPath v0.2.0.
 * 
 * 'Interactive' here means that double-clicking a point on the scatterplot selects the specific cell.
 * 
 * Warning! This is an early, work-in-progress feature subject to change in future releases.
 * It is *not* suitable for very large numbers of cells, because each point on the scatterplot is 
 * rather 'heavy'. It should work fine with thousands of cells, but not with hundreds of thousands.
 *
 * @author Pete Bankhead
 */

def viewer = getCurrentViewer()
def cells = getCellObjects()
def builder = Charts.scatterChart()
    .viewer(viewer)
    .title('My scatterplot')
    .measurements(cells, 'Nucleus: Area', 'Cell: DAB OD max')
    .markerOpacity(0.5)
    .show()

// Uncomment the following line to get more info about available options
// println describe(Charts.scatterChart())