package ru.focus;

import ru.focus.exception.InputOutputException;
import ru.focus.exception.ValidationIntegerFormatException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigPropertiesInitializer {

    private static final String SERVER_PORT_NAME = "server.port";

    private final String relativePathPropertiesFile;
    private final Properties properties;


    public ConfigPropertiesInitializer(String relativePathPropertiesFile) {
        this.properties = new Properties();
        this.relativePathPropertiesFile = relativePathPropertiesFile;
        loadPropertiesFile();
    }

    public int getServerPort() {
        return getPropertiesIntValue(SERVER_PORT_NAME);
    }

    private void loadPropertiesFile() {
        try (InputStream inputStream = getClass().getResourceAsStream(relativePathPropertiesFile)) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new InputOutputException(
                    String.format("Error loading properties file: '%s'", relativePathPropertiesFile), e
            );
        }
    }

    private int getPropertiesIntValue(String key) {
        int value = 0;
        try {
            value = Integer.parseInt(properties.getProperty(key));
        } catch (NumberFormatException e) {
            throw new ValidationIntegerFormatException(
                    String.format("The value from %s is not an integer!", relativePathPropertiesFile), e
            );
        }
        return value;
    }

}
