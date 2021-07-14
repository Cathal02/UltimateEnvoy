package org.cathal.ultimateEnvoy.envoys;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class EnvoyDate {
    private ArrayList<Integer> days;
    private int hour;
    private int minute;

    public EnvoyDate(ArrayList<Integer> days, int hour, int minute){
        this.minute = minute;
        this.hour = hour;
        this.days = days;
    }
    public EnvoyDate(int hour, int minute){
        this.minute = minute;
        this.hour = hour;
        this.days = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            days.add(i);
        }
    }

    public boolean checkDate(EnvoyDate date){
        return hour==date.getHour()&&minute==date.getMinute();
    }

    public boolean checkDate(int day, int hour, int minute){

        return hasDay(day)&&this.hour==hour&&this.minute==minute;
    }


    public ArrayList<Integer> getDays() {
        return days;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }


    public void update(EnvoyDate date) {
        this.minute = date.getMinute();
        this.hour =date.getHour();
        this.days = date.getDays();
    }

    public int getDay(int i) {
        return days.get(i);
    }

    public boolean hasDay(int i ) {
        return days.contains(i);
    }

    //TOOD: Map of <Day,Boolean> probably easier and more efficient but
    // thought only came after implementing this way so fuck it

    public void removeDay(int day) {
        if(!days.contains(day))return;
        int remove=-1;
        for(int i = 0; i < days.size(); i++){
            if(days.get(i)==day){
                remove = i;
                break;
            }
        }

        if(remove==-1)return;
        days.remove(remove);
    }

    public void addDay(int rawSlot) {
        days.add(rawSlot);
    }
}
