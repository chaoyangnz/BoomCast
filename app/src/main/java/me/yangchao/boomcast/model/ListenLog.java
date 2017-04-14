package me.yangchao.boomcast.model;

import com.orm.SugarRecord;

import java.util.Date;
import java.util.List;

import me.yangchao.boomcast.util.DateUtil;

/**
 * Created by richard on 4/13/17.
 */

public class ListenLog extends SugarRecord {

    private Long episodeId;
    private Long duration = 0L;
    private String date;

    public Long getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(Long episodeId) {
        this.episodeId = episodeId;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static ListenLog findByDateAndEpisode(String date, Long episodeId) {
        List<ListenLog> listenLogs = ListenLog.find(ListenLog.class, "episode_id = ? and date = ? limit 1",
                String.valueOf(episodeId), date);
        if(listenLogs.isEmpty()) return null;
        return listenLogs.get(0);
    }

    public static long getListenTimeOfToday() {
        String today = DateUtil.formatDate(new Date());
        List<ListenPerDay> listensToday = SugarRecord.findWithQuery(ListenPerDay.class,
                "select DATE, sum(duration) as DURATION, 0 as ID from listen_log where DATE = ? group by date ", today);
        return listensToday.isEmpty() ? 0L : listensToday.get(0).getDuration();
    }

    public List<ListenPerDay> getListenTimePerDay() {
        return SugarRecord.findWithQuery(ListenPerDay.class,
                "select DATE, sum(duration) as DURATION, 0 as ID from listen_log group by date limit 15");
    }

    public static class ListenPerDay {
        private String date;
        private long duration;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }
    }
}
