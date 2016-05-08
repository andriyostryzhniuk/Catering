package ostryzhniuk.andriy.catering.client;

import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ostryzhniuk.andriy.catering.subsidiary.classes.AlterWindow;

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
                        alterError();
                    }
                }
            }
        }
        return socket;
    }

    private static void alterError(){
        String headerText = "Не вдається отримати доступ до сервера.\nПідключення було невдалим.";
        String contentText = "Сервер не відповідає, спробуйте пізніше будь ласка.";
        AlterWindow alterWindow = new AlterWindow(Alert.AlertType.ERROR, headerText, contentText);
        alterWindow.startShow();
    }
}
