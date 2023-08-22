package me.z1haze.levelup.storage;


import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class DataAccessor {
    public String directory;

    public DataAccessor(String directory) {
        this.directory = directory;
    }

    public void makeDirectory(String path) {
        File file = new File(directory + File.separator + path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public List<File> getFiles(String path) {
        return FileUtils.listFiles(Paths.get(directory, path).toFile(), null, false).stream().toList();
    }

    public boolean exists(String fileName) {
        return new File(directory + File.separator + fileName).exists();
    }

    public boolean create(String fileName) {
        try {
            return new File(directory + File.separator + fileName).createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteDirectory(String path) {
        try {
            FileUtils.deleteDirectory(Paths.get(directory, path).toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFile(String path) {
        try {
            FileUtils.delete(Paths.get(directory, path).toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(String fileName, Object object) {
        create(fileName);
        Path path = Paths.get(directory, fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.append(object.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String read(String fileName) {
        try {
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(directory, fileName)), StandardCharsets.UTF_8));
            Stream<String> fileLines = fileReader.lines();
            List<String> list = fileLines.toList();
            fileLines.close();
            fileReader.close();
            StringBuilder sb = new StringBuilder();
            list.forEach(line -> sb.append(line).append("\n"));
            String str = sb.toString();
            if (str.isEmpty()) return "";
            return str.substring(0, str.length() - 1);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
