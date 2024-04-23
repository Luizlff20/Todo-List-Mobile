package com.example.entregageral;


public class CardItem {

    private String id;
    private String title;
    private String description;
    private String startDate;
    private String endDate;

    public CardItem( String id, String title, String description, String startDate, String endDate) {

        this.id= id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }



    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }


}
