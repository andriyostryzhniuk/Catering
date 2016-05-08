package ostryzhniuk.andriy.catering.menu.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ostryzhniuk.andriy.catering.commands.ClientCommandTypes;
import ostryzhniuk.andriy.catering.menu.view.dto.DtoMenu;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static ostryzhniuk.andriy.catering.client.Client.sendARequestToTheServer;
import static ostryzhniuk.andriy.catering.menu.view.ContextMenu.initContextMenu;

public class MenuWindowController extends MenuTableView {

    public BorderPane rootBorderPane;
    private MenuTableView menuTableView = new MenuTableView();
    private TableView<DtoMenu> tableView;

    @FXML
    public void initialize(){
        menuTableView.initialize();
        tableView = menuTableView.getMenuTableView();
        rootBorderPane.setCenter(menuTableView.getBorderPane());
        initContextMenu(tableView, this);
        initTableReportButton();
    }

    public void editRecord() throws IOException {
        TablePosition pos = tableView.getSelectionModel().getSelectedCells().get(0);
        int rowIndex = pos.getRow();
        DtoMenu dtoClient = tableView.getItems().get(rowIndex);

        showEditingRecordWindow(dtoClient.getId());

    }

    public void removeRecord(){
        TablePosition pos = tableView.getSelectionModel().getSelectedCells().get(0);
        int rowIndex = pos.getRow();
        DtoMenu dtoMenu = tableView.getItems().get(rowIndex);

        List<Object> objectList = new LinkedList<>();
        objectList.add(dtoMenu.getId());
        sendARequestToTheServer(ClientCommandTypes.DELETE_MENU, objectList);
        menuTableView.initTableView();
    }

    public void showEditingRecordWindow(Integer menuIdToUpdate) throws IOException {
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/menu.view/AddingToMenu.fxml"));
        Parent root = fxmlLoader.load();

        AddingToMenuController addingToMenuController = fxmlLoader.getController();
        addingToMenuController.setMenuIdToUpdate(menuIdToUpdate);
        addingToMenuController.setMenuTableView(menuTableView);
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

    private void initTableReportButton(){
        GridPane topGridPane = menuTableView.getTopGridPane();

        Button createTableReportButton = new Button("Створити звіт таблиці");
        createTableReportButton.setMinWidth(155);
        createTableReportButton.setMaxWidth(155);

        createTableReportButton.setOnAction((javafx.event.ActionEvent event) -> {
            ExcelMenuReport excelMenuReport = new ExcelMenuReport();
            excelMenuReport.createTableMenuReport(tableView, menuTableView.getDtoMenuList());
        });

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.SOMETIMES);

        topGridPane.getColumnConstraints().addAll(new ColumnConstraints(), new ColumnConstraints(),
                new ColumnConstraints(), new ColumnConstraints(), new ColumnConstraints(), columnConstraints);
        topGridPane.add(createTableReportButton, 5, 0);
        topGridPane.setHalignment(createTableReportButton, HPos.RIGHT);
    }

}
