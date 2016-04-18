package ostryzhniuk.andriy.catering.clients.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ostryzhniuk.andriy.catering.clients.view.dto.DtoClient;
import ostryzhniuk.andriy.catering.commands.ClientCommandTypes;
import ostryzhniuk.andriy.catering.overridden.elements.table.view.CustomTableColumn;
import ostryzhniuk.andriy.catering.overridden.elements.table.view.TableViewHolder;
import java.math.BigDecimal;
import java.util.LinkedList;

import static ostryzhniuk.andriy.catering.client.Client.sendARequestToTheServer;

public class ClientWindowController<T extends DtoClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientWindowController.class);

    public BorderPane rootBorderPane;
    public StackPane stackPane;

    public Button saveButton;
    public Button escapeButton;

    @FXML
    private TableViewHolder<T> tableView = new TableViewHolder<>();
    public CustomTableColumn<T, String> nameCol = new CustomTableColumn<>("Назва");
    public CustomTableColumn<T, String> addressCol = new CustomTableColumn<>("Адреса");
    public CustomTableColumn<T, String> telephoneCol = new CustomTableColumn<>("Телефон");
    public CustomTableColumn<T, String> contactPersonCol = new CustomTableColumn<>("Контактна особа");
    public CustomTableColumn<T, BigDecimal> discountCol = new CustomTableColumn<>("Знижка (%)");
    public CustomTableColumn<T, String> emailCol = new CustomTableColumn<>("E-mail");
    public CustomTableColumn<T, Integer> icqCol = new CustomTableColumn<>("ICQ");
    public CustomTableColumn<T, String> skypeCol = new CustomTableColumn<>("Skype");

    private ObservableList<T> dtoClientsList = FXCollections.observableArrayList();

    @FXML
    public void initialize(){
        fillCols();
        setColsDateProperties();
        fillTableView();
        tableView.getTableView().getStylesheets().add(getClass().getResource("/order/view/TableViewStyle.css").toExternalForm());
        tableView.getTableView().setEditable(true);
        stackPane.getChildren().add(tableView);
        initTableView();

    }

    public void initTableView(){
        dtoClientsList.clear();
        tableView.getTableView().getItems().clear();

        dtoClientsList.addAll(FXCollections.observableArrayList(
                sendARequestToTheServer(ClientCommandTypes.SELECT_CLIENTS, new LinkedList<>())));
        tableView.getTableView().setItems(dtoClientsList);
    }

    private void fillCols() {
        nameCol.setCellValueFactory(new PropertyValueFactory("name"));
//        nameCol.setOnEditCommit(event -> {
//            final TableColumn.CellEditEvent<T, String> t = (TableColumn.CellEditEvent) event;
//            ((T) t.getTableView().getItems().get(
//                    t.getTablePosition().getRow())
//            ).setName(t.getNewValue());
//        });

        addressCol.setCellValueFactory(new PropertyValueFactory("address"));
        telephoneCol.setCellValueFactory(new PropertyValueFactory("telephoneNumber"));
        contactPersonCol.setCellValueFactory(new PropertyValueFactory("contactPerson"));
        discountCol.setCellValueFactory(new PropertyValueFactory("discount"));
        emailCol.setCellValueFactory(new PropertyValueFactory("email"));
        icqCol.setCellValueFactory(new PropertyValueFactory("icq"));
        skypeCol.setCellValueFactory(new PropertyValueFactory("skype"));

        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        addressCol.setCellFactory(TextFieldTableCell.forTableColumn());
        telephoneCol.setCellFactory(TextFieldTableCell.forTableColumn());
        contactPersonCol.setCellFactory(TextFieldTableCell.forTableColumn());
        discountCol.setCellFactory(TextFieldTableCell.<T, BigDecimal>forTableColumn(new BigDecimalStringConverter()));
        emailCol.setCellFactory(TextFieldTableCell.forTableColumn());
        icqCol.setCellFactory(TextFieldTableCell.<T, Integer>forTableColumn(new IntegerStringConverter()));
        skypeCol.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    public void setColsDateProperties() {
        nameCol.setPercentWidth(170); nameCol.setMinWidth(170);
        addressCol.setPercentWidth(220); addressCol.setMinWidth(220);
        telephoneCol.setPercentWidth(80); telephoneCol.setMinWidth(80);
        contactPersonCol.setPercentWidth(170); contactPersonCol.setMinWidth(170);
        discountCol.setPercentWidth(70); discountCol.setMinWidth(70);
        emailCol.setPercentWidth(100); emailCol.setMinWidth(100);
        icqCol.setPercentWidth(70); icqCol.setMinWidth(70);
        skypeCol.setPercentWidth(100); skypeCol.setMinWidth(100);
    }

    public void fillTableView(){
        tableView.getTableView().getColumns().addAll(nameCol, addressCol, telephoneCol, contactPersonCol,
                discountCol, emailCol, icqCol, skypeCol);
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
