package clientLoaders;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
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

public class Visualiser {

	List<Result> results;

	public Visualiser(List<Result> results) {
		this.results = results;
		initialize();
	}

	private void initialize() {
		if (results.size() == 0) {
			System.err.println("No results to visualise");
		}

		else if (results.size() == 1) {
			JFreeChart chart1 = jsonToChart(results.get(0).getName(), results
					.get(0).asJSON());

			JFrame mainFrame = new JFrame();
			mainFrame.setSize(500, 400);

			ChartPanel chartPanel1 = new ChartPanel(chart1);
			chartPanel1.setPreferredSize(new java.awt.Dimension(500, 400));
			mainFrame.add(chartPanel1);
			mainFrame.setVisible(true);
		}

		else { // if more than 1 result in the results list

			JFreeChart chart1 = jsonToChart(results.get(0).getName(), results
					.get(0).asJSON());
			JFreeChart chart2 = jsonToChart(results.get(1).getName(), results
					.get(1).asJSON());

			JFrame mainFrame = new JFrame();

			mainFrame.setSize(1000, 800);
			ChartPanel chartPanel1 = new ChartPanel(chart1);
			ChartPanel chartPanel2 = new ChartPanel(chart2);
			chartPanel1.setPreferredSize(new java.awt.Dimension(500, 400));
			chartPanel2.setPreferredSize(new java.awt.Dimension(500, 400));

			JPanel comboBoxPanel = new JPanel();
			ChartComboBox chartComboBox1 = new ChartComboBox(chartPanel1,
					chart1, results);
			chartComboBox1.setSelectedIndex(0);
			chartComboBox1.addActionListener(chartComboBox1);
			ChartComboBox chartComboBox2 = new ChartComboBox(chartPanel2,
					chart2, results);
			chartComboBox2.setSelectedIndex(1);
			chartComboBox2.addActionListener(chartComboBox2);
			comboBoxPanel.add(chartComboBox1);
			comboBoxPanel.add(chartComboBox2);

			mainFrame.add(BorderLayout.WEST, chartPanel1);
			mainFrame.add(BorderLayout.EAST, chartPanel2);
			mainFrame.add(BorderLayout.SOUTH, comboBoxPanel);

			mainFrame.setVisible(true);

		}
	}

	public static JFreeChart jsonToChart(String name, String jsondata) {
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

	private static XYDataset createDataset(String name, String jsondata) {

		XYSeriesCollection dataset = new XYSeriesCollection();

		try {
			JSONObject obj = new JSONObject(jsondata);

			String key = JSONObject.getNames(obj)[0];
			// if the first element is a Double assume the rest is going to be a
			// Double
			if (obj.get(key) instanceof Double) {
				XYSeries series = new XYSeries(name);
				int tickNum = 0;
				String[] timestamps = JSONObject.getNames(obj);
				Arrays.sort(timestamps);
				for (String timestamp : timestamps) {
					series.add(tickNum, obj.getLong(timestamp));
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

	class ChartComboBox extends JComboBox<Result> implements ActionListener {

		private static final long serialVersionUID = 1L;
		List<Result> results;
		JFreeChart chartReference;
		ChartPanel panel;

		public ChartComboBox(ChartPanel panel, JFreeChart chart,

		List<Result> results) {
			super();
			this.chartReference = chart;
			this.results = results;
			this.panel = panel;
			for (Result r : results) {

				this.addItem(r);
			}
			panel.revalidate();
			panel.repaint();

		}

		public void actionPerformed(ActionEvent e) {
			Result selectedResult = (Result) this.getSelectedItem();
			chartReference = jsonToChart(selectedResult.getName(),
					selectedResult.asJSON());
			panel.setChart(chartReference);
		}

	}
}
