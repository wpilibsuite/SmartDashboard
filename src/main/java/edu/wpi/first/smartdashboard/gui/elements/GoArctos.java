package edu.wpi.first.smartdashboard.gui.elements;

import edu.wpi.first.smartdashboard.properties.IntegerProperty;
import edu.wpi.first.smartdashboard.properties.Property;

public class GoArctos extends Label {
    public static final String NAME = "Go Arctos";

    public final IntegerProperty howMuchGoArctos = new IntegerProperty(this, "How Much Go Arctos?", 1);

    public GoArctos() {
        super();
    }

    @Override
    public void init() {
        super.init();
        text.setValue("GO ARCTOS");
        propertyChanged(text);
    }

    @Override
    public void propertyChanged(Property property) {
        super.propertyChanged(property);
        if(property.equals(howMuchGoArctos)) {
            int howMuch = howMuchGoArctos.getValue();
            StringBuilder goArctosBuilder = new StringBuilder();

            for(int i = 0; i < howMuch; i++) {
                goArctosBuilder.append("GO ARCTOS ");
            }

            text.setValue(goArctosBuilder.toString());
            propertyChanged(text);
        }
    }
}
