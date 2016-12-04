package edu.wpi.first.smartdashboard.gui.elements;

import edu.wpi.first.smartdashboard.gui.elements.bindings.AbstractValueWidget;
import java.awt.*;

import javax.swing.*;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.xy.*;

import edu.wpi.first.smartdashboard.properties.*;
import edu.wpi.first.smartdashboard.types.*;

/**
 *
 * @author pmalmsten
 */
public class LinePlot extends AbstractValueWidget {

    public static final DataType[] TYPES = {DataType.NUMBER};
    public final IntegerProperty bufferSize = new IntegerProperty(this, "Buffer Size (samples)", 5000);
    JPanel m_chartPanel;
    XYSeries m_data;
    XYDataset m_dataset;
    JFreeChart m_chart;
    int m_timeUnit = 0;

    @Override
    public void init() {
        setLayout(new BorderLayout());

        m_data = new XYSeries(getFieldName());
        m_dataset = new XYSeriesCollection(m_data);

        JFreeChart chart = ChartFactory.createXYLineChart(
                getFieldName(),
                "Time (units)",
                "Data",
                m_dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false);

        m_chartPanel = new ChartPanel(chart);
        m_chartPanel.setPreferredSize(new Dimension(400, 300));
        m_chartPanel.setBackground(getBackground());

        add(m_chartPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    @Override
    public void setValue(double value) {//TODO make sample in thread instead of relying on set value (so that the widget has even time scale)
        m_data.add(m_timeUnit++, value);

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
    }
}
