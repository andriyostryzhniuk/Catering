package ostryzhniuk.andriy.catering.menu.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
import ostryzhniuk.andriy.catering.subsidiary.classes.AlertWindow;
import ostryzhniuk.andriy.catering.subsidiary.classes.EditPanel;

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
        initEditPanel();
        initTableReportButton();
    }

    public void editRecord() throws IOException {
        TablePosition pos;
        try {
            pos = tableView.getSelectionModel().getSelectedCells().get(0);
        } catch (IndexOutOfBoundsException e) {
            AlertWindow alertWindow = new AlertWindow(Alert.AlertType.INFORMATION);
            alertWindow.showEditingInformation();
            return;
        }
        int rowIndex = pos.getRow();
        DtoMenu dtoClient = tableView.getItems().get(rowIndex);

        showEditingRecordWindow(dtoClient.getId());

    }

    public void removeRecord(){
        TablePosition pos;
        try {
            pos = tableView.getSelectionModel().getSelectedCells().get(0);
        } catch (IndexOutOfBoundsException e) {
            AlertWindow alertWindow = new AlertWindow(Alert.AlertType.INFORMATION);
            alertWindow.showEditingInformation();
            return;
        }

        AlertWindow alertWindow = new AlertWindow(Alert.AlertType.WARNING);
        if (! alertWindow.showDeletingWarning()) {
            return;
        }

        int rowIndex = pos.getRow();
        DtoMenu dtoMenu = tableView.getItems().get(rowIndex);

        List<Object> objectList = new LinkedList<>();
        objectList.add(dtoMenu.getId());
        Boolean isSuccessful = (Boolean) sendARequestToTheServer(ClientCommandTypes.DELETE_MENU, objectList).get(0);
        if (! isSuccessful) {
            alertWindow = new AlertWindow(Alert.AlertType.ERROR);
            alertWindow.showDeletingError();
        }
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

        Button tableReportButton = new Button("Створити звіт таблиці");
        tableReportButton.setMinWidth(155);
        tableReportButton.setMaxWidth(155);

        tableReportButton.setOnAction((javafx.event.ActionEvent event) -> {
            ExcelMenuReport excelMenuReport = new ExcelMenuReport();
            excelMenuReport.createTableMenuReport(tableView, menuTableView.getDtoMenuList());
        });

        topGridPane.add(tableReportButton, 6, 0);
        topGridPane.setHalignment(tableReportButton, HPos.RIGHT);
        topGridPane.setMargin(tableReportButton, new Insets(0, 0, 0, 50));
    }

    private void initEditPanel(){
        GridPane topGridPane = menuTableView.getTopGridPane();

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.SOMETIMES);

        topGridPane.getColumnConstraints().add(columnConstraints);

        EditPanel editPanel = new EditPanel(tableView);
        topGridPane.add(editPanel.getContainerGridPane(), 0, 0);

        editPanel.getAddButton().setOnAction((ActionEvent event) -> {
            try {
                showEditingRecordWindow(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        editPanel.getEditButton().setOnAction((ActionEvent event) -> {
            try {
                editRecord();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        editPanel.getDeleteButton().setOnAction((ActionEvent event) -> {
            removeRecord();
        });

    }

}
