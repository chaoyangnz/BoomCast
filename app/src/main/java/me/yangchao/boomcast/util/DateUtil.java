package me.yangchao.boomcast.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by richard on 4/11/17.
 */

public class DateUtil {

    public static String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public static String formatDuration(long milliseconds) {
        long total_seconds = milliseconds / 1000;
        long minutes = total_seconds/60;
        long seconds = total_seconds % 60;

        long hours = 0L;
        if(minutes >= 60) {
            hours = minutes / 60;
            minutes = minutes % 60;
        }

        return (hours > 0 ? hours + ":" : "" ) + minutes + ":" + seconds;
    }
}
