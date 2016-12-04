package edu.wpi.first.smartdashboard.properties;

/**
 *
 * @author Joe Grinstead
 */
public class IPAddressProperty extends IntegerListProperty {

    public IPAddressProperty(PropertyHolder parent, String name) {
        super(parent, name);

        setDelimeter("\\.");
        setValueSplit(".");
    }

    public IPAddressProperty(PropertyHolder parent, String name, int[] value) {
        super(parent, name, value);

        setDelimeter("\\.");
        setValueSplit(".");
    }

    @Override
    protected int[] transformValue(Object value) {
        int[] result = super.transformValue(value);
        if (result == null || result.length != 4) {
            return null;
        } else {
            for (int n : result) {
                if (n < 0 || n > 255) {
                    return null;
                }
            }
            return result;
        }
    }
}
