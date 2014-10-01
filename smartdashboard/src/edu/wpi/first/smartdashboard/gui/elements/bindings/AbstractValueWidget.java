package edu.wpi.first.smartdashboard.gui.elements.bindings;

import edu.wpi.first.smartdashboard.gui.*;
import edu.wpi.first.smartdashboard.robot.*;




public abstract class AbstractValueWidget extends Widget{
	
	private BooleanBindable booleanDisplayer = BooleanBindable.NULL;
	private NumberBindable numberDisplayer = NumberBindable.NULL;
	private StringBindable stringDisplayer = StringBindable.NULL;

	@Override
	public void setValue(Object value) {
		if(value instanceof Boolean)
			setValue(((Boolean)value).booleanValue());
		else if(value instanceof Double)
			setValue(((Double)value).doubleValue());
		else if(value instanceof String)
			setValue(((String)value));
	}
	
	
	public void setBooleanBinding(BooleanBindable booleanDisplayer) {
		this.booleanDisplayer = booleanDisplayer;
	}

	public void setNumberBinding(NumberBindable numberDisplayer) {
		this.numberDisplayer = numberDisplayer;
	}

	public void setStringBinding(StringBindable stringDisplayer) {
		this.stringDisplayer = stringDisplayer;
	}


	public void setValue(boolean value){
		booleanDisplayer.setBindableValue(value);
	}
	public void setValue(double value){
		numberDisplayer.setBindableValue(value);
	}
	public void setValue(String value){
		stringDisplayer.setBindableValue(value);
	}
	
	
	
	
	
	
	public class EditableBooleanValueCheckBox extends BindableBooleanCheckBox{
		public EditableBooleanValueCheckBox(final String key){
			super(new BindableTableEntry(Robot.getTable(), key));
			setBooleanBinding(this);
		}
	}
	public class EditableBooleanValueField extends BindableBooleanField{
		public EditableBooleanValueField(final String key){
			super(new BindableTableEntry(Robot.getTable(), key));
			setBooleanBinding(this);
		}
	}
	public class EditableNumberValueField extends BindableNumberField{
		public EditableNumberValueField(final String key){
			super(new BindableTableEntry(Robot.getTable(), key));
			setNumberBinding(this);
		}
	}
	public class EditableStringValueField extends BindableStringField{
		public EditableStringValueField(final String key){
			super(new BindableTableEntry(Robot.getTable(), key));
			setStringBinding(this);
		}
	}
	
	

}
