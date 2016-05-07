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

        Button rejectSearchingButton = initRejectSearchingButton();
        topGridPane.add(rejectSearchingButton, 2, 0);
        topGridPane.setMargin(rejectSearchingButton, new Insets(0, 0, 0, 5));
    }

    public void initTableView(){
        dtoOrdersList.clear();
        tableView.getTableView().getItems().clear();

        if (datePickerSearch.getValue() == null) {
            dtoOrdersList.addAll(FXCollections.observableArrayList(
                    sendARequestToTheServer(ClientCommandTypes.SELECT_ORDER, new LinkedList<>())));
        } else {
            List<Object> objectList = new LinkedList<>();
            objectList.add(Date.valueOf(datePickerSearch.getValue()));
            dtoOrdersList.addAll(FXCollections.observableArrayList(
                    sendARequestToTheServer(ClientCommandTypes.SELECT_ORDER_BY_DATE, objectList)));
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

//        idCol.setCellFactory(TextFieldTableCell.<T, Integer>forTableColumn(new IntegerStringConverter()));
//        dateCol.setCellFactory(TextFieldTableCell.forTableColumn());
//        clientCol.setCellFactory(TextFieldTableCell.forTableColumn());
//        costCol.setCellFactory(TextFieldTableCell.<T, BigDecimal>forTableColumn(new BigDecimalStringConverter()));
//        discountCol.setCellFactory(TextFieldTableCell.<T, BigDecimal>forTableColumn(new BigDecimalStringConverter()));
//        billCol.setCellFactory(TextFieldTableCell.<T, BigDecimal>forTableColumn(new BigDecimalStringConverter()));
//        paidCol.setCellFactory(TextFieldTableCell.<T, BigDecimal>forTableColumn(new BigDecimalStringConverter()));
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
        TablePosition pos = tableView.getTableView().getSelectionModel().getSelectedCells().get(0);
        int rowIndex = pos.getRow();
        DtoOrder dtoOrder = tableView.getTableView().getItems().get(rowIndex);

        mainWindowController.initOrderingViewForEditing(dtoOrder);
    }

    public void removeRecord(T dtoOrder){
        List<Object> objectList = new LinkedList<>();
        objectList.add(dtoOrder.getId());
        sendARequestToTheServer(ClientCommandTypes.DELETE_ORDER, objectList);
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
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.initOwner(tableView.getTableView().getScene().getWindow());
        primaryStage.showAndWait();
    }

    private void setDatePickerSearchListener() {
        datePickerSearch.setTooltip(new Tooltip("Пошук за датою"));
        datePickerSearch.valueProperty().addListener((obs, oldDate, newDate) -> {
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

    public void createTableReportButton(ActionEvent actionEvent) {
        ExcelOrderReport excelOrderReport = new ExcelOrderReport();
        excelOrderReport.createTableOrderReport(tableView.getTableView(), dtoOrdersList);
    }

    public void createReportButton(ActionEvent actionEvent) {
        ExcelOrderReport excelOrderReport = new ExcelOrderReport();
        if (datePickerSearch.getValue() != null) {
            LocalDate localDate = datePickerSearch.getValue();
            java.util.Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            excelOrderReport.createOrderReport(date);
        } else {
            excelOrderReport.createOrderReport(new java.util.Date());
        }
    }

    public void createMenuButton(ActionEvent actionEvent) {
        ExcelOrderReport excelOrderReport = new ExcelOrderReport();
        if (datePickerSearch.getValue() != null) {
            LocalDate localDate = datePickerSearch.getValue();
            java.util.Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            excelOrderReport.createMenuReport(date);
        } else {
            excelOrderReport.createMenuReport(new java.util.Date());
        }
    }
}
