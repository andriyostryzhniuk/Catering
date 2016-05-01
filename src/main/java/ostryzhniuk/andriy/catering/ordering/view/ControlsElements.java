package ostryzhniuk.andriy.catering.ordering.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ostryzhniuk.andriy.catering.commands.ClientCommandTypes;
import ostryzhniuk.andriy.catering.overridden.elements.combo.box.AutoCompleteComboBoxListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;

import static ostryzhniuk.andriy.catering.client.Client.sendARequestToTheServer;

public class ControlsElements {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControlsElements.class);

    private DatePicker datePicker;
    private ComboBox clientComboBox;
    private ComboBox comboBoxListener;
    private TextField costTextField;
    private TextField discountTextField;
    private TextField billTextField;
    private TextField paidTextField;

    public ControlsElements(TextField billTextField, ComboBox clientComboBox, ComboBox comboBoxListener,
                            TextField costTextField, DatePicker datePicker, TextField discountTextField,
                            TextField paidTextField) {
        this.billTextField = billTextField;
        this.clientComboBox = clientComboBox;
        this.comboBoxListener = comboBoxListener;
        this.costTextField = costTextField;
        this.datePicker = datePicker;
        this.discountTextField = discountTextField;
        this.paidTextField = paidTextField;
    }

    public void initClientComboBox() {
        clientComboBox.getStylesheets().add(getClass().getResource("/styles/ComboBoxStyle.css").toExternalForm());
        clientComboBox.setTooltip(new Tooltip("Вибрати клієнта"));
        clientComboBox.setPromptText("Клієнт");

        ObservableList<String> observableList = FXCollections.observableArrayList();
        observableList.addAll(FXCollections.observableArrayList(
                sendARequestToTheServer(ClientCommandTypes.SELECT_CLIENT_NAMES, new LinkedList<>())));

        clientComboBox.setItems(observableList);

        new AutoCompleteComboBoxListener<>(clientComboBox, comboBoxListener);

        clientComboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                final ListCell<String> cell = new ListCell<String>() {
                    {
                        super.setOnMousePressed((MouseEvent event) -> {
//                            mouse pressed
                            comboBoxListener.setValue(clientComboBox.getValue());
                        });
                    }

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);
                    }
                };
                return cell;
            }
        });

        comboBoxListener.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                clientComboBox.getStyleClass().remove("warning");
            }
        });
    }

    public void setDataPickerListener(){
        datePicker.getStylesheets().add(getClass().getResource("/order/view/DatePickerStyle.css").toExternalForm());
        datePicker.setTooltip(new Tooltip("Дата замовлення"));
        datePicker.setEditable(false);
        datePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            datePicker.getStyleClass().remove("warning");
        });
    }

    public void setCostTextFieldListener() {
        costTextField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        costTextField.setTooltip(new Tooltip("Вартість замовлення"));
        costTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                costTextFieldValidation();
            }
        });

        costTextField.setOnMouseClicked((MouseEvent event) -> {
            costTextField.getStyleClass().remove("warning");
        });
    }

    public void costTextFieldValidation(){
        costTextField.getStyleClass().remove("warning");
        try {
            if (new BigDecimal(costTextField.getText()).compareTo(new BigDecimal(0)) == -1) {
                if (!costTextField.getStyleClass().contains("warning")) {
                    costTextField.getStyleClass().add("warning");
                }
                billTextField.setText("");
            } else {
                if (!costTextField.getText().isEmpty() &&
                        (discountTextField.getText().isEmpty() || discountTextField.getStyleClass().contains("warning"))) {
                    billTextField.setText(costTextField.getText());
                } else if (!costTextField.getText().isEmpty() && !discountTextField.getText().isEmpty() &&
                        !discountTextField.getStyleClass().contains("warning")) {
                    billTextField.setText(new BigDecimal(
                            new BigDecimal(costTextField.getText()).subtract( new BigDecimal (
                            new BigDecimal(costTextField.getText()).
                                    multiply(new BigDecimal(discountTextField.getText())).toString()
                    ).divide(new BigDecimal(100))).toString()).setScale(2, RoundingMode.CEILING).toString());
                }
            }
        } catch (NumberFormatException e) {
            LOGGER.debug("NumberFormatException");
            billTextField.setText("");
            if (!costTextField.getStyleClass().contains("warning") && !costTextField.getText().isEmpty()) {
                costTextField.getStyleClass().add("warning");
            }
        }
    }

    public void setDiscountTextFieldListener() {
        discountTextField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        discountTextField.setTooltip(new Tooltip("Знижка для клієнта (у відсотках)"));
        discountTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                discountTextFieldValidation();
            }
        });

        discountTextField.setOnMouseClicked((MouseEvent event) -> {
            discountTextField.getStyleClass().remove("warning");
        });
    }

    public void discountTextFieldValidation(){
        discountTextField.getStyleClass().remove("warning");
        try {
            if (new BigDecimal(discountTextField.getText()).compareTo(new BigDecimal(100)) == 1 ||
                    new BigDecimal(discountTextField.getText()).compareTo(new BigDecimal(0)) == -1) {
                if (!discountTextField.getStyleClass().contains("warning")) {
                    discountTextField.getStyleClass().add("warning");
                    billTextField.setText(costTextField.getText());
                }
            } else {
                if (!costTextField.getText().isEmpty() && !costTextField.getStyleClass().contains("warning")
                        && discountTextField.getText().isEmpty()) {
                    billTextField.setText(costTextField.getText());
                } else if (!costTextField.getText().isEmpty() && !discountTextField.getText().isEmpty()) {
                    billTextField.setText(new BigDecimal(
                            new BigDecimal(costTextField.getText()).subtract( new BigDecimal (
                            new BigDecimal(costTextField.getText()).
                                    multiply(new BigDecimal(discountTextField.getText())).toString()
                    ).divide(new BigDecimal(100))).toString()).setScale(2, RoundingMode.CEILING).toString());
                }
            }
        } catch (NumberFormatException e) {
            LOGGER.debug("NumberFormatException");
            if (!costTextField.getStyleClass().contains("warning")) {
                billTextField.setText(costTextField.getText());
            }
            if (!discountTextField.getStyleClass().contains("warning") && !discountTextField.getText().isEmpty()) {
                discountTextField.getStyleClass().add("warning");
            }
        }
    }

    public void setPaidTextFieldListener() {
        paidTextField.getStylesheets().add(getClass().getResource("/styles/TextFieldStyle.css").toExternalForm());
        paidTextField.setTooltip(new Tooltip("Сплачено"));
        paidTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                paidTextFieldValidation();
            }
        });

        paidTextField.setOnMouseClicked((MouseEvent event) -> {
            paidTextField.getStyleClass().remove("warning");
        });
    }

    public void paidTextFieldValidation(){
        paidTextField.getStyleClass().remove("warning");
        try {
            if (new BigDecimal(paidTextField.getText()).compareTo(new BigDecimal(0)) == -1) {
                if (!paidTextField.getStyleClass().contains("warning")) {
                    paidTextField.getStyleClass().add("warning");
                }
            }
        } catch (NumberFormatException e) {
            LOGGER.debug("NumberFormatException");
            if (!paidTextField.getStyleClass().contains("warning") && !paidTextField.getText().isEmpty()) {
                paidTextField.getStyleClass().add("warning");
            }
        }
    }

    public void clear(){
        datePicker.setValue(null);
        clientComboBox.setValue(null);
        comboBoxListener.setValue(null);
        costTextField.setText("");
        discountTextField.setText("");
        billTextField.setText("");
        paidTextField.setText("");
    }
}
