package resultFormats;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;
import org.json.JSONException;
import org.json.JSONObject;

import testHarness.output.result.Result;

public class ChartFormat implements OutputFormat {
	private Result result;
	private JFreeChart chart;

	public ChartFormat(Result result) {
		this.result = result;

		chart = jsonToChart(result.getName(), result.getJsonObject());
	}
	public void save(String filename) {
		try {
			ChartUtilities.saveChartAsPNG(new File(filename), chart, 500, 400);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * NB Ignores delayDisplay, since there is no mechanism for grouping charts anyway
	 */
	public void display(boolean delayDisplay) {
		JFrame mainFrame = new JFrame();
		mainFrame.setSize(500, 400);

		ChartPanel chartPanel1 = new ChartPanel(chart);
		chartPanel1.setPreferredSize(new java.awt.Dimension(500, 400));
		mainFrame.add(chartPanel1);
		mainFrame.setVisible(true);
	}

	public static JFreeChart jsonToChart(String name, JSONObject jsondata) {
		XYDataset dataset = createDataset(name, jsondata);

		JFreeChart chart = createChart(name, dataset);
		return chart;
	}

	private static JFreeChart createChart(String name, XYDataset dataset) {
		JFreeChart chart = ChartFactory.createXYLineChart(name, // chart title
				"Tick Number", // x axis label
				"Price", // y axis label
				dataset, // data
				PlotOrientation.VERTICAL, true, // include legend
				true, // tooltips
				false // urls
				);
		chart.setBackgroundPaint(Color.white);
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true);
			renderer.setBaseShapesFilled(true);
		}
		return chart;
	}
	private static XYDataset createDataset(String name, JSONObject obj) {

		XYSeriesCollection dataset = new XYSeriesCollection();

		try {
//			JSONObject obj = new JSONObject(jsondata);

			String key = JSONObject.getNames(obj)[0];
			// if the first element is a Double assume the rest is going to be a
			// Double
			if (obj.get(key) instanceof Number) {
				XYSeries series = new XYSeries(name);
				int tickNum = 0;
				// TODO - if we're going to sort, it would be quicker to sort longs than Strings
				String[] timestamps = JSONObject.getNames(obj);
				Arrays.sort(timestamps);
				for (String timestamp : timestamps) {
					series.add(tickNum, ((Number)obj.get(timestamp)).doubleValue());
					tickNum++;
				}
				dataset.addSeries(series);
			}
			// if the first element contains multiple data create multiple
			// series on the same chart
			else if (obj.get(key) instanceof JSONObject) {
				JSONObject firstSubObject = (JSONObject) obj.get(key);

				// create an array of series of length of the amount of entries
				// in the first JSON subObject
				XYSeries[] series = new XYSeries[JSONObject
						.getNames(firstSubObject).length];

				// initialise series with the name of each of the entries in the
				// subObject
				for (int j = 0; j < series.length; j++) {
					series[j] = new XYSeries(
							JSONObject.getNames(firstSubObject)[j]);
				}
				int tickNum = 0;
				String[] timestamps = JSONObject.getNames(obj);
				Arrays.sort(timestamps);
				for (String timestamp : timestamps) {
					JSONObject subObject = (JSONObject) obj.get(timestamp);
					for (int i = 0; i < JSONObject.getNames(subObject).length; i++) {
						String domain = JSONObject.getNames(subObject)[i];
						series[i].add(tickNum, subObject.getLong(domain));
					}
					tickNum++;
				}

				for (int j = 0; j < series.length; j++) {
					dataset.addSeries(series[j]);
				}

			}
		} catch (JSONException e) {
			System.err.println("Error in parsing the JSONObject");
			e.printStackTrace();
		}

		return dataset;
	}
	
	public void finishDisplay() { 
		
	}
}
