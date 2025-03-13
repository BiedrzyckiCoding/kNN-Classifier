import java.util.ArrayList;

public class TestDataRow {
    ArrayList<Double> vector = new ArrayList<>();
    String label;
    public TestDataRow(ArrayList<Double> vector, String label) {
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
