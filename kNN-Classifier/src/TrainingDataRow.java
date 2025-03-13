import java.util.ArrayList;

public class TrainingDataRow {
    ArrayList<Double> vector = new ArrayList<>();
    String label;
    public TrainingDataRow(ArrayList<Double> vector, String label) {
        this.vector = vector;
        this.label = label;
    }
    public ArrayList<Double> getVector() {
        return vector;
    }

    public String getLabel() {
        return label;
    }

}
