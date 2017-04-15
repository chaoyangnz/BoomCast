package me.yangchao.boomcast.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import me.yangchao.boomcast.R;

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

        podcastFragment = addFragment(PodcastFragment.newInstance(podcastId), R.id.podcast_fragment);
        podcastFragment.episodeClicked.subscribe(episodeId -> {
           EpisodeActivity.startActivity(this, episodeId);
        });
    }
}
