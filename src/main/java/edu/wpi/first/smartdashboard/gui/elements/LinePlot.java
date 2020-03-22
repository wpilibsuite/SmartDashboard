package edu.wpi.first.smartdashboard.gui.elements;

import edu.wpi.first.smartdashboard.gui.elements.bindings.AbstractValueWidget;
import edu.wpi.first.smartdashboard.properties.BooleanProperty;
import edu.wpi.first.smartdashboard.properties.IntegerProperty;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * @author pmalmsten
 */
public class LinePlot extends AbstractValueWidget {

  public static final DataType[] TYPES = {DataType.NUMBER};
  public final IntegerProperty bufferSize
      = new IntegerProperty(this, "Buffer Size (samples)", 5000);
  public final BooleanProperty clear = new BooleanProperty(this, "Clear Graph", false);
  public final BooleanProperty autoScale = new BooleanProperty(this, "Autoscale Y Values", true);
  public final IntegerProperty minY = new IntegerProperty(this, "Minimum Y Value", 0);
  public final IntegerProperty maxY = new IntegerProperty(this, "Maximum Y Value", 1);
  
  JPanel m_chartPanel;
  XYSeries m_data;
  XYDataset m_dataset;
  JFreeChart m_chart;
  double startTime;

  @Override
  public void init() {
    setLayout(new BorderLayout());

    m_data = new XYSeries(getFieldName());
    m_dataset = new XYSeriesCollection(m_data);

    startTime = System.currentTimeMillis() / 1000.0;

    m_chart = ChartFactory.createXYLineChart(
        getFieldName(),
        "Time (Seconds)",
        "Data",
        m_dataset,
        PlotOrientation.VERTICAL,
        false,
        true,
        false);

    updateChartRange();

    m_chartPanel = new ChartPanel(m_chart);
    m_chartPanel.setPreferredSize(new Dimension(400, 300));
    m_chartPanel.setBackground(getBackground());

    add(m_chartPanel, BorderLayout.CENTER);
    revalidate();
    repaint();
  }

  @Override
  public void setValue(double value) { //TODO make sample in thread instead of relying on set
    // value (so that the widget has even time scale)
    m_data.add(System.currentTimeMillis() / 1000.0 - startTime, value);

    if (m_data.getItemCount() > bufferSize.getValue()) {
      m_data.remove(0);
    }

    revalidate();
    repaint();
  }

  @Override
  public void propertyChanged(Property property) {
    if (property == bufferSize) {

      while (m_data.getItemCount() > bufferSize.getValue()) {
        m_data.remove(0);
      }
    }
    if (property == clear) {
      if (clear.getValue()) {
        m_data.clear();
        clear.setValue(false);
        startTime = System.currentTimeMillis() / 1000.0;
      }
    }

    if (property == minY || property == maxY || property == autoScale) {
      if (property == minY && minY.getValue() > maxY.getValue()) {
        minY.setValue(maxY.getValue() - 1);
      } else if (property == maxY && maxY.getValue() < minY.getValue()) {
        maxY.setValue(minY.getValue() + 1);
      }

      updateChartRange();
    }
  }

  public void updateChartRange() {
    if (autoScale.getValue()) {
      m_chart.getXYPlot().getRangeAxis().setAutoRange(true);
    } else {
      m_chart.getXYPlot().getRangeAxis().setRange(minY.getValue(), maxY.getValue());
    }
  }
}
