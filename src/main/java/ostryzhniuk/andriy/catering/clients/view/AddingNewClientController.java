package ostryzhniuk.andriy.catering.clients.view;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
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
        setListenerToNameTextField();
        setListenerToAddressTextField();
        setListenerToTelephoneTextField();
        setListenerToContactPersonTextField();
        setListenerToDiscountTextField();
        setListenerToEmailTextField();
        setListenerToIcqTextField();
        setListenerToSkypeTextField();
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

    public void setListenerToNameTextField() {
        Pattern pattern = Pattern.compile("[^a-zA-Zа-яА-ЯіІїЇєЄ&\\s-]");
        nameTextField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        nameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = pattern.matcher(nameTextField.getText());
            if (matcher.find()) {
                if (!nameTextField.getStyleClass().contains("warning")) {
                    nameTextField.getStyleClass().add("warning");
                }
            }
            exceptionLabel.setText("");
        });

        nameTextField.setOnMouseClicked((MouseEvent event) -> {
            if (nameTextField.getStyleClass().contains("warning")) {
                nameTextField.getStyleClass().remove("warning");
                exceptionLabel.setText("Назва не може містити інших символів крім\n" +
                        "латинських та кириличних, а також & -");
            }
        });

        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = pattern.matcher(newValue);
            if (matcher.find()) {
                exceptionLabel.setText("Назва не може містити інших символів крім\n" +
                        "латинських та кириличних, а також & -");
            } else {
                exceptionLabel.setText("");
            }
        });
    }

    public void setListenerToAddressTextField() {
        Pattern pattern = Pattern.compile("[^a-zA-Zа-яА-ЯіІїЇєЄ'\'.'\','\'/()\\s\\d-]");
        addressTextField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        addressTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = pattern.matcher(addressTextField.getText());
            boolean bool = matcher.find();
            LOGGER.info("matcher.find(): " + bool + " in: " + matcher);
            if (bool) {
                if (!addressTextField.getStyleClass().contains("warning")) {
                    addressTextField.getStyleClass().add("warning");
                }
            }
            exceptionLabel.setText("");
        });

        addressTextField.setOnMouseClicked((MouseEvent event) -> {
            if (addressTextField.getStyleClass().contains("warning")) {
                addressTextField.getStyleClass().remove("warning");
                exceptionLabel.setText("Адреса не може містити інших символів крім\n" +
                        "латинських та кириличних, а також 0 - 9 . , / ( ) -");
            }
        });

        addressTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = pattern.matcher(newValue);
            if (matcher.find()) {
                exceptionLabel.setText("Адреса не може містити інших символів крім\n" +
                        "латинських та кириличних, а також 0 - 9 . , / ( ) -");
            } else {
                exceptionLabel.setText("");
            }
        });
    }

    public void setListenerToTelephoneTextField() {
        Pattern pattern = Pattern.compile("[^\\d]");
        telephoneTextField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        telephoneTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = pattern.matcher(telephoneTextField.getText());
            if (matcher.find()) {
                if (!telephoneTextField.getStyleClass().contains("warning")) {
                    telephoneTextField.getStyleClass().add("warning");
                }
            }
            exceptionLabel.setText("");
        });

        telephoneTextField.setOnMouseClicked((MouseEvent event) -> {
            if (telephoneTextField.getStyleClass().contains("warning")) {
                telephoneTextField.getStyleClass().remove("warning");
                exceptionLabel.setText("Номер телефону може містити лише цифри");
            }
        });

        telephoneTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = pattern.matcher(newValue);
            if (matcher.find()) {
                exceptionLabel.setText("Номер телефону може містити лише цифри");
            } else {
                exceptionLabel.setText("");
            }
        });
    }

    public void setListenerToContactPersonTextField() {
        Pattern pattern = Pattern.compile("[^a-zA-Zа-яА-ЯіІїЇєЄ'\'.\\s-]");
        contactPersonTextField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        contactPersonTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = pattern.matcher(contactPersonTextField.getText());
            if (matcher.find()) {
                if (!contactPersonTextField.getStyleClass().contains("warning")) {
                    contactPersonTextField.getStyleClass().add("warning");
                }
            }
            exceptionLabel.setText("");
        });

        contactPersonTextField.setOnMouseClicked((MouseEvent event) -> {
            if (contactPersonTextField.getStyleClass().contains("warning")) {
                contactPersonTextField.getStyleClass().remove("warning");
                exceptionLabel.setText("Ім'я не може містити інших символів крім\n" +
                        "латинських та кириличних, а також символів - .");
            }
        });

        contactPersonTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = pattern.matcher(newValue);
            if (matcher.find()) {
                exceptionLabel.setText("Ім'я не може містити інших символів крім\n" +
                        "латинських та кириличних, а також символів - .");
            } else {
                exceptionLabel.setText("");
            }
        });
    }

    public void setListenerToDiscountTextField() {
        Pattern pattern = Pattern.compile("[^'\'.\\d]");
        discountTextField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        discountTextField.setTooltip(new Tooltip("Знижка клієнта (у відсотках)"));
        discountTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            discountTextFieldValidation();
            exceptionLabel.setText("");
        });

        discountTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = pattern.matcher(newValue);
            if (matcher.find()) {
                exceptionLabel.setText("Знижка повинна бути числовим відсотковим\nзначенням (не більше 100 і не меше 0)");
            } else {
                exceptionLabel.setText("");
            }
        });

        discountTextField.setOnMouseClicked((MouseEvent event) -> {
            if (discountTextField.getStyleClass().contains("warning")) {
                discountTextField.getStyleClass().remove("warning");
                exceptionLabel.setText("Знижка повинна бути числовим відсотковим\nзначенням (не більше 100 і не меше 0)");
            }
        });
    }

    public void discountTextFieldValidation() {
        discountTextField.getStyleClass().remove("warning");
        try {
            if (!discountTextField.getText().isEmpty() &&
                    (new BigDecimal(discountTextField.getText()).compareTo(new BigDecimal(100)) == 1 ||
                    new BigDecimal(discountTextField.getText()).compareTo(new BigDecimal(0)) == -1)) {
                if (!discountTextField.getStyleClass().contains("warning")) {
                    discountTextField.getStyleClass().add("warning");
                }
            }
        } catch (java.lang.NumberFormatException e) {
            LOGGER.debug("NumberFormatException");
            if (!discountTextField.getText().isEmpty() && !discountTextField.getStyleClass().contains("warning")) {
                discountTextField.getStyleClass().add("warning");
            }
        }
    }

    public void setListenerToEmailTextField() {
        Pattern pattern = Pattern.compile("^[_A-Za-z0-9&-]+(\\.[_A-Za-z0-9&-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
        emailTextField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        emailTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = pattern.matcher(emailTextField.getText());
            if (!matcher.find()) {
                if (!emailTextField.getStyleClass().contains("warning")) {
                    emailTextField.getStyleClass().add("warning");
                }
            }
            exceptionLabel.setText("");
        });

        emailTextField.setOnMouseClicked((MouseEvent event) -> {
            if (emailTextField.getStyleClass().contains("warning")) {
                emailTextField.getStyleClass().remove("warning");
                exceptionLabel.setText("E-mail повиннен відповідати формі, а також\nможе містити лише латинські символи та _- . ");
            }
        });

        emailTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = pattern.matcher(newValue);
            if (!matcher.find()) {
                exceptionLabel.setText("E-mail повиннен відповідати формі, а також\nможе містити лише латинські символи та _- . ");
            } else {
                exceptionLabel.setText("");
            }
        });
    }

    public void setListenerToIcqTextField() {
        String regex = "\\d{5,9}";
        icqTextField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        icqTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!icqTextField.getText().matches(regex)) {
                if (!icqTextField.getStyleClass().contains("warning")) {
                    icqTextField.getStyleClass().add("warning");
                }
            }
            exceptionLabel.setText("");
        });

        icqTextField.setOnMouseClicked((MouseEvent event) -> {
            if (icqTextField.getStyleClass().contains("warning")) {
                icqTextField.getStyleClass().remove("warning");
                exceptionLabel.setText("Номер облікового запису ICQ може\nскладатись лише з 5-9 арабських цифр");
            }
        });

        icqTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!icqTextField.getText().matches(regex)) {
                exceptionLabel.setText("Номер облікового запису ICQ може\nскладатись лише з 5-9 арабських цифр");
            } else {
                exceptionLabel.setText("");
            }
        });
    }

    public void setListenerToSkypeTextField() {
        String regex = "[a-zA-Z][a-zA-Z0-9\\.\\-_]{5,31}";
        skypeTextField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        skypeTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!skypeTextField.getText().matches(regex)) {
                if (!skypeTextField.getStyleClass().contains("warning")) {
                    skypeTextField.getStyleClass().add("warning");
                }
            }
            exceptionLabel.setText("");
        });

        skypeTextField.setOnMouseClicked((MouseEvent event) -> {
            if (skypeTextField.getStyleClass().contains("warning")) {
                skypeTextField.getStyleClass().remove("warning");
                exceptionLabel.setText("Skype має бути довжиною від 6 до 32, починати-\nся з літери та містити лише літери та цифри");
            }
        });

        skypeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!skypeTextField.getText().matches(regex)) {
                exceptionLabel.setText("Skype має бути довжиною від 6 до 32, починати-\nся з літери та містити лише літери та цифри");
            } else {
                exceptionLabel.setText("");
            }
        });
    }

}