package ostryzhniuk.andriy.catering.menu.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ostryzhniuk.andriy.catering.client.Client.sendARequestToTheServer;

/**
 * Created by Andriy on 04/18/2016.
 */
public class AddingToMenuController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddingToMenuController.class);
    public Group rootGroup;
    public TextField nameTextField;
    public TextField priceTextField;
    public TextField massTextField;
    public TextArea ingredients;
    public Label exceptionLabel;
    public Label titleLabel;

    private Integer menuIdToUpdate;
    private MenuWindowController menuWindowController;

    @FXML
    public void initialize(){
        setListenerToNameTextField();
        setListenerToTelephoneTextField();
        setListenerToContactPersonTextField();
        setTooltips();
    }

    public void setTextToTextFields(String dishesType, String name, BigDecimal price, Double mass, String ingredients){
        this.nameTextField.setText(name);
        this.priceTextField.setText(price.toString());
        this.massTextField.setText(mass.toString());
        this.ingredients.setText(ingredients);
    }

    public void escape(ActionEvent actionEvent) {
        close();
    }

    public void close(){
        Stage stage = (Stage) rootGroup.getScene().getWindow();
        menuWindowController.initTableView();
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

        if (!isEmpty(priceTextField) &&
                textFieldMatcherFind(priceTextField, Pattern.compile("[^\\d]"))){
            objectList.add(priceTextField.getText());
        } else {
            return;
        }

        if (!isEmpty(massTextField) &&
                textFieldMatcherFind(massTextField, Pattern.compile("[^a-zA-Zа-яА-ЯіІїЇєЄ'\'.\\s-]"))){
            objectList.add(massTextField.getText());
        } else {
            return;
        }

        if (menuIdToUpdate == null) {
            sendARequestToTheServer(ClientCommandTypes.INSERT_CLIENT, objectList);
        } else {
            objectList.add(menuIdToUpdate);
            sendARequestToTheServer(ClientCommandTypes.UPDATE_CLIENT, objectList);
        }
        close();
    }

    public void setMenuIdToUpdate(Integer menuIdToUpdate) {
        this.menuIdToUpdate = menuIdToUpdate;
        if (menuIdToUpdate == null) {
            titleLabel.setText("Додати клієнта");
        } else {
            titleLabel.setText("Редагувати інформацію про клієнта");
        }
    }

    public void setMenuWindowController(MenuWindowController menuWindowController) {
        this.menuWindowController = menuWindowController;
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

    public void setListenerToTelephoneTextField() {
        priceTextField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        Pattern pattern = Pattern.compile("[^\\d]");
        priceTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            textFieldMatcherFind(priceTextField, pattern);
            exceptionLabel.setText("");
        });

        priceTextField.setOnMouseClicked((MouseEvent event) -> {
            if (priceTextField.getStyleClass().contains("warning")) {
                priceTextField.getStyleClass().remove("warning");
                exceptionLabel.setText("Номер телефону може містити лише цифри");
            }
        });

        priceTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = pattern.matcher(newValue);
            if (matcher.find()) {
                exceptionLabel.setText("Номер телефону може містити лише цифри");
            } else {
                exceptionLabel.setText("");
            }
        });
    }

    public void setListenerToContactPersonTextField() {
        massTextField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        Pattern pattern = Pattern.compile("[^a-zA-Zа-яА-ЯіІїЇєЄ'\'.\\s-]");
        massTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            textFieldMatcherFind(massTextField, pattern);
            exceptionLabel.setText("");
        });

        massTextField.setOnMouseClicked((MouseEvent event) -> {
            if (massTextField.getStyleClass().contains("warning")) {
                massTextField.getStyleClass().remove("warning");
                exceptionLabel.setText("Ім'я не може містити інших символів крім\n" +
                        "латинських та кириличних, а також символів - .");
            }
        });

        massTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = pattern.matcher(newValue);
            if (matcher.find()) {
                exceptionLabel.setText("Ім'я не може містити інших символів крім\n" +
                        "латинських та кириличних, а також символів - .");
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
        priceTextField.setTooltip(new Tooltip("Контактний номер телефону"));
        massTextField.setTooltip(new Tooltip("Контактна особа"));
    }

}