package ru.focus;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ServerMain {

    public static final String RELATIVE_PATH_PROPERTIES_FILE = "/config.properties";

    public static void main(String[] args) {

        int port = 0;
        try {
            ConfigPropertiesInitializer configInitializer = new ConfigPropertiesInitializer(RELATIVE_PATH_PROPERTIES_FILE);
            port = configInitializer.getServerPort();
        } catch (Exception e) {
            log.error("Error receiving data from the configuration file! Underlying cause was [{}]", e.getMessage());
            System.exit(1);
        }

        Server server = new Server(port);
        server.start();

    }
}
