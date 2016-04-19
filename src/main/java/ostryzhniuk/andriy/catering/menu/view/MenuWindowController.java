package ostryzhniuk.andriy.catering.menu.view;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

/**
 * Created by Andriy on 04/19/2016.
 */
public class MenuWindowController {

    public BorderPane rootBorderPane;
    private MenuTableView menuTableView = new MenuTableView();
    private TableView tableView;

    @FXML
    public void initialize(){
        menuTableView.initialize();
        tableView = menuTableView.getMenuTableView();
        rootBorderPane.setCenter(menuTableView.getTableViewsStackPane());
    }
}
