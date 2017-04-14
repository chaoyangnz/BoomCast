package me.yangchao.boomcast.model;

import com.orm.SugarRecord;

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
}
