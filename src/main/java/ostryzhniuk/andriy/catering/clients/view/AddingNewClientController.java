package ostryzhniuk.andriy.catering.clients.view;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ostryzhniuk.andriy.catering.commands.ClientCommandTypes;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public Label exceptionLabel;
    public Label titleLabel;

    private Integer clientIdToUpdate;
    private ClientWindowController clientWindowController;
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

    @FXML
    public void initialize(){
        verifyTextField();
    }

    public void escape(ActionEvent actionEvent) {
        close();
    }

    public void close(){
        Stage stage = (Stage) rootGroup.getScene().getWindow();
        clientWindowController.initTableView();
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

        BigDecimal discount;
        if (discountTextField.getText().isEmpty()) {
            discount = new BigDecimal(0);
        } else {
            discount = new BigDecimal(discountTextField.getText());
        }
        objectList.add(discount);

        String email = emailTextField.getText();
        objectList.add(email);

        Integer icq = new Integer(icqTextField.getText());
        objectList.add(icq);

        String skype = skypeTextField.getText();
        objectList.add(skype);

        if (clientIdToUpdate == null) {
            sendARequestToTheServer(ClientCommandTypes.INSERT_CLIENT, objectList);
        } else {
            objectList.add(clientIdToUpdate);
            sendARequestToTheServer(ClientCommandTypes.UPDATE_CLIENT, objectList);
        }
        close();
    }

    public void setClientIdToUpdate(Integer clientIdToUpdate) {
        this.clientIdToUpdate = clientIdToUpdate;
        if (clientIdToUpdate == null) {
            titleLabel.setText("Додати клієнта");
        } else {
            titleLabel.setText("Редагувати інформацію про клієнта");
        }
    }

    public void setClientWindowController(ClientWindowController clientWindowController) {
        this.clientWindowController = clientWindowController;
    }

    public void setTextToTextFields(String name, String address, String telephoneNumber, String contactPerson,
                                    BigDecimal discount, String email, Integer icq, String skype){
        this.nameTextField.setText(name);
        this.addressTextField.setText(address);
        this.telephoneTextField.setText(telephoneNumber);
        this.contactPersonTextField.setText(contactPerson);
        this.discountTextField.setText(discount.toString());
        this.emailTextField.setText(email);
        this.icqTextField.setText(icq.toString());
        this.skypeTextField.setText(skype);
    }

    public void verifyTextField() {
        nameTextField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        nameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            Pattern pattern = Pattern.compile("[^a-zA-Zа-яА-Я& -]");
            Matcher matcher = pattern.matcher(nameTextField.getText());
            if (matcher.find()) {
                if (!nameTextField.getStyleClass().contains("warning")) {
                    nameTextField.getStyleClass().add("warning");
                    exceptionLabel.setText("Назва не може містити інших символів крім\nлатинських та кириличних");
                }
            }
        });

        nameTextField.setOnMouseClicked((MouseEvent event) -> {
            nameTextField.getStyleClass().remove("warning");
            exceptionLabel.setText("");
        });
    }


}
