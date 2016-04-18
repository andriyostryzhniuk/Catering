package ostryzhniuk.andriy.catering.clients.view;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ostryzhniuk.andriy.catering.commands.ClientCommandTypes;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static ostryzhniuk.andriy.catering.client.Client.sendARequestToTheServer;

/**
 * Created by Andriy on 04/18/2016.
 */
public class AddingNewClientController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddingNewClientController.class);
    public Group rootGroup;
    public TextField nameTextField;
    public TextField addressTextField;
    public TextField telephoneTextField;
    public TextField contactPersonTextField;
    public TextField discountTextField;
    public TextField emailTextField;
    public TextField icqTextField;
    public TextField skypeTextField;

    private boolean isException = false;

    public void showPrompt() {

        Label exceptionLabel = new Label("Значення неприпустиме");
        exceptionLabel.setStyle("-fx-text-fill: red");

        Button saveButton = new Button("Зберегти");
        saveButton.setOnAction((ActionEvent event) -> {
            if (!isException) {
                close();
            } else {
                rootGroup.getChildren().add(exceptionLabel);
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> {
                            rootGroup.getChildren().remove(exceptionLabel);
                        });
                    }
                }, 1000, 5000);
            }
        });
    }

    public void escape(ActionEvent actionEvent) {
        close();
    }

    public void close(){
        Stage stage = (Stage) rootGroup.getScene().getWindow();
        stage.close();
    }

    public void saveToDB(ActionEvent actionEvent) {
        List<Object> objectList = new LinkedList<>();

        String name = nameTextField.getText();
        objectList.add(name);

        String address = addressTextField.getText();
        objectList.add(address);

        String telephone = telephoneTextField.getText();
        objectList.add(telephone);

        String contactPerson = contactPersonTextField.getText();
        objectList.add(contactPerson);

        BigDecimal discount = new BigDecimal(discountTextField.getText());
        objectList.add(discount);

        String email = emailTextField.getText();
        objectList.add(email);

        Integer icq = new Integer(icqTextField.getText());
        objectList.add(icq);

        String skype = skypeTextField.getText();
        objectList.add(skype);

        sendARequestToTheServer(ClientCommandTypes.INSERT_CLIENT, objectList);
    }
}
