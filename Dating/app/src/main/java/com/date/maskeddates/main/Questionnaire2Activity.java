package com.date.maskeddates.main;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.android.volley.toolbox.ImageLoader;
import com.date.maskeddates.MyApplication;
import com.date.maskeddates.R;
import com.date.maskeddates.classes.ViewPagerCustomDuration;
import com.date.maskeddates.commons.Commons;
import com.date.maskeddates.fragments.FragmentA;
import com.date.maskeddates.fragments.FragmentB;
import com.date.maskeddates.fragments.FragmentC;
import com.date.maskeddates.fragments.FragmentD;
import com.date.maskeddates.fragments.FragmentE;
import com.date.maskeddates.fragments.FragmentF;
import com.date.maskeddates.fragments.FragmentG;
import com.date.maskeddates.fragments.FragmentI;
import com.date.maskeddates.fragments.FragmentK;
import com.date.maskeddates.fragments.FragmentL;
import com.date.maskeddates.fragments.FragmentM;
import com.date.maskeddates.fragments.FragmentN;
import com.date.maskeddates.fragments.FragmentO;
import com.date.maskeddates.fragments.FragmentQ;
import com.date.maskeddates.fragments.FragmentR;
import com.date.maskeddates.fragments.FragmentS;
import com.date.maskeddates.fragments.FragmentT;
import com.date.maskeddates.models.Questionnaire;

public class Questionnaire2Activity extends AppCompatActivity {

    ImageLoader _imageLoader;
    ViewPagerCustomDuration pager;
    Toolbar toolbar;
    Typeface font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_questionnaire2);

        toolbar = (Toolbar)findViewById(R.id.toolbar_top);

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Comfortaa_Bold.ttf");
        title.setTypeface(font);

        Typeface font2 = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Comfortaa_Regular.ttf");
        ((TextView)findViewById(R.id.loadingCaption)).setTypeface(font2);

        String[] questions = {"Describe Yourself",
                "When you have the choice of taking the stairs or the elevator, which one do you choose?",
                "Favorite movie genre",
                "What word would your friends use to describe you?",
                "What's the one thing you can't live without?",
                "Your Drink of Choice Is",
                "What's Your Biggest Fear?",
     //           "Do You Like Attention?",     // H
                "Do You Like Change?",
    //            "Mac or PC?",                 // J
                "A perfect date for you is",
                "How do you let the world know what you're feeling?",
                "You find your inspiration by",
                "What is your ideal weekend plan?",
                "What are you looking for?",
    //            "What age group do you prefer being with?",        // P
                "How often do you read?",
                "You are an",
                "Are you a morning person or a night person?",
                "Are you a cat or a dog person?"
        };

        _imageLoader = MyApplication.getInstance().getImageLoader();
        pager = (ViewPagerCustomDuration) findViewById(R.id.viewpager);
        pager.setScrollDuration(800);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        pager.setPageTransformer(true, new CubeOutTransformer());

        if(!Commons.edit_profile_flag){
            for (int i=0; i<questions.length; i++){
                Questionnaire questionnaire = new Questionnaire();
                questionnaire.set_text(questions[i]);
                questionnaire.set_isactive("inactive");
                Commons.questionnaires2.add(questionnaire);
            }
            pager.setCurrentItem(0);

        }else {
            String q = getIntent().getStringExtra("question"); Log.d("Q===>", q);
            for(int j=0;j<questions.length; j++){
                if(q.equals(questions[j])) {
                    Log.d("Num===>", String.valueOf(j));
                    pager.setCurrentItem(j);
                    break;
                }
            }
        }
        Commons.viewPager = pager;
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {
                case 0: return FragmentA.newInstance("FirstFragment, Instance 1");
                case 1: return FragmentB.newInstance("SecondFragment, Instance 1");
                case 2: return FragmentC.newInstance("ThirdFragment, Instance 1");
                case 3: return FragmentD.newInstance("ForthFragment, Instance 1");
                case 4: return FragmentE.newInstance("FifthFragment, Instance 1");
                case 5: return FragmentF.newInstance("SixthFragment, Instance 1");
                case 6: return FragmentG.newInstance("SeventhFragment, Instance 1");
      //          case 7: return FragmentH.newInstance("EighthFragment, Instance 1");
                case 7: return FragmentI.newInstance("NinthFragment, Instance 1");
      //          case 9: return FragmentJ.newInstance("TenthFragment, Instance 1");
                case 8: return FragmentK.newInstance("EleventhFragment, Instance 1");
                case 9: return FragmentL.newInstance("TwelvethFragment, Instance 1");
                case 10: return FragmentM.newInstance("ThirteenthFragment, Instance 1");
                case 11: return FragmentN.newInstance("FourteenthFragment, Instance 1");
                case 12: return FragmentO.newInstance("FifteenthFragment, Instance 1");
       //         case 15: return FragmentP.newInstance("SixteenthFragment, Instance 1");
                case 13: return FragmentQ.newInstance("SeventeenthFragment, Instance 1");
                case 14: return FragmentR.newInstance("EighteenthFragment, Instance 1");
                case 15: return FragmentS.newInstance("NineteenthFragment, Instance 1");
                case 16: return FragmentT.newInstance("TwentiethFragment, Instance 1");

                default: return FragmentA.newInstance("FirstFragment, Default");
            }
        }

        @Override
        public int getCount() {
            return 17;
        }

    }
}