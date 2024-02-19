import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationParser {
    private String mode;
    private String suffix;
    private String[] files;

    public ConfigurationParser(String configFilePath) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(configFilePath));

        // Extract mode, suffix, and files from the properties
        mode = properties.getProperty("mode");
        suffix = properties.getProperty("suffix");
        String filesString = properties.getProperty("files");
        files = filesString.split(":");
    }

    // Getters for mode, suffix, and files
    public String getMode() {
        return mode;
    }

    public String getSuffix() {
        return suffix;
    }

    public String[] getFiles() {
        return files;
    }
}
