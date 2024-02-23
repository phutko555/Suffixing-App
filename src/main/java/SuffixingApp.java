import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;

public class SuffixingApp {
    private static final Logger LOGGER = Logger.getLogger(SuffixingApp.class.getName());

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java SuffixingApp <config-file>");
            System.exit(1);
        }

        String configFile = args[0];
        Properties properties = loadConfig(configFile);
        if (properties == null)
            System.exit(1);
        String mode = properties.getProperty("mode");
        String suffix = properties.getProperty("suffix");
        String files = properties.getProperty("files");

        if (mode == null || !mode.matches("(?i)(copy|move)")) {
            LOGGER.log(Level.SEVERE, "Mode is not recognized: " + mode);
            System.exit(1);
        }

        if (suffix == null) {
            LOGGER.log(Level.SEVERE, "No suffix is configured");
            System.exit(1);
        }

        if (files == null || files.isEmpty()) {
            LOGGER.log(Level.WARNING, "No files are configured to be copied/moved");
            System.exit(1);
        }

        String[] fileList = files.split(":");
        for (String filePath : fileList) {
            processFile(filePath.trim(), suffix, mode);
        }
    }

    private static Properties loadConfig(String configFile) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(configFile)) {
            properties.load(input);
            return properties;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading config file: " + configFile, e);
            return null;
        }
    }

    private static void processFile(String filePath, String suffix, String mode) {
        File file = new File(filePath);
        if (!file.exists()) {
            LOGGER.log(Level.SEVERE, "No such file: " + filePath.replace("\\", "/"));
            return;
        }

        String fileName = file.getName();
        String newName = fileName.substring(0, fileName.lastIndexOf('.')) + "-" + suffix + fileName.substring(fileName.lastIndexOf('.'));
        File newFile = new File(file.getParent(), newName);

        if (mode.equalsIgnoreCase("copy")) {
            try {
                Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                LOGGER.log(Level.INFO, file.getPath().replace("\\", "/") + " -> " + newFile.getPath().replace("\\", "/"));
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error copying file: " + file.getPath(), e);
            }
        } else if (mode.equalsIgnoreCase("move")) {
            try {
                Files.move(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                LOGGER.log(Level.INFO, file.getPath().replace("\\", "/") + " => " + newFile.getPath().replace("\\", "/"));
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error moving file: " + file.getPath(), e);
            }
        }
    }
}
