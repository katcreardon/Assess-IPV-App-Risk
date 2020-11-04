package androidPermissions;

public class Permissions {
    int count;
    String type;

    public Permissions(int c, String t) {
        this.count = c;
        this.type = t;
    }

    public void increaseCount() {
        count++;
    }

    public int getCount() {
        return count;
    }

    public String getType() {
        return type;
    }
}
