package ostryzhniuk.andriy.catering.order.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.converter.BigDecimalStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ostryzhniuk.andriy.catering.commands.ClientCommandTypes;
import ostryzhniuk.andriy.catering.order.view.dto.DtoOrder;
import ostryzhniuk.andriy.catering.overridden.elements.table.view.CustomTableColumn;
import ostryzhniuk.andriy.catering.overridden.elements.table.view.TableViewHolder;
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

    public GridPane controlsGridPane;
    public DatePicker datePicker;
    public ComboBox clientComboBox = new ComboBox();
    public ComboBox comboBoxListener = new ComboBox();
    public TextField costTextField;
    public TextField discountTextField;
    public TextField billTextField;
    public TextField paidTextField;
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
    private ControlsElements controlsElements;
    private Integer orderId = null;

    @FXML
    public void initialize(){
        fillCols();
        setColsDateProperties();
        fillTableView();
        tableView.getTableView().getStylesheets().add(getClass().getResource("/order/view/TableViewStyle.css").toExternalForm());
        tableView.getTableView().setEditable(true);
        stackPane.getChildren().add(tableView);
        initContextMenu(tableView.getTableView(), this);
        initTableView();

        controlsElements = new ControlsElements(billTextField, clientComboBox, comboBoxListener, costTextField,
                datePicker, discountTextField,  paidTextField);

        controlsElements.initClientComboBox();
        controlsGridPane.add(clientComboBox, 2, 1);
        controlsGridPane.setMargin(clientComboBox, new Insets(0, 20, 0, 0));

        controlsElements.setDataPickerListener();
        controlsElements.setCostTextFieldListener();
        controlsElements.setDiscountTextFieldListener();
        billTextField.setEditable(false);
        controlsElements.setPaidTextFieldListener();
        billTextField.setTooltip(new Tooltip("До оплати (з урахуванням знижки)"));
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

        dateCol.setCellFactory(TextFieldTableCell.forTableColumn());
        clientCol.setCellFactory(TextFieldTableCell.forTableColumn());
        costCol.setCellFactory(TextFieldTableCell.<T, BigDecimal>forTableColumn(new BigDecimalStringConverter()));
        discountCol.setCellFactory(TextFieldTableCell.<T, BigDecimal>forTableColumn(new BigDecimalStringConverter()));
        billCol.setCellFactory(TextFieldTableCell.<T, BigDecimal>forTableColumn(new BigDecimalStringConverter()));
        paidCol.setCellFactory(TextFieldTableCell.<T, BigDecimal>forTableColumn(new BigDecimalStringConverter()));
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

    public void saveToDB(ActionEvent actionEvent) {
        boolean isWarning = false;
        List<Object> objectList = new LinkedList<>();

        try {
            objectList.add(Date.valueOf(datePicker.getValue()).toString());
        } catch (NullPointerException e) {
            isWarning = true;
            if (!datePicker.getStyleClass().contains("warning")) {
                datePicker.getStyleClass().add("warning");
            }
        }

        List<Object> clientNameList = new LinkedList<>();
        clientNameList.add(comboBoxListener.getValue());
        Integer clientId = null;
        try {
            clientId = (Integer) sendARequestToTheServer(ClientCommandTypes.SELECT_CLIENT_ID, clientNameList).get(0);
            if (clientComboBox.getStyleClass().contains("warning")) {
                isWarning = true;
            }
        } catch (IndexOutOfBoundsException e) {
            isWarning = true;
            if (!clientComboBox.getStyleClass().contains("warning")) {
                clientComboBox.getStyleClass().add("warning");
            }
        }
        objectList.add(clientId);

        try {
            objectList.add(new BigDecimal(costTextField.getText()));
            if (costTextField.getStyleClass().contains("warning")) {
                isWarning = true;
            }
        } catch (NumberFormatException e) {
            isWarning = true;
            if (!costTextField.getStyleClass().contains("warning")) {
                costTextField.getStyleClass().add("warning");
            }
        }

        try {
            objectList.add(new BigDecimal(discountTextField.getText()));
            if (discountTextField.getStyleClass().contains("warning")) {
                isWarning = true;
            }
        } catch (NumberFormatException e) {
            if (discountTextField.getText().isEmpty()) {
                objectList.add(new BigDecimal(0));
            } else {
                isWarning = true;
                if (!discountTextField.getStyleClass().contains("warning")) {
                    discountTextField.getStyleClass().add("warning");
                }
            }
        }

        try {
            objectList.add(new BigDecimal(paidTextField.getText()));
            if (paidTextField.getStyleClass().contains("warning")) {
                isWarning = true;
            }
        } catch (NumberFormatException e) {
            if (paidTextField.getText().isEmpty()) {
                objectList.add(new BigDecimal(0));
            } else {
                isWarning = true;
                if (!paidTextField.getStyleClass().contains("warning")) {
                    paidTextField.getStyleClass().add("warning");
                }
            }
        }

        if (!isWarning) {
            if (orderId == null) {
                sendARequestToTheServer(ClientCommandTypes.INSERT_ORDER, objectList);
            } else {
                objectList.add(orderId);
                sendARequestToTheServer(ClientCommandTypes.UPDATE_ORDER, objectList);
            }
            orderId = null;
            initTableView();
            controlsElements.clear();
        }
    }

    public void escape(ActionEvent actionEvent) {
        orderId = null;
        controlsElements.clear();
    }

    public void editRecord(){
        TablePosition pos = tableView.getTableView().getSelectionModel().getSelectedCells().get(0);
        int rowIndex = pos.getRow();
        DtoOrder dtoOrder = tableView.getTableView().getItems().get(rowIndex);

        java.util.Date inputDate = dtoOrder.getDate();
        LocalDate localDate = inputDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        datePicker.setValue(localDate);

        clientComboBox.setValue(dtoOrder.getClient());
        comboBoxListener.setValue(dtoOrder.getClient());

        costTextField.setText(dtoOrder.getCost().toString());
        controlsElements.costTextFieldValidation();

        discountTextField.setText(dtoOrder.getDiscount().toString());
        controlsElements.discountTextFieldValidation();

        paidTextField.setText(dtoOrder.getPaid().toString());
        controlsElements.paidTextFieldValidation();

        orderId = dtoOrder.getId();
    }

    public void removeRecord(){
        TablePosition pos = tableView.getTableView().getSelectionModel().getSelectedCells().get(0);
        int rowIndex = pos.getRow();
        DtoOrder dtoOrder = tableView.getTableView().getItems().get(rowIndex);

        List<Object> objectList = new LinkedList<>();
        objectList.add(dtoOrder.getId());
        sendARequestToTheServer(ClientCommandTypes.DELETE_ORDER, objectList);
        initTableView();
    }

}
