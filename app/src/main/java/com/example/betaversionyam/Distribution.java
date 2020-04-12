package com.example.betaversionyam;

import java.util.ArrayList;

/**
 * @author		Yam Azoulay
 * @version	    1.0
 * @since		13/02/2020
 *
 * this java class allows to create a new Item named Distribution. Used to save all of the details of any distribution.
 */
public class Distribution {
    private String dateAndTime;
    private String name;
    private ArrayList<String> SelectedUsersList;
    private Area area;
    private boolean isActive;

    public Distribution (String dateAndTime,String name, ArrayList<String> SelectedUsersList, Area area, boolean isActive ){
        this.dateAndTime = dateAndTime;
        this.name = name;
        this.SelectedUsersList = SelectedUsersList;
        this.area = area;
        this.isActive = isActive;
    }
    public Distribution(){
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setSelectedUsersList(ArrayList<String> selectedUsersList) {
        SelectedUsersList = selectedUsersList;
    }

    public ArrayList<String> getSelectedUsersList() {
        return SelectedUsersList;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
    public boolean isActive(){
        return isActive;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }
}
