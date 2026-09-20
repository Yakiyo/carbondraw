package cs;

public class TestSave {
    public static void main(String[] args) {
        PlayerData.RunData run = new PlayerData.RunData(
            "Normal", 1.0, 1, 10, 1000, 1000,
            null, null, null, null, null, null, null,
            0, false, 0, 0, null, null, 0, false, 0, false
        );
        PlayerDatabase.saveRun(run);
        System.out.println("Saved.");
        
        PlayerData pd = PlayerDatabase.loadData();
        if (pd.getActiveRun() != null) {
            System.out.println("Active run loaded successfully.");
        } else {
            System.out.println("Active run is NULL!");
        }
    }
}
