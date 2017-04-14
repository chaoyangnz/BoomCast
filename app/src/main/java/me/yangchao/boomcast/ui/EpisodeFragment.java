package me.yangchao.boomcast.ui;


import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.yangchao.boomcast.App;
import me.yangchao.boomcast.MediaPlayerService;
import me.yangchao.boomcast.R;
import me.yangchao.boomcast.model.Episode;
import me.yangchao.boomcast.model.Podcast;
import me.yangchao.boomcast.util.BlurTransformation;

/**
 * A simple {@link Fragment} subclass.
 */
public class EpisodeFragment extends Fragment {

    public EpisodeFragment() {
        // Required empty public constructor
    }

    private static final String ARG_EPISODE_ID = "episodeId";
    private static final int BLUR_RADIUS = 125;
    private static final int BLUR_FILTER = 0x8C2E2E2E;
    private static final int SEEKBAR_UPDATE_INTERVEL = 1000;

    // UI widget
    @BindView(R.id.podcast_title) TextView podcastTitle;
    @BindView(R.id.episode_title) TextView episodeTitle;
    @BindView(R.id.episode_description) TextView episodeDescription;
    @BindView(R.id.podcast_image_background) ImageView podcastImageBackground;
    @BindView(R.id.podcast_image) ImageView podcastImage;
    @BindView(R.id.play_pause_button) ImageView playPauseButton;
    @BindView(R.id.rewind_button) ImageView rewindButton;
    @BindView(R.id.forward_button) ImageView forwardButton;
    @BindView(R.id.seekbar) SeekBar seekBar;

    private Handler handler = new Handler();

    // data
    private Podcast podcast;
    private Episode episode;

    /**
     * help to toggle between play and pause.
     */
    private boolean playing = false;

    MediaPlayerService mediaPlayerService;

    public static EpisodeFragment newInstance(Long episodeId) {
        EpisodeFragment fragment = new EpisodeFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_EPISODE_ID, episodeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        Long episodeId = args.getLong(ARG_EPISODE_ID);

        episode = Episode.findById(Episode.class, episodeId);
        podcast = episode.getPodcast();

        getActivity().setTitle(episode.getTitle());

        mediaPlayerService = App.getInstance().mediaPlayerService;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_episode, container, false);
        ButterKnife.bind(this, view);

        podcastTitle.setText(podcast.getTitle());
        episodeTitle.setText(episode.getTitle());
        episodeDescription.setText(episode.getDescription());

        Glide.with(getContext())
                .load(Uri.parse(podcast.getImageUrl()))
                .transform(new BlurTransformation(getContext(), BLUR_RADIUS, BLUR_FILTER))
                .into(podcastImageBackground);

        Glide.with(getContext())
                .load(Uri.parse(podcast.getImageUrl()))
                .into(podcastImage);

        seekBar = (SeekBar) view.findViewById(R.id.seekbar);
        // seekBar for user sliding
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mediaPlayerService.seekTo(episode, progress * 1000);
                }
            }
        });

        // set seekbar when finish loading
        mediaPlayerService.audioPrepared.subscribe(duration -> {
            if(mediaPlayerService.checkOwned(episode)) {
                seekBar.setMax(duration / 1000);
            }
        });

        // set seekbar when finish playing
        mediaPlayerService.audioCompleted.subscribe(r -> {
            if(mediaPlayerService.checkOwned(episode)) {
                playing = false;
                playPauseButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                seekBar.setProgress(0);
            }
        });

        // set initial state for seekbar and play/pause buttons
        if(mediaPlayerService.checkOwned(episode)) {
            seekBar.setMax(mediaPlayerService.getDuration() / 1000);
            if(mediaPlayerService.isPlaying()) playing = true;
            if(playing) {
                playPauseButton.setImageResource(R.drawable.ic_pause_white_24dp);
            } else {
                playPauseButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
            }
        }

        // keep seekBar sync with player
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                int mCurrentPosition = mediaPlayerService.getCurrentPosition(episode) / 1000;
                if(mCurrentPosition >= 0) {
                    seekBar.setProgress(mCurrentPosition);
                }

                handler.postDelayed(this, SEEKBAR_UPDATE_INTERVEL);
            }
        });

        return view;
    }

    @OnClick(R.id.play_pause_button)
    public void playOrPause() {
        if(!playing) {
            boolean result = mediaPlayerService.play(episode, getContext());
            if(result) playPauseButton.setImageResource(R.drawable.ic_pause_white_24dp);
            playing = true;
        } else {
            boolean result = mediaPlayerService.pause(episode);
            if(result) playPauseButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
            playing = false;
        }
    }

    @OnClick(R.id.rewind_button)
    public void rewind() {
        mediaPlayerService.offset(episode, -5);
    }

    @OnClick(R.id.forward_button)
    public void forward() {
        mediaPlayerService.offset(episode, 5);
    }
}
