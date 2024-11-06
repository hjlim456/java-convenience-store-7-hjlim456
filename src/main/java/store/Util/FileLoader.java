package store.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileLoader {
    public static List<String> loadByFilePath(String address) throws IOException {
        Path path = Paths.get(address);
        List<String> lines = Files.readAllLines(path);

        return lines;
    }
}
