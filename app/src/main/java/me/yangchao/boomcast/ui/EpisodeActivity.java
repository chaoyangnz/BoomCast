package me.yangchao.boomcast.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import me.yangchao.boomcast.R;
import me.yangchao.boomcast.model.Episode;
import me.yangchao.boomcast.model.Podcast;

public class EpisodeActivity extends BaseActivity {

    private static final String INTENT_EPISODE_ID = "episodeId";

    private Long episodeId;

    public static void startActivity(Context context, Long episodeId) {
        Intent intent = new Intent(context, EpisodeActivity.class);
        intent.putExtra(EpisodeActivity.INTENT_EPISODE_ID, episodeId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode);

        addToolbar();

        Intent intent = getIntent();
        episodeId = intent.getLongExtra(INTENT_EPISODE_ID, 1);
        addFragment(() -> EpisodeFragment.newInstance(episodeId), R.id.episode_player_fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.episode_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // save notes
            case R.id.action_share:
                // share content
                Episode episode = Episode.findById(episodeId);
                Podcast podcast = Podcast.findById(episode.getPodcastId());
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                String sharingContent = String.format(getString(R.string.share_episode_template),
                        podcast.getTitle(), episode.getTitle(), episode.getEnclosureUrl());
                sendIntent.putExtra(Intent.EXTRA_TEXT, sharingContent);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getString(R.string.share_episode_title)));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
