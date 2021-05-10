package com.date.maskeddates.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.date.maskeddates.R;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.BlurTransformation;

public class ViewProfilePictureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_view_profile_picture);

        String photoUrl = getIntent().getStringExtra("photo_url");
        int score = getIntent().getIntExtra("score", 0);

        if(score == 4) Picasso.with(getApplicationContext())
                .load(photoUrl)
                .error(R.drawable.q9)
                .placeholder(R.drawable.q9)
                .into(((ImageView)findViewById(R.id.picture)));
        else Picasso.with(getApplicationContext())
                .load(photoUrl)
                .transform(new BlurTransformation(getApplicationContext(), 25, 1))
                .error(R.drawable.q9)
                .placeholder(R.drawable.q9)
                .into(((ImageView)findViewById(R.id.picture)));
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0,0);
    }
}
