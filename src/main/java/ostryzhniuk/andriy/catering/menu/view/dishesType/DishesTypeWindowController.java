package ostryzhniuk.andriy.catering.menu.view.dishesType;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ostryzhniuk.andriy.catering.commands.ClientCommandTypes;
import ostryzhniuk.andriy.catering.menu.view.MenuTableView;
import ostryzhniuk.andriy.catering.menu.view.dto.DtoDishesType;
import ostryzhniuk.andriy.catering.subsidiary.classes.AlertWindow;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static ostryzhniuk.andriy.catering.client.Client.sendARequestToTheServer;

public class DishesTypeWindowController<T extends DtoDishesType> {
    public ListView<T> listView;
    private ObservableList<T> dishesTypesList = FXCollections.observableArrayList();

    private MenuTableView menuTableView;

    @FXML
    public void initialize(){
        listView.getStylesheets().add(getClass().getResource("/menu.view/dishesType/ListViewStyle.css").toExternalForm());
        initContextMenu();
        initListView();
    }

    public void initListView(){
        dishesTypesList.clear();
        dishesTypesList.addAll(FXCollections.observableArrayList(
                sendARequestToTheServer(ClientCommandTypes.SELECT_DISHES_TYPE, new LinkedList<>())));
        listView.setItems(dishesTypesList);
    }

    public void add(ActionEvent actionEvent) {
        showEditingRecordWindow(null);
    }

    public void close(ActionEvent actionEvent) {
        Stage stage = (Stage) listView.getScene().getWindow();
        menuTableView.initDishesTypeComboBoxItems();
        menuTableView.initTableView();
        stage.close();
    }

    private void initContextMenu() {
        MenuItem addItem = new MenuItem("Додати");
        addItem.setOnAction((ActionEvent event) -> {
            showEditingRecordWindow(null);
        });

        MenuItem editItem = new MenuItem("Редагувати");
        editItem.setOnAction((ActionEvent event) -> {
            DtoDishesType dtoDishesType = listView.getSelectionModel().getSelectedItem();
            showEditingRecordWindow(dtoDishesType);
        });

        MenuItem removeItem = new MenuItem("Видалити");
        removeItem.setOnAction((ActionEvent event) -> {
            removeRecord();
        });
        final javafx.scene.control.ContextMenu cellMenu = new javafx.scene.control.ContextMenu();
        cellMenu.getItems().addAll(addItem, editItem, removeItem);

        listView.setContextMenu(cellMenu);
    }

    private void showEditingRecordWindow(DtoDishesType dtoDishesType){
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/menu.view/dishesType/PromptAddingWindow.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        PromptAddingWindowController promptAddingWindowController = fxmlLoader.getController();
        promptAddingWindowController.setDishesTypeWindowController(this);
        promptAddingWindowController.setDtoDishesType(dtoDishesType);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(new Scene(root, 300, 162, Color.rgb(0, 0, 0, 0)));
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.initOwner(listView.getScene().getWindow());
        primaryStage.showAndWait();

    }

    public void removeRecord(){
        AlertWindow alertWindow = new AlertWindow(Alert.AlertType.WARNING);
        if (! alertWindow.showDeletingWarning()) {
            return;
        }

        DtoDishesType dtoDishesType = listView.getSelectionModel().getSelectedItem();
        List<Object> objectList = new LinkedList<>();
        objectList.add(dtoDishesType.getId());
        Boolean isSuccessful = (Boolean) sendARequestToTheServer(ClientCommandTypes.DELETE_DISHES_TYPE, objectList).get(0);
        if (! isSuccessful) {
            alertWindow = new AlertWindow(Alert.AlertType.ERROR);
            alertWindow.showDeletingError();
        }
        initListView();
    }

    public void setMenuTableView(MenuTableView menuTableView) {
        this.menuTableView = menuTableView;
    }

}
