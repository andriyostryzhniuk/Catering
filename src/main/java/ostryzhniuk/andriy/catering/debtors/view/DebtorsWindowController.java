package ostryzhniuk.andriy.catering.debtors.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ostryzhniuk.andriy.catering.commands.ClientCommandTypes;
import ostryzhniuk.andriy.catering.debtors.view.dto.DtoDebtors;
import ostryzhniuk.andriy.catering.overridden.elements.table.view.CustomTableColumn;
import ostryzhniuk.andriy.catering.overridden.elements.table.view.TableViewHolder;
import ostryzhniuk.andriy.catering.subsidiary.classes.SetterExcelStyle;

import java.math.BigDecimal;
import java.util.LinkedList;
import static ostryzhniuk.andriy.catering.client.Client.sendARequestToTheServer;

public class DebtorsWindowController<T extends DtoDebtors> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DebtorsWindowController.class);

    public BorderPane rootBorderPane;
    public StackPane stackPane;
    public Button tableReportButton;

    private TableViewHolder<T> tableView = new TableViewHolder<>();
    public CustomTableColumn<T, String> clientNameCol = new CustomTableColumn<>("Клієнт");
    public CustomTableColumn<T, String> telephoneNumberCol = new CustomTableColumn<>("Контактний телефон");
    public CustomTableColumn<T, String> contactPersonCol = new CustomTableColumn<>("Контактна особа");
    public CustomTableColumn<T, Integer> orderIdCol = new CustomTableColumn<>("Замовлення №");
    public CustomTableColumn<T, String> dateCol = new CustomTableColumn<>("Дата");
    public CustomTableColumn<T, BigDecimal> costCol = new CustomTableColumn<>("Вартість");
    public CustomTableColumn<T, BigDecimal> discountCol = new CustomTableColumn<>("Знижка");
    public CustomTableColumn<T, BigDecimal> billCol = new CustomTableColumn<>("До сплати");
    public CustomTableColumn<T, BigDecimal> paidCol = new CustomTableColumn<>("Сплачено");
    public CustomTableColumn<T, BigDecimal> debtCol = new CustomTableColumn<>("Борг");

    private ObservableList<T> dtoDebtorsList = FXCollections.observableArrayList();

    @FXML
    public void initialize(){
        fillCols();
        setColsDateProperties();
        fillTableView();
        tableView.getTableView().getStylesheets().add(getClass().getResource("/styles/TableViewStyle.css").toExternalForm());
        tableView.getTableView().setEditable(true);
        stackPane.getChildren().addAll(tableView);
        initReportButtonsStyle();
        initTableView();
    }

    public void initTableView(){
        dtoDebtorsList.clear();
        tableView.getTableView().getItems().clear();

        dtoDebtorsList.addAll(FXCollections.observableArrayList(
                sendARequestToTheServer(ClientCommandTypes.SELECT_DEBTORS, new LinkedList<>())));
        tableView.getTableView().setItems(dtoDebtorsList);
    }

    private void fillCols() {
        clientNameCol.setCellValueFactory(new PropertyValueFactory("clientName"));
        telephoneNumberCol.setCellValueFactory(new PropertyValueFactory("telephoneNumber"));
        contactPersonCol.setCellValueFactory(new PropertyValueFactory("contactPerson"));
        orderIdCol.setCellValueFactory(new PropertyValueFactory("orderId"));
        dateCol.setCellValueFactory(new PropertyValueFactory("formatDate"));
        costCol.setCellValueFactory(new PropertyValueFactory("cost"));
        discountCol.setCellValueFactory(new PropertyValueFactory("discount"));
        billCol.setCellValueFactory(new PropertyValueFactory("bill"));
        paidCol.setCellValueFactory(new PropertyValueFactory("paid"));
        debtCol.setCellValueFactory(new PropertyValueFactory("debt"));

        clientNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        telephoneNumberCol.setCellFactory(TextFieldTableCell.forTableColumn());
        contactPersonCol.setCellFactory(TextFieldTableCell.forTableColumn());
        orderIdCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        dateCol.setCellFactory(TextFieldTableCell.forTableColumn());
        costCol.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter()));
        discountCol.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter()));
        billCol.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter()));
        paidCol.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter()));
        debtCol.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter()));
    }

    private void setColsDateProperties() {
        clientNameCol.setPercentWidth(150); clientNameCol.setMinWidth(150);
        telephoneNumberCol.setPercentWidth(100); telephoneNumberCol.setMinWidth(100);
        contactPersonCol.setPercentWidth(150); contactPersonCol.setMinWidth(150);
        orderIdCol.setPercentWidth(80); orderIdCol.setMinWidth(80);
        dateCol.setPercentWidth(80); dateCol.setMinWidth(80);
        costCol.setPercentWidth(60); costCol.setMinWidth(60);
        discountCol.setPercentWidth(60); discountCol.setMinWidth(60);
        billCol.setPercentWidth(60); billCol.setMinWidth(60);
        paidCol.setPercentWidth(60); paidCol.setMinWidth(60);
        debtCol.setPercentWidth(80); debtCol.setMinWidth(80);
    }

    public void fillTableView(){
        tableView.getTableView().getColumns().addAll(clientNameCol, telephoneNumberCol, contactPersonCol, orderIdCol,
                dateCol, costCol, discountCol, billCol, paidCol, debtCol);
    }

    public void createTableReportButton(ActionEvent actionEvent) {
        ExcelDebtorsReport excelDebtorsReport = new ExcelDebtorsReport();
        excelDebtorsReport.createTableDebtorsReport(tableView.getTableView(), dtoDebtorsList);
    }

    private void initReportButtonsStyle(){
        SetterExcelStyle setterExcelStyle = new SetterExcelStyle();
        setterExcelStyle.setStyle(tableReportButton, "Створити звіт таблиці");
    }
}
