package clientLoaders;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JComboBox;
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

public class Visualiser {

	List<Result> results;
	
	public Visualiser(List<Result> results){
		this.results = results;
		initialize();
	}
	
	private void initialize(){		
		if(results.size()==0) System.err.println("No results to visualise");
		
		//
		// TODO!!!!
		//
		JFreeChart chart1 ;
		JFreeChart chart2;
		if(results.size()==1) {
			chart1 = jsonToChart(results.get(0).getName(),results.get(0).asJSON());
			chart2 = jsonToChart(results.get(0).getName(),results.get(0).asJSON());
		} else{ //if more than 1 result in the results list
			chart1 = jsonToChart(results.get(0).getName(),results.get(0).asJSON());
			chart2 = jsonToChart(results.get(1).getName(),results.get(1).asJSON());
		}
			JFrame mainFrame = new JFrame();			
			try {
				ChartUtilities.saveChartAsPNG(new File("test.png"), chart1, 500, 400);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			mainFrame.setSize(1000, 800);
			ChartPanel chartPanel1 = new ChartPanel(chart1);
			ChartPanel chartPanel2 = new ChartPanel(chart2);
			chartPanel1.setPreferredSize(new java.awt.Dimension(500, 400));
			chartPanel2.setPreferredSize(new java.awt.Dimension(500, 400));
			//chartPanel1.setMouseZoomable(true, false);
			mainFrame.add(BorderLayout.EAST,chartPanel1);
			mainFrame.add(BorderLayout.WEST,chartPanel2);	
			
			
			
//			
//			ChartComboBox chartComboBox1 = new ChartComboBox(chartPanel1,chart1,results);
//			chartComboBox1.setSelectedIndex(0);
//			chartComboBox1.addActionListener(chartComboBox1);
//			ChartComboBox chartComboBox2 = new ChartComboBox(chartPanel2,chart2,results);
//			chartComboBox2.setSelectedIndex(1);
//			chartComboBox2.addActionListener(chartComboBox2);
//			mainFrame.add(BorderLayout.NORTH,chartComboBox1);
//			mainFrame.add(BorderLayout.SOUTH,chartComboBox2);
//			
			mainFrame.setVisible(true);
			try {
				System.in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
	}
	
	class ChartComboBox extends JComboBox implements ActionListener{
		
		List<Result> results;
		JFreeChart chartReference;
		ChartPanel panel;
		
		public ChartComboBox(ChartPanel panel,JFreeChart chart, List<Result> results){			
			super();			
			this.chartReference = chart;
			this.results = results;
			this.panel = panel;
			for(Result r : results){
				this.addItem(r);
			}
			panel.revalidate();
			panel.repaint();
			
		}
		
		 public void actionPerformed(ActionEvent e) {		        
		        Result selectedResult = (Result)this.getSelectedItem();
		        chartReference = jsonToChart(selectedResult.getName(),selectedResult.asJSON());
		        panel.setChart(chartReference);
		    }
		
	}
	
	public static JFreeChart jsonToChart(String name, String jsondata) {
		XYDataset dataset = createDataset(name, jsonToMap(jsondata));
		JFreeChart chart = createChart(name, dataset);
		return chart;
	}

	private static TreeMap<String, Double> jsonToMap(String jsondata) {
		TreeMap<String, Double> map = new TreeMap<String, Double>();

		try {
			JSONObject obj = new JSONObject(jsondata);
			for (String key : JSONObject.getNames(obj)) {
				map.put(key, (double)obj.getLong(key));
			}

			System.out.println(map);
		} catch (JSONException e) {
			System.err.println("JSON parse exception");
			e.printStackTrace();
		}

		return map;

	}

	private static JFreeChart createChart(String name, XYDataset dataset) {
		JFreeChart chart = ChartFactory.createXYLineChart(
				name, // chart title
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

	private static XYDataset createDataset(String name,
			TreeMap<String, Double> data) {
		XYSeries series = new XYSeries(name);

		int tickNum = 0;
		for (Entry<String, Double> d : data.entrySet()) {
			series.add(tickNum, d.getValue());
			tickNum++;
		}

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);
		return dataset;
	}
}
	
	