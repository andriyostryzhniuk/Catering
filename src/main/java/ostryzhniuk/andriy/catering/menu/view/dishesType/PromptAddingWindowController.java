package ostryzhniuk.andriy.catering.menu.view.dishesType;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import ostryzhniuk.andriy.catering.commands.ClientCommandTypes;
import ostryzhniuk.andriy.catering.menu.view.dto.DtoDishesType;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ostryzhniuk.andriy.catering.client.Client.sendARequestToTheServer;

/**
 * Created by Andriy on 04/26/2016.
 */
public class PromptAddingWindowController {

    public TextField textField;
    public Label exceptionLabel;
    public Label titleLabel;

    private DishesTypeWindowController dishesTypeWindowController;

    private DtoDishesType dtoDishesType;

    @FXML
    public void initialize(){
        setListenerToNameTextField();
    }

    public void save(ActionEvent actionEvent) {
        if (!textFieldIsEmpty() && textFieldMatcherFind()){
            List<Object> objectList = new LinkedList<>();
            objectList.add(textField.getText());

            if (dtoDishesType == null) {
                sendARequestToTheServer(ClientCommandTypes.INSERT_DISHES_TYPE, objectList);
            } else {
                objectList.add(dtoDishesType.getId());
                sendARequestToTheServer(ClientCommandTypes.UPDATE_DISHES_TYPE, objectList);
            }

            dishesTypeWindowController.initListView();
            closeStage();
        } else {
            return;
        }
    }

    public void close(ActionEvent actionEvent) {
        closeStage();
    }

    private void closeStage(){
        Stage stage = (Stage) textField.getScene().getWindow();
        stage.close();
    }

    private void setListenerToNameTextField() {
        textField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            textFieldMatcherFind ();
            exceptionLabel.setText("");
        });

        textField.setOnMouseClicked((MouseEvent event) -> {
            if (textField.getStyleClass().contains("warning")) {
                textField.getStyleClass().remove("warning");
                exceptionLabel.setText("Назва містить неприпустимі символи");
            }
        });

        Pattern pattern = Pattern.compile("[^a-zA-Zа-яА-ЯіІїЇєЄ\\s-]");
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = pattern.matcher(newValue);
            if (matcher.find()) {
                exceptionLabel.setText("Назва містить неприпустимі символи");
            } else {
                exceptionLabel.setText("");
            }
        });
    }

    private boolean textFieldMatcherFind(){
        boolean right = true;
        Pattern pattern = Pattern.compile("[^a-zA-Zа-яА-ЯіІїЇєЄ\\s-]");
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

    private boolean textFieldIsEmpty(){
        boolean isEmpty = false;
        if (textField.getText().isEmpty()) {
            isEmpty = true;
            if (!textField.getStyleClass().contains("warning")) {
                textField.getStyleClass().add("warning");
            }
        }
        return isEmpty;
    }

    public void setDishesTypeWindowController(DishesTypeWindowController dishesTypeWindowController) {
        this.dishesTypeWindowController = dishesTypeWindowController;
    }

    public void setDtoDishesType(DtoDishesType dtoDishesType) {
        this.dtoDishesType = dtoDishesType;
        initTitleLabelText();
    }

    private void initTitleLabelText(){
        if (dtoDishesType == null) {
            titleLabel.setText("Додати категорію");
        } else {
            titleLabel.setText("Редагувати категорію");
            setTextToTextField(dtoDishesType.getType());
        }
    }

    private void setTextToTextField(String text){
        textField.setText(text);
    }

}
