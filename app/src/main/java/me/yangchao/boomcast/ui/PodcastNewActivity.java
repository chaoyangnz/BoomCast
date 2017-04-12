package me.yangchao.boomcast.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import me.yangchao.boomcast.R;

public class PodcastNewActivity extends BaseActivity {

    public static void startActivity(Context context, int requestCode) {
        Intent intent = new Intent(context, PodcastNewActivity.class);
        ((Activity) context).startActivityForResult(intent, requestCode);
//        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast_new);

        addToolbar();

        setTitle("Add Podcast by URL");

        PodcastNewFragment podcastNewFragment = addFragment(PodcastNewFragment::new, R.id.podcast_new_fragment);
        podcastNewFragment.subscriptionSaved.subscribe(podcast -> {
            setResult(RESULT_OK);
            finish();
        });
    }
}
