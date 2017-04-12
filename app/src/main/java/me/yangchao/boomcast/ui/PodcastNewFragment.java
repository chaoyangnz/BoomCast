package me.yangchao.boomcast.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import io.reactivex.subjects.PublishSubject;
import me.yangchao.boomcast.R;
import me.yangchao.boomcast.model.Podcast;
import me.yangchao.boomcast.net.PodcastFeedRequest;

/**
 * A simple {@link Fragment} subclass.
 */
public class PodcastNewFragment extends Fragment implements View.OnClickListener {

    // UI widget
    private EditText podcastFeedUrl;
    private Button subscribeButton;

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

        podcastFeedUrl = (EditText) view.findViewById(R.id.podcast_feed_url);
        subscribeButton = (Button) view.findViewById(R.id.subscribe_button);

        subscribeButton.setOnClickListener(this);

        return view;
    }

    // save subscription
    @Override
    public void onClick(View view) {
        String feedUrl = podcastFeedUrl.getText().toString();

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
}
