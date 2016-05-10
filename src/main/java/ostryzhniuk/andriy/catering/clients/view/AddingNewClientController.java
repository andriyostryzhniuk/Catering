package ostryzhniuk.andriy.catering.clients.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ostryzhniuk.andriy.catering.commands.ClientCommandTypes;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
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
    public Button saveButton;
    public Button cancelButton;

    private Integer clientIdToUpdate;
    private ClientWindowController clientWindowController;

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
        setTooltips();
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

        if (!isEmpty(nameTextField) &&
                textFieldMatcherFind(nameTextField, Pattern.compile("[^a-zA-Zа-яА-ЯіІїЇєЄ&\\s-]"))){
            objectList.add(nameTextField.getText());
        } else {
            return;
        }

        if (!isEmpty(addressTextField) &&
                textFieldMatcherFind(addressTextField, Pattern.compile("[^a-zA-Zа-яА-ЯіІїЇєЄ'\'.'\','\'/()\\s\\d-]"))){
            objectList.add(addressTextField.getText());
        } else {
            return;
        }

        if (!isEmpty(telephoneTextField) &&
                textFieldMatcherFind(telephoneTextField, Pattern.compile("[^\\d]"))){
            objectList.add(telephoneTextField.getText());
        } else {
            return;
        }

        if (!isEmpty(contactPersonTextField) &&
                textFieldMatcherFind(contactPersonTextField, Pattern.compile("[^a-zA-Zа-яА-ЯіІїЇєЄ'\'.\\s-]"))){
            objectList.add(contactPersonTextField.getText());
        } else {
            return;
        }


        if (discountTextField.getText().isEmpty()) {
            objectList.add(new BigDecimal(0));
        } else {
            if (discountTextFieldValidation()){
                objectList.add(new BigDecimal(discountTextField.getText()));
            } else {
                return;
            }
        }

        if (emailTextField.getText().isEmpty()) {
            objectList.add("");
        } else {
            String emailsRegex = "^[_A-Za-z0-9&-]+(\\.[_A-Za-z0-9&-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
            if (textFieldMatches(emailTextField, emailsRegex)){
                objectList.add(emailTextField.getText());
            } else {
                return;
            }
        }

        if (icqTextField.getText().isEmpty()) {
            objectList.add(null);
        } else {
            if (textFieldMatches(icqTextField, "\\d{5,9}")){
                objectList.add(new Integer(icqTextField.getText()));
            } else {
                return;
            }
        }

        if (skypeTextField.getText().isEmpty()) {
            objectList.add("");
        } else {
            if (textFieldMatches(skypeTextField, "[a-zA-Z][a-zA-Z0-9\\.\\-_]{5,31}")){
                objectList.add(skypeTextField.getText());
            } else {
                return;
            }
        }

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
        if (icq != null) {
            this.icqTextField.setText(icq.toString());
        }
        this.skypeTextField.setText(skype);
    }

    public void setListenerToNameTextField() {
        nameTextField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        Pattern pattern = Pattern.compile("[^a-zA-Zа-яА-ЯіІїЇєЄ&\\s-]");
        nameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            textFieldMatcherFind (nameTextField, pattern);
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
        addressTextField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        Pattern pattern = Pattern.compile("[^a-zA-Zа-яА-ЯіІїЇєЄ'\'.'\','\'/()\\s\\d-]");
        addressTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            textFieldMatcherFind(addressTextField, pattern);
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
        telephoneTextField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        Pattern pattern = Pattern.compile("[^\\d]");
        telephoneTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            textFieldMatcherFind(telephoneTextField, pattern);
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
        contactPersonTextField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        Pattern pattern = Pattern.compile("[^a-zA-Zа-яА-ЯіІїЇєЄ'\'.\\s-]");
        contactPersonTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            textFieldMatcherFind(contactPersonTextField, pattern);
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

    public boolean discountTextFieldValidation() {
        boolean right = true;
        discountTextField.setText(discountTextField.getText().trim());
        discountTextField.getStyleClass().remove("warning");
        try {
            if (!discountTextField.getText().isEmpty() &&
                    (new BigDecimal(discountTextField.getText()).compareTo(new BigDecimal(100)) == 1 ||
                    new BigDecimal(discountTextField.getText()).compareTo(new BigDecimal(0)) == -1)) {
                right = false;
                if (!discountTextField.getStyleClass().contains("warning")) {
                    discountTextField.getStyleClass().add("warning");
                }
            }
        } catch (java.lang.NumberFormatException e) {
            LOGGER.debug("NumberFormatException");
            if (!discountTextField.getText().isEmpty()) {
                right = false;
                if (!discountTextField.getStyleClass().contains("warning")) {
                    discountTextField.getStyleClass().add("warning");
                }
            }
        }
        return right;
    }

    public void setListenerToEmailTextField() {
        emailTextField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        String regex = "^[_A-Za-z0-9&-]+(\\.[_A-Za-z0-9&-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        emailTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            textFieldMatches(emailTextField, regex);
            exceptionLabel.setText("");
        });

        emailTextField.setOnMouseClicked((MouseEvent event) -> {
            if (emailTextField.getStyleClass().contains("warning")) {
                emailTextField.getStyleClass().remove("warning");
                exceptionLabel.setText("E-mail повиннен відповідати формі, а також\nможе містити лише латинські символи та _- . ");
            }
        });

        emailTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!emailTextField.getText().matches(regex)) {
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
            textFieldMatches(icqTextField, regex);
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
            textFieldMatches(skypeTextField, regex);
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

    private boolean textFieldMatcherFind(TextField textField, Pattern pattern){
        boolean right = true;
        textField.setText(textField.getText().trim());
        Matcher matcher = pattern.matcher(textField.getText());
        if (matcher.find()) {
            right = false;
            if (!textField.getStyleClass().contains("warning")) {
                textField.getStyleClass().add("warning");
            }
        }
        return right;
    }

    private boolean textFieldMatches(TextField textField, String regex){
        boolean right = true;
        textField.setText(textField.getText().trim());
        if (!textField.getText().isEmpty() && !textField.getText().matches(regex)) {
            right = false;
            if (!textField.getStyleClass().contains("warning")) {
                textField.getStyleClass().add("warning");
            }
        }
        return right;
    }

    private boolean isEmpty(TextField textField){
        boolean isEmpty = false;
        if (textField.getText().isEmpty()) {
            isEmpty = true;
            if (!textField.getStyleClass().contains("warning")) {
                textField.getStyleClass().add("warning");
            }
        }
        return isEmpty;
    }

    private void setTooltips(){
        nameTextField.setTooltip(new Tooltip("Назва клієнта"));
        addressTextField.setTooltip(new Tooltip("Адреса клієнта"));
        telephoneTextField.setTooltip(new Tooltip("Контактний номер телефону"));
        contactPersonTextField.setTooltip(new Tooltip("Контактна особа"));
        discountTextField.setTooltip(new Tooltip("Знижка клієнта (у відсотках)"));
        emailTextField.setTooltip(new Tooltip("Контактний E-mail"));
        icqTextField.setTooltip(new Tooltip("Номер ICQ"));
        skypeTextField.setTooltip(new Tooltip("Ім'я Skype"));
    }

    public void initShortcuts(){
        cancelButton.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.ESCAPE), () -> cancelButton.fire());
        saveButton.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN), () -> saveButton.fire());
    }

}