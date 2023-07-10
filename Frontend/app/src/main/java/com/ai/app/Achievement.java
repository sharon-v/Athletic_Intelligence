package com.ai.app;

import java.util.List;

public class Achievement {
    //attributes
    private String name;
//    private String description;
//    private String icon;
//    private boolean received;
//    private int listOrder;  //after which achievement it should be received
    private List<Integer> data;

    //constructor
    private Achievement(){
        this.name = "";
//        this.icon = "";
//        this.received = false;
        this.data=null;
    }
    public Achievement(List<Integer> data, String name)
    {
        this.data=data;
        this.name=name;
    }

    //getters & setters
    public String getName() {
        return name;
    }

//    public String getDescription() {
//        return description;
//    }

//    public String getIcon() {
//        return icon;
//    }
    public List<Integer> getData(){ return data;}
    public void setData(List<Integer> data){this.data = data;}

//    public boolean isReceived() {
//        return received;
//    }

//    public int getListOrder() {
//        return listOrder;
//    }

    public void setName(String name) {
        this.name = name;
    }

//    public void setDescription(String description) {
//        this.description = description;
//    }

//    public void setIcon(String icon) {
//        this.icon = icon;
//    }

//    public void setReceived(boolean received) {
//        this.received = received;
//    }

//    public void setListOrder(int listOrder) {
//        this.listOrder = listOrder;
//    }

    //more methods ....
}
