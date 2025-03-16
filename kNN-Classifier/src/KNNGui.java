import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class KNNGui extends JFrame {

    //existing fields
    private JTextField trainingFileField;
    private JTextField testFileField;
    private JTextField kValueField;
    private JButton loadFilesButton;
    private JButton calculateButton;

    private JTextArea trainingDataArea;
    private JTextArea testDataArea;
    private JTextArea comparisonsArea;

    //for classifying a single custom vector
    private JTextField customVectorField; //user input
    private JButton classifyVectorButton; //button to classify
    private JLabel classificationResultLabel; //display classification

    public KNNGui() {
        super("kNN-Classifier GUI ");
        setLayout(new GridLayout(1, 4));

        //column 1: File input, k, single vector, and Buttons
        JPanel column1 = new JPanel();
        column1.setLayout(new BoxLayout(column1, BoxLayout.Y_AXIS));

        //1. training file
        column1.add(new JLabel("Training File:"));
        trainingFileField = new JTextField("./kNN-Classifier/src/iris.data", 20);
        column1.add(trainingFileField);

        //2. test file
        column1.add(new JLabel("Test File:"));
        testFileField = new JTextField("./kNN-Classifier/src/iris.test.data", 20);
        column1.add(testFileField);

        //3. k-value input
        column1.add(new JLabel("k:"));
        kValueField = new JTextField("3", 5);
        column1.add(kValueField);

        //4. single vector input
        column1.add(new JLabel("Custom Vector (comma-separated):"));
        customVectorField = new JTextField("5.1,3.5,1.4,0.2");
        column1.add(customVectorField);

        //5. buttons
        loadFilesButton = new JButton("Load Files");
        classifyVectorButton = new JButton("Classify Vector");
        calculateButton = new JButton("Calculate Labels");

        //add them in a row or stacked
        column1.add(loadFilesButton);
        column1.add(calculateButton);
        column1.add(classifyVectorButton);

        //6. classification result
        classificationResultLabel = new JLabel("Classification: [None]");
        column1.add(classificationResultLabel);

        add(column1);

        //column 2: training data display
        trainingDataArea = new JTextArea();
        trainingDataArea.setEditable(false);
        JScrollPane scrollPaneTraining = new JScrollPane(trainingDataArea);
        add(scrollPaneTraining);

        //column 3: test data display
        testDataArea = new JTextArea();
        testDataArea.setEditable(false);
        JScrollPane scrollPaneTest = new JScrollPane(testDataArea);
        add(scrollPaneTest);

        //column 4: comparisons display
        comparisonsArea = new JTextArea();
        comparisonsArea.setEditable(false);
        JScrollPane scrollPaneComparisons = new JScrollPane(comparisonsArea);
        add(scrollPaneComparisons);

        //action Listeners
        loadFilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadFiles();
            }
        });

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateLabels();
            }
        });

        //classify the single custom vector
        classifyVectorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                classifySingleVector();
            }
        });

        //window setup
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Load the training and test data files, storing them in Main.
     */
    private void loadFiles() {
        String trainingPath = trainingFileField.getText().trim();
        String testPath = testFileField.getText().trim();

        //read & convert training data
        ArrayList<String> trainingLines = Main.readFile(trainingPath);
        Main.trainingDataRows = Main.convertTrainingDataRow(trainingLines);

        //read & convert test data
        ArrayList<String> testLines = Main.readFile(testPath);
        Main.testDataRows = Main.convertTestDataRow(testLines);

        //risplay training data
        trainingDataArea.setText("TRAINING DATA LOADED:\n");
        for (var tr : Main.trainingDataRows) {
            trainingDataArea.append(tr.getVector() + " -> " + tr.getLabel() + "\n");
        }

        //display test data
        testDataArea.setText("TEST DATA LOADED:\n");
        for (var tdr : Main.testDataRows) {
            testDataArea.append(tdr.getVector() + " -> " + tdr.getLabel() + "\n");
        }

        //clear comparisons
        comparisonsArea.setText("");
        classificationResultLabel.setText("Classification: [None]");
    }

    /**
     * Calculate labels for the entire test set (already loaded).
     */
    private void calculateLabels() {
        // Ensure data is loaded
        if (Main.trainingDataRows == null || Main.testDataRows == null) {
            JOptionPane.showMessageDialog(this, "Please load the files first!");
            return;
        }

        // Parse k
        int k;
        try {
            k = Integer.parseInt(kValueField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid k value. Please enter an integer.");
            return;
        }

        // Compare predicted labels vs. actual labels
        ArrayList<String> results = Main.compareLabels(k, Main.testDataRows, Main.trainingDataRows);

        // Show comparison details
        comparisonsArea.setText("COMPARISONS:\n");
        for (String line : results) {
            comparisonsArea.append(line + "\n");
        }

        // *** Accuracy Calculation Snippet Below ***
        int correct = 0;
        for (int i = 0; i < Main.testDataRows.size(); i++) {
            String actual = Main.testDataRows.get(i).getLabel();
            String predicted = Main.getLabelOfTrainingVector(k, i, Main.testDataRows, Main.trainingDataRows);

            if (actual.equals(predicted)) {
                correct++;
            }
        }

        double accuracy = (double) correct / Main.testDataRows.size() * 100.0;
        System.out.println("Accuracy: " + accuracy + "%"); // Prints to console
        comparisonsArea.append("\nAccuracy: " + accuracy + "%\n"); // Optionally show in GUI
    }


    /**
     * Classify a single custom vector that the user enters.
     */
    private void classifySingleVector() {
        //ensure training data is loaded
        if (Main.trainingDataRows == null || Main.trainingDataRows.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please load the training file first!");
            return;
        }

        //parse k
        int k;
        try {
            k = Integer.parseInt(kValueField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid k value. Please enter an integer.");
            return;
        }

        //parse custom vector from comma-separated input
        String vectorInput = customVectorField.getText().trim();
        if (vectorInput.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a vector (comma-separated).");
            return;
        }

        ArrayList<Double> customVector = new ArrayList<>();
        try {
            String[] parts = vectorInput.split(",");
            for (String p : parts) {
                customVector.add(Double.parseDouble(p.trim()));
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid vector format. Please ensure numeric values.");
            return;
        }

        //classify the custom vector
        String predictedLabel = Main.classifyCustomVector(customVector, k, Main.trainingDataRows);

        //display the classification result
        classificationResultLabel.setText("Classification: " + predictedLabel);
    }
}
