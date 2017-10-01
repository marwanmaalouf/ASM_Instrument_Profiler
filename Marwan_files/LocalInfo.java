

public class LocalInfo {

    int pushLocation;
    int loadOrStoreLocation;
    int loadOrStorePosition;
    int oldIndex;
    int newIndex;
    String oldName;
    String newName;

    public String toString() {
        return "" + pushLocation + " " + loadOrStoreLocation + " " + loadOrStorePosition + " " + oldIndex + " " + newIndex + " " + oldName + " " + newName;
    }
}
