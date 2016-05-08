package ostryzhniuk.andriy.catering.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ostryzhniuk.andriy.catering.commands.ClientCommand;
import ostryzhniuk.andriy.catering.commands.ClientCommandTypes;
import ostryzhniuk.andriy.catering.ordering.view.dto.DtoOrdering;
import ostryzhniuk.andriy.catering.subsidiary.classes.AlterWindow;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static ostryzhniuk.andriy.catering.commands.ClientCommandTypes.UPDATE_ORDERING;

public class Client extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

    private static Socket socket;
    private static OutputStream socketOutputStream;
    private static InputStream socketInputStream;
    private static ObjectOutputStream objectSocketOS;
    private static ObjectInputStream objectSocketIS;

    public void initSocket(Socket socket) throws IOException {
        this.socket = socket;
        this.socketOutputStream = this.socket.getOutputStream();
        this.socketInputStream = this.socket.getInputStream();
        this.objectSocketOS = new ObjectOutputStream(this.socketOutputStream);
        this.objectSocketIS = new ObjectInputStream(this.socketInputStream);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Locale.setDefault(new Locale("uk"));
        initSocket(ostryzhniuk.andriy.catering.client.Socket.getSocket());

        ClassLoader classLoader = getClass().getClassLoader();
        Parent root = FXMLLoader.load(classLoader.getResource("main/window/MainWindow.fxml"));
        primaryStage.setTitle("Catering Company");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(final WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });
        primaryStage.show();
    }

    public static List sendARequestToTheServer(ClientCommandTypes clientCommandType, List<Object> parametersList) {
        ClientCommand clientCommand = new ClientCommand(clientCommandType, parametersList);
        try {
            objectSocketOS.writeObject(clientCommand);
            return (List) objectSocketIS.readObject();
        } catch (SocketException e) {
            LOGGER.error("server has been down" + e);
            alterError();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void alterError(){
        String headerText = "Ой, під час підключення до сервера трапилась помилка.\nПідключення було невдалим.";
        String contentText = "Перезапустіть програму та спробуйте ще раз.";
        AlterWindow alterWindow = new AlterWindow(Alert.AlertType.ERROR, headerText, contentText);
        alterWindow.startShow();
    }

}
