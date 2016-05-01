package ostryzhniuk.andriy.catering.ordering.view;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
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

public class OrderingWindowController extends MenuTableView {

    public DatePicker datePicker;
    public ComboBox clientComboBox = new ComboBox();
    public ComboBox comboBoxListener = new ComboBox();
    public TextField costTextField;
    public TextField discountTextField;
    public TextField billTextField;
    public TextField paidTextField;
    public GridPane controlsGridPane;
    public GridPane rootGridPane;
    public ColumnConstraints tableViewColumnConstraints;
    private ostryzhniuk.andriy.catering.ordering.view.ControlsElements controlsElements;
    private Integer orderId = null;

    private MenuTableView menuTableView = new MenuTableView();
    private TableView<DtoMenu> tableView;

    private Map<Integer, Integer> orderingMenuMap = FXCollections.observableHashMap();

    @FXML
    public void initialize(){
        controlsElements = new ostryzhniuk.andriy.catering.ordering.view.ControlsElements(billTextField, clientComboBox, comboBoxListener, costTextField,
                datePicker, discountTextField,  paidTextField);

        controlsElements.initClientComboBox();
        controlsGridPane.add(clientComboBox, 1, 1);

        controlsElements.setDataPickerListener();
        controlsElements.setCostTextFieldListener();
        controlsElements.setDiscountTextFieldListener();
        billTextField.setEditable(false);
        controlsElements.setPaidTextFieldListener();
        billTextField.setTooltip(new Tooltip("До оплати (з урахуванням знижки)"));

        initMenuTableView();
        initTopBorderPane();
        setColsDateProperties();

    }

    public void saveToDB() {
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

    private void initMenuTableView(){
        menuTableView.initialize();
        tableView = menuTableView.getMenuTableView();
        tableView.setEditable(false);
        IntStream.range(3, tableView.getColumns().size()).forEach(i -> {
            tableView.getColumns().get(i).setVisible(false);
        });
        tableView.setMaxWidth(530);
        rootGridPane.add(menuTableView.getBorderPane(), 0, 0);
//        tableViewColumnConstraints.setPrefWidth(tableView.getMaxWidth());
//        tableViewColumnConstraints.setMaxWidth(tableView.getMaxWidth());
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

    public void initTopBorderPane(){
        GridPane topGridPane = new GridPane();

        Label dishesTypeLabel = new Label("Класифікація:");
        topGridPane.add(dishesTypeLabel, 0, 0);
        topGridPane.setMargin(dishesTypeLabel, new Insets(0, 0, 7, 0));

        initDishesTypeComboBox();
        topGridPane.add(menuTableView.getDishesTypeComboBox(), 0, 1);

        Label dishesNameLabel = new Label("Пошук:");
        topGridPane.add(dishesNameLabel, 1, 0);
        topGridPane.setMargin(dishesNameLabel, new Insets(0, 0, 7, 30));

        initDishesNameComboBox();
        topGridPane.add(menuTableView.getDishesNameComboBox(), 1, 1);
        topGridPane.setMargin(menuTableView.getDishesNameComboBox(), new Insets(0, 0, 0, 30));

        menuTableView.getBorderPane().setTop(topGridPane);
        menuTableView.getBorderPane().setAlignment(topGridPane, Pos.TOP_LEFT);
        menuTableView.getBorderPane().setMargin(topGridPane, new Insets(0, 0, 5, 0));

    }

    private void setColsDateProperties() {
        menuTableView.dishesTypeCol.setPercentWidth(206); menuTableView.dishesTypeCol.setMinWidth(206);
        menuTableView.nameCol.setPercentWidth(236); menuTableView.nameCol.setMinWidth(236);
        menuTableView.priceCol.setPercentWidth(86); menuTableView.priceCol.setMinWidth(86);
    }

}
