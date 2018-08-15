package com.LBR;

import java.time.LocalDate;
import java.util.ArrayList;

public class Event {

    private long event_id;
    private long venue_id;
    private String event_name;
    private LocalDate date;
    private String description;
    private ArrayList<Genre> hasGenre;
    private double distance;

    /**
     * Constructor for an Event
     * @param event_id The EventID
     * @param venue_id The VenueID where the event is
     * @param event_name The Name
     * @param date Date of the Event
     * @param description Description of the Event
     */
    public Event(long event_id, long venue_id, String event_name, LocalDate date, String description, double distance) {
        this.event_id = event_id;
        this.venue_id = venue_id;
        this.event_name = event_name;
        this.date = date;
        this.description = description;
        this.distance = distance;
        this.hasGenre = new ArrayList<>();
    }

    /**
     * Sets a List of Genre
     * @param hasGenre The Genrelist to set
     */
    public void setHasGenre(ArrayList<Genre> hasGenre) {
        this.hasGenre = hasGenre;
    }

    public long getEvent_id() {
        return event_id;
    }

    public long getVenue_id() {
        return venue_id;
    }

    public String getEvent_name() {
        return event_name;
    }

    public String getDate() {
        if(date == null){
            return "No Date";
        }else{
            return date.toString();
        }
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<Genre> getHasGenre() {
        return hasGenre;
    }

    public double getDistance() {
        return distance;
    }
}
