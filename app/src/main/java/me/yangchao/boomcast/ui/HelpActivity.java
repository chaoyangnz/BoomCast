package me.yangchao.boomcast.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import me.yangchao.boomcast.R;
import us.feras.mdv.MarkdownView;

public class HelpActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        setTitle(getString(R.string.help_page_title));

        addToolbar();

        MarkdownView markdownView = (MarkdownView) findViewById(R.id.help_markdownView);
        markdownView.loadMarkdownFile("file:///android_asset/README.md", "file:///android_asset/markdown.css");
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, HelpActivity.class);
        context.startActivity(intent);
    }
}
