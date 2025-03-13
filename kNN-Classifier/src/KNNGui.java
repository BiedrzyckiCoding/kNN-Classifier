import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class KNNGui extends JFrame {

    // -- GUI Components --
    private JTextField trainingFileField;
    private JTextField testFileField;
    private JTextField kValueField;       // New text field for k
    private JButton loadFilesButton;
    private JButton calculateButton;

    private JTextArea trainingDataArea;
    private JTextArea testDataArea;
    private JTextArea comparisonsArea;

    public KNNGui() {
        super("kNN-Classifier");
        setLayout(new GridLayout(1, 4));

        //column 1: File input, k, and Buttons
        JPanel column1 = new JPanel();
        column1.setLayout(new BoxLayout(column1, BoxLayout.Y_AXIS));

        //training file path
        column1.add(new JLabel("Training File:"));
        trainingFileField = new JTextField("C:\\Users\\Admin\\Desktop\\iris.data", 20);
        column1.add(trainingFileField);

        //test file path
        column1.add(new JLabel("Test File:"));
        testFileField = new JTextField("C:\\Users\\Admin\\Desktop\\iris.test.data", 20);
        column1.add(testFileField);

        //k-value input
        column1.add(new JLabel("k:"));
        kValueField = new JTextField("3", 5);
        column1.add(kValueField);

        //load Files button
        loadFilesButton = new JButton("Load Files");
        column1.add(loadFilesButton);

        //calculate Labels button
        calculateButton = new JButton("Calculate Labels");
        column1.add(calculateButton);

        add(column1);

        //column 2: Training data display
        trainingDataArea = new JTextArea();
        trainingDataArea.setEditable(false);
        JScrollPane scrollPaneTraining = new JScrollPane(trainingDataArea);
        add(scrollPaneTraining);

        //column 3: Test data display
        testDataArea = new JTextArea();
        testDataArea.setEditable(false);
        JScrollPane scrollPaneTest = new JScrollPane(testDataArea);
        add(scrollPaneTest);

        //column 4: Comparisons display
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

        //window setup
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Load the training and test data files.
     * Store them in Main.trainingDataRows and Main.testDataRows.
     */
    private void loadFiles() {
        String trainingPath = trainingFileField.getText().trim();
        String testPath = testFileField.getText().trim();

        //use Main's static methods to read/convert data
        ArrayList<String> trainingLines = Main.readFile(trainingPath);
        Main.trainingDataRows = Main.convertTrainingDataRow(trainingLines);

        ArrayList<String> testLines = Main.readFile(testPath);
        Main.testDataRows = Main.convertTestDataRow(testLines);

        //display training data
        trainingDataArea.setText("TRAINING DATA LOADED:\n");
        for (var tr : Main.trainingDataRows) {
            trainingDataArea.append(tr.getVector() + " -> " + tr.getLabel() + "\n");
        }

        //display test data
        testDataArea.setText("TEST DATA LOADED:\n");
        for (var tdr : Main.testDataRows) {
            testDataArea.append(tdr.getVector() + " -> " + tdr.getLabel() + "\n");
        }

        //clear previous comparisons
        comparisonsArea.setText("");
    }

    /**
     * Parse k, run KNN logic, and display comparison results.
     */
    private void calculateLabels() {
        //make sure data is loaded
        if (Main.trainingDataRows == null || Main.testDataRows == null) {
            JOptionPane.showMessageDialog(this, "Please load the files first!");
            return;
        }

        //get user-specified k
        int k;
        try {
            k = Integer.parseInt(kValueField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid k value. Please enter an integer.");
            return;
        }

        //compare labels using Main's static method
        ArrayList<String> comparisons = Main.compareLabels(k, Main.testDataRows, Main.trainingDataRows);

        //display results
        comparisonsArea.setText("COMPARISONS:\n");
        for (String line : comparisons) {
            comparisonsArea.append(line + "\n");
        }
    }
}
