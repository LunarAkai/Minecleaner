package de.lunarakai.minecleaner.utils;

import java.net.URL;

public class MinecleanerStringUtil {
    private MinecleanerStringUtil() {
    }    
    
    public static String timeToString(long millis, boolean shorten) {
        int sec = (int) (millis / 1000);
        int min = sec / 60;
        int hours = min / 60;
        sec = sec % 60;
        min = min % 60;
        StringBuilder timeString = new StringBuilder();
        if (hours > 0) {
            String hoursString = " Stunden";
            if(shorten) {
                hoursString = " h";
            }
            timeString.append(hours).append(hoursString);
        }
        if (min > 0 || !timeString.isEmpty()) {
            if (!timeString.isEmpty()) {
                timeString.append(", ");
            }
            String minString = " Minuten";
            if(shorten) {
                minString = " min";
            }
            timeString.append(min).append(minString);
        }
        if (sec > 0 || !timeString.isEmpty()) {
            if (!timeString.isEmpty()) {
                timeString.append(" und ");
            }
            String secondsString = " Sekunden";
            if(shorten) {
                secondsString = " s";
            }
            timeString.append(sec).append(secondsString);
        }
        return timeString.toString();
    }

    public static String percentageString(int whatPercentage, int fromPercentage) {
        if(fromPercentage == 0) {
            return "-";
        }
        float percent = (float) whatPercentage/fromPercentage;
        percent = percent * 100;
        String percentageString = String.format("%.1f", percent);
        return percentageString + "%";
    }

    public static boolean isValidURL(String urlString) {
        try {
            URL url = new URL(urlString);
            url.toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
