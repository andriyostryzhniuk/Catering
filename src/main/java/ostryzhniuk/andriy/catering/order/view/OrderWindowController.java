package ostryzhniuk.andriy.catering.order.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ostryzhniuk.andriy.catering.commands.ClientCommandTypes;
import ostryzhniuk.andriy.catering.main.window.MainWindowController;
import ostryzhniuk.andriy.catering.order.view.dto.DtoOrder;
import ostryzhniuk.andriy.catering.overridden.elements.table.view.CustomTableColumn;
import ostryzhniuk.andriy.catering.overridden.elements.table.view.TableViewHolder;
import ostryzhniuk.andriy.catering.subsidiary.classes.AlertWindow;
import ostryzhniuk.andriy.catering.subsidiary.classes.EditPanel;
import ostryzhniuk.andriy.catering.subsidiary.classes.SetterExcelStyle;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;
import static ostryzhniuk.andriy.catering.client.Client.sendARequestToTheServer;
import static ostryzhniuk.andriy.catering.order.view.ContextMenu.initContextMenu;

public class OrderWindowController<T extends DtoOrder> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderWindowController.class);

    public BorderPane rootBorderPane;
    public StackPane stackPane;
    public DatePicker datePickerSearch;
    public GridPane topGridPane;
    public Button tableReportButton;
    public Button orderingReportButton;
    public Button menuReportButton;
    public TextField textFieldSearch;

    private MainWindowController mainWindowController;

    @FXML
    private TableViewHolder<T> tableView = new TableViewHolder<>();
    public CustomTableColumn<T, Integer> idCol = new CustomTableColumn<>("Номер");
    public CustomTableColumn<T, String> dateCol = new CustomTableColumn<>("Дата");
    public CustomTableColumn<T, String> clientCol = new CustomTableColumn<>("Клієнт");
    public CustomTableColumn<T, BigDecimal> costCol = new CustomTableColumn<>("Вартість");
    public CustomTableColumn<T, BigDecimal> discountCol = new CustomTableColumn<>("Знижка (%)");
    public CustomTableColumn<T, BigDecimal> billCol = new CustomTableColumn<>("До сплати");
    public CustomTableColumn<T, BigDecimal> paidCol = new CustomTableColumn<>("Сплачено");

    private ObservableList<T> dtoOrdersList = FXCollections.observableArrayList();

    @FXML
    public void initialize(){
        fillCols();
        setColsDateProperties();
        fillTableView();
        tableView.getTableView().getStylesheets().add(getClass().getResource("/styles/TableViewStyle.css").toExternalForm());
        tableView.getTableView().setPlaceholder(new Label("Не знайдено жодного замовлення"));
        stackPane.getChildren().add(tableView);
        initContextMenu(tableView.getTableView(), this);
        initTableView();
        setDatePickerSearchListener();
        setTextFieldSearchListener();

        Button rejectSearchingButton = initRejectSearchingButton();
        topGridPane.add(rejectSearchingButton, 5, 0);
        topGridPane.setMargin(rejectSearchingButton, new Insets(0, 0, 0, 5));
        initReportButtonsStyle();
    }

    public void initTableView(){
        dtoOrdersList.clear();
        tableView.getTableView().getItems().clear();

        if (textFieldSearch.getText() != null && ! textFieldSearch.getText().isEmpty()) {
            List<Object> objectList = new LinkedList<>();
            objectList.add(Integer.parseInt(textFieldSearch.getText()));
            dtoOrdersList.addAll(FXCollections.observableArrayList(
                    sendARequestToTheServer(ClientCommandTypes.SELECT_ORDER_BY_ID, objectList)));
        } else if (datePickerSearch.getValue() != null) {
            List<Object> objectList = new LinkedList<>();
            objectList.add(Date.valueOf(datePickerSearch.getValue()));
            dtoOrdersList.addAll(FXCollections.observableArrayList(
                    sendARequestToTheServer(ClientCommandTypes.SELECT_ORDER_BY_DATE, objectList)));
        } else {
            dtoOrdersList.addAll(FXCollections.observableArrayList(
                    sendARequestToTheServer(ClientCommandTypes.SELECT_ORDER, new LinkedList<>())));
        }

        tableView.getTableView().setItems(dtoOrdersList);
    }

    private void fillCols() {
        idCol.setCellValueFactory(new PropertyValueFactory("id"));
        dateCol.setCellValueFactory(new PropertyValueFactory("formatDate"));
        clientCol.setCellValueFactory(new PropertyValueFactory("client"));
        costCol.setCellValueFactory(new PropertyValueFactory("cost"));
        discountCol.setCellValueFactory(new PropertyValueFactory("discount"));
        billCol.setCellValueFactory(new PropertyValueFactory("bill"));
        paidCol.setCellValueFactory(new PropertyValueFactory("paid"));
    }

    public void setColsDateProperties() {
        idCol.setPercentWidth(50); idCol.setMinWidth(50);
        dateCol.setPercentWidth(80); dateCol.setMinWidth(80);
        clientCol.setPercentWidth(200); clientCol.setMinWidth(200);
        costCol.setPercentWidth(100); costCol.setMinWidth(100);
        discountCol.setPercentWidth(100); discountCol.setMinWidth(100);
        billCol.setPercentWidth(100); billCol.setMinWidth(100);
        paidCol.setPercentWidth(100); paidCol.setMinWidth(100);
    }

    public void fillTableView(){
        tableView.getTableView().getColumns().addAll(idCol, dateCol, clientCol, costCol, discountCol, billCol, paidCol);
    }

    public void addRecord(){
        mainWindowController.initOrderingView();
    }

    public void editRecord(){
        TablePosition pos;
        try {
            pos = tableView.getTableView().getSelectionModel().getSelectedCells().get(0);
        } catch (IndexOutOfBoundsException e) {
            AlertWindow alertWindow = new AlertWindow(Alert.AlertType.INFORMATION);
            alertWindow.showEditingInformation();
            return;
        }
        int rowIndex = pos.getRow();
        DtoOrder dtoOrder = tableView.getTableView().getItems().get(rowIndex);

        mainWindowController.initOrderingViewForEditing(dtoOrder);
    }

    public void removeRecord(){
        TablePosition pos;
        try {
            pos = tableView.getTableView().getSelectionModel().getSelectedCells().get(0);
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
        DtoOrder dtoOrder = tableView.getTableView().getItems().get(rowIndex);

        List<Object> objectList = new LinkedList<>();
        objectList.add(dtoOrder.getId());
        Boolean isSuccessful = (Boolean) sendARequestToTheServer(ClientCommandTypes.DELETE_ORDER, objectList).get(0);
        if (! isSuccessful) {
            alertWindow = new AlertWindow(Alert.AlertType.ERROR);
            alertWindow.showDeletingError();
        }
        initTableView();
    }

    public void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }

    public void showOrderDetailsWindow(T dtoOrder) throws IOException {
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/order/view/OrderDetailsWindow.fxml"));
        Parent root = fxmlLoader.load();

        OrderDetailsWindowController orderDetailsWindowController = fxmlLoader.getController();
        orderDetailsWindowController.setOrderId(dtoOrder.getId());
        orderDetailsWindowController.setOrderWindowController(this);
        orderDetailsWindowController.initOrderingTableView();

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(new Scene(root, 500, 500, Color.rgb(0, 0, 0, 0)));
        orderDetailsWindowController.initShortcuts();
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.initOwner(tableView.getTableView().getScene().getWindow());
        primaryStage.showAndWait();
    }

    private void setDatePickerSearchListener() {
        datePickerSearch.setTooltip(new Tooltip("Пошук за датою"));
        datePickerSearch.valueProperty().addListener((obs, oldDate, newDate) -> {
            textFieldSearch.setText(null);
            initTableView();
        });
    }

    private Button initRejectSearchingButton(){
        Button button = new Button();
        Image image = new Image(getClass().getResourceAsStream("/icons/reject_icon.png"));
        button.getStylesheets().add(getClass().getResource("/order/view/RejectSearchingButtonStyle.css").toExternalForm());
        button.setGraphic(new ImageView(image));
        button.setTooltip(new Tooltip("Відмінити пошук"));

        button.setOnAction((javafx.event.ActionEvent event) -> {
            datePickerSearch.setValue(null);
        });

        return button;
    }

    public void actionTableReportButton(ActionEvent actionEvent) {
        createTableReport();
    }

    private void createTableReport() {
        ExcelOrderReport excelOrderReport = new ExcelOrderReport();
        excelOrderReport.createTableOrderReport(tableView.getTableView(), dtoOrdersList);
    }

    public void actionOrderingReportButton(ActionEvent actionEvent) {
        createOrderingReport();
    }

    private void createOrderingReport() {
        ExcelOrderReport excelOrderReport = new ExcelOrderReport();
        if (datePickerSearch.getValue() != null) {
            LocalDate localDate = datePickerSearch.getValue();
            java.util.Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            excelOrderReport.createOrderReport(date);
        } else {
            excelOrderReport.createOrderReport(new java.util.Date());
        }
    }

    public void actionMenuReportButton(ActionEvent actionEvent) {
        createMenuReport();
    }

    private void createMenuReport() {
        ExcelOrderReport excelOrderReport = new ExcelOrderReport();
        if (datePickerSearch.getValue() != null) {
            LocalDate localDate = datePickerSearch.getValue();
            java.util.Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            excelOrderReport.createMenuReport(date);
        } else {
            excelOrderReport.createMenuReport(new java.util.Date());
        }
    }

    public void initEditPanel(MenuItem addMenuItem, MenuItem editMenuItem, MenuItem deleteMenuItem, MenuItem infoMenuItem){
        EditPanel editPanel = new EditPanel(tableView.getTableView(), FXCollections.observableArrayList(
                editMenuItem, deleteMenuItem, infoMenuItem));

        topGridPane.add(editPanel.getContainerGridPane(), 0, 0);

        editPanel.getAddButton().setOnAction((ActionEvent event) -> {
            addRecord();
        });

        editPanel.getEditButton().setOnAction((ActionEvent event) -> {
            editRecord();
        });

        editPanel.getDeleteButton().setOnAction((ActionEvent event) -> {
            removeRecord();
        });

        editPanel.initInfoButton();
        editPanel.getInfoButton().setOnAction((ActionEvent event) -> {
            infoButtonAction();
        });

        setMenuItems(addMenuItem, editMenuItem, deleteMenuItem, infoMenuItem);
    }

    private void setMenuItems(MenuItem addMenuItem, MenuItem editMenuItem, MenuItem deleteMenuItem, MenuItem infoMenuItem){
        addMenuItem.setOnAction((ActionEvent event) -> {
            addRecord();
        });

        editMenuItem.setOnAction((ActionEvent event) -> {
            editRecord();
        });

        deleteMenuItem.setOnAction((ActionEvent event) -> {
            removeRecord();
        });

        infoMenuItem.setOnAction((ActionEvent event) -> {
            infoButtonAction();
        });
    }

    private void infoButtonAction(){
        TablePosition pos;
        try {
            pos = tableView.getTableView().getSelectionModel().getSelectedCells().get(0);
        } catch (IndexOutOfBoundsException e) {
            String headerText = null;
            String contentText = "Не обрано жодного запису для перегляду!\n" +
                    "Будь ласка, оберіть запис з таблиці та повторіть спробу.";
            AlertWindow alertWindow = new AlertWindow(Alert.AlertType.INFORMATION, headerText, contentText);
            alertWindow.showInformation();
            return;
        }
        int rowIndex = pos.getRow();
        T dtoOrder = tableView.getTableView().getItems().get(rowIndex);
        try {
            showOrderDetailsWindow(dtoOrder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initReportButtonsStyle(){
        SetterExcelStyle setterExcelStyle = new SetterExcelStyle();
        setterExcelStyle.setStyle(tableReportButton, "Створити звіт таблиці");
        setterExcelStyle.setStyle(orderingReportButton, "Створити звіт замовлень");
        setterExcelStyle.setStyle(menuReportButton, "Створити звіт кількості замовлених страв");
    }

    private void setTextFieldSearchListener() {
        textFieldSearch.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        textFieldSearch.setTooltip(new Tooltip("Пошук за номером замовлення"));
        textFieldSearch.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER)  {
                if (textFieldSearchValidation()) {
                    datePickerSearch.setValue(null);
                    initTableView();
                }
            }
        });

        textFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            textFieldSearchValidation();
        });
    }

    private boolean textFieldSearchValidation(){
        try {
            if (textFieldSearch.getText() != null && ! textFieldSearch.getText().isEmpty()) {
                Integer.parseInt(textFieldSearch.getText());
            } else {
                textFieldSearch.getStyleClass().remove("warning");
            }
        } catch (NumberFormatException e) {
            if (! textFieldSearch.getStyleClass().contains("warning")) {
                textFieldSearch.getStyleClass().add("warning");
            }
            return false;
        }
        return true;
    }

    public void initReportMenu(Menu reportMenu) {
        MenuItem tableReportMenuItem = new MenuItem("Створити звіт таблиці");
        tableReportMenuItem.setOnAction((ActionEvent event) -> {
            createTableReport();
        });

        MenuItem orderingReportMenuItem = new MenuItem("Створити звіт замовлень");
        orderingReportMenuItem.setOnAction((ActionEvent event) -> {
            createOrderingReport();
        });

        MenuItem menuReportMenuItem = new MenuItem("Створити звіт меню");
        menuReportMenuItem.setOnAction((ActionEvent event) -> {
            createMenuReport();
        });

        reportMenu.getItems().addAll(tableReportMenuItem, orderingReportMenuItem, menuReportMenuItem);
    }

}
