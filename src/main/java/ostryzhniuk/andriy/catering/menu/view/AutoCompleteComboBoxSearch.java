package ostryzhniuk.andriy.catering.menu.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import ostryzhniuk.andriy.catering.overridden.elements.combo.box.AutoCompleteComboBoxListener;

/**
 * Created by Andriy on 04/24/2016.
 */
public class AutoCompleteComboBoxSearch extends AutoCompleteComboBoxListener {

    public AutoCompleteComboBoxSearch(ComboBox comboBox, ComboBox comboBoxListener) {
        super(comboBox, comboBoxListener);
    }

    protected void setValueToComboBoxListener(){
        comboBoxListener.setValue(comboBox.getValue());
        System.out.println(1);
//        if (comboBox.getItems().contains(comboBox.getValue())) {
//            comboBoxListener.setValue(comboBox.getValue());
//        } else {
//            setWarningStyle();
//        }
    }
}
