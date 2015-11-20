package com.procrastinate.davidtheprocrastinator.gestionduparc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by David on 15/11/2015.
 */
public class Issue {

    private int id;
    private IssueType type;
    private String address;
    private double latitude;
    private double longitude;
    private String title;
    private String date;
    private boolean resolved;
    private String description;


    public Issue(){

    }

    public Issue(String title, IssueType issue, double lat, double lng) throws WrongIssueParametersException{
        if(title.trim().equals(""))
            throw new WrongIssueParametersException("Le titre est vide.");
        this.title = title;
        if(issue == null || Double.isNaN(lat) || Double.isNaN(lng))
            throw new WrongIssueParametersException("Un des paramètres est nul.");
        this.type = issue;
        this.latitude = lat;
        this.longitude = lng;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public IssueType getType() {
        return type;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public boolean isResolved() {
        return resolved;
    }

    public String getDescription() {
        return this.description;
    }

    public void setAddress(String string) {
        this.address = string;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setType(IssueType type) {
        this.type = type;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
