package me.yangchao.boomcast.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import me.yangchao.boomcast.R;
import me.yangchao.boomcast.model.Podcast;
import me.yangchao.boomcast.net.PodcastFeedRequest;

public class PodcastActivity extends BaseActivity {

    private static final String INTENT_PODCAST_ID = "podcastId";

    private Long podcastId;

    // managed Fragments
    PodcastFragment podcastFragment;

    public static void startActivity(Context context, Long podcastId) {
        Intent intent = new Intent(context, PodcastActivity.class);
        intent.putExtra(PodcastActivity.INTENT_PODCAST_ID, podcastId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast);

        addToolbar();

        Intent intent = getIntent();
        podcastId = intent.getLongExtra(INTENT_PODCAST_ID, 1);

        podcastFragment = addFragment(() -> PodcastFragment.newInstance(podcastId), R.id.podcast_fragment);
        podcastFragment.episodeClicked.subscribe(episodeId -> {
           EpisodeActivity.startActivity(this, episodeId);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.podcast_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // save notes
            case R.id.action_refresh:
                // refresh episode
                Podcast p = Podcast.findById(Podcast.class, podcastId);
                PodcastFeedRequest.requstFeedSource(this, p.getFeedUrl(),
                        podcast -> {
                            // SnackBar to display successful message
                            Snackbar.make(findViewById(R.id.podcast_fragment), R.string.info_episodes_refreshed,
                                    Snackbar.LENGTH_SHORT)
                                    .show();
                            podcastFragment.refresh();
                        }, error -> {});
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
