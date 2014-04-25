package ca.etsmtl.applets.etsmobile.ui.adapter;


public class SessionCoteItem {
	public String sigle;
	public String cote;
	public String groupe;

	public SessionCoteItem(String course, String cote, String groupe) {
		this.sigle = course;
		this.cote = cote;
		this.groupe = groupe;
	}
}
