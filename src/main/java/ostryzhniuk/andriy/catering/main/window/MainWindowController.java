package ostryzhniuk.andriy.catering.main.window;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ostryzhniuk.andriy.catering.commands.ClientCommandTypes;
import ostryzhniuk.andriy.catering.dto.DtoOrder;

import java.util.LinkedList;

import static ostryzhniuk.andriy.catering.client.Client.sendARequestToTheServer;

public class MainWindowController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainWindowController.class);

    @FXML
    public void initialize(){

    }

    public void initEmployeesWorkTracking(ActionEvent actionEvent) {
        ObservableList<DtoOrder> dtoOrdersList = FXCollections.observableArrayList(
                sendARequestToTheServer(ClientCommandTypes.SELECT_ORDER, new LinkedList<>()));
        LOGGER.info("dtoOrderingLists size: " + dtoOrdersList.size());
        dtoOrdersList.forEach(item -> {
            LOGGER.info("response " + item);
        });
    }

    public void initStockTracking(ActionEvent actionEvent) {

    }
}
