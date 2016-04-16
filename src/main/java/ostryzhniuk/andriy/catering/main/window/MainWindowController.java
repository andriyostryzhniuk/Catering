package ostryzhniuk.andriy.catering.main.window;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ostryzhniuk.andriy.catering.order.view.OrderWindowController;
import java.io.IOException;
import java.io.UncheckedIOException;

public class MainWindowController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainWindowController.class);
    public GridPane mainGridPane;

    private OrderWindowController orderWindowController;

    @FXML
    public void initialize(){

    }

    public void initOrderView(ActionEvent actionEvent) {
        removeMainGridPaneChildren();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/order/view/OrderWindow.fxml"));
        try {
            mainGridPane.add(fxmlLoader.load(), 1, 1);
            orderWindowController = fxmlLoader.getController();
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    public void initStockTracking(ActionEvent actionEvent) {

    }

    public void removeMainGridPaneChildren(){
        for (int i = mainGridPane.getChildren().size(); i > 1; i--) {
            mainGridPane.getChildren().remove(i-1);
        }
    }
}
