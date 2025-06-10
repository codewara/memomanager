package Model;

import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class FileSaver {

    // Saves the directory structure to a JSON file
    public static void saveToJSON (Directory root, java.io.File output) {
        JSONObject json = dirToJson(root); // Convert the directory structure to JSON (from root)
        try (FileWriter writer = new FileWriter(output)) {
            writer.write(json.toString(4)); // Pretty print with 4 spaces
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save directory structure to JSON", e);
        }
    }

    // Loads the directory structure from a JSON file
    private static JSONObject dirToJson (Directory dir) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", dir.getName());
        map.put("type", "directory");

        List<Object> children = new ArrayList<>();
        for (SystemNode child : dir.getChildren()) { // Iterate through children of the directory
            if (child instanceof Directory subDir) children.add(dirToJson(subDir)); // If child is a Directory, recursively convert it to JSON
            else if (child instanceof File file) children.add(fileToJson(file)); // If child is a File, convert it to JSON
        }
        map.put("children", children);
        map.put("modified", dir.getModifiedTime());
        return new JSONObject(map); // Create a JSONObject from the map
    }

    // Converts a File object to a JSON representation
    public static JSONObject fileToJson(File file) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", file.getName());
        map.put("type", "file");
        map.put("content", file.getContent());
        map.put("modified", file.getModifiedTime());
        map.put("startBlock", file.getStartBlock());
        map.put("endBlock", file.getEndBlock());
        return new JSONObject(map); // Create a JSONObject from the map
    }
}
