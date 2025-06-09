package Model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

public class FileSaver {
    public static void saveToJSON (Directory root, java.io.File output) {
        JSONObject json = dirToJson(root);
        try (FileWriter writer = new FileWriter(output)) {
            writer.write(json.toString(4)); // Pretty print with 4 spaces
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save directory structure to JSON", e);
        }
    }

    private static JSONObject dirToJson (Directory dir) {
        JSONObject json = new JSONObject();
        json.put("name", dir.getName());
        json.put("type", "directory");
        json.put("modified", dir.getModifiedTime());

        JSONArray children = new JSONArray();
        for (SystemNode child : dir.getChildren()) {
            if (child instanceof Directory subDir) children.put(dirToJson(subDir));
            else if (child instanceof File file) {
                JSONObject f = new JSONObject();
                f.put("name", file.getName());
                f.put("type", "file");
                f.put("content", file.getContent());
                f.put("modified", file.getModifiedTime());
                children.put(f);
            }
        }
        json.put("children", children);
        return json;
    }
}
