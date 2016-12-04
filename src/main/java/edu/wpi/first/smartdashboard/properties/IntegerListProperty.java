package edu.wpi.first.smartdashboard.properties;

import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Joe Grinstead
 */
public class IntegerListProperty extends Property {

    private String valueSplit = ", ";
    private String delimiter = ",";

    public IntegerListProperty(PropertyHolder parent, String name) {
        super(parent, name);
    }

    public IntegerListProperty(PropertyHolder parent, String name, int[] value) {
        super(parent, name, value);
    }

    protected void setValueSplit(String split) {
        valueSplit = split;
    }

    protected void setDelimeter(String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    protected int[] transformValue(Object value) {
        if (value instanceof String) {
            String text = (String) value;

            String[] texts = text.split(delimiter);
            int[] values = new int[texts.length];

            for (int i = 0; i < texts.length; i++) {
                try {
                    values[i] = Integer.parseInt(texts[i].trim());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return values;
        } else if (value instanceof int[]) {
            return (int[]) value;
        }
        return null;
    }

    @Override
    public Object getTableValue() {
        return getSaveValue();
    }

    @Override
    public TableCellRenderer getRenderer() {
        return null;
    }

    @Override
    public String getSaveValue() {
        String text = "";
        int[] value = getValue();
        for (int i = 0; i < value.length; i++) {
            if (i > 0) {
                text += valueSplit;
            }
            text += value[i];
        }
        return text;
    }

    public int[] getValue() {
        return (int[]) super.getValue();
    }
}
