package me.yangchao.boomcast.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;

import me.yangchao.boomcast.R;

public class MainActivity extends BaseActivity {

    private final static int REQUEST_NEW_PODCAST = 1;

    // managed Fragements
    PodcastsFragment podcastsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addToolbar(true);
        addNavigation();

        podcastsFragment = addFragment(PodcastsFragment::new, R.id.podcasts_fragment);
        podcastsFragment.podcastClicked.subscribe(podcastId -> {
            PodcastActivity.startActivity(this, podcastId);
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_fab);
        fab.setOnClickListener(v -> {
            PodcastNewActivity.startActivity(this, REQUEST_NEW_PODCAST);
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_NEW_PODCAST) {
            // Handle result
            if(resultCode == RESULT_OK) {
                Snackbar.make(findViewById(R.id.podcasts_fragment), R.string.info_podcast_subscribed, Snackbar.LENGTH_LONG)
                        .show();

                podcastsFragment.refresh();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) { // handle home button to show drawer
            case android.R.id.home:
                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    private void addNavigation() {
        // drawer: navigation view
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav);
        ImageView userAvatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.user_avatar);
        TextDrawable drawable = TextDrawable.builder().buildRound("RY", R.color.playerGrey);
        userAvatar.setImageDrawable(drawable);
        navigationView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawers();
            switch (item.getItemId()) {
//                case R.id.nav_settings:
//                    break;
                case R.id.nav_statistics:
//                    Intent intent = new Intent(SearchActivity.ACTION_FAVORITE);
//                    startActivity(intent);
                    break;
                case R.id.nav_help:
                    HelpActivity.startActivity(this);
                    break;
                default:
            }
            return true;
        });
    }
}
