/*******************************************************************************
 * Copyright 2013 Club ApplETS
 ***/
package ca.etsmtl.applets.etsmobile.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import ca.etsmtl.applets.etsmobile.model.ElementEvaluation;
import ca.etsmtl.applets.etsmobile.model.ListeDesElementsEvaluation;
import ca.etsmtl.applets.etsmobile2.R;


public class MyCourseDetailAdapter extends BaseAdapter {

	public class ViewHolder {

		public TextView txtViewSeparator;
		public TextView txtView;
		public TextView txtViewValue;
		public TextView txtViewEcType;
		public TextView txtViewCent;
		public TextView txtViewMed;
		public TextView txtViewMoy;
		public TextView txtViewPond;

	}

	private static final int ITEM_VIEW_TYPE_LIST_ITEM = 0;
	private static final int ITEM_VIEW_TYPE_SEPARATOR = 1;
	private static final int ITEM_VIEW_TYPE_COUNT = 2;
	final String inflater = Context.LAYOUT_INFLATER_SERVICE;
	NumberFormat nf_frCA;
	NumberFormat nf_enUS;
	private ListeDesElementsEvaluation courseEvaluation;
	private double total;
	private final LayoutInflater li;
	private final Context ctx; 
	private ViewHolder holder = null;
	private String cote ;

	public MyCourseDetailAdapter(final Context context, final ListeDesElementsEvaluation courseEvaluation, String cote) {
		super();
		this.courseEvaluation = courseEvaluation;
		this.cote = cote;
		nf_frCA = new DecimalFormat("##,#", new DecimalFormatSymbols(Locale.CANADA_FRENCH));
		nf_enUS = new DecimalFormat("##.#");
		// parse exams results
		for ( ElementEvaluation evaluationElement : courseEvaluation.liste) {
			if(evaluationElement.note !=null){
				if(evaluationElement.ignoreDuCalcul.equals("Non")){
					try {
						final String pond = evaluationElement.ponderation;
						final double value = nf_frCA.parse(pond).doubleValue();
						total += value;
						if(total>100){
							total = 100;
						}
					} catch (final ParseException e) {
					}
				}
			}
		}
		
		ctx = context;
		li = (LayoutInflater) ctx.getSystemService(inflater);
	}

	@Override
	public ElementEvaluation getItem(final int position) {
		ElementEvaluation elementEvaluation = null;

		if (position > 7) {
			// offset for static rows
			elementEvaluation = courseEvaluation.liste.get(position - 8);
		}
		return elementEvaluation;
	}

