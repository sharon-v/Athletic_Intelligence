package com.ai.app;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class User {
    private String username;
    private String email;
    private List<String> fights;
    private List<String> achievements;
    private int lost;
    private int win;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, List<String> fights, List<String> achievements) {
        this.username = username;
        this.email = email;
        this.fights = fights;
        this.achievements = achievements;
        this.lost = 0;
        this.win = 0;
    }
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUserId() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getFights() {
        return fights;
    }

    public List<String> getAchievements() {
        return achievements;
    }

    public int getLost(){return lost;}
    public int getWin(){return win;}
    public void setLost(int num){this.lost =num;}
    public void setWin(int num){this.win = num;}
}

