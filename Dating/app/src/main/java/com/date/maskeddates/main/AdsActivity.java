package com.date.maskeddates.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.date.maskeddates.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class AdsActivity extends AppCompatActivity {

    AdView adView;
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);

        MobileAds.initialize(this,
                "ca-app-pub-4372722091889992~4297477477");

        adView=(AdView)findViewById(R.id.adView);


        AdRequest adRequest1=new AdRequest.Builder().build();
        adView.loadAd(adRequest1);

        interstitialAd=new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-4372722091889992/9314479366");

        AdRequest adRequest=new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest1);
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

    }

    @Override
    protected void onDestroy() {
        if(adView!=null){
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if(adView!=null){
            adView.resume();
        }
        super.onResume();
    }

    public void showAds(View view){
        if(interstitialAd.isLoaded()){
            interstitialAd.show();
        }else{
            Toast.makeText(this, "Please wait for ad to load", Toast.LENGTH_SHORT).show();
        }
    }
}
