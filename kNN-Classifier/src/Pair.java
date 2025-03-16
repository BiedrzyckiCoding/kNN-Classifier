class Pair implements Comparable<Pair> {
    private final double distance;
    private final String label;

    public Pair(double distance, String label) {
        this.distance = distance;
        this.label = label;
    }

    public double getDistance() {
        return distance;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public int compareTo(Pair other) {
        // Compare by distance
        return Double.compare(this.distance, other.distance);
    }
}
