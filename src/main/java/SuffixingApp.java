
import java.io.IOException;
import java.nio.file.*;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SuffixingApp {

    private static final Logger logger = Logger.getLogger(SuffixingApp.class.getName());

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java -jar suffixing.jar <config-file>");
            System.exit(1);
        }

        String configFile = args[0];

        try {
            Properties config = loadConfig(configFile);

            String mode = config.getProperty("mode", "").toLowerCase();
            String suffix = config.getProperty("suffix", "");
            String filesList = config.getProperty("files", "");

            if (mode.isEmpty() || (!mode.equals("copy") && !mode.equals("move"))) {
                logger.log(Level.SEVERE, "Mode is not recognized: {0}", mode);
                System.exit(1);
            }

            if (suffix.isEmpty()) {
                logger.log(Level.SEVERE, "No suffix is configured");
                System.exit(1);
            }

            if (filesList.isEmpty()) {
                logger.log(Level.WARNING, "No files are configured to be copied/moved");
                System.exit(1);
            }

            String[] files = filesList.split(":");
            for (String filePath : files) {
                processFile(filePath, mode, suffix);
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading config file: " + configFile, e);
            System.exit(1);
        }
    }

    private static Properties loadConfig(String configFile) throws IOException {
        Properties properties = new Properties();
        try (FileSystem fileSystem = FileSystems.newFileSystem(Paths.get(configFile), (ClassLoader) null)) {
            Path configPath = fileSystem.getPath("/config.properties");
            properties.load(Files.newInputStream(configPath));
        }
        return properties;
    }

    private static void processFile(String filePath, String mode, String suffix) {
        Path sourcePath = Paths.get(filePath);
        if (Files.exists(sourcePath)) {
            String destinationFileName = sourcePath.getFileName().toString().replaceFirst("\\.\\w+$", "-" + suffix + "$0");
            Path destinationPath = sourcePath.resolveSibling(destinationFileName);

            try {
                if (mode.equals("copy")) {
                    Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                    logger.log(Level.INFO, "{0} -> {1}", new Object[]{sourcePath, destinationPath});
                } else if (mode.equals("move")) {
                    Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                    logger.log(Level.INFO, "{0} => {1}", new Object[]{sourcePath, destinationPath});
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error processing file: " + filePath, e);
            }
        } else {
            logger.log(Level.SEVERE, "No such file: {0}", filePath.replace('\\', '/'));
        }
    }
}
