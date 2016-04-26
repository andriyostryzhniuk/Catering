package ostryzhniuk.andriy.catering.menu.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.DoubleStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ostryzhniuk.andriy.catering.commands.ClientCommandTypes;
import ostryzhniuk.andriy.catering.menu.view.dto.DtoMenu;
import ostryzhniuk.andriy.catering.overridden.elements.combo.box.AutoCompleteComboBoxListener;
import ostryzhniuk.andriy.catering.overridden.elements.table.view.CustomTableColumn;
import ostryzhniuk.andriy.catering.overridden.elements.table.view.TableViewHolder;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import static ostryzhniuk.andriy.catering.client.Client.sendARequestToTheServer;

public class MenuTableView<T extends DtoMenu> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuTableView.class);

    private StackPane stackPane = new StackPane();
    private BorderPane borderPane = new BorderPane();
    private ComboBox dishesTypeComboBox = new ComboBox();
    private ComboBox dishesTypeComboBoxListener = new ComboBox();
    private ComboBox dishesNameComboBox = new ComboBox();
    private ComboBox dishesNameComboBoxListener = new ComboBox();

    @FXML
    private TableViewHolder<T> tableView = new TableViewHolder<>();

    public CustomTableColumn<T, String> dishesTypeCol = new CustomTableColumn<>("Тип страви");
    public CustomTableColumn<T, String> nameCol = new CustomTableColumn<>("Назва страви");
    public CustomTableColumn<T, BigDecimal> priceCol = new CustomTableColumn<>("Вартість");
    public CustomTableColumn<T, Double> massCol = new CustomTableColumn<>("Вага");
    public CustomTableColumn<T, String> ingredientsCol = new CustomTableColumn<>("Інградієнти");

    private ObservableList<T> dtoMenuList = FXCollections.observableArrayList();

    public void initialize(){
        fillCols();
        setColsDateProperties();
        fillTableView();
        tableView.getTableView().getStylesheets().add(getClass().getResource("/styles/TableViewStyle.css").toExternalForm());
        tableView.getTableView().setEditable(true);
        stackPane.getChildren().add(tableView);
        borderPane.setCenter(stackPane);
        initTopBorderPane();
        initTableView();
    }

    public void initTableView(){
        dtoMenuList.clear();
        tableView.getTableView().getItems().clear();

        if (dishesTypeComboBoxListener.getValue().equals("Всі категорії") && dishesNameComboBoxListener.getValue() == null) {
            dtoMenuList.addAll(FXCollections.observableArrayList(
                    sendARequestToTheServer(ClientCommandTypes.SELECT_ALL_OF_MENU, new LinkedList<>())));
            setDishesNameComboBoxItems();
        } else if (dishesNameComboBoxListener.getValue() != null) {
            List<Object> objectList = new LinkedList<>();
            objectList.add(dishesNameComboBoxListener.getValue());
            objectList.add(dishesTypeComboBoxListener.getValue());
            dtoMenuList.addAll(FXCollections.observableArrayList(
                    sendARequestToTheServer(ClientCommandTypes.SELECT_OF_LIKE_NAMES_MENU, objectList)));
        } else {
            List<Object> dishesTypeNameList = new LinkedList<>();
            dishesTypeNameList.add(dishesTypeComboBoxListener.getValue());
            Integer dishesTypeId = (Integer)
                    sendARequestToTheServer(ClientCommandTypes.SELECT_DISHES_TYPE_ID, dishesTypeNameList).get(0);
            List<Object> objectList = new LinkedList<>();
            objectList.add(dishesTypeId);
            dtoMenuList.addAll(FXCollections.observableArrayList(
                    sendARequestToTheServer(ClientCommandTypes.SELECT_SOME_TYPE_OF_MENU, objectList)));
            setDishesNameComboBoxItems();
        }

        tableView.getTableView().setItems(dtoMenuList);
    }

    private void fillCols() {
        dishesTypeCol.setCellValueFactory(new PropertyValueFactory("dishesTypeName"));
        nameCol.setCellValueFactory(new PropertyValueFactory("name"));
        priceCol.setCellValueFactory(new PropertyValueFactory("price"));
        massCol.setCellValueFactory(new PropertyValueFactory("mass"));
        ingredientsCol.setCellValueFactory(new PropertyValueFactory("ingredients"));

        dishesTypeCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        priceCol.setCellFactory(TextFieldTableCell.<T, BigDecimal>forTableColumn(new BigDecimalStringConverter()));
        massCol.setCellFactory(TextFieldTableCell.<T, Double>forTableColumn(new DoubleStringConverter()));
        ingredientsCol.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    private void setColsDateProperties() {
        dishesTypeCol.setPercentWidth(200); dishesTypeCol.setMinWidth(200);
        nameCol.setPercentWidth(220); nameCol.setMinWidth(220);
        priceCol.setPercentWidth(80); priceCol.setMinWidth(80);
        massCol.setPercentWidth(80); massCol.setMinWidth(80);
        ingredientsCol.setPercentWidth(500); ingredientsCol.setMinWidth(500);
    }

    private void fillTableView(){
        tableView.getTableView().getColumns().addAll(dishesTypeCol, nameCol, priceCol, massCol, ingredientsCol);
    }

    private void initTopBorderPane(){
        GridPane topGridPane = new GridPane();

        Label dishesTypeLabel = new Label("Класифікація:");
        topGridPane.add(dishesTypeLabel, 0, 0);
        topGridPane.setMargin(dishesTypeLabel, new Insets(0, 10, 0, 0));

        initDishesTypeComboBox();
        topGridPane.add(dishesTypeComboBox, 1, 0);

        Button dishesTypeButton = initDishesTypeButton();
        topGridPane.add(dishesTypeButton, 2, 0);
        topGridPane.setHalignment(dishesTypeButton, HPos.RIGHT);
        topGridPane.setMargin(dishesTypeButton, new Insets(0, 0, 0, 2));

        Label dishesNameLabel = new Label("Пошук за назвою:");
        topGridPane.add(dishesNameLabel, 3, 0);
        topGridPane.setMargin(dishesNameLabel, new Insets(0, 10, 0, 80));

        initDishesNameComboBox();
        topGridPane.add(dishesNameComboBox, 4, 0);

        borderPane.setTop(topGridPane);
        borderPane.setAlignment(topGridPane, Pos.TOP_LEFT);
        borderPane.setMargin(topGridPane, new Insets(0.0, 0.0, 20.0, 0.0));

    }

    private void initDishesTypeComboBox() {
        dishesTypeComboBox.getStylesheets().add(getClass().getResource("/styles/ComboBoxStyle.css").toExternalForm());
        dishesTypeComboBox.setTooltip(new Tooltip("Тип страви"));
        dishesTypeComboBox.setPromptText("Тип страви");

        ObservableList<String> observableList = FXCollections.observableArrayList();
        observableList.addAll(FXCollections.observableArrayList(
                sendARequestToTheServer(ClientCommandTypes.SELECT_DISHES_TYPE_NAME, new LinkedList<>())));

        dishesTypeComboBox.getItems().add("Всі категорії");
        dishesTypeComboBox.getItems().addAll(observableList);

        new AutoCompleteComboBoxListener<>(dishesTypeComboBox, dishesTypeComboBoxListener);

        dishesTypeComboBox.setValue("Всі категорії");
        dishesTypeComboBoxListener.setValue("Всі категорії");

        setComboBoxCellFactory(dishesTypeComboBox, dishesTypeComboBoxListener);

        dishesTypeComboBoxListener.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                dishesTypeComboBox.getStyleClass().remove("warning");
                dishesNameComboBox.setValue(null);
                dishesNameComboBoxListener.setValue(null);
                initTableView();
            }
        });
    }

    private void initDishesNameComboBox() {
        dishesNameComboBox.getStylesheets().add(getClass().getResource("/menu.view/ComboBoxStyle.css").toExternalForm());
        dishesNameComboBox.setTooltip(new Tooltip("Пошук за назвою страви"));
        dishesNameComboBox.setPromptText("Пошук за назвою");

        new AutoCompleteComboBoxSearch(dishesNameComboBox, dishesNameComboBoxListener);

        setComboBoxCellFactory(dishesNameComboBox, dishesNameComboBoxListener);

        dishesNameComboBoxListener.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            dishesNameComboBox.getStyleClass().remove("warning");
            initTableView();
        });
    }

    private Button initDishesTypeButton() {
        Button button = new Button();
        Image image = new Image(getClass().getResourceAsStream("/icons/settings.png"));
        button.getStylesheets().add(getClass().getResource("/menu.view/dishesType/DishesTypeButtonStyle.css").toExternalForm());
        button.setGraphic(new ImageView(image));

        button.setOnAction((javafx.event.ActionEvent event) -> {
            Stage primaryStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/menu.view/dishesType/DishesTypeWindow.fxml"));
            Parent root = null;
            try {
                root = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            primaryStage.initStyle(StageStyle.TRANSPARENT);
            primaryStage.setScene(new Scene(root, 500, 500, Color.rgb(0, 0, 0, 0)));
            primaryStage.initModality(Modality.WINDOW_MODAL);
            primaryStage.initOwner(tableView.getScene().getWindow());
            primaryStage.showAndWait();
        });

        return button;
    }

    public TableView getMenuTableView(){
        return tableView.getTableView();
    }

    public BorderPane getBorderPane() {
        return borderPane;
    }

    private void setComboBoxCellFactory(ComboBox comboBox, ComboBox comboBoxListener) {
        comboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
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
            }
        });
    }

    private void setDishesNameComboBoxItems(){
        dishesNameComboBox.getItems().clear();
        dtoMenuList.forEach(item -> dishesNameComboBox.getItems().add(item.getName()));
        new AutoCompleteComboBoxSearch(dishesNameComboBox, dishesNameComboBoxListener);

    }
}
