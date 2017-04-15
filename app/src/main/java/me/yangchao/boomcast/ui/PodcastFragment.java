package me.yangchao.boomcast.ui;


import android.app.SearchManager;
import android.content.ComponentName;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.subjects.PublishSubject;
import me.yangchao.boomcast.R;
import me.yangchao.boomcast.model.Episode;
import me.yangchao.boomcast.model.Podcast;
import me.yangchao.boomcast.net.PodcastFeedRequest;
import me.yangchao.boomcast.util.DateUtil;
import me.yangchao.boomcast.util.StringUtil;

import static android.content.Context.SEARCH_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class PodcastFragment extends Fragment {

    private static final String ARG_PODCAST_ID = "podcastId";

    public static PodcastFragment newInstance(Long podcastId) {
        PodcastFragment fragment = new PodcastFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PODCAST_ID, podcastId);
        fragment.setArguments(args);
        return fragment;
    }

    // data
    private Podcast podcast;
    private List<Episode> episodes = new ArrayList<>();

    // UI widgets
    @BindView(R.id.podcast_image) ImageView podcastImage;
    @BindView(R.id.podcast_description) TextView podcastDescription;
    @BindView(R.id.episode_recyclerview) RecyclerView episodesRecyclerView;

    // event subject
    PublishSubject<Long> episodeClicked = PublishSubject.create();

    // others
    LayoutInflater inflater;
    Long podcastId;

    public PodcastFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle args = getArguments();
        podcastId = args.getLong(ARG_PODCAST_ID);

        podcast = Podcast.findById(podcastId);

        getActivity().setTitle(podcast.getTitle());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflater = inflater;
        View view = inflater.inflate(R.layout.fragment_podcast, container, false);
        ButterKnife.bind(this, view);

        Glide.with(getContext())
                .load(Uri.parse(podcast.getImageUrl()))
//                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .into(podcastImage);

        podcastDescription.setText(podcast.getDescription());

        episodesRecyclerView.setAdapter(new EpisodesRecyclerAdapter());
        episodesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refresh(null);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // don't show option menu when searching
        inflater.inflate(R.menu.podcast_menu, menu);
        // search action
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setQueryHint(getString(R.string.episodes_search_hint));
        // fix full screen in landscape
        int options = searchView.getImeOptions();
        searchView.setImeOptions(options| EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                refresh(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) { return false;}
        });

        searchView.setOnCloseListener(() -> {
            refresh(null);
            return false;
        });

        // don't use system intent-based search manager.
        SearchManager searchManager = (SearchManager) getContext().getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(getContext(), PodcastActivity.class)));

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // save notes
            case R.id.action_refresh:
                // refresh episode
                PodcastFeedRequest.requstFeedSource(getContext(), podcast.getFeedUrl(),
                        refreshedPodcast -> {
                            // SnackBar to display successful message
                            Snackbar.make(getView(), R.string.info_episodes_refreshed,
                                    Snackbar.LENGTH_SHORT)
                                    .show();
                            refresh(null);
                        }, error -> {});
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refresh(String query) {
        episodes = Episode.findByPocastAndKeyword(podcastId, query);

        if(episodesRecyclerView != null) {
            episodesRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    class EpisodesRecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {
        public EpisodesRecyclerAdapter() {}

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = inflater.inflate(R.layout.recycler_episodes_item, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Episode episode = episodes.get(position);

            if(position % 2 == 1) {
                holder.itemView.setBackgroundResource(R.color.episodeOdd);
            }

            // podcast title, cover, date
            holder.episodeTitle.setText(episode.getTitle());
            holder.episodeDescription.setText(StringUtil.trim(episode.getDescription().trim(), 100));
            holder.episodeDuration.setText(DateUtil.formatDuration(episode.getItunesDuration()));
            holder.episodePublishedDate.setText(DateUtil.formatDate(episode.getPublishedDate()));

            // click item event handler
            holder.itemView.setOnClickListener(v -> {
                episodeClicked.onNext(episode.getId());
            });
        }

        @Override
        public int getItemCount() {
            return episodes.size();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.episode_title) TextView episodeTitle;
        @BindView(R.id.episode_description) TextView episodeDescription;
        @BindView(R.id.episode_duration) TextView episodeDuration;
        @BindView(R.id.episode_publisheddate) TextView episodePublishedDate;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
