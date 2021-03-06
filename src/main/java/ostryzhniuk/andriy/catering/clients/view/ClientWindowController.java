package ostryzhniuk.andriy.catering.clients.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ostryzhniuk.andriy.catering.clients.view.dto.DtoClient;
import ostryzhniuk.andriy.catering.commands.ClientCommandTypes;
import ostryzhniuk.andriy.catering.menu.view.AutoCompleteComboBoxSearch;
import ostryzhniuk.andriy.catering.overridden.elements.table.view.CustomTableColumn;
import ostryzhniuk.andriy.catering.overridden.elements.table.view.TableViewHolder;
import ostryzhniuk.andriy.catering.subsidiary.classes.AlertWindow;
import ostryzhniuk.andriy.catering.subsidiary.classes.EditPanel;
import ostryzhniuk.andriy.catering.subsidiary.classes.SetterExcelStyle;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import static ostryzhniuk.andriy.catering.client.Client.sendARequestToTheServer;
import static ostryzhniuk.andriy.catering.clients.view.ContextMenu.initContextMenu;

public class ClientWindowController<T extends DtoClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientWindowController.class);

    public BorderPane rootBorderPane;
    public StackPane stackPane;
    public GridPane topGridPane;
    public Button tableReportButton;

    private ComboBox clientNameComboBox = new ComboBox();
    private ComboBox clientNameComboBoxListener = new ComboBox();

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
        tableView.getTableView().getStylesheets().add(getClass().getResource("/styles/TableViewStyle.css").toExternalForm());
        tableView.getTableView().setEditable(true);
        stackPane.getChildren().add(tableView);
        initContextMenu(tableView.getTableView(), this);
        initTableView();
        initClientNameComboBox();
        initReportButtonsStyle();
    }

    public void initTableView(){
        dtoClientsList.clear();
        tableView.getTableView().getItems().clear();

        if (clientNameComboBoxListener.getValue() != null) {
            List<Object> objectList = new LinkedList<>();
            objectList.add(clientNameComboBoxListener.getValue());
            dtoClientsList.addAll(FXCollections.observableArrayList(
                    sendARequestToTheServer(ClientCommandTypes.SELECT_OF_LIKE_NAMES_CLIENT, objectList)));
        } else {
            dtoClientsList.addAll(FXCollections.observableArrayList(
                    sendARequestToTheServer(ClientCommandTypes.SELECT_CLIENT, new LinkedList<>())));
            setClientNameComboBoxItems();
        }

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

    private void setColsDateProperties() {
        nameCol.setPercentWidth(170); nameCol.setMinWidth(170);
        addressCol.setPercentWidth(220); addressCol.setMinWidth(220);
        telephoneCol.setPercentWidth(80); telephoneCol.setMinWidth(80);
        contactPersonCol.setPercentWidth(170); contactPersonCol.setMinWidth(170);
        discountCol.setPercentWidth(70); discountCol.setMinWidth(70);
        emailCol.setPercentWidth(100); emailCol.setMinWidth(100);
        icqCol.setPercentWidth(70); icqCol.setMinWidth(70);
        skypeCol.setPercentWidth(100); skypeCol.setMinWidth(100);
    }

    private void fillTableView(){
        tableView.getTableView().getColumns().addAll(nameCol, addressCol, telephoneCol, contactPersonCol,
                discountCol, emailCol, icqCol, skypeCol);
    }

    public void editRecord() throws IOException {
        TablePosition pos;
        try {
            pos = tableView.getTableView().getSelectionModel().getSelectedCells().get(0);
        } catch (IndexOutOfBoundsException e) {
            AlertWindow alertWindow = new AlertWindow(Alert.AlertType.INFORMATION);
            alertWindow.showEditingInformation();
            return;
        }
        int rowIndex = pos.getRow();
        DtoClient dtoClient = tableView.getTableView().getItems().get(rowIndex);

        showEditingRecordWindow(dtoClient.getId());
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
        DtoClient dtoClient = tableView.getTableView().getItems().get(rowIndex);

        List<Object> objectList = new LinkedList<>();
        objectList.add(dtoClient.getId());
        Boolean isSuccessful = (Boolean) sendARequestToTheServer(ClientCommandTypes.DELETE_CLIENT, objectList).get(0);
        if (! isSuccessful) {
            alertWindow = new AlertWindow(Alert.AlertType.ERROR);
            alertWindow.showDeletingError();
        }
        initTableView();
    }

    public void showEditingRecordWindow(Integer clientIdToUpdate) throws IOException {
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/clients.view/AddingNewClient.fxml"));
        Parent root = fxmlLoader.load();

        AddingNewClientController addingNewClientController = fxmlLoader.getController();
        addingNewClientController.setClientIdToUpdate(clientIdToUpdate);
        addingNewClientController.setClientWindowController(this);
        if (clientIdToUpdate != null) {
            TablePosition pos = tableView.getTableView().getSelectionModel().getSelectedCells().get(0);
            int rowIndex = pos.getRow();
            DtoClient dtoClient = tableView.getTableView().getItems().get(rowIndex);
            addingNewClientController.setTextToTextFields(dtoClient.getName(), dtoClient.getAddress(),
                    dtoClient.getTelephoneNumber(), dtoClient.getContactPerson(), dtoClient.getDiscount(),
                    dtoClient.getEmail(), dtoClient.getIcq(), dtoClient.getSkype());
        }

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(new Scene(root, 500, 500, Color.rgb(0, 0, 0, 0)));
        addingNewClientController.initShortcuts();
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.initOwner(tableView.getTableView().getScene().getWindow());
        primaryStage.showAndWait();
    }

    public void actionTableReportButton(ActionEvent actionEvent) {
        createTableReport();
    }

    private void createTableReport() {
        ExcelClientsReport excelClientsReport = new ExcelClientsReport();
        excelClientsReport.createTableClientsReport(tableView.getTableView(), dtoClientsList);
    }

    public void initEditPanel(MenuItem addMenuItem, MenuItem editMenuItem, MenuItem deleteMenuItem){
        EditPanel editPanel = new EditPanel(tableView.getTableView(), FXCollections.observableArrayList(
                editMenuItem, deleteMenuItem));
        topGridPane.add(editPanel.getContainerGridPane(), 0, 0);

        editPanel.getAddButton().setOnAction((ActionEvent event) -> {
            try {
                showEditingRecordWindow(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        editPanel.getEditButton().setOnAction((ActionEvent event) -> {
            try {
                editRecord();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        editPanel.getDeleteButton().setOnAction((ActionEvent event) -> {
            removeRecord();
        });

        setMenuItems(addMenuItem, editMenuItem, deleteMenuItem);

    }

    private void setMenuItems(MenuItem addMenuItem, MenuItem editMenuItem, MenuItem deleteMenuItem){
        addMenuItem.setOnAction((ActionEvent event) -> {
            try {
                showEditingRecordWindow(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        editMenuItem.setOnAction((ActionEvent event) -> {
            try {
                editRecord();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        deleteMenuItem.setOnAction((ActionEvent event) -> {
            removeRecord();
        });
    }

    private void initReportButtonsStyle(){
        SetterExcelStyle setterExcelStyle = new SetterExcelStyle();
        setterExcelStyle.setStyle(tableReportButton, "Створити звіт таблиці");
    }

    private void initClientNameComboBox() {
        clientNameComboBox.getStylesheets().add(getClass().getResource("/menu.view/ComboBoxStyle.css").toExternalForm());
        clientNameComboBox.setTooltip(new Tooltip("Пошук за назвою клієнта"));
        clientNameComboBox.setPromptText("Введіть назву клієнта");

        topGridPane.add(clientNameComboBox, 2, 0);

        new AutoCompleteComboBoxSearch(clientNameComboBox, clientNameComboBoxListener);

        setComboBoxCellFactory(clientNameComboBox, clientNameComboBoxListener);

        clientNameComboBoxListener.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            clientNameComboBox.getStyleClass().remove("warning");
            initTableView();
        });
    }

    private void setClientNameComboBoxItems(){
        clientNameComboBox.getItems().clear();
        dtoClientsList.forEach(item -> clientNameComboBox.getItems().add(item.getName()));
        new AutoCompleteComboBoxSearch(clientNameComboBox, clientNameComboBoxListener);

    }

    private void setComboBoxCellFactory(ComboBox comboBox, ComboBox comboBoxListener) {
        comboBox.setCellFactory(listCell -> {
            final ListCell<String> cell = new ListCell<String>() {
                {
                    super.setOnMousePressed((MouseEvent event) -> {
//                            mouse pressed
                        comboBoxListener.setValue(comboBox.getValue());
                    });
                }

                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item);
                }
            };
            return cell;
        });
    }

    public void initReportMenu(Menu reportMenu) {
        MenuItem tableReportMenuItem = new MenuItem("Створити звіт таблиці");
        tableReportMenuItem.setOnAction((ActionEvent event) -> {
            createTableReport();
        });

        reportMenu.getItems().add(tableReportMenuItem);
    }
}
