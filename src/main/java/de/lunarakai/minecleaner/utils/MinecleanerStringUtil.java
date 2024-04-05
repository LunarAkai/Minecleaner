package de.lunarakai.minecleaner.utils;

public class MinecleanerStringUtil {
    private MinecleanerStringUtil() {
    }    
    
    public static String timeToString(long millis) {
        int sec = (int) (millis / 1000);
        int min = sec / 60;
        int hours = min / 60;
        sec = sec % 60;
        min = min % 60;
        StringBuilder timeString = new StringBuilder();
        if (hours > 0) {
            timeString.append(hours).append(" Stunden");
        }
        if (min > 0 || !timeString.isEmpty()) {
            if (!timeString.isEmpty()) {
                timeString.append(", ");
            }
            timeString.append(min).append(" Minuten");
        }
        if (sec > 0 || !timeString.isEmpty()) {
            if (!timeString.isEmpty()) {
                timeString.append(" und ");
            }
            timeString.append(sec).append(" Sekunden");
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
}
