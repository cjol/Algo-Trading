//package clientLoaders;
//
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.util.Arrays;
//import java.util.List;
//import javax.swing.JComboBox;
//import javax.swing.JFrame;
//import javax.swing.JPanel;
//
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartPanel;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.plot.PlotOrientation;
//import org.jfree.chart.plot.XYPlot;
//import org.jfree.chart.renderer.xy.XYItemRenderer;
//import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
//import org.jfree.data.xy.XYDataset;
//import org.jfree.data.xy.XYSeries;
//import org.jfree.data.xy.XYSeriesCollection;
//import org.jfree.ui.RectangleInsets;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import testHarness.output.result.Result;
//
//public class Visualiser {
//
//	List<Result> results;
//
//	public Visualiser(List<Result> results) {
//		this.results = results;
//		initialize();
//	}
//
//	private void initialize() {
//		if (results.size() == 0) {
//			System.err.println("No results to visualise");
//		}
//
////		else if (results.size() == 1) {
//		}
//
//		else { // if more than 1 result in the results list
//
//			JFreeChart chart1 = jsonToChart(results.get(0).getName(), results
//					.get(0).asJSON());
//			JFreeChart chart2 = jsonToChart(results.get(1).getName(), results
//					.get(1).asJSON());
//
//			JFrame mainFrame = new JFrame();
//
//			mainFrame.setSize(1000, 800);
//			ChartPanel chartPanel1 = new ChartPanel(chart1);
//			ChartPanel chartPanel2 = new ChartPanel(chart2);
//			chartPanel1.setPreferredSize(new java.awt.Dimension(500, 400));
//			chartPanel2.setPreferredSize(new java.awt.Dimension(500, 400));
//
//			JPanel comboBoxPanel = new JPanel();
//			ChartComboBox chartComboBox1 = new ChartComboBox(chartPanel1,
//					chart1, results);
//			chartComboBox1.setSelectedIndex(0);
//			chartComboBox1.addActionListener(chartComboBox1);
//			ChartComboBox chartComboBox2 = new ChartComboBox(chartPanel2,
//					chart2, results);
//			chartComboBox2.setSelectedIndex(1);
//			chartComboBox2.addActionListener(chartComboBox2);
//			comboBoxPanel.add(chartComboBox1);
//			comboBoxPanel.add(chartComboBox2);
//
//			mainFrame.add(BorderLayout.WEST, chartPanel1);
//			mainFrame.add(BorderLayout.EAST, chartPanel2);
//			mainFrame.add(BorderLayout.SOUTH, comboBoxPanel);
//
//			mainFrame.setVisible(true);
//
//		}
//	}
//
//
//
//	class ChartComboBox extends JComboBox<Result> implements ActionListener {
//
//		private static final long serialVersionUID = 1L;
//		List<Result> results;
//		JFreeChart chartReference;
//		ChartPanel panel;
//
//		public ChartComboBox(ChartPanel panel, JFreeChart chart,
//
//		List<Result> results) {
//			super();
//			this.chartReference = chart;
//			this.results = results;
//			this.panel = panel;
//			for (Result r : results) {
//
//				this.addItem(r);
//			}
//			panel.revalidate();
//			panel.repaint();
//
//		}
//
//		public void actionPerformed(ActionEvent e) {
//			Result selectedResult = (Result) this.getSelectedItem();
//			chartReference = jsonToChart(selectedResult.getName(),
//					selectedResult.asJSON());
//			panel.setChart(chartReference);
//		}
//
//	}
//}
