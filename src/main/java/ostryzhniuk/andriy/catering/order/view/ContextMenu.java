package ostryzhniuk.andriy.catering.order.view;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import ostryzhniuk.andriy.catering.order.view.dto.DtoOrder;

/**
 * Created by Andriy on 04/17/2016.
 */
public class ContextMenu {

    public static void initContextMenu(TableView tableView, OrderWindowController orderWindowController) {
        tableView.setRowFactory(new Callback<TableView<DtoOrder>, TableRow<DtoOrder>>() {
            public TableRow<DtoOrder> call(final TableView<DtoOrder> StudentsTableView) {
                final TableRow<DtoOrder> row = new TableRow();
                final javafx.scene.control.ContextMenu rowMenu = new javafx.scene.control.ContextMenu();
                javafx.scene.control.ContextMenu tableMenu = StudentsTableView.getContextMenu();
                if (tableMenu != null) {
                    rowMenu.getItems().addAll(tableMenu.getItems());
                    rowMenu.getItems().add(new SeparatorMenuItem());
                }
                MenuItem addItem = new MenuItem("Додати");
                addItem.setOnAction((ActionEvent event) -> {
                    orderWindowController.addRecord();
                });

                MenuItem editItem = new MenuItem("Редагувати");
                editItem.setOnAction((ActionEvent event) -> {
                    orderWindowController.editRecord();
                });

                MenuItem removeItem = new MenuItem("Видалити");
                removeItem.setOnAction((ActionEvent event) -> {
                    orderWindowController.removeRecord();
                });
                rowMenu.getItems().addAll(addItem, editItem, removeItem);
                row.contextMenuProperty().bind(
                        Bindings.when(Bindings.isNotNull(row.itemProperty()))
                                .then(rowMenu)
                                .otherwise((javafx.scene.control.ContextMenu) null));
                return row;
            }
        });
    }
}
