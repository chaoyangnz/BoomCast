package me.yangchao.boomcast.ui;


import android.content.Context;
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
    private ImageView podcastImage;
    private TextView podcastDescription;
    private RecyclerView episodesRecyclerView;

    // event subject
    PublishSubject<Long> episodeClicked = PublishSubject.create();

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
        View view = inflater.inflate(R.layout.fragment_podcast, container, false);
        podcastImage = (ImageView) view.findViewById(R.id.podcast_image);
        podcastDescription = (TextView) view.findViewById(R.id.podcast_description);

        Glide.with(container.getContext())
                .load(Uri.parse(podcast.getImageUrl()))
//                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .into(podcastImage);

        podcastDescription.setText(podcast.getDescription());

        episodesRecyclerView = (RecyclerView) view.findViewById(R.id.episode_recyclerview);
        episodesRecyclerView.setAdapter(new EpisodesRecyclerAdapter());
        episodesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    class EpisodesRecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {

        private Context context;

        public EpisodesRecyclerAdapter() {};

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (context == null) context = parent.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.recycler_episodes_item, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Episode episode = PodcastFragment.this.podcast.getEpisodes().get(position);

            if(position % 2 == 1) {
                holder.containterLayout.setBackgroundResource(R.color.episodeOdd);
            }

            // podcast title, cover, date
            holder.episodeTitle.setText(episode.getTitle());
            holder.episodeDescription.setText(episode.getDescription().trim());
            holder.episodeDuration.setText(DateUtil.formatDuration(episode.getItunesDuration()));
            holder.episodePublishedDate.setText(DateUtil.formatDate(episode.getPublishedDate()));

            // click item event handler
            holder.container.setOnClickListener(v -> {
                episodeClicked.onNext(episode.getId());
            });
        }

        @Override
        public int getItemCount() {
            return podcast.getEpisodes().size();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View container;
//        ImageView episodeImage;
        View containterLayout;
        TextView episodeTitle;
        TextView episodeDescription;
        TextView episodeDuration;
        TextView episodePublishedDate;

        public ViewHolder(View view) {
            super(view);
            container = view;
            containterLayout = view.findViewById(R.id.container_layout);
//            episodeImage = (ImageView) view.findViewById(R.id.episode_image);
            episodeTitle = (TextView) view.findViewById(R.id.episode_title);
            episodeDescription = (TextView) view.findViewById(R.id.episode_description);
            episodeDuration = (TextView) view.findViewById(R.id.episode_duration);
            episodePublishedDate = (TextView) view.findViewById(R.id.episode_publisheddate);
        }
    }

}
