/**
 * A basic GUI to help monitor memory usage in QuPath.
 *
 * This helps both to find & address out-of-memory troubles by
 *   1. Showing how much memory is in use over time
 *   2. Giving a button to clear the tile cache - which can be
 *      using up precious memory
 *   3. Giving quick access to control the number of threads used
 *      for parallel processing
 *
 * You can run this command in the background while going about your
 * normal analysis, and check in to see how it is doing.
 *
 * If you find QuPath crashing/freezing, look to see if the memory
 * use is especially high.
 *
 * If it crashes when running memory-hungry commands like cell detection
 * across a large image or TMA, try reducing the number of parallel threads.
 *
 * @author Pete Bankhead
 */

import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleLongProperty
import javafx.beans.value.ChangeListener
import javafx.geometry.Insets
import javafx.geometry.Side
import javafx.scene.Scene
import javafx.scene.chart.AreaChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import qupath.lib.gui.QuPathGUI
import qupath.lib.gui.prefs.PathPrefs

// Create a timer to poll for memory status once per second
def timer = new Timer("QuPath memory monitor", true)
long sampleFrequency = 1000L

// Observable properties to store memory values
def maxMemory = new SimpleLongProperty()
def totalMemory = new SimpleLongProperty()
def usedMemory = new SimpleLongProperty()

// Let's sometimes scale to MB, sometimes to GB
double scaleMB = 1.0/1024.0/1024.0
double scaleGB = scaleMB/1024.0

// Create a chart to show how memory use evolves over time
def xAxis = new NumberAxis()
xAxis.setLabel("Time (samples)")
def yAxis = new NumberAxis()
yAxis.setLabel("Memory (GB)")
def chart = new AreaChart(xAxis, yAxis)
def seriesTotal = new XYChart.Series()
def seriesUsed = new XYChart.Series()
yAxis.setAutoRanging(false)
yAxis.setLowerBound(0.0)
yAxis.setTickUnit(1.0)
yAxis.setUpperBound(Math.ceil(Runtime.getRuntime().maxMemory() * scaleGB))
xAxis.setAutoRanging(true)
// Bind the series names to the latest values, in MB
seriesTotal.nameProperty().bind(Bindings.createStringBinding(
        {-> String.format("Total memory (%.1f MB)", totalMemory.get() * scaleMB)}, totalMemory))
seriesUsed.nameProperty().bind(Bindings.createStringBinding(
        {-> String.format("Used memory (%.1f MB)", usedMemory.get() * scaleMB)}, usedMemory))
chart.getData().addAll(seriesTotal, seriesUsed)
chart.setLegendVisible(true)
chart.setLegendSide(Side.TOP)
chart.setAnimated(false)
chart.setCreateSymbols(false)

// Add it button to make it possible to clear the tile cache
// This is a bit of a hack, since there is no clean way to do it yet
def btnClearCache = new Button("Clear tile cache")
btnClearCache.setOnAction {e ->
    try {
        print "Clearing cache..."
        QuPathGUI.getInstance().getViewer().getImageRegionStore().cache.clear()
        System.gc()
    } catch (Exception e2) {
        e2.printStackTrace()
    }
}
btnClearCache.setMaxWidth(Double.MAX_VALUE)

// Add a button to run the garbage collector
def btnGarbageCollector = new Button("Reclaim memory")
btnGarbageCollector.setOnAction {e ->
    System.gc()
}
btnGarbageCollector.setMaxWidth(Double.MAX_VALUE)

// Add a text field to adjust the number of parallel threads
// This is handy to scale back memory use when running things like cell detection
def runtime = Runtime.getRuntime()
def labThreads = new Label("Parallel threads")
def tfThreads = new TextField(Integer.toString(PathPrefs.getNumCommandThreads()))
PathPrefs.numCommandThreadsProperty().addListener({ v, o, n ->
    def text = Integer.toString(n)
    if (!text.trim().equals(tfThreads.getText().trim()))
        tfThreads.setText(text)
} as ChangeListener)
tfThreads.setPrefColumnCount(4)
tfThreads.textProperty().addListener({ v, o, n ->
    try {
        PathPrefs.setNumCommandThreads(Integer.parseInt(n.trim()))
    } catch (Exception e) {}
} as ChangeListener)
labThreads.setLabelFor(tfThreads)

// Create a pane to show it all
def paneBottom = new GridPane()
int col = 0
int row = 0
paneBottom.add(new Label("Num processors: " + runtime.availableProcessors()), col, row++, 1, 1)
paneBottom.add(labThreads, col, row, 1, 1)
paneBottom.add(tfThreads, col+1, row++, 1, 1)
paneBottom.add(btnClearCache, col, row++, 2, 1)
paneBottom.add(btnGarbageCollector, col, row++, 2, 1)
paneBottom.setPadding(new Insets(10))
paneBottom.setVgap(5)
def pane = new BorderPane(chart)
pane.setRight(paneBottom)

// Add a data point for the current memory usage
def snapshot = { ->
    def time = seriesUsed.getData().size() + 1
    seriesUsed.getData().add(new XYChart.Data<Number, Number>(time, usedMemory.get()*scaleGB))
    seriesTotal.getData().add(new XYChart.Data<Number, Number>(time, totalMemory.get()*scaleGB))
}

// Switch to the application thread...
Platform.runLater {
    // Create a timer that will snapshot the current memory usage & update the chart
    timer.schedule({ ->
        Platform.runLater {
            totalMemory.set(runtime.totalMemory())
            maxMemory.set(runtime.maxMemory())
            usedMemory.set(runtime.totalMemory() - runtime.freeMemory())
            snapshot()
        }
    }, 0L, sampleFrequency)

    // Show the GUI
    def stage = new Stage()
    stage.initOwner(QuPathGUI.getInstance().getStage())
    stage.setScene(new Scene(pane))
    stage.setTitle("Memory monitor")
    stage.show()
    stage.setOnHiding {timer.cancel()}
}