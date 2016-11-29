package edu.wpi.first.smartdashboard.gui.elements.bindings;

public interface BooleanBindable {
	BooleanBindable NULL = new BooleanBindable(){
		@Override
		public void setBindableValue(boolean value) {
		}
	};
	public void setBindableValue(boolean value);
}
