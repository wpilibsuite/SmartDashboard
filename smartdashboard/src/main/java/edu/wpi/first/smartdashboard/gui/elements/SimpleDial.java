package edu.wpi.first.smartdashboard.gui.elements;

import edu.wpi.first.smartdashboard.gui.elements.bindings.AbstractNumberDatasetWidget;
import java.awt.*;

import javax.swing.*;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.*;

import edu.wpi.first.smartdashboard.properties.*;
import edu.wpi.first.smartdashboard.types.*;

/**
 *
 * @author Paul
 */
public class SimpleDial extends AbstractNumberDatasetWidget {

	public static final DataType[] TYPES = { DataType.NUMBER };

    public final DoubleProperty max = new DoubleProperty(this, "Upper Limit", 100);
    public final DoubleProperty min = new DoubleProperty(this, "Lower Limit", 0);
    public final DoubleProperty tickInterval = new DoubleProperty(this, "Tick Interval", 10);

    private final JPanel chartPanel;
    private final MeterPlot m_meter;
    private Range m_plotRange;
    
    public SimpleDial() {
		super(0);
		
        setLayout(new BorderLayout());

        m_meter = new MeterPlot(getDataset());
        m_plotRange = new Range(min.getValue(), max.getValue());
        m_meter.setRange(m_plotRange);
        //plot.addInterval(new MeterInterval("High", new Range(80.0, 100.0)));
        JFreeChart chart = new JFreeChart(getFieldName(), JFreeChart.DEFAULT_TITLE_FONT, m_meter, false);
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(250, 150));

        propertyChanged(tickInterval);

        add(chartPanel, BorderLayout.CENTER);
	}

    @Override
    public void init() {
        m_plotRange= new Range(min.getValue(), max.getValue());
        m_meter.setRange(m_plotRange);
        m_meter.setTickSize(tickInterval.getValue());
        revalidate();
        repaint();
    }

    @Override
    public void propertyChanged(Property property) {
        if (property == min || property == max) {
            m_plotRange = new Range(min.getValue(), max.getValue());
            m_meter.setRange(m_plotRange);
        } else if (property == tickInterval) {
            m_meter.setTickSize(tickInterval.getValue());
        }
    }
}