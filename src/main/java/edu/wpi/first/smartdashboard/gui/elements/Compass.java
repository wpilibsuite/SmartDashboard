package edu.wpi.first.smartdashboard.gui.elements;

import edu.wpi.first.smartdashboard.gui.elements.bindings.AbstractTableWidget;
import edu.wpi.first.smartdashboard.gui.elements.bindings.NumberBindable;
import edu.wpi.first.smartdashboard.properties.ColorProperty;
import edu.wpi.first.smartdashboard.properties.DoubleProperty;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;
import edu.wpi.first.smartdashboard.types.named.GyroType;
import edu.wpi.first.wpilibj.tables.ITable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CompassPlot;
import org.jfree.data.general.DefaultValueDataset;

/**
 * @author Paul
 */
public class Compass extends AbstractTableWidget implements NumberBindable {

  public static final DataType[] TYPES = {DataType.NUMBER, GyroType.get()};
  public final DoubleProperty circumference = new DoubleProperty(this, "Circumference", 360.0);
  public final ColorProperty ringColor = new ColorProperty(this, "Ring Color", Color.YELLOW);
  private JPanel chartPanel;
  private DefaultValueDataset data = new DefaultValueDataset(0);
  private CompassPlot m_compass;

  @Override
  public void init() {
    setLayout(new BorderLayout());

    m_compass = new CompassPlot(data);
    m_compass.setSeriesNeedle(7);
    m_compass.setSeriesPaint(0, Color.RED);
    m_compass.setSeriesOutlinePaint(0, Color.RED);

    JFreeChart chart
        = new JFreeChart(getFieldName(), JFreeChart.DEFAULT_TITLE_FONT, m_compass, false);
    chartPanel = new ChartPanel(chart);
    chartPanel.setPreferredSize(new Dimension(250, 150));

    add(chartPanel, BorderLayout.CENTER);

    //        System.out.println(m_compass.getPlotType());

    revalidate();
    repaint();
  }

  public void setValue(double value) {
    if (table != null) {
      setValue((ITable) null);
    }
    updateValue(value);
  }

  @Override
  public void doubleChanged(ITable source, String key, double value, boolean isNew) {
    if (key.equals("angle") || key.equals("Value")) {
      updateValue(value);
    }
  }

  public void updateValue(double value) {
    data.setValue(value + m_compass.getRevolutionDistance() / 2);
    repaint();
  }

  @Override
  public void propertyChanged(Property property) {
    if (property == circumference) {
      double compassUncorrectedValue = (data.getValue()).doubleValue()
          - m_compass.getRevolutionDistance() / 2;
      m_compass.setRevolutionDistance(circumference.getValue());
      updateValue(compassUncorrectedValue);
    } else if (property == ringColor) {
      m_compass.setRosePaint(ringColor.getValue());
    }
  }

  public void setBindableValue(double value) {
    doubleChanged(table, "Value", value, true);
  }
}
