package com.Me.ShiftBoard.Util;

public class HistoryGenerator {


    private String type;
    private String action;
    private String result;



    public static String generateHistoryString(String object, String action, String result){

        return object +" " + action + " " + result + ".";
    }

}
