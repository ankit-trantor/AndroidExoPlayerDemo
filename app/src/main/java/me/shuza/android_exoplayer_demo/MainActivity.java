package me.shuza.android_exoplayer_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.exo_player_view)
    SimpleExoPlayerView exoPlayerView;

    SimpleExoPlayer exoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initPlayer();
    }

    void initPlayer() {
        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory(defaultBandwidthMeter);
        DefaultTrackSelector trackSelection = new DefaultTrackSelector(trackSelectionFactory);

        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelection);
        exoPlayerView.setPlayer(exoPlayer);
        exoPlayer.setPlayWhenReady(true);

        DataSource.Factory mediaDataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "Android-ExoPlayer-Demo"),
                defaultBandwidthMeter);

        MediaSource mediaSource = new ExtractorMediaSource(CommonUtil.getVideoUri(), mediaDataSourceFactory,
                new DefaultExtractorsFactory(), null, null);
        exoPlayer.prepare(mediaSource);
    }

    void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }
}
