package org.cathal.ultimateEnvoy.utils;

import org.cathal.ultimateEnvoy.envoys.Envoy;
import org.cathal.ultimateEnvoy.envoys.EnvoyDate;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TimeUtils {

    public TimeUtils(){
    }
    public static String formatHour(int hour){
        if(hour<10){
            return "0" + hour + ":00";
        } else {
            return hour + ":00";
        }
    }

    public static String formatHour(int hour, int minute){
        if(hour<10){
            if(minute < 10){
                return "0" + hour + ":0" + minute;

            }
            return "0" + hour + ":" + minute;
        } else {
            if(minute < 10){
                return hour + ":0"+minute;
            }
            return hour + ":"+minute;
        }
    }

    public static String formatDate(EnvoyDate date){
            return formatHour(date.getHour(),date.getMinute());

    }



    public static EnvoyDate parseTime(String text) {
        String[] strArray = text.split(":");
        if(strArray.length!=2){
            Logger.getLogger("minecraft").log(Level.WARNING,"[UltimateEnvoy] date used is invalid.");
            return null;
        }

        int hour = 0;
        int minute = 0;
        try{
            hour = Integer.parseInt(strArray[0]);
            minute = Integer.parseInt(strArray[1]);
        } catch (NumberFormatException e){
            Logger.getLogger("minecraft").log(Level.WARNING,"[UltimateEnvoy] date used is invalid.");
            return null;
        }

        return new EnvoyDate(hour,minute);
    }

    public static boolean checkForDuplicates(List<EnvoyDate> dates, EnvoyDate dateToCheck){
        for(EnvoyDate d :  dates){
            if(d.checkDate(dateToCheck)){
                return true;
            }
        }

        return false;
    }

    public static String parseDay(int i) {
        switch(i){
            case 0:
                return "Monday";

            case 1:
                return "Tuesday";

            case 2:
                return "Wednesday";

            case 3:
                return "Thursday";

            case 4:
                return "Friday";

            case 5:
                return "Saturday";

            case 6:
            default:
                return "Sunday";
        }
    }

    public static EnvoyDate getCurrentDate(Envoy envoy){
        LocalDateTime time = LocalDateTime.now();
        for(EnvoyDate date : envoy.getEnvoyDates()){
            if(date.getHour()==time.getHour() && date.getMinute()==time.getMinute()&&date.hasDay(time.getDayOfWeek().getValue())){
                return date;
            }
        }
        return null;
    }
}
