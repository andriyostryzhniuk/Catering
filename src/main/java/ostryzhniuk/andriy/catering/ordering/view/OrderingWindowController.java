package ostryzhniuk.andriy.catering.ordering.view;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ostryzhniuk.andriy.catering.commands.ClientCommandTypes;
import ostryzhniuk.andriy.catering.menu.view.MenuTableView;
import ostryzhniuk.andriy.catering.menu.view.dto.DtoMenu;
import ostryzhniuk.andriy.catering.order.view.ControlsElements;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static ostryzhniuk.andriy.catering.client.Client.sendARequestToTheServer;

/**
 * Created by Andriy on 04/26/2016.
 */
public class OrderingWindowController {

    public DatePicker datePicker;
    public ComboBox clientComboBox = new ComboBox();
    public ComboBox comboBoxListener = new ComboBox();
    public TextField costTextField;
    public TextField discountTextField;
    public TextField billTextField;
    public TextField paidTextField;
    public Button saveButton;
    public Button escapeButton;
    public GridPane controlsGridPane;
    public GridPane rootGridPane;
    private ostryzhniuk.andriy.catering.order.view.ControlsElements controlsElements;
    private Integer orderId = null;

    private MenuTableView menuTableView = new MenuTableView();
    private TableView<DtoMenu> tableView;

    private Map<Integer, Integer> orderingMenuMap = FXCollections.observableHashMap();

    @FXML
    public void initialize(){
        controlsElements = new ControlsElements(billTextField, clientComboBox, comboBoxListener, costTextField,
                datePicker, discountTextField,  paidTextField);

        controlsElements.initClientComboBox();
        controlsGridPane.add(clientComboBox, 1, 2);

        controlsElements.setDataPickerListener();
        controlsElements.setCostTextFieldListener();
        controlsElements.setDiscountTextFieldListener();
        billTextField.setEditable(false);
        controlsElements.setPaidTextFieldListener();
        billTextField.setTooltip(new Tooltip("До оплати (з урахуванням знижки)"));

        initMenuTableView();

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
            controlsElements.clear();
        }
    }

    public void escape(ActionEvent actionEvent) {
        orderId = null;
        controlsElements.clear();
    }

    private void initMenuTableView(){
        menuTableView.initialize();
        tableView = menuTableView.getMenuTableView();
        tableView.setEditable(false);
        IntStream.range(3, tableView.getColumns().size()).forEach(i -> {
            tableView.getColumns().get(i).setVisible(false);
        });
        tableView.setMaxWidth(502);
        rootGridPane.add(menuTableView.getBorderPane(), 2, 0);
        setOnDoubleClickToTableView();
    }

    private void setOnDoubleClickToTableView(){
        tableView.setRowFactory(tv -> {
            TableRow<DtoMenu> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    DtoMenu rowData = row.getItem();
                    PromptNumberOfServings promptNumberOfServings = new PromptNumberOfServings(rowData.getName());
                    Integer numberOfServings = promptNumberOfServings.showPrompt(rootGridPane.getScene().getWindow());
                    System.out.println("nameOfDish: " + rowData.getId() + "  numberOfServings: " + numberOfServings);

                }
            });
            return row ;
        });
    }
}
