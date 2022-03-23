//https://groups.google.com/forum/#!topic/qupath-users/GEI99RECuAc
/**
 * Script to combine results tables exported by QuPath.
 *
 * This is particularly intended to deal with the fact that results tables of annotations can produce results
 * with different column names, numbers and orders - making them awkward to combine later manually.
 *
 * It prompts for a directory containing exported text files, and then writes a new file in the same directory.
 * The name of the new file can be modified - see the first lines below.
 *
 * Note: This hasn't been tested very extensively - please check the results carefully, and report any problems so they
 * can be fixed!
 *
 * @author Pete Bankhead
 */

import qupath.lib.gui.QuPathGUI

// Some parameters you might want to change...
String ext = '.txt' // File extension to search for
String delimiter = '\t' // Use tab-delimiter (this is for the *input*, not the output)
String outputName = 'Combined_results.txt' // Name to use for output; use .csv if you really want comma separators

// Prompt for directory containing the results
def dirResults = QuPathGUI.getSharedDialogHelper().promptForDirectory()
if (dirResults == null)
    return
def fileResults = new File(dirResults, outputName)

// Get a list of all the files to merge
def files = dirResults.listFiles({
    File f -> f.isFile() &&
            f.getName().toLowerCase().endsWith(ext) &&
            f.getName() != outputName} as FileFilter)
if (files.size() <= 1) {
    print 'At least two results files needed to merge!'
    return
} else
    print 'Will try to merge ' + files.size() + ' files'

// Represent final results as a 'list of maps'
def results = new ArrayList<Map<String, String>>()

// Store all column names that we see - not all files necessarily have all columns
def allColumns = new LinkedHashSet<String>()
allColumns.add('File name')

// Loop through the files
for (file in files) {
    // Check if we have anything to read
    def lines = file.readLines()
    if (lines.size() <= 1) {
        print 'No results found in ' + file
        continue
    }
    // Get the header columns
    def iter = lines.iterator()
    def columns = iter.next().split(delimiter)
    allColumns.addAll(columns)
    // Create the entries
    while (iter.hasNext()) {
        def line = iter.next()
        if (line.isEmpty())
            continue
        def map = ['File name': file.getName()]
        def values = line.split(delimiter)
        // Check if we have the expected number of columns
        if (values.size() != columns.size()) {
            print String.format('Number of entries (%d) does not match the number of columns (%d)!', columns.size(), values.size())
            print('I will stop processing ' + file.getName())
            break
        }
        // Store the results
        for (int i = 0; i < columns.size(); i++)
            map[columns[i]] = values[i]
        results.add(map)
    }
}

// Create a new results file - using a comma delimiter if the extension is csv
if (outputName.toLowerCase().endsWith('.csv'))
    delimiter = ','
int count = 0
fileResults.withPrintWriter {
    def header = String.join(delimiter, allColumns)
    it.println(header)
    // Add each of the results, with blank columns for missing values
    for (result in results) {
        for (column in allColumns) {
            it.print(result.getOrDefault(column, ''))
            it.print(delimiter)
        }
        it.println()
        count++
    }
}

// Success!  Hopefully...
print 'Done! ' + count + ' result(s) written to ' + fileResults.getAbsolutePath()
