package ca.etsmtl.applets.etsmobile.views;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.text.Layout;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
/**
 * code pris de Archartengine
 * @author Laurence de Villers
 *
 */
public class PieChart {

	private Context context;
	private GraphicalView mChartView;
	
	public PieChart( Context context,  double[] values, int[] colors, LinearLayout layout) {
		this.context = context;
	
		DefaultRenderer renderer = buildCategoryRenderer(colors);
	    renderer.setChartTitleTextSize(50);
	    renderer.setDisplayValues(true);
	    renderer.setShowLabels(false);
	    renderer.setPanEnabled(false);
	    renderer.setZoomEnabled(false);
	    renderer.setShowLegend(false);
	    
	    
	   if(mChartView == null){
	    	layout.removeAllViews();
	    	mChartView = ChartFactory.getPieChartView(context,  buildCategoryDataset("Project budget", values), renderer);
	    	layout.addView(mChartView);
	    }else{
	    	mChartView.repaint();
	   }
	  
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
	    for(int i=0; i< values.length; i++){
	    	if(i<values.length-1)
	    		series.add(" "+values[i]+"Go",values[i]);
	    	else
	    		series.add("Go Restant : "+values[i]+"Go", values[i]);
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
	    renderer.setLabelsTextSize(40);
	   // renderer.setLegendTextSize(25);
	    renderer.setMargins(new int[]{10,0,0,0});
	  //  renderer.setLegendHeight(200);

	    for (int color : colors) {
	      SimpleSeriesRenderer r = new SimpleSeriesRenderer();
	      r.setColor(color);
	      renderer.addSeriesRenderer(r);
	    }
	    return renderer;
	  }
}
