package edu.wpi.first.smartdashboard.types;

import edu.wpi.first.smartdashboard.gui.Widget;
import edu.wpi.first.smartdashboard.gui.elements.TextBox;
import edu.wpi.first.wpilibj.tables.ITable;

/**
 *
 * @author Joe Grinstead
 */
public class DataType {

    private final DataType[] parents;
    private final String name;
    private Class<? extends Widget> defaultClass;

    // This is package in order to prevent others from subclassing DataType directly.
    DataType(String name, DataType... parents) {
        this.name = name;
        this.parents = parents;
    }

    // This is package in order to prevent others from subclassing DataType directly.
    DataType(String name, Class<? extends Widget> defaultClass, DataType... parents) {
        this.name = name;
        this.parents = parents;
        this.defaultClass = defaultClass;
    }

    public void setDefault(Class<? extends Widget> defaultClass) {
        this.defaultClass = defaultClass;
    }

    public Class<? extends Widget> getDefault() {
        return defaultClass;
    }

    public DataType[] getParents() {
        return parents.clone();
    }

    public boolean isNamed() {
        return false;
    }

    public String getName() {
        return name;
    }

    public boolean isChildOf(DataType parent) {
        if (equals(parent)) {
            return true;
        } else for (int i = 0; i < parents.length; i++) {
            if (parents[i].isChildOf(parent)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "[Type:" + name + "]";
    }

    public static DataType getType(String type, boolean isNamedType) {
        if (isNamedType) {
            return NamedDataType.get(type);
        }
        if (type.equals(NUMBER.getName())) {
            return NUMBER;
        } else if (type.equals(BOOLEAN.getName())) {
            return BOOLEAN;
        } else if (type.equals(BASIC.getName())) {
            return BASIC;
        } else if (type.equals(STRING.getName())) {
            return STRING;
        } else if (type.equals(TABLE.getName())) {
            return TABLE;
        } else {
            return NamedDataType.get(type);
        }
    }

    public static DataType getType(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Can not be given null value");
        } else if (value instanceof ITable) {
            ITable table = (ITable) value;

            if (table.containsKey("~TYPE~")) {
                String typeName = table.getString("~TYPE~");
                return NamedDataType.get(typeName);
            } else {
                return DataType.TABLE;
            }
        } else if (value instanceof Double) {
            return DataType.NUMBER;
        } else if (value instanceof Boolean) {
            return DataType.BOOLEAN;
        } else if (value instanceof String) { // String
            return DataType.STRING;
        } else {
            throw new IllegalArgumentException("Can not get type for class:" + value.getClass().getName());
        }
    }
    public static final DataType BASIC = new DataType("Basic", TextBox.class);
    public static final DataType NUMBER = new DataType("Number", TextBox.class, BASIC);
    public static final DataType BOOLEAN = new DataType("Boolean", TextBox.class, BASIC);
    public static final DataType STRING = new DataType("String", TextBox.class, BASIC);
    public static final DataType TABLE = new DataType("Table");
}
