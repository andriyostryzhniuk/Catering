package ostryzhniuk.andriy.catering.debtors.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ostryzhniuk.andriy.catering.commands.ClientCommandTypes;
import ostryzhniuk.andriy.catering.debtors.view.dto.DtoDebtors;

import java.util.LinkedList;

import static ostryzhniuk.andriy.catering.client.Client.sendARequestToTheServer;

/**
 * Created by Andriy on 05/05/2016.
 */
public class DebtorsWindowController<T extends DtoDebtors> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DebtorsWindowController.class);

    public BorderPane rootBorderPane;
    public StackPane stackPane;

    private ObservableList<T> dtoDebtorsList = FXCollections.observableArrayList();

    @FXML
    public void initialize(){
        dtoDebtorsList.addAll(sendARequestToTheServer(ClientCommandTypes.SELECT_DEBTORS, new LinkedList<>()));
        LOGGER.info("dtoDebtorsList size: " + dtoDebtorsList.size());
    }
}
