import java.io.File;
import java.io.IOException;
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
            ConfigurationParser parser = new ConfigurationParser(configFile);
            String mode = parser.getMode();
            String suffix = parser.getSuffix();
            String[] files = parser.getFiles();

            if (mode == null || suffix == null || files.length == 0) {
                logger.log(Level.SEVERE, "Invalid configuration: Mode, suffix, or files are missing.");
                System.exit(1);
            }

            FileOperation fileOperation = new FileOperation(mode, suffix);

            for (String filePath : files) {
                fileOperation.processFile(filePath);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading configuration file: " + e.getMessage());
            System.exit(1);
        }
    }
}
