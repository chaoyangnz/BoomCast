package me.yangchao.boomcast.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.subjects.PublishSubject;
import me.yangchao.boomcast.R;
import me.yangchao.boomcast.model.Podcast;
import me.yangchao.boomcast.net.PodcastFeedRequest;

/**
 * A simple {@link Fragment} subclass.
 */
public class PodcastNewFragment extends Fragment {

    // UI widgets
    @BindView(R.id.podcast_feed_url) EditText podcastFeedUrl;

    // event subject
    public PublishSubject<Podcast> subscriptionSaved = PublishSubject.create();

    public PodcastNewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_podcast_new, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    // save subscription
    @OnClick(R.id.subscribe_button)
    public void subscribe() {
        String feedUrl = podcastFeedUrl.getText().toString();
        subscribe(feedUrl);
    }

    public void subscribe(String feedUrl) {
        // hide keyboard
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        // query
        List<Podcast> list = Podcast.find(Podcast.class, "feed_url = ?", feedUrl);
        if(list.isEmpty()) {
            PodcastFeedRequest.requstFeedSource(getContext(), feedUrl,
                    podcast -> {
                        subscriptionSaved.onNext(podcast);
                    }, error -> {});
        } else {
            Snackbar.make(getActivity().findViewById(R.id.podcast_new_fragment),
                    R.string.warn_podcast_subscribed, Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    @OnClick({R.id.podcast_ondemand, R.id.podcast_sixminutesgrammar, R.id.podcast_outlook, R.id.podcast_sixminutesenglish})
    public void subscribeRecommendations(ImageView view) {
        subscribe((String)view.getTag());
    }

}
