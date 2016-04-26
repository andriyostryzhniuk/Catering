package ostryzhniuk.andriy.catering.menu.view.dishesType;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import ostryzhniuk.andriy.catering.commands.ClientCommandTypes;

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

    private DishesTypeWindowController dishesTypeWindowController;

    @FXML
    public void initialize(){
        setListenerToNameTextField();
    }

    public void save(ActionEvent actionEvent) {
        if (!textFieldIsEmpty() && textFieldMatcherFind()){
            List<Object> objectList = new LinkedList<>();
            objectList.add(textField.getText());
            sendARequestToTheServer(ClientCommandTypes.INSERT_DISHES_TYPE, objectList);
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

    public void setDishesTypeWindowController(DishesTypeWindowController dishesTypeWindowController) {
        this.dishesTypeWindowController = dishesTypeWindowController;
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
                exceptionLabel.setText("Неприпустима назва");
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

//    public static String parseApostrophe(String string){
//        Integer apostropheIndex = string.indexOf("'");
//        if (apostropheIndex != -1) {
//            System.out.println(apostropheIndex);
//            string = string.substring(0, apostropheIndex) + "\\" + string.substring(apostropheIndex);
//            System.out.println(string);
//        }
//        return string;
//    }
}
