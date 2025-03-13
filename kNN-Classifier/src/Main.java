import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/*basic understanding: get two data sets: 1-traning set, 2-test set, calculate the distance, get k (number) of lowest elements and print their label

important:
    1. calculate the distance of a vector from test data set to a vector from training data set, then find k smallest distances and print the most occuring label from training data set for given test vector
*/


public class Main {
    //gets the distance of two vectors
    public static double calculateDistance(ArrayList<Double> trainingDataVector, ArrayList<Double> testDataVector) {
        int size = trainingDataVector.size();
        double distance = 0;
        for (int i = 0; i < size; i++) {
            distance += Math.pow(trainingDataVector.get(i) - testDataVector.get(i), 2);
        }
        return Math.sqrt(distance);
    }

    //gets arraylist of Strings (the arraylist consisting of rows of a file) will make a new object for every line
    public static ArrayList<TestDataRow> convertTestDataRow(ArrayList<String> testData){
        ArrayList<TestDataRow> result = new ArrayList<>();

        for (String line : testData) {
            String[] parts = line.split(",");  // split using comma

            ArrayList<Double> vector = new ArrayList<>();
            for (int i = 0; i < parts.length - 1; i++) {
                vector.add(Double.parseDouble(parts[i])); // convert numeric values
            }

            String label = parts[parts.length - 1]; // last part is the label

            result.add(new TestDataRow(vector, label));
        }
        return result;
    }

    //gets arraylist of Strings (the arraylist consisting of rows of a file) will make a new object for every line
    public static ArrayList<TrainingDataRow> convertTrainingDataRow(ArrayList<String> testData){
        ArrayList<TrainingDataRow> result = new ArrayList<>();

        for (String line : testData) {
            String[] parts = line.split(",");  // split using comma

            ArrayList<Double> vector = new ArrayList<>();
            for (int i = 0; i < parts.length - 1; i++) {
                vector.add(Double.parseDouble(parts[i])); // convert numeric values
            }

            String label = parts[parts.length - 1]; // last part is the label

            result.add(new TrainingDataRow(vector, label));
        }
        return result;
    }

    public static String getLabelOfTrainingVector(int k, int testDataIndex, ArrayList<TestDataRow> testDataRowObjArrayList, ArrayList<TrainingDataRow> trainingDataRowObjArrayList) {

        //priority queue to store the k smallest distances with labels
        PriorityQueue<Map.Entry<Double, String>> minHeap =
                new PriorityQueue<>(Comparator.comparingDouble(Map.Entry::getKey));

        //test vector
        ArrayList<Double> testVector = testDataRowObjArrayList.get(testDataIndex).getVector();

        //compute distances and store them in the minHeap
        for (TrainingDataRow trdr : trainingDataRowObjArrayList) {
            double distance = calculateDistance(trdr.getVector(), testVector);
            minHeap.offer(new AbstractMap.SimpleEntry<>(distance, trdr.getLabel()));

            //keep only k elements in the heap
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        //count occurrences of each label
        HashMap<String, Integer> labelCount = new HashMap<>();
        while (!minHeap.isEmpty()) {
            String label = minHeap.poll().getValue();
            labelCount.put(label, labelCount.getOrDefault(label, 0) + 1);
        }

        //find the most frequent label
        return Collections.max(labelCount.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    //returns comparison between calculated label and given label in the form: "Givenlabel, should be: CalculatedLabel"
    public static ArrayList<String> compareLabels(int k, ArrayList<TestDataRow> testDataRowObjArrayList, ArrayList<TrainingDataRow> trainingDataRowObjArrayList) {
        ArrayList<String> comparisons = new ArrayList<>();

        //for each test row
        for (int i = 0; i < testDataRowObjArrayList.size(); i++) {
            //predict the label using KNN
            String predictedLabel = getLabelOfTrainingVector(k, i, testDataRowObjArrayList, trainingDataRowObjArrayList);

            //actual label from the test data
            String actualLabel = testDataRowObjArrayList.get(i).getLabel();

            //build the comparison string
            String comparison = actualLabel + ", should be: " + predictedLabel;

            //store in the results list
            comparisons.add(comparison);
        }

        return comparisons;
    }

    public static void main(String[] args) throws IOException {
        int k = 3;
        //1. read test data
        BufferedReader inTest = new BufferedReader(new FileReader("C:\\Users\\Admin\\Desktop\\iris.test.data"));
        String testLine;
        ArrayList<String> testData = new ArrayList<>();
        while ((testLine = inTest.readLine()) != null) {
            testData.add(testLine);
        }
        inTest.close();

        //convert test data lines into TestDataRow objects
        ArrayList<TestDataRow> testDataRows = convertTestDataRow(testData);

        //2. read training data
        BufferedReader inTraining = new BufferedReader(new FileReader("C:\\Users\\Admin\\Desktop\\iris.data"));
        String trainingLine;
        ArrayList<String> trainingData = new ArrayList<>();
        while ((trainingLine = inTraining.readLine()) != null) {
            trainingData.add(trainingLine);
        }
        inTraining.close();

        //convert training data lines into TrainingDataRow objects
        ArrayList<TrainingDataRow> trainingDataRows = convertTrainingDataRow(trainingData);

        //3. Compare labels
        ArrayList<String> comparisonResults = compareLabels(k, testDataRows, trainingDataRows);

        //print out each comparison
        for (String result : comparisonResults) {
            System.out.println(result);
        }
    }

}