	@Override
	public int getItemViewType(final int position) {
		return position == 0 || position == 7 ? MyCourseDetailAdapter.ITEM_VIEW_TYPE_SEPARATOR
				: MyCourseDetailAdapter.ITEM_VIEW_TYPE_LIST_ITEM;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {

		final int type = getItemViewType(position);
		if (convertView == null) {
			holder = new ViewHolder();
			// inflate from xml
			convertView = li.inflate(type == MyCourseDetailAdapter.ITEM_VIEW_TYPE_LIST_ITEM ? R.layout.list_item_value
					: R.layout.list_separator, null);
			// init objs
			holder.txtViewSeparator = (TextView) convertView.findViewById(R.id.textViewSeparator);
			holder.txtView = (TextView) convertView.findViewById(R.id.textView);
			holder.txtViewValue = (TextView) convertView.findViewById(R.id.value);
			holder.txtViewMoy = (TextView) convertView.findViewById(R.id.item_value_moy);
			holder.txtViewMed = (TextView) convertView.findViewById(R.id.item_value_med);
			holder.txtViewCent = (TextView) convertView.findViewById(R.id.item_value_centile);
			holder.txtViewEcType = (TextView) convertView.findViewById(R.id.item_value_ec_type);
			holder.txtViewPond = (TextView) convertView.findViewById(R.id.item_value_pond);
			// set tag
			convertView.setTag(holder);
		} else {
			// get tag
			holder = (ViewHolder) convertView.getTag();
		}
	
		// ui display of inflated xml
		if (type == MyCourseDetailAdapter.ITEM_VIEW_TYPE_SEPARATOR) {
			if (position == 0) {
				holder.txtViewSeparator.setText(R.string.sommaire);
			} else {
				holder.txtViewSeparator.setText(R.string.mesNotes);
			}
		} else {
			holder.txtViewMoy.setVisibility(View.GONE);
			holder.txtViewMed.setVisibility(View.GONE);
			holder.txtViewCent.setVisibility(View.GONE);
			holder.txtViewEcType.setVisibility(View.GONE);
			holder.txtViewPond.setVisibility(View.GONE);
			holder.txtViewValue.setTextColor(Color.BLACK);
			holder.txtViewPond.setTextColor(Color.BLACK);
			holder.txtView.setTextColor(Color.BLACK);
			switch (position) {
			case 1:// COURS EVAL
				holder.txtView.setText(R.string.cote);
				holder.txtViewValue.setText(cote);
				break;
			case 2:// NOTE À CE JOUR
				holder.txtView.setText(R.string.noteACejour);
				if(courseEvaluation.scoreFinalSur100!=null){
					final String note = courseEvaluation.scoreFinalSur100;
					ctx.getString(R.string.noteOnPourcent,note , nf_enUS.format(total), courseEvaluation.noteACeJour );
					holder.txtViewValue.setText(ctx.getString(R.string.noteOnPourcent,note , nf_enUS.format(total), courseEvaluation.noteACeJour));
				}
				break;
			case 3:// MOYENNE CLASSE
				holder.txtView.setText(R.string.moyenne);
				if(courseEvaluation.moyenneClasse!=null){
					final String m = courseEvaluation.moyenneClasse;
					try {
						String value = nf_enUS.format(+(nf_frCA.parse(m).doubleValue() / total) * 100);
						holder.txtViewValue.setText(ctx.getString(R.string.noteOnPourcent,m,nf_enUS.format(total), value));
						
					} catch (final ParseException e1) {
						e1.printStackTrace();
					}
				}
				break;
			case 4:// ?CART TYPE
				holder.txtView.setText(R.string.ecartType);
				holder.txtViewValue.setText(courseEvaluation.ecartTypeClasse);
				break;
			case 5:// MÉDIANE
				holder.txtView.setText(R.string.mediane);
			    holder.txtViewValue.setText(courseEvaluation.medianeClasse);
				break;
			case 6:// RAND CENTILLE
				holder.txtView.setText(R.string.rangCentile);
				holder.txtViewValue.setText(courseEvaluation.rangCentileClasse);
				break;
			default:// ELSE
				final ElementEvaluation element = getItem(position);

				if (element != null) {
					holder.txtView.setText(element.nom);
					try {
						final String notee = element.note;
						final String sur = element.corrigeSur;
						double sur100 = 0;
						if (element.note!=null && sur!=null) {
							sur100 = nf_frCA.parse(notee).doubleValue() / nf_frCA.parse(sur).doubleValue() * 100;

							final String tmp = nf_enUS.format(sur100);
							holder.txtViewValue.setText(ctx.getString(R.string.noteOnPourcent,element.note,element.corrigeSur,tmp));

							holder.txtViewMoy.setVisibility(View.VISIBLE);
							holder.txtViewMed.setVisibility(View.VISIBLE);
							holder.txtViewCent.setVisibility(View.VISIBLE);
							holder.txtViewEcType.setVisibility(View.VISIBLE);
							holder.txtViewPond.setVisibility(View.VISIBLE);

							holder.txtViewMoy.setText(ctx.getString(R.string.moyenne)
									+ ": "
									+ nf_enUS.format(nf_frCA.parse(element.moyenne).doubleValue()
											/ nf_frCA.parse(sur).doubleValue() * 100) + "%");

							holder.txtViewMed.setText(ctx.getString(R.string.mediane)+": "
									+ nf_enUS.format(nf_frCA.parse(element.mediane).doubleValue()
											/ nf_frCA.parse(sur).doubleValue() * 100) + "%");

							holder.txtViewCent.setText(ctx.getString(R.string.rangCentile)+": " + element.rangCentile);

							holder.txtViewEcType.setText(ctx.getString(R.string.ecartType)+": " + element.ecartType);

							holder.txtViewPond.setText(ctx.getString(R.string.ponderation)+": " + element.ponderation + "%");
							
							if(element.ignoreDuCalcul.equals("Oui")){
								holder.txtViewValue.setTextColor(Color.RED);
								holder.txtViewPond.setTextColor(Color.RED);
								holder.txtView.setTextColor(Color.RED);
							}
						} else {
							holder.txtViewPond.setVisibility(View.VISIBLE);
							holder.txtViewMoy.setVisibility(View.INVISIBLE);
							holder.txtViewValue.setText("/" + sur);
							holder.txtViewPond.setText(ctx.getString(R.string.ponderation)+": " + element.ponderation + "%");

						}
					} catch (final ParseException e) {
						e.printStackTrace();
					}
					
				}
				break;
			}
		}

		return convertView;
	}

	@Override
	public int getViewTypeCount() {
		return MyCourseDetailAdapter.ITEM_VIEW_TYPE_COUNT;
	}

	@Override
	public boolean isEnabled(final int position) {
		return false;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getCount() {
		return 8 + courseEvaluation.liste.size();
	}
}
