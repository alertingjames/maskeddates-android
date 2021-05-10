package com.date.maskeddates.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.date.maskeddates.R;
import com.date.maskeddates.commons.Commons;
import com.marozzi.roundbutton.RoundButton;
import com.skyfishjy.library.RippleBackground;

public class PremiumGuideActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private static final int PERCENTAGE_TO_SHOW_IMAGE = 20;
    private View mFab;
    private int mMaxScrollSize;
    private boolean mIsImageHidden;
    Toolbar toolbar;
    RippleBackground rippleBackground;
    RoundButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_premium_guide);

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Comfortaa_Bold.ttf");
        ((TextView)findViewById(R.id.title)).setTypeface(font);
        ((TextView)findViewById(R.id.title1)).setTypeface(font);
        ((TextView)findViewById(R.id.title2)).setTypeface(font);
        ((TextView)findViewById(R.id.title3)).setTypeface(font);
        ((TextView)findViewById(R.id.title4)).setTypeface(font);
        ((TextView)findViewById(R.id.title5)).setTypeface(font);
        ((TextView)findViewById(R.id.content1)).setTypeface(font);
        ((TextView)findViewById(R.id.content2)).setTypeface(font);
        ((TextView)findViewById(R.id.content3)).setTypeface(font);
        ((TextView)findViewById(R.id.content4)).setTypeface(font);
        ((TextView)findViewById(R.id.content5)).setTypeface(font);

        String planString =
                String.valueOf(Commons.plan.getMonths1()) + " months plan - Be on " + String.valueOf(Commons.plan.getDates1()) + " dates at the same time\n" +
                        String.valueOf(Commons.plan.getMonths2()) + " months plan - Be on " + String.valueOf(Commons.plan.getDates2()) + " dates at the same time\n" +
                        String.valueOf(Commons.plan.getMonths3()) + " months plan - Be on " + String.valueOf(Commons.plan.getDates3()) + " dates at the same time";
        ((TextView)findViewById(R.id.content1)).setText(planString);

        mFab = findViewById(R.id.flexible_example_fab);

        toolbar = (Toolbar) findViewById(R.id.flexible_example_toolbar);
        toolbar.setTitle("Masked Dates");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });

        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.flexible_example_appbar);
        appbar.addOnOffsetChangedListener(this);

        rippleBackground=(RippleBackground)findViewById(R.id.content);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rippleBackground.startRippleAnimation();
                rippleBackground.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rippleBackground.stopRippleAnimation();
                        rippleBackground.setVisibility(View.GONE);
                        Intent intent = new Intent(getApplicationContext(), MembershipPlansActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    }
                }, 1200);
            }
        });
        button = (RoundButton)findViewById(R.id.nextButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.startAnimation();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        button.revertAnimation();
                        Intent intent = new Intent(getApplicationContext(), MembershipPlansActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    }
                }, 1200);
            }
        });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int currentScrollPercentage = (Math.abs(verticalOffset)) * 100
                / mMaxScrollSize;

        if (currentScrollPercentage >= PERCENTAGE_TO_SHOW_IMAGE) {
            if (!mIsImageHidden) {
                mIsImageHidden = true;

                ViewCompat.animate(mFab).scaleY(0).scaleX(0).start();
            }
        }

        if (currentScrollPercentage >= PERCENTAGE_TO_SHOW_IMAGE + 70) {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
            toolbar.setAnimation(animation);
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }else if (currentScrollPercentage <= PERCENTAGE_TO_SHOW_IMAGE + 70) {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_off);
            toolbar.setAnimation(animation);
            toolbar.setBackground(null);

        }

        if (currentScrollPercentage < PERCENTAGE_TO_SHOW_IMAGE) {
            if (mIsImageHidden) {
                mIsImageHidden = false;
                ViewCompat.animate(mFab).scaleY(1).scaleX(1).start();
            }
        }
    }

    public static void start(Context c) {
        c.startActivity(new Intent(c, PremiumGuideActivity.class));
    }
}
