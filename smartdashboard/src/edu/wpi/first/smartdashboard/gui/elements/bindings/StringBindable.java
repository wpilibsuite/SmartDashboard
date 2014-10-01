package edu.wpi.first.smartdashboard.gui.elements.bindings;

public interface StringBindable {
	StringBindable NULL = new StringBindable(){
		@Override
		public void setBindableValue(String value) {
		}
	};
	public void setBindableValue(String value);
}
