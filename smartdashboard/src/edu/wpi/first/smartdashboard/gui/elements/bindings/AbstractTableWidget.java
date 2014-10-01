package edu.wpi.first.smartdashboard.gui.elements.bindings;

import edu.wpi.first.smartdashboard.gui.*;
import edu.wpi.first.smartdashboard.livewindow.elements.LWSubsystem;
import edu.wpi.first.smartdashboard.livewindow.elements.NameTag;
import edu.wpi.first.smartdashboard.types.DataType;
import edu.wpi.first.wpilibj.tables.*;
import java.awt.Point;
import java.util.*;

/**
 * An abstraction for creating a widget that wraps a network table
 * 
 * 
 * @author Mitchell
 *
 */
public abstract class AbstractTableWidget extends Widget implements ITableListener{
    protected ITable table;

    /** A NameTag for this widget. */
    public NameTag nameTag;
    
	private final boolean listenSubtables;
    
	public AbstractTableWidget(){
		this(false);
	}
	protected AbstractTableWidget(boolean listenSubtables){
		this.listenSubtables = listenSubtables;
	}

	@Override
	public void setValue(Object value) {
		if(value instanceof ITable){
			ITable table = (ITable)value;

			if (table != null)
				table.removeTableListener(this);

			this.table = table;
			if(table!=null){
				table.addTableListener(this, true);
				if(listenSubtables)
					table.addSubTableListener(this);
			}
		}
	}
    
    public void setField(String name, Widget element, DataType type, Object value, LWSubsystem subsystem, Point point) {
        element.setFieldName(name);
        element.setType(type);
        element.init();
        if(value != null) {
            element.setValue(value);
        }
        subsystem.add(element);
    }

	private Map<String, BooleanBindable> booleanFields = new HashMap<String, BooleanBindable>();
	private Map<String, NumberBindable> numberFields = new HashMap<String, NumberBindable>();
	private Map<String, StringBindable> stringFields = new HashMap<String, StringBindable>();
	@Override
	public void valueChanged(ITable source, String key, Object value, boolean isNew) {
		if(value instanceof Boolean)
			booleanChanged(source, key, ((Boolean)value).booleanValue(), isNew);
		if(value instanceof Double)
			doubleChanged(source, key, ((Double)value).doubleValue(), isNew);
		if(value instanceof String)
			stringChanged(source, key, (String)value, isNew);
		if(value instanceof ITable)
			tableChanged(source, key, (ITable)value, isNew);
	}


	public void booleanChanged(ITable source, String key, boolean value, boolean isNew) {
		BooleanBindable field = booleanFields.get(key);
		if(field!=null)
			field.setBindableValue(value);
	}
	public void doubleChanged(ITable source, String key, double value, boolean isNew) {
		NumberBindable field = numberFields.get(key);
		if(field!=null)
			field.setBindableValue(value);
	}
	public void stringChanged(ITable source, String key, String value, boolean isNew) {
		StringBindable field = stringFields.get(key);
		if(field!=null)
			field.setBindableValue(value);
	}
	public void tableChanged(ITable source, String key, ITable value, boolean isNew) {
	}


	public MultiTypeBindable getTableEntryBindable(final String key){
		return new MultiTypeBindable(){
			@Override
			public void setBindableValue(boolean value) {
				if(table!=null)
					table.putBoolean(key, value);
			}
			@Override
			public void setBindableValue(double value) {
				if(table!=null)
					table.putNumber(key, value);
			}
			@Override
			public void setBindableValue(String value) {
				if(table!=null)
					table.putString(key, value);
			}
		};
	}


	protected void setBooleanBinding(String key, BooleanBindable displayer){
		if(booleanFields.containsKey(key))//TODO maybe remove and just let them overwrite???
			throw new RuntimeException("Cannot have multiple boolean fields for the same key: "+key);
		booleanFields.put(key, displayer);
	}
	protected void setNumberBinding(String key, NumberBindable displayer){
		if(numberFields.containsKey(key))
			throw new RuntimeException("Cannot have multiple number fields for the same key: "+key);
		numberFields.put(key, displayer);
	}
	protected void setStringBinding(String key, StringBindable displayer, String defaultValue){
		if(stringFields.containsKey(key))
			throw new RuntimeException("Cannot have multiple string fields for the same key: "+key);
                displayer.setBindableValue(defaultValue);
		stringFields.put(key, displayer);
	}



	
	public class BooleanTableCheckBox extends BindableBooleanCheckBox{
		public BooleanTableCheckBox(final String key){
			super(getTableEntryBindable(key));
			setBooleanBinding(key, this);
		}
	}
	public class BooleanTableField extends BindableBooleanField{
		public BooleanTableField(final String key){
			super(getTableEntryBindable(key));
			setBooleanBinding(key, this);
		}
	}
	public class NumberTableField extends BindableNumberField{
		public NumberTableField(final String key){
			super(getTableEntryBindable(key));
			setNumberBinding(key, this);
		}
	}
	public class StringTableField extends BindableStringField{
		public StringTableField(final String key){
			super(getTableEntryBindable(key));
			setStringBinding(key, this, "");
		}
	}
}
