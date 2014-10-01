package edu.wpi.first.smartdashboard.gui.elements;

import edu.wpi.first.smartdashboard.gui.elements.bindings.AbstractValueWidget;
import javax.swing.*;

import edu.wpi.first.smartdashboard.properties.*;
import edu.wpi.first.smartdashboard.types.*;

/**
 * Implements a simple text box UI element with a name label.
 * @author pmalmsten
 * @author Joe Grinstead
 */
public class TextBox extends AbstractValueWidget {

    public static final DataType[] TYPES = {DataType.BASIC};
    public static final String NAME = "Text Box";
    
    public final BooleanProperty editable = new BooleanProperty(this, "Editable", true);
    public final ColorProperty background = new ColorProperty(this, "Background");

    private JTextField valueField;

    public void init() {
    	setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

    	JLabel nameLabel = new JLabel(getFieldName());

    	if (getType().isChildOf(DataType.BOOLEAN))
    		valueField = new EditableBooleanValueField(getFieldName());
    	else if (getType().isChildOf(DataType.NUMBER))
    		valueField = new EditableNumberValueField(getFieldName());
    	else if (getType().isChildOf(DataType.STRING))
    		valueField = new EditableStringValueField(getFieldName());
    	else{
    		valueField = new JTextField();
    		valueField.setText("Unupported basic data type: "+getType());
    		valueField.setEditable(false);
    	}

    	update(background, valueField.getBackground());

    	valueField.setEditable(editable.getValue());
    	valueField.setColumns(10);

        add(nameLabel);
        add(valueField);
    }

    @Override
    public void propertyChanged(Property property) {
        if (property == background) {
            valueField.setBackground(background.getValue());
        } else if (property == editable) {
            valueField.setEditable(editable.getValue());
        }
    }
    
}
