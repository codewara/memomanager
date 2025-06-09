package Model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.util.*;

public class FileLoader {
    public static Directory loadFromJSON(File file, DiskManager diskManager) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(file);
        return (Directory) parseDir(root, null, diskManager);
    }

    private static SystemNode parseDir(JsonNode node, Directory parent, DiskManager diskManager) {
        if (!"directory".equals(node.get("type").asText())) throw new IllegalArgumentException("Expected a directory node");

        Directory dir = new Directory(node.get("name").asText(), parent, node.get("modified").asText(""));
        for (JsonNode childNode : node.get("children")) {
            String type = childNode.get("type").asText();
            if ("directory".equals(type)) dir.addChild(parseDir(childNode, dir, diskManager));
            else if ("file".equals(type)) {
                String name = childNode.get("name").asText();
                String content = childNode.get("content").asText("");
                String modifiedTime = childNode.get("modified").asText("");
                int startBlock = childNode.has("startBlock") ? childNode.get("startBlock").asInt() : -1;
                int endBlock = childNode.has("endBlock") ? childNode.get("endBlock").asInt() : -1;
                List<Integer> usedBlocks;
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
                } else usedBlocks = diskManager.allocateContiguous(content);
                Model.File file = new Model.File(name, dir, content, modifiedTime, usedBlocks);
                dir.addChild(file);
            }
        }
        return dir;
    }
}
