package ostryzhniuk.andriy.catering.order.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ostryzhniuk.andriy.catering.commands.ClientCommandTypes;
import ostryzhniuk.andriy.catering.order.view.dto.DtoOrder;
import ostryzhniuk.andriy.catering.overridden.elements.combo.box.AutoCompleteComboBoxListener;
import ostryzhniuk.andriy.catering.overridden.elements.table.view.CustomTableColumn;
import ostryzhniuk.andriy.catering.overridden.elements.table.view.TableViewHolder;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static ostryzhniuk.andriy.catering.client.Client.sendARequestToTheServer;

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
    public CustomTableColumn<T, BigDecimal> discountCol = new CustomTableColumn<>("Знижка");
    public CustomTableColumn<T, BigDecimal> billCol = new CustomTableColumn<>("До сплати");
    public CustomTableColumn<T, BigDecimal> paidCol = new CustomTableColumn<>("Сплачено");

    public ObservableList<T> dtoOrdersList = FXCollections.observableArrayList();

    @FXML
    public void initialize(){
        fillCols();
        setColsDateProperties();
        fillTableView();
        stackPane.getChildren().add(tableView);
        initTableView();
        tableView.getTableView().getStylesheets().add(getClass().getResource("/order/view/TableViewStyle.css").toExternalForm());

        clientComboBox = initClientComboBox();
        controlsGridPane.add(clientComboBox, 2, 1);
        controlsGridPane.setMargin(clientComboBox, new Insets(0, 20, 0, 0));
    }

    public void initTableView(){
        dtoOrdersList.clear();
        dtoOrdersList.addAll(FXCollections.observableArrayList(
                sendARequestToTheServer(ClientCommandTypes.SELECT_ORDER, new LinkedList<>())));

        tableView.getTableView().getItems().clear();
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

    public ComboBox initClientComboBox() {
        ComboBox comboBox = new ComboBox();

        comboBox.getStylesheets().add(getClass().getResource("/order/view/ComboBoxStyle.css").toExternalForm());
        comboBox.setTooltip(new Tooltip("Вибрати клієнта"));
        comboBox.setPromptText("Клієнт");

        ObservableList<String> observableList = FXCollections.observableArrayList();
        observableList.addAll(FXCollections.observableArrayList(
                sendARequestToTheServer(ClientCommandTypes.SELECT_CLIENT_NAMES, new LinkedList<>())));

        comboBox.setItems(observableList);

        new AutoCompleteComboBoxListener<>(comboBox, comboBoxListener);

        comboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                final ListCell<String> cell = new ListCell<String>() {
                    {
                        super.setOnMousePressed(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
//                                mouse pressed
                                comboBoxListener.setValue(comboBox.getValue());
                            }
                        });
                    }

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);
                    }
                };
                return cell;
            }
        });

        comboBoxListener.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue observableValue, String oldValue, String newValue) {
//                change detected
                if (newValue != null) {
                    comboBox.getStyleClass().remove("warning");
//                    searchInListView();
//                    comboBoxListener.setValue(null);
                }
            }
        });
        return comboBox;
    }

    public void saveToDB(ActionEvent actionEvent) {
        List<Object> objectList = new LinkedList<>();

        System.out.println(Date.valueOf(datePicker.getValue()).toString());
        objectList.add(Date.valueOf(datePicker.getValue()).toString());

        List<Object> clientNameList = new LinkedList<>();
        clientNameList.add(comboBoxListener.getValue());
        Integer clientId = (Integer) sendARequestToTheServer(ClientCommandTypes.SELECT_CLIENT_ID, clientNameList).get(0);
        objectList.add(clientId);

        objectList.add(new BigDecimal(costTextField.getText()));
        objectList.add(new BigDecimal(discountTextField.getText()));
        objectList.add(new BigDecimal(paidTextField.getText()));

        sendARequestToTheServer(ClientCommandTypes.INSERT_ORDER, objectList);
    }
}
