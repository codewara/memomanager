package Model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;

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
                Model.File file = new Model.File(name, dir, content, modifiedTime);
                diskManager.allocateContiguous(file.getContent());
                dir.addChild(file);
            }
        }
        return dir;
    }
}
