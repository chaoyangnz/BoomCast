package me.yangchao.boomcast.model;

import com.orm.SugarRecord;
import com.rometools.modules.itunes.EntryInformation;
import com.rometools.rome.feed.synd.SyndEntry;

import java.util.Date;
import java.util.List;

/**
 * Created by richard on 4/10/17.
 */

public class Episode extends SugarRecord {
    private Long podcastId;

    private String title;
    private String description;
    private String enclosureUrl;
    private String link;
    private Date publishedDate;
    private String itunesSummary;
    private Long itunesDuration;
    private String itunesAuthor;

    public Long getPodcastId() {
        return podcastId;
    }

    public void setPodcastId(Long podcastId) {
        this.podcastId = podcastId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEnclosureUrl() {
        return enclosureUrl;
    }

    public void setEnclosureUrl(String enclosureUrl) {
        this.enclosureUrl = enclosureUrl;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getItunesSummary() {
        return itunesSummary;
    }

    public void setItunesSummary(String itunesSummary) {
        this.itunesSummary = itunesSummary;
    }

    public Long getItunesDuration() {
        return itunesDuration;
    }

    public void setItunesDuration(Long itunesDuration) {
        this.itunesDuration = itunesDuration;
    }

    public String getItunesAuthor() {
        return itunesAuthor;
    }

    public void setItunesAuthor(String itunesAuthor) {
        this.itunesAuthor = itunesAuthor;
    }

    public static Episode fromSyndEntry(SyndEntry entry) {
        EntryInformation entryInfo = (EntryInformation) entry.getModule("http://www.itunes.com/dtds/podcast-1.0.dtd");

        Episode episode = new Episode();
        episode.setTitle(entry.getTitle());
        episode.setDescription(entry.getDescription().getValue());
        episode.setLink(entry.getLink());
        episode.setEnclosureUrl(entry.getEnclosures().get(0).getUrl());
        episode.setPublishedDate(entry.getPublishedDate());
        episode.setItunesSummary(entryInfo.getSummary());
        episode.setItunesSummary(entryInfo.getAuthor());
        episode.setItunesDuration(entryInfo.getDuration().getMilliseconds());

        return episode;
    }

    public static Episode findById(Long id) {
        return Episode.findById(Episode.class, id);
    }

    public static Episode findByPodcastAndLink(Long podcastId, String link) {
        List<Episode> existingEpisodes = Episode.find(Episode.class, "podcast_id = ? and link = ? limit 1",
                String.valueOf(podcastId), link);
        if(existingEpisodes.isEmpty()) return null;
        return existingEpisodes.get(0);
    }

    public static List<Episode> findByPodcast(Long podcastId) {
        return Episode.find(Episode.class, "podcast_id = ? order by published_date desc", String.valueOf(podcastId));
    }

    public static List<Episode> findByPocastAndKeyword(Long podcastId, String query) {
        if(query == null) return findByPodcast(podcastId);
        return Episode.find(Episode.class, "podcast_id = ? and (title like ? or description like ?) order by published_date desc",
                String.valueOf(podcastId), "%"+query+"%", "%"+query+"%");
    }
}
