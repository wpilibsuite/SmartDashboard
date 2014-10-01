package edu.wpi.first.smartdashboard.gui.elements;

import edu.wpi.first.smartdashboard.gui.elements.bindings.AbstractTableWidget;
import edu.wpi.first.smartdashboard.properties.*;
import edu.wpi.first.smartdashboard.types.*;
import edu.wpi.first.smartdashboard.types.named.*;
import edu.wpi.first.wpilibj.networktables2.type.StringArray;
import edu.wpi.first.wpilibj.tables.*;
import java.awt.Component;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 *
 * @author Joe Grinstead
 */
public class Chooser extends AbstractTableWidget implements ITableListener
{

	private static final String DEFAULT = "default";
	private static final String SELECTED = "selected";
	private static final String OPTIONS = "options";
	public static final DataType[] TYPES =
	{
		StringChooserType.get()
	};
	public final BooleanProperty editable = new BooleanProperty(this, "Editable", true);
	public final BooleanProperty useRadioButtons = new BooleanProperty(this, "Use Radio Buttons", true);
	private Display display;
	private String selection;
	private StringArray choices = new StringArray();

	@Override
	public void init()
	{
		setResizable(false);

		selection = null;

		display = useRadioButtons.getValue() ? new RadioButtons() : new ComboBox();
		display.setChoices(choices);
	}

	@Override
	public void valueChanged(ITable source, String key, Object value, boolean isNew)
	{
		if (key.equals(OPTIONS))
		{
			table.retrieveValue(OPTIONS, choices);
			display.setChoices(choices);
		}
		//if(key.equals(DEFAULT))
		//    display.setDefault(source.getString(DEFAULT)); //TODO handle change in default?
		if (key.equals(SELECTED))
		{
			display.setSelected(source.getString(SELECTED));
		}
	}

	@Override
	public void propertyChanged(Property property)
	{
		if (property == useRadioButtons)
		{
			changeChoices();
		} else
		{
			if (property == editable)
			{
				display.setEditable(editable.getValue());
			}
		}
	}

	private void changeChoices()
	{
		remove(display.getComponent());
		display = useRadioButtons.getValue() ? new RadioButtons() : new ComboBox();
		display.setChoices(choices);
	}

	private abstract class Display
	{

		JPanel panel = new JPanel();

		public Display()
		{
			panel.setOpaque(false);

			add(panel);
		}

		abstract void setEditable(boolean editable);

		abstract void setChoices(StringArray choices);

		abstract void setSelected(String selected);
		
		Component getComponent()
		{
			return panel;
		}
	}

	private class RadioButtons extends Display implements ActionListener
	{

		JPanel groupPanel;
		ButtonGroup group;
		JRadioButton selected;
		Map<String, JRadioButton> buttons;

		@Override
		void setEditable(boolean editable)
		{
			for (JRadioButton button : buttons.values())
			{
				button.setEnabled(editable);
			}
		}

		@Override
		void setChoices(StringArray choices)
		{
			if (groupPanel != null)
			{
				panel.remove(groupPanel);
				for (JRadioButton button : buttons.values())
				{
					group.remove(button);
				}
				buttons.clear();
			}

			groupPanel = new JPanel();
			groupPanel.setOpaque(false);
			groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.Y_AXIS));

			group = new ButtonGroup();

			buttons = new HashMap<String, JRadioButton>();


			boolean hasSelection = false;

			for (int i = 0; i < choices.size(); i++)
			{
				String choice = choices.get(i);
				hasSelection |= choice.equals(selection);

				JRadioButton button = new JRadioButton(choice);
				buttons.put(choice, button);
				group.add(button);
				groupPanel.add(button);
				button.setActionCommand(choice);
				button.addActionListener(this);
			}

			if (!hasSelection)
			{
				selection = null;
			}

			if (table != null && table.containsKey(SELECTED))
			{
				selection = table.getString(SELECTED);
			}

			if (table != null && selection != null)
			{
				table.putString(SELECTED, selection);
				selected = buttons.get(selection);
				selected.setSelected(true);
			} else
			{
				if (table != null && table.containsKey(DEFAULT))
				{
					selected = buttons.get(table.getString(DEFAULT));
					selected.setSelected(true);
				} else
				{
					selected = null;
				}
			}

			setEnabled(editable.getValue());

			panel.add(groupPanel);

			revalidate();
			repaint();

			setSize(getPreferredSize());
		}

		@Override
		void setSelected(String selected)
		{
			buttons.get(selected).setSelected(true);
		}

		public void actionPerformed(ActionEvent e)
		{
			String userChoice = e.getActionCommand();
			if (selection == null || !selection.equals(userChoice))
			{
				selection = userChoice;
				table.putString(SELECTED, selection);
			}
		}
	}

	private class ComboBox extends Display implements ItemListener
	{

		JComboBox combo;

		@Override
		void setEditable(boolean editable)
		{
			if (combo != null)
			{
				combo.setEnabled(editable);
			}
		}

		@Override
		void setChoices(StringArray choices)
		{
			if (combo != null)
			{
				panel.remove(combo);
				combo.removeItemListener(this);
			}

			combo = new JComboBox();

			synchronized (table)
			{
				boolean hasSelection = false;

				for (int i = 0; i < choices.size(); i++)
				{
					String choice = choices.get(i);
					hasSelection |= choice.equals(selection);
					combo.addItem(choice);
				}

				if (!hasSelection)
				{
					selection = null;
				}

				if (table.containsKey(SELECTED))
				{
					selection = table.getString(SELECTED);
				}

				if (selection != null)
				{
					combo.setSelectedItem(selection);
					table.putString(SELECTED, selection);
				} else
				{
					if (table.containsKey(DEFAULT))
					{
						combo.setSelectedItem(table.getString(DEFAULT));
					}
				}
			}

			panel.add(combo);

			combo.addItemListener(this);

			combo.setEnabled(editable.getValue());

			revalidate();
			repaint();

			setSize(getPreferredSize());
		}

		@Override
		void setSelected(String selected)
		{
			if (combo != null)
			{
				combo.setSelectedItem(selected);
			}
		}

		public void itemStateChanged(ItemEvent e)
		{
			if (e.getStateChange() == ItemEvent.SELECTED)
			{
				String userChoice = (String) e.getItem();
				if (!userChoice.equals(selection))
				{
					selection = userChoice;
					table.putString(SELECTED, selection);
				}
			}
		}
	}
}
