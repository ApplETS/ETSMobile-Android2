package ca.etsmtl.applets.etsmobile.views;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
/**
 * code pris de Archartengine
 * @author Laurence de Villers
 *
 */
public class PieChart {
	private double[] values;
	private int[] colors; 
	private Context context;
	public PieChart( Context context,  double[] values, int[] colors) {
		this.context = context;
		values = new double[] { 12, 14, 11, 10 };
		colors = new int[] { Color.BLUE, Color.GREEN, Color.YELLOW, Color.CYAN };
		DefaultRenderer renderer = buildCategoryRenderer(colors);
		renderer.setZoomButtonsVisible(true);
	    renderer.setZoomEnabled(true);
	    renderer.setChartTitleTextSize(20);
	    renderer.setDisplayValues(true);
	    renderer.setShowLabels(false);
	    SimpleSeriesRenderer r = renderer.getSeriesRendererAt(0);
	    r.setGradientEnabled(true);
	    r.setGradientStart(0, Color.BLUE);
	    r.setGradientStop(0, Color.GREEN);
	    r.setHighlighted(true);
	    Intent intent = ChartFactory.getPieChartIntent(context,
	        buildCategoryDataset("Project budget", values), renderer, "Budget");
	}

	/**
	   * Builds a category series using the provided values.
	   * 
	   * @param titles the series titles
	   * @param values the values
	   * @return the category series
	   */
	  protected CategorySeries buildCategoryDataset(String title, double[] values) {
	    CategorySeries series = new CategorySeries(title);
	    int k = 0;
	    for (double value : values) {
	      series.add("Project " + ++k, value);
	    }

	    return series;
	  }
	  
	  

	  /**
	   * Builds a category renderer to use the provided colors.
	   * 
	   * @param colors the colors
	   * @return the category renderer
	   */
	  protected DefaultRenderer buildCategoryRenderer(int[] colors) {
	    DefaultRenderer renderer = new DefaultRenderer();
	    renderer.setLabelsTextSize(15);
	    renderer.setLegendTextSize(15);
	    renderer.setMargins(new int[] { 20, 30, 15, 0 });
	    for (int color : colors) {
	      SimpleSeriesRenderer r = new SimpleSeriesRenderer();
	      r.setColor(color);
	      renderer.addSeriesRenderer(r);
	    }
	    return renderer;
	  }
	  
	  
	  
	  private XYMultipleSeriesRenderer getDemoRenderer() {
		    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		    renderer.setAxisTitleTextSize(16);
		    renderer.setChartTitleTextSize(20);
		    renderer.setLabelsTextSize(15);
		    renderer.setLegendTextSize(15);
		    renderer.setPointSize(5f);
		    renderer.setMargins(new int[] {20, 30, 15, 0});
		    XYSeriesRenderer r = new XYSeriesRenderer();
		    r.setColor(Color.BLUE);
		    r.setPointStyle(PointStyle.SQUARE);
		    r.setFillBelowLine(true);
		    r.setFillBelowLineColor(Color.WHITE);
		    r.setFillPoints(true);
		    renderer.addSeriesRenderer(r);
		    r = new XYSeriesRenderer();
		    r.setPointStyle(PointStyle.CIRCLE);
		    r.setColor(Color.GREEN);
		    r.setFillPoints(true);
		    renderer.addSeriesRenderer(r);
		    renderer.setAxesColor(Color.DKGRAY);
		    renderer.setLabelsColor(Color.LTGRAY);
		    return renderer;
	  }
}
