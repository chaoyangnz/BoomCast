package me.yangchao.boomcast.net;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.HttpHeaderParser;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import me.yangchao.boomcast.App;
import me.yangchao.boomcast.model.Episode;
import me.yangchao.boomcast.model.Podcast;

import static me.yangchao.boomcast.App.getContext;

/**
 * Created by richard on 4/11/17.
 */

public class PodcastFeedRequest extends Request<Podcast> {
    private final Response.Listener<Podcast> mListener;

    public PodcastFeedRequest(int method, String url, Response.Listener<Podcast> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
    }

    public static void requstFeedSource(Context context, String feedUrl, Response.Listener<Podcast> listener, Response.ErrorListener errorListener) {
        ProgressDialog progress = new ProgressDialog(context);
        progress.setCanceledOnTouchOutside(false);
        progress.setMessage("Loading and parsing...");
        progress.show();

        PodcastFeedRequest request = new PodcastFeedRequest(Request.Method.GET, feedUrl,
                podcast -> {
                    listener.onResponse(podcast);
                    if (progress.isShowing()) {
                        progress.cancel();
                    }
                }, error -> {
                    Toast.makeText(getContext(), "Network errors, please check network settings.", Toast.LENGTH_LONG);
                    System.err.println(error);
                    errorListener.onErrorResponse(error);
                    if (progress.isShowing()) {
                        progress.cancel();
                    }
        });
        int socketTimeout = 30_000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        request.setRetryPolicy(policy);
        App.addRequest(request, "RSS");
    }

    @Override
    protected Response<Podcast> parseNetworkResponse(NetworkResponse response) {

        try {
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(new ByteArrayInputStream(response.data)));

            Podcast podcast = Podcast.fromSyncFeed(feed);
            podcast.setFeedUrl(getUrl());
            Podcast existingPodcast = Podcast.findByFeedUrl(getUrl());
            if(existingPodcast != null) {
                podcast.setId(existingPodcast.getId());
            }
            podcast.save();

            for(SyndEntry entry : feed.getEntries()) {
                Episode episode = Episode.fromSyndEntry(entry);
                episode.setPodcastId(podcast.getId());

                Episode existingEpisode = Episode.findByPodcastAndLink(podcast.getId(), entry.getLink());
                if(existingEpisode != null) {
                    episode.setId(existingEpisode.getId());
                }

                episode.save();
            }
            return Response.success(podcast, HttpHeaderParser.parseCacheHeaders(response));
        } catch (FeedException | IOException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(Podcast response) {
        mListener.onResponse(response);
    }
}
