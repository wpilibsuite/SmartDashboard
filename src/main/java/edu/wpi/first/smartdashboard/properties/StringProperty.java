package edu.wpi.first.smartdashboard.properties;

/**
 * @author Joe Grinstead
 */
public class StringProperty extends TextInputProperty<String> {

  public StringProperty(PropertyHolder element, String name) {
    super(String.class, element, name);
  }

  public StringProperty(PropertyHolder element, String name, String defaultValue) {
    super(String.class, element, name, defaultValue);
  }

  @Override
  public String getSaveValue() {
    // In XML, the characters "&" and "<" are not valid in values by themselves
    // Therefore here we replace these invalid characters by their escape sequences.
    // Retrieve the save value from the superclass
    String saveValue = super.getSaveValue();
    // Replace &, <, >, ', and "
    saveValue = saveValue.replace("&", "&amp;");
    saveValue = saveValue.replace("<", "&lt;");
    saveValue = saveValue.replace(">", "&gt;");
    saveValue = saveValue.replace("\'", "&apos;");
    saveValue = saveValue.replace("\"", "&quot;");
    return saveValue;
  }

  @Override
  protected String transformValue(Object value) {
    return value.toString();
  }
}
