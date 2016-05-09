package ostryzhniuk.andriy.catering.ordering.view;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import ostryzhniuk.andriy.catering.order.view.dto.DtoOrder;

public class ContextMenu {

    public static void initOrderingContextMenu(TableView tableView, OrderingWindowController orderingWindowController) {
        tableView.setRowFactory(new Callback<TableView<DtoOrder>, TableRow<DtoOrder>>() {
            public TableRow<DtoOrder> call(final TableView<DtoOrder> tableView) {
                final TableRow<DtoOrder> row = new TableRow();
                final javafx.scene.control.ContextMenu rowMenu = new javafx.scene.control.ContextMenu();
                javafx.scene.control.ContextMenu tableMenu = tableView.getContextMenu();
                if (tableMenu != null) {
                    rowMenu.getItems().addAll(tableMenu.getItems());
                    rowMenu.getItems().add(new SeparatorMenuItem());
                }
                MenuItem editItem = new MenuItem("Змінити кількість порцій");
                editItem.setOnAction((ActionEvent event) -> {
                    orderingWindowController.editNumberOfServings();
                });

                MenuItem removeItem = new MenuItem("Відмінити страву");
                removeItem.setOnAction((ActionEvent event) -> {
                    orderingWindowController.rejectDishes();
                });
                rowMenu.getItems().addAll(editItem, removeItem);
                row.contextMenuProperty().bind(
                        Bindings.when(Bindings.isNotNull(row.itemProperty()))
                                .then(rowMenu)
                                .otherwise((javafx.scene.control.ContextMenu) null));
                return row;
            }
        });
    }
}
