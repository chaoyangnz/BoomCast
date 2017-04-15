package me.yangchao.boomcast.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.yangchao.boomcast.App;
import me.yangchao.boomcast.R;
import me.yangchao.boomcast.model.Episode;

public class MainActivity extends BaseActivity {

    private final static int REQUEST_NEW_PODCAST = 1;

    // managed Fragements
    PodcastsFragment podcastsFragment;

    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.nav) NavigationView navigationView;

    boolean twoPanel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        addToolbar(true);
        addNavigation();
        drawerLayout.setFocusableInTouchMode(false);

        View view = findViewById(R.id.right_fragment);
        twoPanel = view != null;
        podcastsFragment = addFragment(new PodcastsFragment(), R.id.podcasts_fragment);
        if(twoPanel) { // two panel
            podcastsFragment.podcastClicked.subscribe(podcastId -> {
                PodcastFragment podcastFragment = addFragment(PodcastFragment.newInstance(podcastId),
                        R.id.right_fragment);
                podcastFragment.episodeClicked.subscribe(episodeId -> {
                    EpisodeFragment episodeFragment = addFragment(EpisodeFragment.newInstance(episodeId),
                            R.id.right_fragment);
                });
            });
        } else {
            // MUST remove, since Android retains fragments when rotate screen
            removeFragment(R.id.right_fragment);
            podcastsFragment.podcastClicked.subscribe(podcastId -> {
                PodcastActivity.startActivity(this, podcastId);
            });
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_fab);
        fab.setOnClickListener(v -> {
            if(twoPanel) {
                addFragment(PodcastNewFragment.newInstance(null), R.id.right_fragment);
            } else {
                PodcastNewActivity.startActivity(this, REQUEST_NEW_PODCAST);
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
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
                return true;
            case R.id.action_player:
                // refresh episode
                Episode episode = App.getInstance().mediaPlayerService.episode;
                if(episode != null) {
                    if(twoPanel) {
                        addFragment(EpisodeFragment.newInstance(episode.getId()), R.id.right_fragment);
                    } else {
                        EpisodeActivity.startActivity(this, episode.getId());
                    }
                } else {
                    // SnackBar to display successful message
                    Snackbar.make(findViewById(R.id.podcasts_fragment), R.string.warn_no_episode_playing,
                            Snackbar.LENGTH_SHORT)
                            .show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private void addNavigation() {
        // drawer: navigation view
        ImageView userAvatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.user_avatar);
        TextDrawable drawable = TextDrawable.builder().buildRound("RY", R.color.playerGrey);
        userAvatar.setImageDrawable(drawable);
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_statistics:
                    StatisticsActivity.startActivity(this);
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
