package apprisk;

public class Permissions {
    String type;
    double weight;

    public Permissions(String t, double w) {
        this.weight = w;
        this.type = t;
    }

    public double getWeight() {
        return weight;
    }

    public String getType() {
        return type;
    }
}

