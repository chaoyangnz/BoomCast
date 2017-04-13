package me.yangchao.boomcast.ui;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.subjects.PublishSubject;
import me.yangchao.boomcast.R;
import me.yangchao.boomcast.model.Episode;
import me.yangchao.boomcast.model.Podcast;
import me.yangchao.boomcast.util.DateUtil;

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

    // UI widgets
    @BindView(R.id.podcast_image) ImageView podcastImage;
    @BindView(R.id.podcast_description) TextView podcastDescription;
    @BindView(R.id.episode_recyclerview) RecyclerView episodesRecyclerView;

    // event subject
    PublishSubject<Long> episodeClicked = PublishSubject.create();

    // others
    LayoutInflater inflater;

    public PodcastFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        Long podcastId = args.getLong(ARG_PODCAST_ID);

        podcast = Podcast.findById(Podcast.class, podcastId);

        getActivity().setTitle(podcast.getTitle());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflater = inflater;
        View view = inflater.inflate(R.layout.fragment_podcast, container, false);
        ButterKnife.bind(this, view);

        Glide.with(container.getContext())
                .load(Uri.parse(podcast.getImageUrl()))
//                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .into(podcastImage);

        podcastDescription.setText(podcast.getDescription());

        episodesRecyclerView.setAdapter(new EpisodesRecyclerAdapter());
        episodesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    class EpisodesRecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {
        public EpisodesRecyclerAdapter() {};

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = inflater.inflate(R.layout.recycler_episodes_item, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Episode episode = PodcastFragment.this.podcast.getEpisodes().get(position);

            if(position % 2 == 1) {
                holder.itemView.setBackgroundResource(R.color.episodeOdd);
            }

            // podcast title, cover, date
            holder.episodeTitle.setText(episode.getTitle());
            holder.episodeDescription.setText(episode.getDescription().trim());
            holder.episodeDuration.setText(DateUtil.formatDuration(episode.getItunesDuration()));
            holder.episodePublishedDate.setText(DateUtil.formatDate(episode.getPublishedDate()));

            // click item event handler
            holder.itemView.setOnClickListener(v -> {
                episodeClicked.onNext(episode.getId());
            });
        }

        @Override
        public int getItemCount() {
            return podcast.getEpisodes().size();
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
