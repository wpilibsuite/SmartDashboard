package edu.wpi.first.smartdashboard.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import edu.wpi.first.smartdashboard.types.*;

/**
 * 
 * @author Joe Grinstead
 */
public class GlassPane extends JPanel {

	private static final int DRAG_BUFFER = 5;
	private boolean dragging;
	private Rectangle dragStartBounds;
	private Dimension dragMinSizeDelta;
	private Dimension dragMaxSizeDelta;
	private Point dragStartPoint;
	private int dragType = -1;
	private Map<Integer, Rectangle> areas = new HashMap<Integer, Rectangle>();
	private JPopupMenu elementMenu;
	private JMenuItem resizeMenu;
	private JMenu changeToMenu;
	private DisplayElement selectedElement;
	private DisplayElement menuElement;
	private boolean showGrid = false;

	private final DashboardPanel panel;
	private final DashboardFrame frame;

	GlassPane(DashboardFrame frame, DashboardPanel panel) {
		this.frame = frame;
		this.panel = panel;
		elementMenu = new JPopupMenu();
		elementMenu.add(changeToMenu = new JMenu("Change to..."));
		elementMenu.add(new JMenuItem(new PropertiesItemAction("Properties...")));
		elementMenu.add(new JMenuItem(new MoveToBackAction("Send to Back")));
		elementMenu.add(resizeMenu = new JMenuItem(new ResetSizeAction()));
		elementMenu.add(new JMenuItem(new DeleteItemAction()));

		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent e) {
				requestFocus();
				setShowingGrid(false);
			}
		});

		addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
					setShowingGrid(true);
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
					setShowingGrid(false);
				}
			}
		});

		setOpaque(false);
		setFocusable(true);

		GlassMouseListener listener = new GlassMouseListener();
		addMouseListener(listener);
		addMouseMotionListener(listener);
	}

	public void setShowingGrid(boolean showGrid) {
		if (this.showGrid != showGrid) {
			this.showGrid = showGrid;
			repaint();
		}
	}

	private DisplayElement findElementContaining(Point point) {
		return panel.findElementContaining(point);
	}

	private void prepareElementMenu(DisplayElement element) {
		menuElement = element;

		Dimension savedSize = menuElement.getSavedSize();
		resizeMenu.setEnabled(savedSize.width != -1 || savedSize.height != -1);

		if (element instanceof Widget) {
			DataType type = ((Widget) element).getType();

			if (type == null) {
				changeToMenu.setEnabled(false);
			} else {
				changeToMenu.setEnabled(true);

				Set<Class<? extends Widget>> choices = DisplayElementRegistry.getWidgetsForType(type);

				changeToMenu.removeAll();
				int count = 0;
				for (Class<? extends Widget> c : choices) {
					if (!c.equals(element.getClass())) {
						count++;
						changeToMenu.add(new ChangeToAction(DisplayElement.getName(c), c));
					}
				}
				if (count == 0) {
					changeToMenu.setEnabled(false);
				}
			}
		} else {
			changeToMenu.setEnabled(false);
		}
	}

	private void showEditor(DisplayElement element) {
		PropertyEditor editor = frame.getPropertyEditor();
		editor.setPropertyHolder(element);
		editor.setVisible(true);
	}

	private void defineBounds() {
		Rectangle sb = selectedElement.getBounds();

		int ybuffer = Math.max(Math.min(sb.height / 5, DRAG_BUFFER), 1);
		int xbuffer = Math.max(Math.min(sb.width / 5, DRAG_BUFFER), 1);

		areas.clear();

		if (selectedElement.isResizable()) {
			Rectangle area = new Rectangle(sb.x - xbuffer, sb.y - ybuffer, 2 * xbuffer, 2 * ybuffer);
			areas.put(SwingConstants.NORTH_WEST, area);

			area = new Rectangle(sb.x + xbuffer, sb.y - ybuffer, sb.width - 2 * xbuffer, 2 * ybuffer);
			areas.put(SwingConstants.NORTH, area);

			area = new Rectangle(sb.x + sb.width - xbuffer, sb.y - ybuffer, 2 * xbuffer, 2 * ybuffer);
			areas.put(SwingConstants.NORTH_EAST, area);

			area = new Rectangle(sb.x + sb.width - xbuffer, sb.y + ybuffer, 2 * xbuffer, sb.height - 2 * ybuffer);
			areas.put(SwingConstants.EAST, area);

			area = new Rectangle(sb.x + sb.width - xbuffer, sb.y + sb.height - ybuffer, 2 * xbuffer, 2 * ybuffer);
			areas.put(SwingConstants.SOUTH_EAST, area);

			area = new Rectangle(sb.x + xbuffer, sb.y + sb.height - ybuffer, sb.width - 2 * xbuffer, 2 * ybuffer);
			areas.put(SwingConstants.SOUTH, area);

			area = new Rectangle(sb.x - xbuffer, sb.y + sb.height - ybuffer, 2 * xbuffer, 2 * ybuffer);
			areas.put(SwingConstants.SOUTH_WEST, area);

			area = new Rectangle(sb.x - xbuffer, sb.y + ybuffer, 2 * xbuffer, sb.height - 2 * ybuffer);
			areas.put(SwingConstants.WEST, area);

			area = new Rectangle(sb.x + xbuffer, sb.y + ybuffer, sb.width - 2 * xbuffer, sb.height - 2 * ybuffer);
			areas.put(SwingConstants.CENTER, area);
		} else {
			areas.put(SwingConstants.CENTER, sb);
		}
	}

	private static final Color GRID_COLOR = new Color(0, 0, 0, 40);

	@Override
	protected void paintComponent(Graphics g) {
		Rectangle bounds = getBounds();

		if (selectedElement != null) {
			Rectangle eb = selectedElement.getBounds();

			g.setColor(Color.GRAY);
			g.drawRoundRect(eb.x - 1, eb.y - 1, eb.width + 1, eb.height + 1, 8, 8);
		}

		if (showGrid) {
			g.setColor(GRID_COLOR);

			DashboardPrefs pref = frame.getPrefs();
			int[] w = pref.grid_widths.getValue();
			int[] h = pref.grid_heights.getValue();

			int cell = -1;
			for (int i = 0; i < bounds.width; i += w[cell = (cell + 1) % w.length]) {
				g.drawLine(i, 0, i, bounds.height);
			}

			cell = -1;
			for (int i = 0; i < bounds.height; i += h[cell = (cell + 1) % h.length]) {
				g.drawLine(0, i, bounds.width, i);
			}
		}
	}

	private void setSelected(DisplayElement element) {
		if (selectedElement != element) {
			selectedElement = element;
			if (selectedElement == null) {
				areas.clear();
			} else {
				defineBounds();
			}
			repaint();
		}
	}

	private class GlassMouseListener extends MouseAdapter {

		private int lastDW;
		private int lastDH;
		private int lastDX;
		private int lastDY;

		private int adjust(int delta, int original, int[] cells) {
			if (showGrid) {
				int total = 0;
				for (int cell : cells) {
					total += cell;
				}

				int n = (delta + original) % total;
				if (n < 0) {
					n += total;
				}

				for (int i = 0, cumulative = 0; i < cells.length; cumulative += cells[i++]) {
					if (n < cumulative + cells[i] / 2) {
						return delta - n + cumulative;
					}
				}

				return delta - n + total;
			} else {
				return delta;
			}
		}

		private int adjustX(int value) {
			return adjust(value, dragStartBounds.x, frame.getPrefs().grid_widths.getValue());
		}

		private int adjustY(int value) {
			return adjust(value, dragStartBounds.y, frame.getPrefs().grid_heights.getValue());
		}

		private int adjustW(int value) {
			return adjust(value, dragStartBounds.x + dragStartBounds.width, frame.getPrefs().grid_widths.getValue());
		}

		private int adjustH(int value) {
			return adjust(value, dragStartBounds.y + dragStartBounds.height, frame.getPrefs().grid_heights.getValue());
		}

		private int inRange(boolean horizontal, int value) {
			int min = horizontal ? dragMinSizeDelta.width : dragMinSizeDelta.height;
			int max = horizontal ? dragMaxSizeDelta.width : dragMaxSizeDelta.height;
			return value <= max ? value < min ? min : value : max;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			dragType = -1;

			if (selectedElement != null) {
				if (e.isPopupTrigger()) {
					prepareElementMenu(selectedElement);
					elementMenu.show(GlassPane.this, e.getPoint().x, e.getPoint().y);
				} else {
					for (Map.Entry<Integer, Rectangle> entry : areas.entrySet()) {
						if (entry.getValue().contains(e.getPoint())) {
							dragType = entry.getKey();
							break;
						}
					}
				}
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (e.isMetaDown()) {
				return;
			}

			if (selectedElement != null && dragType != -1) {
				if (!dragging) {
					dragging = true;

					dragStartBounds = selectedElement.getBounds();
					dragMinSizeDelta = selectedElement.getMinimumSize();
					dragMinSizeDelta.width -= dragStartBounds.width;
					dragMinSizeDelta.height -= dragStartBounds.height;
					dragMaxSizeDelta = selectedElement.getMaximumSize();
					dragMaxSizeDelta.width -= dragStartBounds.width;
					dragMaxSizeDelta.height -= dragStartBounds.height;
					dragStartPoint = e.getPoint();

					lastDH = lastDW = lastDX = lastDY = 0;
				}

				int dx, dy, dw, dh;
				dx = dy = dw = dh = 0;

				switch (dragType) {
				case SwingConstants.NORTH:
					dh = inRange(false, -adjustY(e.getPoint().y - dragStartPoint.y));
					dy = -dh;
					break;
				case SwingConstants.NORTH_EAST:
					dh = inRange(false, -adjustY(e.getPoint().y - dragStartPoint.y));
					dy = -dh;
					dw = inRange(true, adjustW(e.getPoint().x - dragStartPoint.x));
					break;
				case SwingConstants.EAST:
					dw = inRange(true, adjustW(e.getPoint().x - dragStartPoint.x));
					break;
				case SwingConstants.SOUTH_EAST:
					dw = inRange(true, adjustW(e.getPoint().x - dragStartPoint.x));
					dh = inRange(false, adjustH(e.getPoint().y - dragStartPoint.y));
					break;
				case SwingConstants.SOUTH:
					dh = inRange(false, adjustH(e.getPoint().y - dragStartPoint.y));
					break;
				case SwingConstants.SOUTH_WEST:
					dh = inRange(false, adjustH(e.getPoint().y - dragStartPoint.y));
					dw = inRange(true, -adjustX(e.getPoint().x - dragStartPoint.x));
					dx = -dw;
					break;
				case SwingConstants.WEST:
					dw = inRange(true, -adjustX(e.getPoint().x - dragStartPoint.x));
					dx = -dw;
					break;
				case SwingConstants.NORTH_WEST:
					dh = inRange(false, -adjustY(e.getPoint().y - dragStartPoint.y));
					dy = -dh;
					dw = inRange(true, -adjustX(e.getPoint().x - dragStartPoint.x));
					dx = -dw;
					break;
				case SwingConstants.CENTER:
					DashboardPrefs prefs = frame.getPrefs();
					int leading = adjust(e.getPoint().x - dragStartPoint.x, dragStartBounds.x, prefs.grid_widths.getValue());
					int trailing = adjust(e.getPoint().x - dragStartPoint.x, dragStartBounds.x + dragStartBounds.width, prefs.grid_widths.getValue());
					dx = Math.abs(leading) < Math.abs(trailing) ? leading : trailing;
					leading = adjust(e.getPoint().y - dragStartPoint.y, dragStartBounds.y, prefs.grid_heights.getValue());
					trailing = adjust(e.getPoint().y - dragStartPoint.y, dragStartBounds.y + dragStartBounds.height, prefs.grid_heights.getValue());
					dy = Math.abs(leading) < Math.abs(trailing) ? leading : trailing;
					break;
				default:
					assert false;
				}

				boolean changed = false;

				if (dw != lastDW || dh != lastDH) {
					changed = true;
					Dimension size = selectedElement.getSavedSize();
					if (dw != lastDW) {
						size.width = dragStartBounds.width + dw;
						lastDW = dw;
					}
					if (dh != lastDH) {
						size.height = dragStartBounds.height + dh;
						lastDH = dh;
					}
					selectedElement.setSavedSize(size);
				}
				if (dx != lastDX || dy != lastDY) {
					changed = true;
					Point origin = dragStartBounds.getLocation();
					origin.translate(dx, dy);
					selectedElement.setSavedLocation(origin);
					lastDX = dx;
					lastDY = dy;
				}

				if (changed) {
					panel.revalidateBacking();
					frame.repaint();
				}
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			dragType = -1;
			dragging = false;
			setSelected(null);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (dragging == true) {
				dragging = false;
				defineBounds();
				mouseMoved(e);
			} else {
				if (selectedElement != null) {
					if (e.isPopupTrigger()) {
						prepareElementMenu(selectedElement);
						elementMenu.show(GlassPane.this, e.getPoint().x, e.getPoint().y);
					} else if (e.getClickCount() == 2) {
						showEditor(selectedElement);
					}
				}
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			DisplayElement element = findElementContaining(e.getPoint());
			if (element != selectedElement) {
				if (element == null) {
					boolean found = false;
					for (Rectangle area : areas.values()) {
						if (area.contains(e.getPoint())) {
							found = true;
							break;
						}
					}
					if (!found) {
						setSelected(null);
					}
				} else {
					setSelected(element);
				}
			}

			if (!areas.isEmpty()) {
				AreaLoop: for (Map.Entry<Integer, Rectangle> entry : areas.entrySet()) {
					Rectangle area = entry.getValue();
					if (area.contains(e.getPoint())) {
						switch (entry.getKey()) {
						case SwingConstants.NORTH:
							setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
							break AreaLoop;
						case SwingConstants.NORTH_EAST:
							setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
							break AreaLoop;
						case SwingConstants.EAST:
							setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
							break AreaLoop;
						case SwingConstants.SOUTH_EAST:
							setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
							break AreaLoop;
						case SwingConstants.SOUTH:
							setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
							break AreaLoop;
						case SwingConstants.SOUTH_WEST:
							setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
							break AreaLoop;
						case SwingConstants.WEST:
							setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
							break AreaLoop;
						case SwingConstants.NORTH_WEST:
							setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
							break AreaLoop;
						case SwingConstants.CENTER:
							setCursor(Cursor.getDefaultCursor());
							break AreaLoop;
						default:
							assert false;
						}
					}
				}
			} else {
				setCursor(Cursor.getDefaultCursor());
			}
		}
	}

	private class ChangeToAction extends AbstractAction {

		Class<? extends Widget> elementClass;

		private ChangeToAction(String string, Class<? extends Widget> elementClass) {
			super(string);
			this.elementClass = elementClass;
		}

		public void actionPerformed(ActionEvent e) {
			if (menuElement instanceof Widget) {
				Widget oldElement = (Widget) menuElement;

				if (panel.getTable().containsKey(oldElement.getFieldName())) {
					Object value = panel.getTable().getValue(oldElement.getFieldName());
					panel.setField(oldElement.getFieldName(), elementClass, value, oldElement.getLocation());
				} else {
					panel.setField(oldElement.getFieldName(), elementClass, oldElement.getType(), null, oldElement.getLocation());
				}
			}
		}
	}

	private class MoveToBackAction extends AbstractAction {

		private MoveToBackAction(String string) {
			super(string);
		}

		public void actionPerformed(ActionEvent e) {
			panel.shiftToBack(menuElement);
		}
	}

	private class ResetSizeAction extends AbstractAction {

		private ResetSizeAction() {
			super("Reset Size");
		}

		public void actionPerformed(ActionEvent e) {
			menuElement.setSavedSize(new Dimension(-1, -1));
			panel.revalidateBacking();
		}
	}

	/**
	 * Display the properties for a display element. The properties are
	 * displayed for a display element so they can be viewed and updated.
	 */
	private class PropertiesItemAction extends AbstractAction {

		private PropertiesItemAction(String string) {
			super(string);
		}

		public void actionPerformed(ActionEvent ae) {
			showEditor(menuElement);
		}
	}

	private class DeleteItemAction extends AbstractAction {

		public DeleteItemAction() {
			super("Remove");
		}

		public void actionPerformed(ActionEvent e) {
			if (menuElement instanceof StaticWidget) {
				panel.removeElement((StaticWidget) menuElement);
			} else if (menuElement instanceof Widget) {
				panel.removeField(((Widget) menuElement).getFieldName());
			}
		}
	}
}
