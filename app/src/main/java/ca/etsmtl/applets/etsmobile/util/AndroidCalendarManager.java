package ca.etsmtl.applets.etsmobile.util;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ca.etsmtl.applets.etsmobile.ApplicationManager;

/**
 * Created by gnut3ll4 on 6/23/14.
 */
public class AndroidCalendarManager {


    private final Context context;
    private final String CALENDAR_ACCOUNT_NAME = "Calendrier ApplETS";

    String[] projection = new String[] {
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.NAME,
            CalendarContract.Calendars.CALENDAR_COLOR,

    };

    public AndroidCalendarManager(Context context){
        this.context = context;
    }

    /**
     * Create a calendar locally in the phone
     * @param calendarName
     */
    public void createCalendar(String calendarName){

		ContentValues values = new ContentValues();

        values.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDAR_ACCOUNT_NAME);
        values.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        values.put(CalendarContract.Calendars.NAME, calendarName);
        values.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, calendarName);
        values.put(CalendarContract.Calendars.CALENDAR_COLOR, 0xffff0000);
        values.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        values.put(CalendarContract.Calendars.OWNER_ACCOUNT, ApplicationManager.userCredentials.getUsername());
        values.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, "America/Montreal");

        values.put(CalendarContract.Calendars.VISIBLE, 1);
        values.put(CalendarContract.Calendars.SYNC_EVENTS, 1);

        Uri uri = CalendarContract.Calendars.CONTENT_URI.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER,"true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDAR_ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL).build();;


        context.getContentResolver().insert(uri, values);

    }


    /**
     * Returns the id of the specified calendar
     * @param calendarName
     * @return id
     */
    public int getCalendarId(String calendarName) throws Exception {

        String selection = "(" + CalendarContract.Calendars.CALENDAR_DISPLAY_NAME+ " = ?)";
        String[] selectionArgs = new String[] {calendarName};
        Cursor calendarCursor = context.getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, new String[] {CalendarContract.Calendars._ID,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.NAME,
                CalendarContract.Calendars.CALENDAR_COLOR}, selection, selectionArgs, null);

        long id = 0;
        if (calendarCursor.moveToFirst()) {
            id = calendarCursor.getLong(0);
        }

        if(id==0) {
            throw new Exception("Calendar not found");
        }

        calendarCursor.close();
        return (int)id;

    }


    /**
     * Returns a String containing details about an event in the specified calendar
     * @param eventName
     * @param calendarName
     * @return
     * @throws Exception
     */
    public String getEventInCalendar(String eventName,String calendarName) throws Exception {
        String response = "";
        int idCal = getCalendarId(calendarName);

        Uri uriEvents = CalendarContract.Events.CONTENT_URI;
        String selection = "(" + CalendarContract.Events.CALENDAR_ID+ " = ?)";
        String[] selectionArgs = new String[] {""+idCal};

        Cursor c = context.getContentResolver().query(uriEvents, new String[] { "calendar_id", "title", "description",
                "dtstart", "dtend", "event_location" }, selection, selectionArgs, null);
        if (c.moveToFirst()) {
            do {
                long id = c.getLong(0);
                String title = c.getString(1);
                String description = c.getString(2);
                long dtstart = c.getLong(3);
                long dtend = c.getLong(4);
                String eventLocation = c.getString(5);

                response += id+" "+ title+" "+description+" "+dtstart+" "+dtend +"\n";

                Log.e(getClass().getSimpleName(), "selectAllEventFromCalendarById");
                Log.e(getClass().getSimpleName(), id+"-"+title+"-"+description+"-"+dtstart+"-"+dtend+"-"+eventLocation);
            } while (c.moveToNext());
        }
        c.close();
        return response;
    }


    /**
     * Insert an event in the local calendar
     * @param calendarName
     * @param title
     * @param description
     * @param place
     * @param start
     * @param end
     * @throws Exception
     */
    public void insertEventInCalendar(String calendarName,String title,String description, String place, Date start, Date end) throws Exception {
        int calendarId = getCalendarId(calendarName);

        Calendar beginTime = Calendar.getInstance();
        beginTime.setTime(start);

        Calendar endTime = Calendar.getInstance();
        endTime.setTime(end);

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, beginTime.getTimeInMillis());
        values.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.EVENT_LOCATION, place);
        values.put(CalendarContract.Events.CALENDAR_ID,calendarId );
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "America/Montreal");

        context.getContentResolver().insert(CalendarContract.Events.CONTENT_URI, values);
    }

    /**
     * Insert a recurrent event in the local calendar
     * @param calendarName
     * @param title
     * @param description
     * @param place
     * @param startEvent
     * @param endEvent
     * @param until
     * @throws Exception
     */
    public void insertRecurrentEventInCalendar(String calendarName,String title,String description, String place, Date startEvent, Date endEvent, Date until) throws Exception {
        int calendarId = getCalendarId(calendarName);

        //années, mois (à partir de 0), jour, heure, minutes beginTime.set(2014,5, 23, 9, 30);
        Calendar beginTime = Calendar.getInstance();
        beginTime.setTime(startEvent);

        Calendar endTime = Calendar.getInstance();
        endTime.setTime(endEvent);

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, beginTime.getTimeInMillis());
        values.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.EVENT_LOCATION, place);
        values.put(CalendarContract.Events.CALENDAR_ID,calendarId );


        SimpleDateFormat dateFormatter =  new SimpleDateFormat("yyyyMMdd");


        values.put(CalendarContract.Events.RRULE,"FREQ=WEEKLY;UNTIL="+dateFormatter.format(until) );
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "America/Montreal");
        context.getContentResolver().insert(CalendarContract.Events.CONTENT_URI, values);
    }

    /**
     * Returns a string containing all user's calendars
     * @return
     */
    public String selectAllCalendars() {
        Cursor calendarCursor = context.getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, projection, null, null, null);
        String response = "";

        if (calendarCursor.moveToFirst()) {
            do {
                long id = calendarCursor.getLong(0);
                String accountName = calendarCursor.getString(1);
                String calendarDisplayName = calendarCursor.getString(2);
                String name = calendarCursor.getString(3);
                String calendarColor = calendarCursor.getString(4);

                response += id+"-"+accountName+"-"+calendarDisplayName+"-"+name+"-"+calendarColor+"\n";

            } while (calendarCursor.moveToNext());
        }
        calendarCursor.close();
        return response;
    }

    /**
     * Delete a local calendar
     * @param calendarName
     */
    public void deleteCalendar(String calendarName) {
    	try{
    		
	        int calendarId = getCalendarId(calendarName);
	        Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Calendars.CONTENT_URI, calendarId);
	        context.getContentResolver().delete(deleteUri, null, null);
	        
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
}