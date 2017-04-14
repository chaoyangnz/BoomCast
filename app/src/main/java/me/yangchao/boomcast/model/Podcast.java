package me.yangchao.boomcast.model;

import com.orm.SugarRecord;
import com.rometools.modules.itunes.FeedInformation;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.synd.SyndFeed;

import java.util.Date;
import java.util.List;

/**
 * Created by richard on 4/10/17.
 */
public class Podcast extends SugarRecord {

    private String feedUrl;
    private String title;
    private String link;
    private String description;
    private String imageUrl;
    private String imageData;
    private String author;
    private String language;

    private Date publishedDate;

    private Date refreshedDate;

    public String getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public Date getRefreshedDate() {
        return refreshedDate;
    }

    public void setRefreshedDate(Date refreshedDate) {
        this.refreshedDate = refreshedDate;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public static Podcast fromSyncFeed(SyndFeed feed) {
        Module module = feed.getModule("http://www.itunes.com/dtds/podcast-1.0.dtd");
        FeedInformation feedInfo = (FeedInformation) module;

        Podcast podcast = new Podcast();
//        podcast.setFeedUrl(feed.getUri());
        podcast.setTitle(feed.getTitle());
        podcast.setDescription(feed.getDescription());
        podcast.setAuthor(feed.getAuthor());
        podcast.setLink(feed.getLink());
        String imageUrl = "";
        if(feed.getImage() != null) {
            imageUrl = feed.getImage().getUrl();
        } else if(feedInfo.getImage() != null) {
            imageUrl = feedInfo.getImage().toString();
        }
        podcast.setImageUrl(imageUrl);
        podcast.setImageData("");
        podcast.setPublishedDate(feed.getPublishedDate());
        podcast.setRefreshedDate(new Date());

        return podcast;
    }

    public static Podcast findById(Long id) {
        return Podcast.findById(Podcast.class, id);
    }

    public static Podcast findByFeedUrl(String feedUrl) {
        List<Podcast> existingPodcasts = Podcast.find(Podcast.class, "feed_url = ? limit 1", feedUrl);
        if(existingPodcasts.isEmpty()) return null;
        return existingPodcasts.get(0);
    }
}
