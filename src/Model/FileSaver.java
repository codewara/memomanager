package Model;

import org.json.JSONObject;

import java.io.*;
import java.util.*;

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
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", dir.getName());
        map.put("type", "directory");

        List<Object> children = new ArrayList<>();
        for (SystemNode child : dir.getChildren()) {
            if (child instanceof Directory subDir) children.add(dirToJson(subDir));
            else if (child instanceof File file) children.add(fileToJson(file));
        }
        map.put("children", children);
        map.put("modified", dir.getModifiedTime());
        return new JSONObject(map);
    }

    public static JSONObject fileToJson(File file) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", file.getName());
        map.put("type", "file");
        map.put("content", file.getContent());
        map.put("modified", file.getModifiedTime());
        map.put("startBlock", file.getStartBlock());
        map.put("endBlock", file.getEndBlock());
        return new JSONObject(map);
    }
}
