package me.yangchao.boomcast.ui;


import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.bumptech.glide.signature.StringSignature;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.yangchao.boomcast.R;
import me.yangchao.boomcast.model.Episode;
import me.yangchao.boomcast.util.BlurTransformation;

/**
 * A simple {@link Fragment} subclass.
 */
public class EpisodeFragment extends Fragment {


    public EpisodeFragment() {
        // Required empty public constructor
    }

    private static final String ARG_EPISODE_ID = "episodeId";
    private static final int BLUR_RADIS = 125;
    private static final int BLUR_FILTER = 0x8C2E2E2E;

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
    private Episode episode;

    /**
     * help to toggle between play and pause.
     */
    private boolean playPause;
    private MediaPlayer mediaPlayer;
    /**
     * remain false till media is not completed, inside OnCompletionListener make it true.
     */
    private boolean intialStage = true;

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

        getActivity().setTitle(episode.getTitle());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_episode, container, false);
        ButterKnife.bind(this, view);

        podcastTitle.setText(episode.getPodcast().getTitle());
        episodeTitle.setText(episode.getTitle());
        episodeDescription.setText(episode.getDescription());

        Glide.with(getContext())
                .load(Uri.parse(episode.getPodcast().getImageUrl()))
                .transform(new BlurTransformation(getContext(), BLUR_RADIS, BLUR_FILTER))
                .into(podcastImageBackground);

        Glide.with(getContext())
                .load(Uri.parse(episode.getPodcast().getImageUrl()))
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .into(podcastImage);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        seekBar = (SeekBar) view.findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress * 1000);
                }
            }
        });
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null){
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                }
                handler.postDelayed(this, 1000);
            }
        });

        return view;
    }

    @OnClick(R.id.play_pause_button)
    public void playOrPause() {
        if (!playPause) {
            playPauseButton.setImageResource(R.drawable.ic_pause_white_24dp);
            if (intialStage)
                new PlayerBufferTask().execute(episode.getEnclosureUrl());
            else {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            }
            playPause = true;
        } else {
            playPauseButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();
            playPause = false;
        }
    }

    @OnClick(R.id.rewind_button)
    public void rewind() {
        if(mediaPlayer != null && !intialStage){
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5 * 1000);
        }
    }

    @OnClick(R.id.forward_button)
    public void forward() {
        if(mediaPlayer != null && !intialStage){
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5 * 1000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * preparing mediaplayer will take sometime to buffer the content
     * so prepare it inside the background thread and starting it on UI thread.
     */
    class PlayerBufferTask extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog progress;

        public PlayerBufferTask() {
            progress = new ProgressDialog(getContext());
            progress.setCanceledOnTouchOutside(false);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean prepared;
            try {

                mediaPlayer.setDataSource(params[0]);

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        intialStage = true;
                        playPause=false;
                        playPauseButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });
                mediaPlayer.prepare();
                prepared = true;
            } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
                prepared = false;
                e.printStackTrace();
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (progress.isShowing()) {
                progress.cancel();
            }
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            mediaPlayer.start();

            intialStage = false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progress.setMessage(getString(R.string.progress_buffering));
            this.progress.show();
        }
    }
}
