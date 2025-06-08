package Model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;

public class FileLoader {
    public static Directory loadFromJSON(File file) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(file);
        return (Directory) parseDir(root, null);
    }

    private static SystemNode parseDir(JsonNode node, Directory parent) {
        if (!"directory".equals(node.get("type").asText())) {
            throw new IllegalArgumentException("Expected a directory node");
        }

        Directory dir = new Directory(node.get("name").asText(), parent);
        for (JsonNode childNode : node.get("children")) {
            String type = childNode.get("type").asText();
            if ("directory".equals(type)) dir.addChild(parseDir(childNode, dir));
            else if ("file".equals(type)) {
                String name = childNode.get("name").asText();
                String content = childNode.get("content").asText("");
                dir.addChild(new Model.File(name, dir, content));
            }
        }
        return dir;
    }
}
