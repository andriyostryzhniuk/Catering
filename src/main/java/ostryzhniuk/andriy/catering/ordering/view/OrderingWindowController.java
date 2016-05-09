package ostryzhniuk.andriy.catering.ordering.view;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ostryzhniuk.andriy.catering.commands.ClientCommandTypes;
import ostryzhniuk.andriy.catering.menu.view.AddingToMenuController;
import ostryzhniuk.andriy.catering.menu.view.MenuTableView;
import ostryzhniuk.andriy.catering.menu.view.dto.DtoMenu;
import ostryzhniuk.andriy.catering.order.view.dto.DtoOrder;
import ostryzhniuk.andriy.catering.ordering.view.dto.DtoOrdering;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import static ostryzhniuk.andriy.catering.client.Client.sendARequestToTheServer;
import static ostryzhniuk.andriy.catering.ordering.view.ContextMenu.initOrderingContextMenu;

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
    public Label orderIdLabel;
    public TextField sumPriceTextField;
    private ostryzhniuk.andriy.catering.ordering.view.ControlsElements controlsElements;
    private Integer orderId = null;

    private MenuTableView menuTableView = new MenuTableView();
    private TableView<DtoMenu> tableView;

    public TableView<DtoOrdering> orderingTableView;
    public TableColumn orderingDishesNameCol;
    public TableColumn orderingNumberOfServingsCol;
    public TableColumn sumPriceCol;
    private ObservableList<DtoOrdering> orderingObservableList = FXCollections.observableArrayList();
    private List<DtoOrdering> initialDataList = new LinkedList<>();
    private DtoOrder currentDataOrder;

    @FXML
    public void initialize(){
        orderIdLabel.setText("-");
        orderingTableView.setPlaceholder(new Label("Не замовлено жодної страви"));

        controlsElements = new ostryzhniuk.andriy.catering.ordering.view.ControlsElements(billTextField, clientComboBox, comboBoxListener, costTextField,
                datePicker, discountTextField,  paidTextField);

        controlsElements.initClientComboBox();
        controlsGridPane.add(clientComboBox, 2, 1);

        controlsElements.setDataPickerListener();
        controlsElements.setCostTextFieldListener();
        controlsElements.setDiscountTextFieldListener();
        billTextField.setEditable(false);
        controlsElements.setPaidTextFieldListener();
        billTextField.setTooltip(new Tooltip("До оплати (з урахуванням знижки)"));

        initMenuTableView();
        initTopBorderPane();
        setColsDateProperties();
        initOrderingContextMenu(orderingTableView, this);
        initOrderingTableView();

    }

    public boolean saveToDB() {
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

        if (! isWarning) {
            if (orderId == null) {
                orderId = (Integer) sendARequestToTheServer(ClientCommandTypes.INSERT_ORDER, objectList).get(0);
                orderIdLabel.setText(orderId.toString());
                insertOrderingFromObservableList();
            } else {
                objectList.add(orderId);
                sendARequestToTheServer(ClientCommandTypes.UPDATE_ORDER, objectList);
                chooseAction();
            }

            currentDataOrder = new DtoOrder(orderId,
                    Date.from(datePicker.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                    comboBoxListener.getValue().toString(), (BigDecimal) objectList.get(2),
                    (BigDecimal) objectList.get(3), (BigDecimal) objectList.get(4));
        }
        return isWarning;
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
        setOnDoubleClickToTableView();
    }

    private void initOrderingTableView(){
        orderingTableView.getStylesheets().add(getClass().getResource("/styles/TableViewStyle.css").toExternalForm());
        orderingDishesNameCol.setCellValueFactory(new PropertyValueFactory("dishesName"));
        orderingNumberOfServingsCol.setCellValueFactory(new PropertyValueFactory("numberOfServings"));
        sumPriceCol.setCellValueFactory(new PropertyValueFactory("sumPrice"));
        orderingTableView.setItems(orderingObservableList);
    }

    private void setOnDoubleClickToTableView(){
        tableView.setRowFactory(tv -> {
            TableRow<DtoMenu> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    DtoMenu rowData = row.getItem();
                    PromptNumberOfServings promptNumberOfServings = new PromptNumberOfServings(rowData.getName(), 1);
                    Integer numberOfServings = promptNumberOfServings.showPrompt(rootGridPane.getScene().getWindow());
                    if (numberOfServings > 0) {
                        addOrdering(rowData, numberOfServings);
                    }
                }
            });

            initContextMenu(row);
            return row ;
        });
    }

    private void initContextMenu(TableRow<DtoMenu> row){
        MenuItem infoItem = new MenuItem("Детальніше");
        infoItem.setOnAction((ActionEvent event) -> {
            try {
                showMenuInfoWindow(row.getItem());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        final javafx.scene.control.ContextMenu rowMenu = new javafx.scene.control.ContextMenu();
        rowMenu.getItems().addAll(infoItem);
        row.contextMenuProperty().bind(
                Bindings.when(Bindings.isNotNull(row.itemProperty()))
                        .then(rowMenu)
                        .otherwise((javafx.scene.control.ContextMenu) null));
    }

    public void showMenuInfoWindow(DtoMenu dtoMenu) throws IOException {
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/menu.view/AddingToMenu.fxml"));
        Parent root = fxmlLoader.load();

        AddingToMenuController addingToMenuController = fxmlLoader.getController();
        addingToMenuController.setMenuIdToUpdate(dtoMenu.getId());
        addingToMenuController.setReadOnly();

        addingToMenuController.setMenuTableView(menuTableView);
        addingToMenuController.setTextToTextFields(dtoMenu.getDishesTypeName(), dtoMenu.getName(),
                dtoMenu.getPrice(), dtoMenu.getMass(), dtoMenu.getIngredients());

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(new Scene(root, 500, 500, Color.rgb(0, 0, 0, 0)));
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.initOwner(tableView.getScene().getWindow());
        primaryStage.showAndWait();
    }

    private void addOrdering(DtoMenu dtoMenu, Integer numberOfServings) {
        for (DtoOrdering item : orderingObservableList) {
            if (item.getMenuId() == dtoMenu.getId()) {
                int sumNumbersOfServings = item.getNumberOfServings() + numberOfServings;
                if (sumNumbersOfServings > 10000) {
                    sumNumbersOfServings = 10000;
                }
                item.setNumberOfServings(sumNumbersOfServings);
                calculateSumPrice(item);
                orderingTableView.refresh();
                return;
            }
        }
        DtoOrdering dtoOrdering = new DtoOrdering(null, orderId, dtoMenu.getId(), dtoMenu.getName(),
                numberOfServings, dtoMenu.getPrice());
        orderingObservableList.add(dtoOrdering);
        calculateSumPrice(dtoOrdering);
    }

    public void editNumberOfServings(){
        TablePosition pos = orderingTableView.getSelectionModel().getSelectedCells().get(0);
        int rowIndex = pos.getRow();
        DtoOrdering dtoOrdering = orderingTableView.getItems().get(rowIndex);

        PromptNumberOfServings promptNumberOfServings =
                new PromptNumberOfServings(dtoOrdering.getDishesName(), dtoOrdering.getNumberOfServings());
        Integer numberOfServings = promptNumberOfServings.showPrompt(rootGridPane.getScene().getWindow());
        if (numberOfServings > 0) {
            dtoOrdering.setNumberOfServings(numberOfServings);
            calculateSumPrice(dtoOrdering);
            orderingTableView.refresh();
        }
    }

    public void rejectDishes(){
        TablePosition pos = orderingTableView.getSelectionModel().getSelectedCells().get(0);
        int rowIndex = pos.getRow();
        DtoOrdering dtoOrdering = orderingTableView.getItems().get(rowIndex);
        orderingObservableList.remove(dtoOrdering);
        calculateSumPrice(dtoOrdering);
    }

    public void initTopBorderPane(){
        GridPane topGridPane = new GridPane();

        Label dishesTypeLabel = new Label("Класифікація:");
        dishesTypeLabel.setStyle("-fx-text-fill: white");
        topGridPane.add(dishesTypeLabel, 0, 0);
        topGridPane.setMargin(dishesTypeLabel, new Insets(0, 0, 7, 0));

        initDishesTypeComboBox();
        topGridPane.add(menuTableView.getDishesTypeComboBox(), 0, 1);

        Label dishesNameLabel = new Label("Пошук:");
        dishesNameLabel.setStyle("-fx-text-fill: white");
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

    public void setDataForEditing(DtoOrder dtoOrder){
        orderId = dtoOrder.getId();
        orderIdLabel.setText(orderId.toString());

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

        List<Object> objectList = new LinkedList<>();
        objectList.add(orderId);
        initialDataList.addAll(FXCollections.observableArrayList(
                sendARequestToTheServer(ClientCommandTypes.SELECT_ORDERING, objectList)));
        orderingObservableList.addAll(initialDataList);
        orderingObservableList.forEach(item -> calculateSumPrice(item));
    }

    private void insertOrderingFromObservableList(){
        if (! orderingObservableList.isEmpty()) {
            orderingObservableList.forEach(item -> item.setOrderId(orderId));
            List<Object> orderingObjectList = new LinkedList<>();
            orderingObjectList.addAll(orderingObservableList);
            sendARequestToTheServer(ClientCommandTypes.INSERT_ORDERING, orderingObjectList);
        }
    }

    private void chooseAction(){
        List<Object> objectListForInserting = new LinkedList<>();
        List<Object> objectListForUpdating = new LinkedList<>();
        List<Object> objectListForDeleting = new LinkedList<>();

        orderingObservableList.forEach(item -> {
            if (item.getId() == null) {
                objectListForInserting.add(item);
            } else {
                objectListForUpdating.add(item);
            }
        });

        initialDataList.forEach(item -> {
            if (! orderingObservableList.contains(item)) {
                objectListForDeleting.add(item.getId());
            }
        });

        if (! objectListForInserting.isEmpty()) {
            sendARequestToTheServer(ClientCommandTypes.INSERT_ORDERING, objectListForInserting);
        }
        if (! objectListForUpdating.isEmpty()) {
            sendARequestToTheServer(ClientCommandTypes.UPDATE_ORDERING, objectListForUpdating);
        }
        if (! objectListForDeleting.isEmpty()) {
            sendARequestToTheServer(ClientCommandTypes.DELETE_ORDERING, objectListForDeleting);
        }
    }

    private void calculateSumPrice(DtoOrdering dtoOrdering){
        dtoOrdering.calculateSumPrice();
        BigDecimal sumPrice = new BigDecimal(0);
        for (DtoOrdering item : orderingObservableList) {
            sumPrice = sumPrice.add(item.getSumPrice());
        }
        sumPriceTextField.setText(sumPrice.toString());
    }

    public DtoOrder getCurrentDataOrder() {
        return currentDataOrder;
    }
}