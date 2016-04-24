package ostryzhniuk.andriy.catering.menu.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ostryzhniuk.andriy.catering.commands.ClientCommandTypes;
import ostryzhniuk.andriy.catering.overridden.elements.combo.box.AutoCompleteComboBoxListener;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ostryzhniuk.andriy.catering.client.Client.sendARequestToTheServer;

public class AddingToMenuController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddingToMenuController.class);
    public Group rootGroup;
    public GridPane controlsGridPane;
    private ComboBox dishesTypeComboBox = new ComboBox();
    private ComboBox comboBoxListener = new ComboBox();
    public TextField nameTextField;
    public TextField priceTextField;
    public TextField massTextField;
    public TextArea ingredientsTextArea;
    public Label exceptionLabel;
    public Label titleLabel;

    private Integer menuIdToUpdate;
    private MenuWindowController menuWindowController;

    @FXML
    public void initialize(){
        initDishesTypeComboBox();
        controlsGridPane.add(dishesTypeComboBox, 1, 1);
        setListenerToNameTextField();
        setListenerToPriceTextField();
        setListenerToMassTextField();
        setListenerToIngredientsTextArea();
        setTooltips();
    }

    public void setTextToTextFields(String dishesType, String name, BigDecimal price, Double mass, String ingredients){
        this.dishesTypeComboBox.setValue(dishesType);
        this.comboBoxListener.setValue(dishesType);
        this.nameTextField.setText(name);
        this.priceTextField.setText(price.toString());
        this.massTextField.setText(mass.toString());
        this.ingredientsTextArea.setText(ingredients);
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

        List<Object> dishesTypeNameList = new LinkedList<>();
        dishesTypeNameList.add(comboBoxListener.getValue());
        Integer dishesTypeId;
        try {
            dishesTypeId = (Integer)
                    sendARequestToTheServer(ClientCommandTypes.SELECT_DISHES_TYPE_ID, dishesTypeNameList).get(0);
            if (dishesTypeComboBox.getStyleClass().contains("warning")) {
                return;
            }
        } catch (IndexOutOfBoundsException e) {
            if (!dishesTypeComboBox.getStyleClass().contains("warning")) {
                dishesTypeComboBox.getStyleClass().add("warning");
            }
            return;
        }
        objectList.add(dishesTypeId);

        if (!isEmpty(nameTextField) &&
                textFieldMatcherFind(nameTextField, Pattern.compile("[^a-zA-Zа-яА-ЯіІїЇєЄ&()\\s-]"))){
            objectList.add(nameTextField.getText());
        } else {
            return;
        }

        if (priceTextField.getText().isEmpty()) {
            objectList.add(new BigDecimal(0));
        } else {
            if (priceTextFieldValidation()){
                objectList.add(new BigDecimal(priceTextField.getText()));
            } else {
                return;
            }
        }

        if (massTextField.getText().isEmpty()) {
            objectList.add(new Double(0));
        } else {
            if (massTextFieldValidation()){
                objectList.add(new Double(massTextField.getText()));
            } else {
                return;
            }
        }

        if (ingredientsTextArea.getText().isEmpty()) {
            objectList.add("");
        } else {
            if (ingredientsTextAreaMatcherFind()){
                objectList.add(ingredientsTextArea.getText());
            } else {
                return;
            }
        }

        if (menuIdToUpdate == null) {
            sendARequestToTheServer(ClientCommandTypes.INSERT_MENU, objectList);
        } else {
            objectList.add(menuIdToUpdate);
            sendARequestToTheServer(ClientCommandTypes.UPDATE_MENU, objectList);
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
        Pattern pattern = Pattern.compile("[^a-zA-Zа-яА-ЯіІїЇєЄ&()\\s-]");
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

    public void setListenerToPriceTextField() {
        Pattern pattern = Pattern.compile("[^'\'.\\d]");
        priceTextField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        priceTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            priceTextFieldValidation();
            exceptionLabel.setText("");
        });

        priceTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = pattern.matcher(newValue);
            if (matcher.find()) {
                exceptionLabel.setText("Ціна повинна бути числовим значенням,\nта містити лише цифри");
            } else {
                exceptionLabel.setText("");
            }
        });

        priceTextField.setOnMouseClicked((MouseEvent event) -> {
            if (priceTextField.getStyleClass().contains("warning")) {
                priceTextField.getStyleClass().remove("warning");
                exceptionLabel.setText("Ціна повинна бути числовим значенням,\nта містити лише цифри");
            }
        });
    }

    public boolean priceTextFieldValidation() {
        boolean right = true;
        priceTextField.setText(priceTextField.getText().trim());
        priceTextField.getStyleClass().remove("warning");
        try {
            if (!priceTextField.getText().isEmpty() &&
                            new BigDecimal(priceTextField.getText()).compareTo(new BigDecimal(0)) == -1) {
                right = false;
                if (!priceTextField.getStyleClass().contains("warning")) {
                    priceTextField.getStyleClass().add("warning");
                }
            }
        } catch (NumberFormatException e) {
            LOGGER.debug("NumberFormatException");
            if (!priceTextField.getText().isEmpty()) {
                right = false;
                if (!priceTextField.getStyleClass().contains("warning")) {
                    priceTextField.getStyleClass().add("warning");
                }
            }
        }
        return right;
    }

    public void setListenerToMassTextField() {
        Pattern pattern = Pattern.compile("[^'\'.\\d]");
        massTextField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        massTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            massTextFieldValidation();
            exceptionLabel.setText("");
        });

        massTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = pattern.matcher(newValue);
            if (matcher.find()) {
                exceptionLabel.setText("Вага повинна бути числовим значенням,\nта містити лише цифри");
            } else {
                exceptionLabel.setText("");
            }
        });

        massTextField.setOnMouseClicked((MouseEvent event) -> {
            if (massTextField.getStyleClass().contains("warning")) {
                massTextField.getStyleClass().remove("warning");
                exceptionLabel.setText("Вага повинна бути числовим значенням,\nта містити лише цифри");
            }
        });
    }

    public boolean massTextFieldValidation() {
        boolean right = true;
        massTextField.setText(massTextField.getText().trim());
        massTextField.getStyleClass().remove("warning");
        try {
            if (!massTextField.getText().isEmpty() && new Double(massTextField.getText()) < 0) {
                right = false;
                if (!massTextField.getStyleClass().contains("warning")) {
                    massTextField.getStyleClass().add("warning");
                }
            }
        } catch (NumberFormatException e) {
            if (!massTextField.getText().isEmpty()) {
                right = false;
                if (!massTextField.getStyleClass().contains("warning")) {
                    massTextField.getStyleClass().add("warning");
                }
            }
        }
        return right;
    }

    public void setListenerToIngredientsTextArea() {
        ingredientsTextArea.getStylesheets().add(getClass().getResource("/styles/TextAreaStyle.css").toExternalForm());
        Pattern pattern = Pattern.compile("[^a-zA-Zа-яА-ЯіІїЇєЄ()'\','\'%''\'.\\s\\d-]");
        ingredientsTextArea.focusedProperty().addListener((observable, oldValue, newValue) -> {
            ingredientsTextAreaMatcherFind();
            exceptionLabel.setText("");
        });

        ingredientsTextArea.setOnMouseClicked((MouseEvent event) -> {
            if (ingredientsTextArea.getStyleClass().contains("warning")) {
                ingredientsTextArea.getStyleClass().remove("warning");
                exceptionLabel.setText("Опис інградієнтів містить\nнеприпустимі символи");
            }
        });

        ingredientsTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = pattern.matcher(newValue);
            if (matcher.find()) {
                exceptionLabel.setText("Опис інградієнтів містить\nнеприпустимі символи");
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

    private boolean ingredientsTextAreaMatcherFind(){
        boolean right = true;
        Pattern pattern = Pattern.compile("[^a-zA-Zа-яА-ЯіІїЇєЄ()'\','\'%''\'.\\s\\d-]");
        ingredientsTextArea.setText(ingredientsTextArea.getText().trim());
        Matcher matcher = pattern.matcher(ingredientsTextArea.getText());
        if (matcher.find()) {
            right = false;
            if (!ingredientsTextArea.getStyleClass().contains("warning")) {
                ingredientsTextArea.getStyleClass().add("warning");
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
        nameTextField.setTooltip(new Tooltip("Назва страви"));
        priceTextField.setTooltip(new Tooltip("Ціна"));
        massTextField.setTooltip(new Tooltip("Вага страви"));
        ingredientsTextArea.setTooltip(new Tooltip("Інградієнти"));
    }

    public void initDishesTypeComboBox() {
        dishesTypeComboBox.getStylesheets().add(getClass().getResource("/styles/ComboBoxStyle.css").toExternalForm());
        dishesTypeComboBox.setTooltip(new Tooltip("Тип Страви"));
        dishesTypeComboBox.setPromptText("Тип Страви");

        ObservableList<String> observableList = FXCollections.observableArrayList();
        observableList.addAll(FXCollections.observableArrayList(
                sendARequestToTheServer(ClientCommandTypes.SELECT_DISHES_TYPE_NAME, new LinkedList<>())));

        dishesTypeComboBox.setItems(observableList);

        new AutoCompleteComboBoxListener<>(dishesTypeComboBox, comboBoxListener);

        dishesTypeComboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                final ListCell<String> cell = new ListCell<String>() {
                    {
                        super.setOnMousePressed((MouseEvent event) -> {
//                            mouse pressed
                            comboBoxListener.setValue(dishesTypeComboBox.getValue());
                        });
                    }

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);
                    }
                };
                return cell;
            }
        });

        comboBoxListener.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                dishesTypeComboBox.getStyleClass().remove("warning");
            }
        });
    }

}