def measurementName = 'PHOX2B: Nucleus: Mean'
double k = 5
selectTMACores();
def cells = getCellObjects()

def allMeasurements = cells.stream()
        .mapToDouble({p -> p.getMeasurementList().getMeasurementValue(measurementName)})
        .filter({d -> !Double.isNaN(d)})
        .toArray()
double median = getMedian(allMeasurements)

// Subtract median & get absolute value
def absMedianSubtracted = Arrays.stream(allMeasurements).map({d -> Math.abs(d - median)}).toArray()

// Compute median absolute deviation & convert to standard deviation approximation
double medianAbsoluteDeviation = getMedian(absMedianSubtracted)
double sigma = medianAbsoluteDeviation / 0.6745

// Return threshold
double threshold = median + k * sigma

/**
 * Get median value from array (this will sort the array!)
 */
double getMedian(double[] vals) {
    if (vals.length == 0)
        return Double.NaN
    Arrays.sort(vals)
    if (vals.length % 2 == 1)
        return vals[(int)(vals.length / 2)]
    else
        return (vals[(int)(vals.length / 2)-1] + vals[(int)(vals.length / 2)]) / 2.0
}