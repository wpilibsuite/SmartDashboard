package edu.wpi.first.smartdashboard.gui.elements.bindings;

import org.jfree.data.general.*;


public abstract class AbstractNumberDatasetWidget extends AbstractValueWidget{

	private final NumberDatasetDisplayer data;
	protected AbstractNumberDatasetWidget(double defaultValue){
		setNumberBinding(data = new NumberDatasetDisplayer(defaultValue));
	}
	
	protected DefaultValueDataset getDataset(){
		return data;
	}
	
}
