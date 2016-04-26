package ostryzhniuk.andriy.catering.menu.view.dishesType;


import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import ostryzhniuk.andriy.catering.commands.ClientCommandTypes;
import ostryzhniuk.andriy.catering.menu.view.MenuWindowController;
import ostryzhniuk.andriy.catering.menu.view.dto.DtoDishesType;
import ostryzhniuk.andriy.catering.menu.view.dto.DtoMenu;

import java.io.IOException;
import java.util.LinkedList;

import static ostryzhniuk.andriy.catering.client.Client.sendARequestToTheServer;

public class DishesTypeWindowController<T extends DtoDishesType> {
    public ListView<T> listView;
    private ObservableList<T> dishesTypesList = FXCollections.observableArrayList();

    @FXML
    public void initialize(){
        initContextMenu();
        dishesTypesList.addAll(FXCollections.observableArrayList(
                sendARequestToTheServer(ClientCommandTypes.SELECT_DISHES_TYPE, new LinkedList<>())));
        listView.setItems(dishesTypesList);
    }

    public void add(ActionEvent actionEvent) {

    }

    public void close(ActionEvent actionEvent) {
        Stage stage = (Stage) listView.getScene().getWindow();
        stage.close();
    }

    public void initContextMenu() {
        MenuItem addItem = new MenuItem("Додати");
        addItem.setOnAction((ActionEvent event) -> {
//                    try {
//                        menuWindowController.showEditingRecordWindow(null);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
        });

        MenuItem editItem = new MenuItem("Редагувати");
        editItem.setOnAction((ActionEvent event) -> {
//                    try {
//                        menuWindowController.editRecord();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
        });

        MenuItem removeItem = new MenuItem("Видалити");
        removeItem.setOnAction((ActionEvent event) -> {
//                    menuWindowController.removeRecord();
        });
        final javafx.scene.control.ContextMenu cellMenu = new javafx.scene.control.ContextMenu();
        cellMenu.getItems().addAll(addItem, editItem, removeItem);

        listView.setContextMenu(cellMenu);
    }
}
