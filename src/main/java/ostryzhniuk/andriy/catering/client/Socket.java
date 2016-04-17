package ostryzhniuk.andriy.catering.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class Socket {

    private static final Logger LOGGER = LoggerFactory.getLogger(Socket.class);

    private static java.net.Socket socket;

    public static java.net.Socket getSocket() {
        if (socket == null){
            synchronized (Socket.class) {
                if (socket == null) {
                    try {
                        socket = new java.net.Socket("localhost",2000);
                        LOGGER.info("I connected!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return socket;
    }
}