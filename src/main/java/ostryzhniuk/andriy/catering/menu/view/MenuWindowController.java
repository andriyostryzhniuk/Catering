package ostryzhniuk.andriy.catering.menu.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ostryzhniuk.andriy.catering.menu.view.dto.DtoMenu;

import java.io.IOException;
import java.math.BigDecimal;

import static ostryzhniuk.andriy.catering.menu.view.ContextMenu.initContextMenu;

public class MenuWindowController extends MenuTableView {

    public BorderPane rootBorderPane;
    private MenuTableView menuTableView = new MenuTableView();
    private TableView<DtoMenu> tableView;

    @FXML
    public void initialize(){
        menuTableView.initialize();
        tableView = menuTableView.getMenuTableView();
        rootBorderPane.setCenter(menuTableView.getTableViewsStackPane());
        initContextMenu(tableView, this);
    }

    public void editRecord() throws IOException {
        TablePosition pos = tableView.getSelectionModel().getSelectedCells().get(0);
        int rowIndex = pos.getRow();
        DtoMenu dtoClient = tableView.getItems().get(rowIndex);

        showEditingRecordWindow(dtoClient.getId());

    }

    public void showEditingRecordWindow(Integer menuIdToUpdate) throws IOException {
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/menu.view/AddingToMenu.fxml"));
        Parent root = fxmlLoader.load();

        AddingToMenuController addingToMenuController = fxmlLoader.getController();
        addingToMenuController.setMenuIdToUpdate(menuIdToUpdate);
        addingToMenuController.setMenuWindowController(this);
        if (menuIdToUpdate != null) {
            TablePosition pos = tableView.getSelectionModel().getSelectedCells().get(0);
            int rowIndex = pos.getRow();
            DtoMenu dtoMenu = tableView.getItems().get(rowIndex);
            addingToMenuController.setTextToTextFields(dtoMenu.getDishesTypeName(), dtoMenu.getName(),
                    dtoMenu.getPrice(), dtoMenu.getMass(), dtoMenu.getIngredients());
        }

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(new Scene(root, 500, 500, Color.rgb(0, 0, 0, 0)));
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.initOwner(tableView.getScene().getWindow());
        primaryStage.showAndWait();
    }
}
