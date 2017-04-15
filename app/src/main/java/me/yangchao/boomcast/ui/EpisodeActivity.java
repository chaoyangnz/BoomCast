package me.yangchao.boomcast.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import me.yangchao.boomcast.R;

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
        addFragment(EpisodeFragment.newInstance(episodeId), R.id.episode_player_fragment);
    }
}
