package ostryzhniuk.andriy.catering.menu.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.StackPane;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.DoubleStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ostryzhniuk.andriy.catering.commands.ClientCommandTypes;
import ostryzhniuk.andriy.catering.menu.view.dto.DtoMenu;
import ostryzhniuk.andriy.catering.overridden.elements.table.view.CustomTableColumn;
import ostryzhniuk.andriy.catering.overridden.elements.table.view.TableViewHolder;
import java.math.BigDecimal;
import java.util.LinkedList;
import static ostryzhniuk.andriy.catering.client.Client.sendARequestToTheServer;

/**
 * Created by Andriy on 04/19/2016.
 */
public class MenuTableView<T extends DtoMenu> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuTableView.class);

    StackPane stackPane = new StackPane();

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
        tableView.getTableView().getStylesheets().add(getClass().getResource("/order/view/TableViewStyle.css").toExternalForm());
        tableView.getTableView().setEditable(true);
        stackPane.getChildren().add(tableView);
//        initContextMenu(tableView.getTableView(), this);
        initTableView();
    }

    public void initTableView(){
        dtoMenuList.clear();
        tableView.getTableView().getItems().clear();

        dtoMenuList.addAll(FXCollections.observableArrayList(
                sendARequestToTheServer(ClientCommandTypes.SELECT_MENU, new LinkedList<>())));
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

    public TableView getMenuTableView(){
        return tableView.getTableView();
    }

    public StackPane getTableViewsStackPane(){
        return stackPane;
    }
}
