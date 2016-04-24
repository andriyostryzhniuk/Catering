package ostryzhniuk.andriy.catering.menu.view;

import javafx.scene.control.ComboBox;
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
        if (!comboBox.getStyleClass().contains("warning")) {
            comboBoxListener.setValue(comboBox.getValue());
        }
    }
}
