package edu.wpi.first.smartdashboard.gui.elements.bindings;

public interface NumberBindable {
	NumberBindable NULL = new NumberBindable(){
		@Override
		public void setBindableValue(double value) {
		}
	};

	public void setBindableValue(double value);
}
