package ca.etsmtl.applets.etsmobile.ui.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import butterknife.InjectView;
import ca.etsmtl.applets.etsmobile.http.TypedRequest;
import ca.etsmtl.applets.etsmobile2.R;

import com.octo.android.robospice.persistence.exception.SpiceException;

/**
 * Created by Phil on 17/11/13.
 */
public class TodaysFragment extends HttpFragment {

	private class TodaysCourses {
		public String codePerm = "DAVP02068904";
		public String erreur = null;
		public ArrayList<Seance> horaire = new ArrayList<Seance>();

		private class Seance {
			public String coursGroupe = "GTI745-01 ";
			public String dateDebut = "2014-04-09T09=00:00";
			public String dateFin = "2014-04-09T12:00:00";
			public String descriptionActivite = "Examen final";
			public String libelleCours = "Interfaces utilisateurs avancées";
			public String local = "A-1600B";
			public String nomActivite = "Final";
			public String plageHoraire = "09:00-12:00";
		}

		public boolean masculin = true;
		public String nom = "David ";
		public String prenom = "Philippe ";
		public String programme = "Baccalauréat en génie logiciel";
		public String soldeTotal = "0,00$";
	}

	@InjectView(R.id.expandableListView1)
	ListView list;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_today, container, false);
		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		TypedRequest<TodaysCourses> request = new TypedRequest<TodaysCourses>(TodaysCourses.class,
				"http://api.clubapplets.ca/signets/card/login?id=1");
		dataManager.sendRequest(request, this);
	}

	@Override
	public void onRequestFailure(SpiceException arg0) {
		progressBar.setVisibility(View.GONE);
	}

	@Override
	public void onRequestSuccess(Object arg0) {

	}

	@Override
	void updateUI() {
		// TODO Auto-generated method stub

	}
}
