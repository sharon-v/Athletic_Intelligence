package com.ai.app;

public class Fight {
    private String name1;
    private String score1;
    private String name2;
    private String score2;
    private String amount;
    private String exercise;
    private String winner;

    public Fight()
    {
        this.name1="";
        this.score1="";
        this.name2="";
        this.score2="";
        this.amount="";
        this.exercise="";
        this.winner="";
    }

    public String getName1() {
        return name1;
    }
    public String getName2() {
        return name2;
    }
    public void setName1(String name1) {
        this.name1 = name1;
    }
    public void setName2(String name2) {
        this.name2 = name2;
    }
    public void setWinner(String winner) {this.winner = winner;}

    public String getScore1() {
        return score1;
    }
    public String getScore2() {
        return score2;
    }
    public String getWinner() {return winner;}
    public String getAmount(){return amount;}

    public void setScore1(String score1) {
        this.score1 = score1;
    }
    public void setScore2(String score2) {
        this.score2 = score2;
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }
    public void setExercise(String exercise) {
        this.exercise = exercise;
    }
}
