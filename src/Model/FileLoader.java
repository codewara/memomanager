package Model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.util.*;

public class FileLoader {
    // Load a directory structure from a JSON file
    public static Directory loadFromJSON(File file, DiskManager diskManager) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(file); // Parse the JSON into a JsonNode
        return (Directory) parseDir(root, null, diskManager); // Start parsing from the root node
    }

    // Parse a JSON node into a Directory or File object
    private static SystemNode parseDir(JsonNode node, Directory parent, DiskManager diskManager) {
        // Check if the node is a directory (type should be "directory")
        if (!"directory".equals(node.get("type").asText())) throw new IllegalArgumentException("Expected a directory node");

        // Create a Directory object with the name and modified time (if parent is null, this is the root directory)
        Directory dir = new Directory(node.get("name").asText(), parent, node.get("modified").asText(""));

        // Get the children array from the JSON node
        for (JsonNode childNode : node.get("children")) {
            String type = childNode.get("type").asText();
            if ("directory".equals(type)) dir.addChild(parseDir(childNode, dir, diskManager)); // Recursively parse subdirectories
            else if ("file".equals(type)) {
                // Parse file attributes
                String name = childNode.get("name").asText();
                String content = childNode.get("content").asText("");
                String modifiedTime = childNode.get("modified").asText("");
                int startBlock = childNode.has("startBlock") ? childNode.get("startBlock").asInt() : -1;
                int endBlock = childNode.has("endBlock") ? childNode.get("endBlock").asInt() : -1;
                List<Integer> usedBlocks;

                // If startBlock and endBlock are specified, allocate those blocks
                if (startBlock != -1 && endBlock != -1) {
                    usedBlocks = new ArrayList<>();
                    for (int i = startBlock; i <= endBlock; i++) {
                        int contentStart = (i - startBlock) * 10;
                        int contentEnd = Math.min(content.length(), contentStart + 10);
                        String chunk = content.substring(contentStart, contentEnd);

                        diskManager.getBlock(i).setUsed(true);
                        diskManager.getBlock(i).setData(chunk);
                        usedBlocks.add(i);
                    }
                } else usedBlocks = diskManager.allocateContiguous(content); // If not specified, allocate blocks for the content

                // Create a File object and add it to the directory
                Model.File file = new Model.File(name, dir, content, modifiedTime, usedBlocks);
                dir.addChild(file);
            }
        } return dir; // Return the populated directory object
    }
}
