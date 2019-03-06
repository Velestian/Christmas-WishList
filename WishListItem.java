package com.gretchen.christmaswishlist;


public class WishListItem {

    private long itemId;
    private long listId;
    private String item;
    private String description;
    private int priority;
    private int cost;
    private int color;
    private String hidden;
    
    public static final String TRUE = "1";
    public static final String FALSE = "0";
    
    public WishListItem() {
        item = "";
        description = "";
        priority = 0;
        cost = 0;
        color = 0;
        hidden = FALSE;
    }

    public WishListItem(int listId, String item, String description, int priority, int cost, int color, String hidden) {
        this.listId = listId;
        this.item = item;
        this.description = description;
        this.priority = priority;
        this.cost = cost;
        this.color = color;
        this.hidden = hidden;
    }

    public WishListItem(int itemId, int listId, String item, String description, int priority, int cost, int color, String hidden) {
        this.itemId = itemId;
        this.listId = listId;
        this.item = item;
        this.description = description;
        this.priority = priority;
        this.cost = cost;
        this.color = color;
        this.hidden = hidden;
    }

    public long getId() {
        return itemId;
    }

    public void setId(long itemId) {
        this.itemId = itemId;
    }
    
    public long getListId() {
        return listId;
    }

    public void setListId(long listId) {
        this.listId = listId;
    }
    
    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getCost(){
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getColor()   {return color;  }

    public void setColor(int color)  {this.color = color;   }
    
    public String getHidden(){
        return hidden;
    }
    
    public void setHidden(String hidden) {
        this.hidden = hidden;    
    }    
}