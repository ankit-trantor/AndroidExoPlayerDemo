package me.shuza.android_exoplayer_demo;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.PlaybackControlView;
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

    @BindView(R.id.fr_layout)
    FrameLayout frExoPlayerContainer;

    SimpleExoPlayer exoPlayer;
    ImageView mFullScreenIcon;

    MediaSource mediaSource;
    Dialog exoPlayerFullScreenDialog;

    private final String STATE_RESUME_WINDOW = "resumeWindow";
    private final String STATE_RESUME_POSITION = "resumePosition";
    private final String STATE_PLAYER_FULLSCREEN = "playerFullscreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initPlayer();
        iniExoPlayerFullScreenDialog();
        initFullScreenButton();
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

        mediaSource = new ExtractorMediaSource(CommonUtil.getVideoUri(), mediaDataSourceFactory,
                new DefaultExtractorsFactory(), null, null);
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);
    }

    private void iniExoPlayerFullScreenDialog() {
        exoPlayerFullScreenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            @Override
            public void onBackPressed() {
                if (exoPlayerFullScreenDialog.isShowing()) {
                    closeFullScreenDialog();
                }
                super.onBackPressed();
            }
        };
    }

    private void openFullScreen() {
        long resumePosition = Math.max(0, exoPlayer.getCurrentPosition());
        ((ViewGroup) exoPlayerView.getParent()).removeView(exoPlayerView);
        exoPlayerFullScreenDialog.addContentView(exoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_skrink));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        exoPlayerFullScreenDialog.show();
        exoPlayer.seekTo(resumePosition);
    }

    private void closeFullScreenDialog() {
        long resumePosition = Math.max(0, exoPlayer.getCurrentPosition());
        ((ViewGroup) exoPlayerView.getParent()).removeView(exoPlayerView);
        frExoPlayerContainer.addView(exoPlayerView);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        exoPlayerFullScreenDialog.dismiss();
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_expand));
        exoPlayer.seekTo(resumePosition);
    }

    private void initFullScreenButton() {
        PlaybackControlView controlView = exoPlayerView.findViewById(R.id.exo_controller);
        mFullScreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
        mFullScreenIcon.setOnClickListener((v) -> {
            if (!exoPlayerFullScreenDialog.isShowing())
                openFullScreen();
            else
                closeFullScreenDialog();
        });
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
