package ostryzhniuk.andriy.catering.menu.view;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import ostryzhniuk.andriy.catering.menu.view.dto.DtoMenu;

import java.io.IOException;

/**
 * Created by Andriy on 04/17/2016.
 */
public class ContextMenu {

    public static void initContextMenu(TableView tableView, MenuWindowController menuWindowController) {
        tableView.setRowFactory(new Callback<TableView<DtoMenu>, TableRow<DtoMenu>>() {
            public TableRow<DtoMenu> call(final TableView<DtoMenu> StudentsTableView) {
                final TableRow<DtoMenu> row = new TableRow();
                final javafx.scene.control.ContextMenu rowMenu = new javafx.scene.control.ContextMenu();
                javafx.scene.control.ContextMenu tableMenu = StudentsTableView.getContextMenu();
                if (tableMenu != null) {
                    rowMenu.getItems().addAll(tableMenu.getItems());
                    rowMenu.getItems().add(new SeparatorMenuItem());
                }
                MenuItem addItem = new MenuItem("Додати");
                addItem.setOnAction((ActionEvent event) -> {
                    try {
                        menuWindowController.showEditingRecordWindow(null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                MenuItem editItem = new MenuItem("Редагувати");
                editItem.setOnAction((ActionEvent event) -> {
                    try {
                        menuWindowController.editRecord();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                MenuItem removeItem = new MenuItem("Видалити");
                removeItem.setOnAction((ActionEvent event) -> {
                    menuWindowController.removeRecord();
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
