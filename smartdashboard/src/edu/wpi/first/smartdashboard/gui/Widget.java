package edu.wpi.first.smartdashboard.gui;

import edu.wpi.first.smartdashboard.gui.elements.bindings.StringBindable;
import edu.wpi.first.smartdashboard.gui.elements.bindings.NumberBindable;
import edu.wpi.first.smartdashboard.gui.elements.bindings.BooleanBindable;
import java.awt.event.*;
import java.text.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import org.jfree.data.general.*;

import edu.wpi.first.smartdashboard.gui.elements.*;
import edu.wpi.first.smartdashboard.types.*;
import edu.wpi.first.wpilibj.tables.*;

/**
 *
 * @author Joe Grinstead
 */
public abstract class Widget extends DisplayElement {

    private String name;
    private DataType type;

    public void setFieldName(String name) {
        this.name = name;
    }

    public String getFieldName() {
        return name;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    public DataType getType() {
        return type;
    }

    public abstract void setValue(Object value);

    public boolean supportsType(DataType type) {
        return DisplayElementRegistry.supportsType(getClass(), type);
    }

    public interface MultiTypeBindable extends BooleanBindable, NumberBindable, StringBindable {
    }

    public static class NumberSlider extends JSlider implements NumberBindable, ComponentListener, ChangeListener {

        private double min = 0;
        private double max = 100;
        private double value = 0;
        private int pixelWidth = 0;
        private final NumberBindable bindable;

        public NumberSlider(NumberBindable bindable) {
            this.bindable = bindable;
            addComponentListener(this);
            addChangeListener(this);
            calcBounds();
        }

        @Override
        public void setBindableValue(double value) {
            this.value = value;
            setUnscaledValue(value);
            revalidate();
            repaint();
        }

        private void setUnscaledValue(double value) {
            if (value < min) {
                value = min;
            }
            if (value > max) {
                value = max;
            }
            double percent = (value - min) / (max - min);
            setValue((int) (percent * pixelWidth));
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            if (!getValueIsAdjusting())//TODO implement better delay
            {
                bindable.setBindableValue(getValue() * (max - min) / pixelWidth + min);
            }
        }

        public void setMin(double min) {
            this.min = min;
            calcBounds();
        }

        public void setMax(double max) {
            this.max = max;
            calcBounds();
        }

        private void calcBounds() {
            pixelWidth = getWidth();
            setMinimum(0);
            setMaximum(pixelWidth);
            setUnscaledValue(value);
        }

        @Override
        public void componentResized(ComponentEvent e) {
            calcBounds();
        }

        @Override
        public void componentMoved(ComponentEvent e) {
        }

        @Override
        public void componentShown(ComponentEvent e) {
        }

        @Override
        public void componentHidden(ComponentEvent e) {
        }
    }

    public static class NumberProgressBar extends JProgressBar implements NumberBindable, ComponentListener {

        private double min = 0;
        private double max = 100;
        private double value = 0;
        private int pixelWidth = 0;

        public NumberProgressBar() {
            addComponentListener(this);
            calcBounds();
        }

        @Override
        public void setBindableValue(double value) {
            this.value = value;
            setUnscaledValue(value);
            revalidate();
            repaint();
        }

        private void setUnscaledValue(double value) {
            if (value < min) {
                value = min;
            }
            if (value > max) {
                value = max;
            }
            double percent = (value - min) / (max - min);
            setValue((int) (percent * pixelWidth));
        }

        public void setMin(double min) {
            this.min = min;
            calcBounds();
        }

        public void setMax(double max) {
            this.max = max;
            calcBounds();
        }

        private void calcBounds() {
            pixelWidth = getWidth();
            setMinimum(0);
            setMaximum(pixelWidth);
            setUnscaledValue(value);
        }

        @Override
        public void componentResized(ComponentEvent e) {
            calcBounds();
        }

        @Override
        public void componentMoved(ComponentEvent e) {
        }

        @Override
        public void componentShown(ComponentEvent e) {
        }

        @Override
        public void componentHidden(ComponentEvent e) {
        }
    }

    public static class NumberDatasetDisplayer extends DefaultValueDataset implements NumberBindable {

        public NumberDatasetDisplayer(double defaultValue) {
            super(defaultValue);
        }

        @Override
        public void setBindableValue(double value) {
            setValue(value);
        }
    }

    public static class ThreadSafeTextField extends JTextField {

        public ThreadSafeTextField(String text) {
            super(text);
        }

        public ThreadSafeTextField() {
            super();
        }

        public void setText(final String text) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ThreadSafeTextField.super.setText(text);
                }
            });
        }
    }

    public static abstract class EditorTextField extends ThreadSafeTextField {

        public EditorTextField() {
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    textChanged(getText());
                }
            });
            addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent fe) {}
                public void focusLost(FocusEvent fe) {
                    textChanged(getText());
                }
            });
            setHorizontalAlignment(JTextField.LEFT);
        }

        protected abstract void textChanged(String text);
    }

    public static class ThreadSafeCheckBox extends JCheckBox {

        public void setText(final boolean selected) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ThreadSafeCheckBox.super.setSelected(selected);
                }
            });
        }
    }

    public static abstract class BooleanCheckBox extends ThreadSafeCheckBox implements BooleanBindable {

        private boolean value;

        public BooleanCheckBox() {
            value = isSelected();
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    boolean newValue = isSelected();
                    if (value != newValue) {
                        if (setValue(newValue)) {
                            value = newValue;
                        } else {
                            resetValue();
                        }
                    }
                }
            });
        }

        protected void resetValue() {
            setBindableValue(value);
        }

        @Override
        public void setBindableValue(boolean value) {
            this.value = value;
            setSelected(value);
        }

        protected abstract boolean setValue(boolean value);
    }

    public static abstract class BooleanField extends EditorTextField implements BooleanBindable {

        private boolean value = false;

        protected void textChanged(String text) {
            if (!Boolean.toString(value).equals(text)) {
                boolean newValue = Boolean.parseBoolean(text);
                if (value != newValue) {
                    if (setValue(newValue)) {
                        setBindableValue(newValue);
                    } else {
                        resetValue();
                    }
                } else {
                    resetValue();//reset the text but don't set the value again
                }
            }
        }

        protected void resetValue() {
            setBindableValue(value);
        }

        @Override
        public void setBindableValue(boolean value) {
            this.value = value;
            setText(Boolean.toString(value));
        }

        protected abstract boolean setValue(boolean value);
    }

    public static abstract class NumberField extends EditorTextField implements NumberBindable {

        private double value = Double.NaN;
        private DecimalFormat formatter = new DecimalFormat("0.000", new DecimalFormatSymbols(Locale.US));

        protected void textChanged(String text) {
            try {
                double newValue = Double.parseDouble(text);
                if (value != newValue) {
                    if (setValue(newValue)) {
                        value = newValue;
                    } else {
                        resetValue();
                    }
                }
            } catch (NumberFormatException ex) {
                resetValue();
            }
        }

        protected void resetValue() {
            if (Double.isNaN(value)) {
                setText("");
            } else {
                setBindableValue(value);
            }
        }

        @Override
        public void setBindableValue(double value) {
            this.value = value;
            setText(formatter.format(value));
        }

        public void setFormatter(DecimalFormat formatter) {
            this.formatter = formatter;
        }

        protected abstract boolean setValue(double value);
    }

    public static abstract class StringField extends EditorTextField implements StringBindable {

        private String value = null;

        protected void textChanged(String newValue) {
            if (!value.equals(newValue)) {
                if (setValue(newValue)) {
                    value = newValue;
                } else {
                    resetValue();
                }
            }
        }

        protected void resetValue() {
            if (value == null) {
                setText("");
            } else {
                setBindableValue(value);
            }
        }

        @Override
        public void setBindableValue(String value) {
            this.value = value;
            setText(value);
        }

        protected abstract boolean setValue(String value);
    }

    public static class UneditableBooleanCheckBox extends BooleanCheckBox {

        {
            setEnabled(false);
        }

        @Override
        protected boolean setValue(boolean value) {
            return false;
        }
    }

    public static class UneditableBooleanField extends BooleanField {

        {
            setEditable(false);
        }

        @Override
        protected boolean setValue(boolean value) {
            return false;
        }
    }

    public static class UneditableNumberField extends NumberField {

        {
            setEditable(false);
        }

        @Override
        protected boolean setValue(double value) {
            return false;
        }
    }

    public static class UneditableStringField extends StringField {

        {
            setEditable(false);
        }

        @Override
        protected boolean setValue(String value) {
            return false;
        }
    }

    public static class TitledBorderStringDisplayer implements StringBindable {

        private final JComponent component;

        public TitledBorderStringDisplayer(JComponent component) {
            this.component = component;
        }

        @Override
        public void setBindableValue(String value) {
            component.setBorder(BorderFactory.createTitledBorder(value));
        }
    }

    public static class BindableBooleanCheckBox extends BooleanCheckBox {

        private final BooleanBindable bindable;

        public BindableBooleanCheckBox(BooleanBindable bindable) {
            this.bindable = bindable;
        }

        @Override
        protected boolean setValue(boolean value) {
            bindable.setBindableValue(value);
            return true;
        }
    }

    public static class BindableBooleanField extends BooleanField {

        private final BooleanBindable bindable;

        public BindableBooleanField(BooleanBindable bindable) {
            this.bindable = bindable;
        }

        @Override
        protected boolean setValue(boolean value) {
            bindable.setBindableValue(value);
            return true;
        }
    }

    public static class BindableNumberField extends NumberField {

        private final NumberBindable bindable;
       

        public BindableNumberField(NumberBindable bindable) {
            this.bindable = bindable;
        }

        @Override
        protected boolean setValue(double value) {
            bindable.setBindableValue(value);
            return true;
        }
    }

    public static class BindableStringField extends StringField {

        private final StringBindable bindable;

        public BindableStringField(StringBindable bindable) {
            this.bindable = bindable;
        }

        @Override
        protected boolean setValue(String value) {
            bindable.setBindableValue(value);
            return true;
        }
    }

    public static class BindableTableEntry implements BooleanBindable, NumberBindable, StringBindable {

        private final ITable table;
        private final String key;

        public BindableTableEntry(ITable table, String key) {
            this.table = table;
            this.key = key;
        }

        @Override
        public void setBindableValue(String value) {
            table.putString(key, value);
        }

        @Override
        public void setBindableValue(double value) {
            table.putNumber(key, value);
        }

        @Override
        public void setBindableValue(boolean value) {
            table.putBoolean(key, value);
        }
    }

    public static class BooleanMultiBindable implements BooleanBindable {

        private final BooleanBindable[] bindables;

        public BooleanMultiBindable(BooleanBindable... bindables) {
            this.bindables = bindables;
        }

        @Override
        public void setBindableValue(boolean value) {
            for (BooleanBindable bindable : bindables) {
                bindable.setBindableValue(value);
            }
        }
    }

    public static class NumberMultiBindable implements NumberBindable {

        private final NumberBindable[] bindables;

        public NumberMultiBindable(NumberBindable... bindables) {
            this.bindables = bindables;
        }

        @Override
        public void setBindableValue(double value) {
            for (NumberBindable bindable : bindables) {
                bindable.setBindableValue(value);
            }
        }
    }

    public static class StringMultiBindable implements StringBindable {

        private final StringBindable[] bindables;

        public StringMultiBindable(StringBindable... bindables) {
            this.bindables = bindables;
        }

        @Override
        public void setBindableValue(String value) {
            for (StringBindable bindable : bindables) {
                bindable.setBindableValue(value);
            }
        }
    }
}
