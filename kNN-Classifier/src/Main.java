import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/*basic understanding: get two data sets: 1-traning set, 2-test set, calculate the distance, get k (number) of lowest elements and print their label

important:
    1. calculate the distance of a vector from test data set to a vector from training data set, then find k smallest distances and print the most occuring label from training data set for given test vector
*/


public class Main {
    //fields for the gui class
    public static ArrayList<TrainingDataRow> trainingDataRows;
    public static ArrayList<TestDataRow> testDataRows;

    //function to read the files
    public static ArrayList<String> readFile(String path) {
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return lines;
    }
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
    public static ArrayList<TrainingDataRow> convertTrainingDataRow(ArrayList<String> data) {
        ArrayList<TrainingDataRow> result = new ArrayList<>();
        for (String line : data) {
            String[] parts = line.split(",");
            ArrayList<Double> vector = new ArrayList<>();
            for (int i = 0; i < parts.length - 1; i++) {
                vector.add(Double.parseDouble(parts[i]));
            }
            String label = parts[parts.length - 1];
            result.add(new TrainingDataRow(vector, label));
        }
        return result;
    }

    public static ArrayList<TestDataRow> convertTestDataRow(ArrayList<String> data) {
        ArrayList<TestDataRow> result = new ArrayList<>();
        for (String line : data) {
            String[] parts = line.split(",");
            ArrayList<Double> vector = new ArrayList<>();
            for (int i = 0; i < parts.length - 1; i++) {
                vector.add(Double.parseDouble(parts[i]));
            }
            String label = parts[parts.length - 1];
            result.add(new TestDataRow(vector, label));
        }
        return result;
    }

    public static String getLabelOfTrainingVector(int k, int testDataIndex, ArrayList<TestDataRow> testDataRowObjArrayList, ArrayList<TrainingDataRow> trainingDataRowObjArrayList) {

        //priorityQueue for k smallest distances
        PriorityQueue<Map.Entry<Double, String>> minHeap =
                new PriorityQueue<>(Comparator.comparingDouble(Map.Entry::getKey));

        //test vector
        ArrayList<Double> testVector = testDataRowObjArrayList.get(testDataIndex).getVector();

        //compute distances to each training row
        for (TrainingDataRow trdr : trainingDataRowObjArrayList) {
            double distance = calculateDistance(trdr.getVector(), testVector);
            minHeap.offer(new AbstractMap.SimpleEntry<>(distance, trdr.getLabel()));

            if (minHeap.size() > k) {
                minHeap.poll(); // remove largest
            }
        }

        //count label frequencies among k nearest neighbors
        HashMap<String, Integer> labelCount = new HashMap<>();
        while (!minHeap.isEmpty()) {
            String label = minHeap.poll().getValue();
            labelCount.put(label, labelCount.getOrDefault(label, 0) + 1);
        }

        //return most frequent label
        return Collections.max(labelCount.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    //returns comparison between calculated label and given label in the form: "Givenlabel, should be: CalculatedLabel"
    public static ArrayList<String> compareLabels(int k, ArrayList<TestDataRow> testDataRowObjArrayList, ArrayList<TrainingDataRow> trainingDataRowObjArrayList) {
        ArrayList<String> comparisons = new ArrayList<>();

        for (int i = 0; i < testDataRowObjArrayList.size(); i++) {
            String actual = testDataRowObjArrayList.get(i).getLabel();
            String predicted = getLabelOfTrainingVector(k, i, testDataRowObjArrayList, trainingDataRowObjArrayList);
            comparisons.add(actual + ", should be: " + predicted);
        }

        return comparisons;
    }

    public static void main(String[] args) {
        // Launch the GUI
        javax.swing.SwingUtilities.invokeLater(() -> new KNNGui());
    }

}