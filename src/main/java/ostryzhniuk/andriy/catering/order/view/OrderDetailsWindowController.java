package ostryzhniuk.andriy.catering.order.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ostryzhniuk.andriy.catering.commands.ClientCommandTypes;
import ostryzhniuk.andriy.catering.ordering.view.dto.DtoOrdering;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import static ostryzhniuk.andriy.catering.client.Client.sendARequestToTheServer;

/**
 * Created by Andriy on 05/05/2016.
 */
public class OrderDetailsWindowController {

    public TableView<DtoOrdering> tableView;
    public TableColumn dishesNameCol;
    public TableColumn numberOfServingsCol;
    public TableColumn sumPriceCol;
    public TextField sumPriceTextField;
    public Label orderIdLabel;
    private Integer orderId;
    private OrderWindowController orderWindowController;

    private ObservableList<DtoOrdering> orderingObservableList = FXCollections.observableArrayList();

    @FXML
    public void initialize(){
        tableView.getStylesheets().add(getClass().getResource("/styles/TableViewStyle.css").toExternalForm());
        dishesNameCol.setCellValueFactory(new PropertyValueFactory("dishesName"));
        numberOfServingsCol.setCellValueFactory(new PropertyValueFactory("numberOfServings"));
        sumPriceCol.setCellValueFactory(new PropertyValueFactory("sumPrice"));
    }

    public void initOrderingTableView(){
        List<Object> objectList = new LinkedList<>();
        objectList.add(orderId);
        orderingObservableList.addAll(FXCollections.observableArrayList(
                sendARequestToTheServer(ClientCommandTypes.SELECT_ORDERING, objectList)));
        orderingObservableList.forEach(item -> calculateSumPrice(item));

        tableView.setItems(orderingObservableList);
    }

    private void calculateSumPrice(DtoOrdering dtoOrdering){
        dtoOrdering.calculateSumPrice();
        BigDecimal sumPrice = new BigDecimal(0);
        for (DtoOrdering item : orderingObservableList) {
            sumPrice = sumPrice.add(item.getSumPrice());
        }
        sumPriceTextField.setText(sumPrice.toString());
    }

    public void onActionCloseButton(ActionEvent actionEvent) {
        close();
    }

    private void close(){
        Stage stage = (Stage) tableView.getScene().getWindow();
        stage.close();
    }

    public void editOrdering(ActionEvent actionEvent) {
        close();
        orderWindowController.editRecord();
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
        orderIdLabel.setText("Замовлення № " + orderId.toString());
    }

    public void setOrderWindowController(OrderWindowController orderWindowController) {
        this.orderWindowController = orderWindowController;
    }
}
