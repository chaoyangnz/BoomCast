package me.yangchao.boomcast.ui;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import me.yangchao.boomcast.R;
import me.yangchao.boomcast.model.Podcast;
import me.yangchao.boomcast.net.PodcastFeedRequest;

public class PodcastActivity extends BaseActivity {

    private static final String INTENT_PODCAST_ID = "podcastId";

    private Long podcastId;
    private String query;

    // managed Fragments
    PodcastFragment podcastFragment;

    public static void startActivity(Context context, Long podcastId) {
        Intent intent = new Intent(context, PodcastActivity.class);
        intent.putExtra(PodcastActivity.INTENT_PODCAST_ID, podcastId);
        context.startActivity(intent);
    }

    @Override
    public void startActivity(Intent intent) {
        //check if search intent
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            intent.putExtra(PodcastActivity.INTENT_PODCAST_ID, podcastId);
        }

        super.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast);

        addToolbar();

        Intent intent = getIntent();
        podcastId = intent.getLongExtra(INTENT_PODCAST_ID, 1);

        query = Intent.ACTION_SEARCH.equals(intent.getAction()) ?
                intent.getStringExtra(SearchManager.QUERY) : null;


        podcastFragment = addFragment(() -> PodcastFragment.newInstance(podcastId, query), R.id.podcast_fragment);
        podcastFragment.episodeClicked.subscribe(episodeId -> {
           EpisodeActivity.startActivity(this, episodeId);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // don't show option menu when searching
        if(query != null) return true;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.podcast_menu, menu);

        // search action
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, PodcastActivity.class)));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // save notes
            case R.id.action_refresh:
                // refresh episode
                Podcast p = Podcast.findById(podcastId);
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
