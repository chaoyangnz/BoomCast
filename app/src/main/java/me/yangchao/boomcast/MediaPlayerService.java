package me.yangchao.boomcast;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.Date;

import io.reactivex.subjects.PublishSubject;
import me.yangchao.boomcast.model.Episode;
import me.yangchao.boomcast.model.ListenLog;
import me.yangchao.boomcast.util.DateUtil;

import static me.yangchao.boomcast.App.LOG_TAG;

/**
 * Created by richard on 4/13/17.
 */

public class MediaPlayerService {
    private MediaPlayer mediaPlayer;

    public Episode episode;
    private boolean intialStage = true;
    public PublishSubject<Integer> audioPrepared = PublishSubject.create();
    public PublishSubject<Boolean> audioCompleted = PublishSubject.create();

    public long startTime = -1;

    public MediaPlayerService() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {

                audioCompleted.onNext(true);
                reset();
            }
        });
    }

    private void reset() {
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        intialStage = true;

        updateListenDuration();
    }

    public boolean checkOwned(Episode episode) {
        if(episode == null) return false;
        if(this.episode == null) return false;
        return this.episode.getId().equals(episode.getId());
    }

    public boolean play(Episode episode, Context context) {
        if(!checkOwned(episode)) {
            reset();
        }

        this.episode = episode;

        if (intialStage)
            new PlayerBufferTask(context).execute(episode.getEnclosureUrl());
        else {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                startTime = new Date().getTime();
            }
        }
        return true;
    }

    private void updateListenDuration() {
        if(startTime < 0) return;
        if(episode == null) return;;

        Date now = new Date();
        String date = DateUtil.formatDate(now);
        long duration = now.getTime() - startTime;
        Log.d(LOG_TAG, "Duration: " + duration/(1000*60) + " minutes (" + duration/1000 + " seconds)");

        ListenLog listenLog = ListenLog.findByDateAndEpisode(date, episode.getId());
        if(listenLog == null) {
            listenLog = new ListenLog();
            listenLog.setEpisodeId(episode.getId());
            listenLog.setDate(date);
            listenLog.setDuration(duration);
        } else {
            listenLog.setDuration(listenLog.getDuration() + duration);
        }
        listenLog.save();
        startTime = -1;
    }

    public boolean pause(Episode episode) {
        if(!checkOwned(episode)) return false;

        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();

        updateListenDuration();
        return true;
    }

    public void offset(Episode episode, Integer seconds) {
        if(!checkOwned(episode)) return;

        seekTo(episode, mediaPlayer.getCurrentPosition() + seconds*1000);
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public int getDuration() {
        return intialStage ? 0 : mediaPlayer.getDuration();
    }

    public int getCurrentPosition(Episode episode) {
        if(!checkOwned(episode)) return -1;

        return mediaPlayer.getCurrentPosition();
    }

    public void seekTo(Episode episode, Integer position) {
        if(!checkOwned(episode)) return;

        mediaPlayer.seekTo(position);
    }

    public void destroy() {
        reset();
        mediaPlayer.release();
        mediaPlayer = null;

        episode = null;
    }

    class PlayerBufferTask extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog progress;

        public PlayerBufferTask(Context context) {
            progress = new ProgressDialog(context);
            progress.setCanceledOnTouchOutside(false);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean prepared;
            try {
                mediaPlayer.setDataSource(params[0]);
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

            audioPrepared.onNext(mediaPlayer.getDuration());

            intialStage = false;
            mediaPlayer.start();
            startTime = new Date().getTime();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progress.setMessage(App.getInstance().getString(R.string.progress_buffering));
            this.progress.show();
        }
    }
}
