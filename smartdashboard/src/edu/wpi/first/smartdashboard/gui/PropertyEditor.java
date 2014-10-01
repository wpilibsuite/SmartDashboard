package edu.wpi.first.smartdashboard.gui;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

import edu.wpi.first.smartdashboard.properties.*;

/**
 * 
 * @author brad
 */
public class PropertyEditor extends JDialog {

	private JTable table;
	private PropTableModel tableModel;
	private Map<String, Property> values;
	private String[] names;

	public PropertyEditor(JFrame frame) {
		super(frame, true);
		tableModel = new PropTableModel();
		table = new PropertiesTable(tableModel);
		table.setGridColor(Color.LIGHT_GRAY);
		table.setRowSelectionAllowed(false);
		JScrollPane scrollPane = new JScrollPane(table);
		setBounds(100, 100, 300, 400);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scrollPane, BorderLayout.CENTER);
	}

	void setPropertyHolder(PropertyHolder data) {
		if (table.isEditing())
			table.getCellEditor().stopCellEditing();
		
		if (data instanceof Widget) {
			this.setTitle(((Widget) data).getFieldName());
		} else {
			this.setTitle("Edit Properties");
		}
		values = data.getProperties();
		names = values.keySet().toArray(new String[values.size()]);
		tableModel.fireTableDataChanged();
	}

	class PropertiesTable extends JTable {

		AbstractTableModel model;

		PropertiesTable(AbstractTableModel model) {
			super(model);
			this.model = model;
		}

		@Override
		public TableCellEditor getCellEditor(int row, int col) {
			TableCellEditor editor = values.get(names[row]).getEditor(PropertyEditor.this);
			return editor == null ? super.getCellEditor(row, col) : editor;
		}

		@Override
		public TableCellRenderer getCellRenderer(int row, int col) {
			if (col == 0) {
				return super.getCellRenderer(row, col);
			}
			TableCellRenderer renderer = values.get(names[row]).getRenderer();
			return renderer == null ? super.getCellRenderer(row, col) : renderer;
		}
	}

	class PropTableModel extends AbstractTableModel {

		public int getRowCount() {
			return values.size();
		}

		public int getColumnCount() {
			return 2;
		}

		@Override
		public String getColumnName(int i) {
			if (i == 0) {
				return "Property";
			} else if (i == 1) {
				return "Value";
			} else {
				return "Error";
			}
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			boolean editable = (col == 1);
			return editable;
		}

		public Object getValueAt(int row, int col) {
			switch (col) {
			case 0:
				return names[row];
			case 1:
				return values.get(names[row]).getTableValue();
			default:
				assert false;
				return "Bad row, col";
			}
		}

		@Override
		public void setValueAt(Object value, int row, int col) {
			assert (col == 1);
			values.get(names[row]).setValue(value);
		}
	}
}
