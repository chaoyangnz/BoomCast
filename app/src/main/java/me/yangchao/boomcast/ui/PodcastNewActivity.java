package me.yangchao.boomcast.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;

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

        String feedUrl = handleSharedFeedUrl();

        PodcastNewFragment podcastNewFragment = addFragment(PodcastNewFragment.newInstance(feedUrl), R.id.podcast_new_fragment);
        podcastNewFragment.subscriptionSaved.subscribe(podcast -> {
            setResult(RESULT_OK);
            finish();
        });
    }

    private String handleSharedFeedUrl() {
        String feedUrl = null;
        // handle receiving sharing
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String url = intent.getStringExtra(Intent.EXTRA_TEXT).trim();
                if(url.startsWith("http://") || url.startsWith("https://")) {
                    feedUrl = url;
                } else {
                    Snackbar.make(findViewById(R.id.podcast_new_fragment),
                            "Invalid feed URL", BaseTransientBottomBar.LENGTH_LONG)
                            .show();
                }

            }
        }
        return feedUrl;
    }
}
