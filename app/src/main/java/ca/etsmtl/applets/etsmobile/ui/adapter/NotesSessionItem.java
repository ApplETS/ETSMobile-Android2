package ca.etsmtl.applets.etsmobile.ui.adapter;

public class NotesSessionItem {

	public String sessionName;
	public String abrege;
	public SessionCoteAdapter arrayAdapter;

	public NotesSessionItem(String sessionName, String abrege, SessionCoteAdapter arrayAdapter) {
		this.sessionName = sessionName;
		this.abrege = abrege;
		this.arrayAdapter = arrayAdapter;
	}

}
