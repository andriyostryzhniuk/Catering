package ostryzhniuk.andriy.catering.main.window;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ostryzhniuk.andriy.catering.clients.view.ClientWindowController;
import ostryzhniuk.andriy.catering.debtors.view.DebtorsWindowController;
import ostryzhniuk.andriy.catering.menu.view.MenuWindowController;
import ostryzhniuk.andriy.catering.order.view.OrderWindowController;
import ostryzhniuk.andriy.catering.order.view.dto.DtoOrder;
import ostryzhniuk.andriy.catering.ordering.view.OrderingWindowController;
import ostryzhniuk.andriy.catering.subsidiary.classes.AlertWindow;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.stream.IntStream;

public class MainWindowController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainWindowController.class);
    public GridPane mainGridPane;
    public MenuItem addMenuItem;
    public MenuItem editMenuItem;
    public MenuItem deleteMenuItem;
    public MenuItem infoMenuItem;
    public Menu reportMenu;

    private OrderingWindowController orderingWindowController;

    @FXML
    public void initialize(){
        initOrderView(new ActionEvent());
    }

    public void initOrderView(ActionEvent actionEvent) {
        removeMainGridPaneChildren(false);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/order/view/OrderWindow.fxml"));
        try {
            mainGridPane.add(fxmlLoader.load(), 1, 1);
            OrderWindowController orderWindowController = fxmlLoader.getController();
            orderWindowController.setMainWindowController(this);
            orderWindowController.initEditPanel(addMenuItem, editMenuItem, deleteMenuItem, infoMenuItem);
            orderWindowController.initReportMenu(reportMenu);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
        mainGridPane.add(initButtonContainer(initButtonClose()), 1, 2);
    }

    public void initMenuView(ActionEvent actionEvent) {
        removeMainGridPaneChildren(false);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/menu.view/MenuWindow.fxml"));
        try {
            mainGridPane.add(fxmlLoader.load(), 1, 1);
            MenuWindowController menuWindowController = fxmlLoader.getController();
            menuWindowController.initEditPanel(addMenuItem, editMenuItem, deleteMenuItem);
            menuWindowController.initReportMenu(reportMenu);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
        mainGridPane.add(initButtonContainer(initButtonClose()), 1, 2);
    }

    public void initClientView(ActionEvent actionEvent) {
        removeMainGridPaneChildren(false);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/clients.view/ClientWindow.fxml"));
        try {
            mainGridPane.add(fxmlLoader.load(), 1, 1);
            ClientWindowController clientWindowController = fxmlLoader.getController();
            clientWindowController.initEditPanel(addMenuItem, editMenuItem, deleteMenuItem);
            clientWindowController.initReportMenu(reportMenu);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
        mainGridPane.add(initButtonContainer(initButtonClose()), 1, 2);
    }

    public GridPane initButtonContainer(Button... buttons) {
        GridPane buttonContainer = new GridPane();
        buttonContainer.setAlignment(Pos.TOP_RIGHT);

        IntStream.range(0, buttons.length).forEach(i -> {
            if (i == 0 && buttons.length != 1) {
                buttonContainer.add(buttons[i], i, 0);
                buttonContainer.setMargin(buttons[i], new Insets(20, 5, 0, 0));
            } else {
                buttonContainer.add(buttons[i], i, 0);
                buttonContainer.setMargin(buttons[i], new Insets(20, 0, 0, 0));
            }
        });
        return buttonContainer;
    }

    public Button initButtonClose (){
        Button buttonClose = initButton("Закрити");
        buttonClose.setOnAction((ActionEvent event) -> {
            removeMainGridPaneChildren(true);
        });
        return buttonClose;
    }

    public Button initButton(String text){
        Button button = new Button(text);
        button.setPrefHeight(25.0);
        button.setPrefWidth(72.0);
        return button;
    }

    public void removeMainGridPaneChildren(boolean addMenuItemState){
        for (int i = mainGridPane.getChildren().size(); i > 3; i--) {
            mainGridPane.getChildren().remove(i-1);
        }
        setMenuItemsDisable(addMenuItemState);
    }

    public void onActionOrderingButton(ActionEvent actionEvent) {
        initOrderingView();
    }

    public void initOrderingView(){
        removeMainGridPaneChildren(true);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ordering.view/OrderingWindow.fxml"));
        try {
            mainGridPane.add(fxmlLoader.load(), 1, 1);
            orderingWindowController = fxmlLoader.getController();
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
        mainGridPane.add(initButtonContainer(initButtonOrderingSave (), initButtonOrderingCancel()), 1, 2);
    }

    public void initOrderingViewForEditing(DtoOrder dtoOrder){
        removeMainGridPaneChildren(true);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ordering.view/OrderingWindow.fxml"));
        try {
            mainGridPane.add(fxmlLoader.load(), 1, 1);
            orderingWindowController = fxmlLoader.getController();
            orderingWindowController.setDataForEditing(dtoOrder);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
        mainGridPane.add(initButtonContainer(initButtonOrderingSave (), initButtonOrderingCancel()), 1, 2);
    }

    public Button initButtonOrderingCancel (){
        Button buttonCancel = initButton("Скасувати");
        buttonCancel.setOnAction((ActionEvent event) -> {
            removeMainGridPaneChildren(true);
        });
        return buttonCancel;
    }

    public Button initButtonOrderingSave (){
        Button buttonSave = initButton("Зберегти");
        buttonSave.setOnAction((ActionEvent event) -> {
            if (! orderingWindowController.saveToDB()) {
                DtoOrder dtoOrder = orderingWindowController.getCurrentDataOrder();
                initOrderingViewForEditing(dtoOrder);
            }
        });
        return buttonSave;
    }

    public void onActionDebtorsButton(ActionEvent actionEvent) {
        removeMainGridPaneChildren(true);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/debtors.view/DebtorsWindow.fxml"));
        try {
            mainGridPane.add(fxmlLoader.load(), 1, 1);
            DebtorsWindowController debtorsWindowController = fxmlLoader.getController();
            debtorsWindowController.initReportMenu(reportMenu);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
        mainGridPane.add(initButtonContainer(initButtonClose()), 1, 2);
    }

    public void close(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

    private void setMenuItemsDisable(boolean addMenuItemState){
        addMenuItem.setDisable(addMenuItemState);
        editMenuItem.setDisable(true);
        deleteMenuItem.setDisable(true);
        infoMenuItem.setDisable(true);
        reportMenu.getItems().clear();
    }

    public void aboutProgram(ActionEvent actionEvent) {
        String headerText = null;
        String contentText = "Про програму";
        AlertWindow alertWindow = new AlertWindow(Alert.AlertType.INFORMATION, headerText, contentText);
        alertWindow.showInformation();
    }
}
