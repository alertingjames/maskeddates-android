package com.date.maskeddates.video;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.date.maskeddates.R;
import com.date.maskeddates.commons.Commons;

public class VideoPlayActivity extends AppCompatActivity {

    private static final String TAG = "VideoPlayActivity";
    RelativeLayout ui_lytvideo;
    ImageView downloader;
    VideoView videoView;
    TextView videoUrl;
    private ProgressDialog mProgresDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        downloader=(ImageView)findViewById(R.id.download);
        downloader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Commons.videouri);
                startActivity(i);
            }
        });
        ui_lytvideo=(RelativeLayout) findViewById(R.id.lytvideo);
        videoView=(VideoView) findViewById(R.id.videoView);
        videoView.setBackground(null);
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(Commons.videouri);

        videoView.requestFocus();
        videoView.start();

        videoUrl=(TextView)findViewById(R.id.videoUrl);
        videoUrl.setText(Commons.videouri.toString());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Commons.videouri=null;
    }
}

