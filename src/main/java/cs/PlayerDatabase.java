package cs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PlayerDatabase {
    private static final String FILE_PATH = "player_data.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static PlayerData loadData() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                PlayerData data = gson.fromJson(reader, PlayerData.class);
                if (data == null) {
                    return new PlayerData();
                }
                return data;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new PlayerData();
    }

    public static void saveData(PlayerData data) {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addCurrency(int amount) {
        PlayerData data = loadData();
        data.setCurrency(data.getCurrency() + amount);
        saveData(data);
    }

    public static int getCurrency() {
        return loadData().getCurrency();
    }
}
