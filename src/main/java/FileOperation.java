import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileOperation {
    private static final Logger logger = Logger.getLogger(FileOperation.class.getName());
    private final String mode;
    private final String suffix;

    public FileOperation(String mode, String suffix) {
        this.mode = mode;
        this.suffix = suffix;
    }

    public void processFile(String filePath) {
        File file = new File(filePath);

        if (!file.exists()) {
            logger.log(Level.SEVERE, "No such file: " + filePath);
            return;
        }

        String newFileName = file.getName().replaceFirst("\\.([^.]*)$", "-" + suffix + ".$1");
        File newFile = new File(file.getParent(), newFileName);

        try {
            if (mode.equalsIgnoreCase("copy")) {
                Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                logger.log(Level.INFO, file.getPath() + " -> " + newFile.getPath());
            } else if (mode.equalsIgnoreCase("move")) {
                Files.move(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                logger.log(Level.INFO, file.getPath() + " => " + newFile.getPath());
            } else {
                logger.log(Level.SEVERE, "Mode is not recognized: " + mode);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error processing file: " + e.getMessage());
        }
    }
}
