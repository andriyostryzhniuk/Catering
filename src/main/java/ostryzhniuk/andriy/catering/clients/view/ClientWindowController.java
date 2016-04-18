package ostryzhniuk.andriy.catering.clients.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ostryzhniuk.andriy.catering.commands.ClientCommandTypes;
import ostryzhniuk.andriy.catering.order.view.dto.DtoOrder;
import ostryzhniuk.andriy.catering.overridden.elements.table.view.CustomTableColumn;
import ostryzhniuk.andriy.catering.overridden.elements.table.view.TableViewHolder;

import java.math.BigDecimal;
import java.util.LinkedList;

import static ostryzhniuk.andriy.catering.client.Client.sendARequestToTheServer;

public class ClientWindowController<T extends DtoOrder> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientWindowController.class);

    public BorderPane rootBorderPane;
    public StackPane stackPane;

    public Button saveButton;
    public Button escapeButton;

    @FXML
    private TableViewHolder<T> tableView = new TableViewHolder<>();
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
        tableView.getTableView().getStylesheets().add(getClass().getResource("/order/view/TableViewStyle.css").toExternalForm());
        stackPane.getChildren().add(tableView);
        initTableView();

    }

    public void initTableView(){
        dtoOrdersList.clear();
        tableView.getTableView().getItems().clear();

        dtoOrdersList.addAll(FXCollections.observableArrayList(
                sendARequestToTheServer(ClientCommandTypes.SELECT_ORDER, new LinkedList<>())));
        tableView.getTableView().setItems(dtoOrdersList);
    }

    private void fillCols() {
        dateCol.setCellValueFactory(new PropertyValueFactory("formatDate"));
        clientCol.setCellValueFactory(new PropertyValueFactory("client"));
        costCol.setCellValueFactory(new PropertyValueFactory("cost"));
        discountCol.setCellValueFactory(new PropertyValueFactory("discount"));
        billCol.setCellValueFactory(new PropertyValueFactory("bill"));
        paidCol.setCellValueFactory(new PropertyValueFactory("paid"));
    }

    public void setColsDateProperties() {
        dateCol.setPercentWidth(80); dateCol.setMinWidth(80);
        clientCol.setPercentWidth(200); clientCol.setMinWidth(200);
        costCol.setPercentWidth(100); costCol.setMinWidth(100);
        discountCol.setPercentWidth(100); discountCol.setMinWidth(100);
        billCol.setPercentWidth(100); billCol.setMinWidth(100);
        paidCol.setPercentWidth(100); paidCol.setMinWidth(100);
    }

    public void fillTableView(){
        tableView.getTableView().getColumns().addAll(dateCol, clientCol, costCol, discountCol, billCol, paidCol);
    }

//    public void saveToDB(ActionEvent actionEvent) {

//    }
//
//    public void escape(ActionEvent actionEvent) {
//        orderId = null;
//        controlsElements.clear();
//    }
//
//    public void editRecord(){
//        TablePosition pos = tableView.getTableView().getSelectionModel().getSelectedCells().get(0);
//        int rowIndex = pos.getRow();
//        DtoOrder dtoOrder = tableView.getTableView().getItems().get(rowIndex);
//
//        java.util.Date inputDate = dtoOrder.getDate();
//        LocalDate localDate = inputDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//        datePicker.setValue(localDate);
//
//        clientComboBox.setValue(dtoOrder.getClient());
//        comboBoxListener.setValue(dtoOrder.getClient());
//
//        costTextField.setText(dtoOrder.getCost().toString());
//        controlsElements.costTextFieldValidation();
//
//        discountTextField.setText(dtoOrder.getDiscount().toString());
//        controlsElements.discountTextFieldValidation();
//
//        paidTextField.setText(dtoOrder.getPaid().toString());
//        controlsElements.paidTextFieldValidation();
//
//        orderId = dtoOrder.getId();
//    }
//
//    public void removeRecord(){
//        TablePosition pos = tableView.getTableView().getSelectionModel().getSelectedCells().get(0);
//        int rowIndex = pos.getRow();
//        DtoOrder dtoOrder = tableView.getTableView().getItems().get(rowIndex);
//
//        List<Object> objectList = new LinkedList<>();
//        objectList.add(dtoOrder.getId());
//        sendARequestToTheServer(ClientCommandTypes.DELETE_ORDER, objectList);
//        initTableView();
//    }

}
