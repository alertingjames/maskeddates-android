package com.date.maskeddates.main;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;

import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.date.maskeddates.MyApplication;
import com.date.maskeddates.R;
import com.date.maskeddates.adapters.MyWallPhotosAdapter;
import com.date.maskeddates.commons.Commons;
import com.date.maskeddates.commons.Constants;
import com.date.maskeddates.commons.ReqConst;
import com.date.maskeddates.models.Notification;
import com.date.maskeddates.models.Online;
import com.date.maskeddates.models.Picture;
import com.date.maskeddates.models.Post;
import com.date.maskeddates.models.Questionnaire;
import com.date.maskeddates.preference.PrefConst;
import com.date.maskeddates.preference.Preference;
import com.date.maskeddates.utils.MultiPartRequest;
import com.eyalbira.loadingdots.LoadingDots;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.github.mmin18.widget.RealtimeBlurView;
import com.google.android.flexbox.FlexboxLayout;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import me.leolin.shortcutbadger.ShortcutBadger;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MyWallActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener  {

    private static final String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.INSTALL_PACKAGES,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.VIBRATE,
            android.Manifest.permission.SET_TIME,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAPTURE_VIDEO_OUTPUT,
            android.Manifest.permission.WAKE_LOCK,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.LOCATION_HARDWARE};

    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private static boolean mIsAvatarShown = true;

    private ImageView mProfileImage, no_result;
    private CircleImageView profileImage;
    private int mMaxScrollSize;
    TextView name, name2, gender, age, location, progressText, aboutme, posts, photos;
    RealtimeBlurView blurView;
    ProgressBar progressBar;
    LoadingDots progress_bar;
    int percent = 0;
    LinearLayout nameLayout, dynamical, staticq, postLayout, photoLayout;
    FlexboxLayout iamlayout, iamherelayout, ilikelayout, ilayout;
    TextView match, matchlabel, lastlogin, actives, membership, premium, maxdatesbox;
    NestedScrollView contain;
    View aboutmeView, postView, photoView;
    Typeface font, font2;
    FrameLayout noticon;
    TextView count;
    LinearLayout alert_post;
    ListView list;
    ArrayList<Questionnaire> updatedQuestions = new ArrayList<>();

    FrameLayout userButton, chatButton, usersButton;

    ArrayList<Picture> pictures = new ArrayList<>();
    MyWallPhotosAdapter adapter = new MyWallPhotosAdapter(this);
    int pictureCount = 0;
    Map<Integer, Object> notiMap = new HashMap<>();
    Map<String, Object> onlineMap = new HashMap<>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wall);

        checkAllPermission();

        Firebase.setAndroidContext(this);

        notiMap.clear();
        onlineMap.clear();

        alert_post = (LinearLayout)findViewById(R.id.alert_post);

        noticon = (FrameLayout) findViewById(R.id.badge);
        count = (TextView)findViewById(R.id.count);

        progressBar=(ProgressBar) findViewById(R.id.progressBar);
        progress_bar=(LoadingDots) findViewById(R.id.progress_bar);
        Commons.edit_profile_flag = false;

        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Comfortaa_Bold.ttf");
        font2 = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Comfortaa_Regular.ttf");

        contain = (NestedScrollView) findViewById(R.id.contain);
        aboutme = (TextView) findViewById(R.id.aboutme);
        posts = (TextView) findViewById(R.id.posts);
        photos = (TextView)findViewById(R.id.photos);

        lastlogin = (TextView) findViewById(R.id.lastlogin);
        actives = (TextView) findViewById(R.id.actives);
        membership = (TextView) findViewById(R.id.membershipBox);
        premium = (TextView) findViewById(R.id.premiumBox);
        maxdatesbox = (TextView) findViewById(R.id.maxdatesBox);
        maxdatesbox.setTypeface(font2);
        String premiumCaption = "Bronze member";
        if(Commons.thisUser.get_premium().length() > 0){
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            String myDate = dateFormat.format(new Date(Commons.premium_expired));
            int[] max_dates_list = new int[]{0, Commons.plan.getDates1(), Commons.plan.getDates2(), Commons.plan.getDates3()};
            String num = Commons.thisUser.get_premium().substring(Commons.thisUser.get_premium().indexOf("_") + 1, Commons.thisUser.get_premium().length());
            if(num.equals("1"))premiumCaption = "Silver member";
            else if(num.equals("2"))premiumCaption = "Gold member";
            else if(num.equals("3"))premiumCaption = "Platinum member";
            premium.setText(premiumCaption + " till " + myDate);
            maxdatesbox.setText("Max dates: " + String.valueOf(max_dates_list[Integer.parseInt(num)]));
            premium.setVisibility(View.VISIBLE);
            membership.setVisibility(View.GONE);
        }else {
            membership.setText(premiumCaption);
            maxdatesbox.setText("Max dates: " + String.valueOf(Commons.max_dates));
            membership.setVisibility(View.VISIBLE);
            premium.setVisibility(View.GONE);
        }

        membership.setTypeface(font2);
        premium.setTypeface(font2);

        AppBarLayout appbarLayout = (AppBarLayout) findViewById(R.id.materialup_appbar);
        mProfileImage = (ImageView) findViewById(R.id.materialup_profile_image);

        profileImage = (CircleImageView) findViewById(R.id.materialup_profile_image2);

        matchlabel = (TextView) findViewById(R.id.matchlabel);
        match = (TextView) findViewById(R.id.match);
        nameLayout = (LinearLayout) findViewById(R.id.nameLayout);

        name = (TextView) findViewById(R.id.name);
        name.setTypeface(font);
        location = (TextView) findViewById(R.id.location);
        location.setTypeface(font2);
        name2 = (TextView) findViewById(R.id.name2);
        name2.setTypeface(font);
        gender = (TextView) findViewById(R.id.gender);
        age = (TextView) findViewById(R.id.age);
        gender.setTypeface(font);
        age.setTypeface(font);

        actives.setText(String.valueOf(Commons.thisUser.get_actives()));

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        String myDate = dateFormat.format(new Date(Long.parseLong(Commons.thisUser.get_lastlogin())));
        lastlogin.setText(myDate);

        name.setText(rename(Commons.thisUser.get_name().trim()));
        name2.setText(rename(Commons.thisUser.get_name().trim()));

//        name.setTypeface(font);
//        name2.setTypeface(font);

        userButton = (FrameLayout) findViewById(R.id.userbutton);
        chatButton = (FrameLayout) findViewById(R.id.chatbutton);
        usersButton = (FrameLayout) findViewById(R.id.usersbutton);
        usersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UsersActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
            }
        });

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NotificationsActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
            }
        });

        gender.setText(Commons.thisUser.get_gender());
        age.setText(Commons.thisUser.get_age());
        location.setText(Commons.thisUser.get_address());

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressText = (TextView) findViewById(R.id.txtProgress);

        percent = Integer.parseInt(Commons.thisUser.get_photo_unlock());

        progressBar.setProgress(percent);
        progressText.setText(String.valueOf(percent) + " %");
        match.setText(String.valueOf(percent) + " %");

        ((RelativeLayout)findViewById(R.id.matchframe)).setVisibility(View.GONE);
        matchlabel.setVisibility(View.GONE);
        ((LinearLayout)findViewById(R.id.matchbar)).setVisibility(View.GONE);

        Picasso.with(getApplicationContext())
                .load(Commons.thisUser.get_photoUrl())
          //      .transform(new BlurTransformation(getApplicationContext(), 25, 1))
                .error(R.drawable.q9)
                .placeholder(R.drawable.q9)
                .into(profileImage);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ViewProfilePictureActivity.class);
                intent.putExtra("photo_url", Commons.thisUser.get_photoUrl());
                intent.putExtra("score", 4);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

        Picasso.with(getApplicationContext())
                .load(Commons.thisUser.get_photoUrl())
                .error(R.drawable.q9)
                .placeholder(R.drawable.q9)
                .into((ImageView)findViewById(R.id.backgroundImage));

        Toolbar toolbar = (Toolbar) findViewById(R.id.materialup_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });

        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        aboutmeView = inflater.inflate(R.layout.fragment_myaboutme, null);
        dynamical = (LinearLayout)aboutmeView.findViewById(R.id.dynamical);
        staticq = (LinearLayout)aboutmeView.findViewById(R.id.staticq);

        iamlayout = (FlexboxLayout)aboutmeView.findViewById(R.id.iamlayout);
        ilikelayout = (FlexboxLayout)aboutmeView.findViewById(R.id.ilikelayout);
        iamherelayout = (FlexboxLayout)aboutmeView.findViewById(R.id.iamherelayout);
        ilayout = (FlexboxLayout)aboutmeView.findViewById(R.id.ilayout);

        photoLayout = (LinearLayout)findViewById(R.id.photoLayout);

 //       parseQAJson(Commons.thisUser.get_answers(), Commons.thisUser.get_answers2());
        getDynamicals(Commons.thisUser.get_answers());

        aboutme.setTextColor(Color.BLACK);
        aboutme.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        posts.setTextColor(Color.GRAY);
        photos.setTextColor(Color.GRAY);

        aboutme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearTabColors();
                aboutme.setTextColor(Color.BLACK);
                ((View)findViewById(R.id.indicator_aboutme)).setVisibility(View.VISIBLE);
                parseQAJson(Commons.thisUser.get_answers(), Commons.thisUser.get_answers2());
                photoLayout.setVisibility(View.GONE);
            }
        });

        inflater = LayoutInflater.from(getApplicationContext());
        postView = inflater.inflate(R.layout.fragment_posts, null);
        postLayout = (LinearLayout)postView.findViewById(R.id.posts);
        no_result = (ImageView)postView.findViewById(R.id.no_result);
        Button postButton = (Button) postView.findViewById(R.id.newpost);
        postButton.setTypeface(font);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Commons.container = contain;
                Commons.alert_post = alert_post;
                Commons.deletepostbackground = (FrameLayout)findViewById(R.id.postdeletebackground);
                Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

        list = (ListView) findViewById(R.id.photoList);
        list.setNestedScrollingEnabled(true);

        photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearTabColors();
                photos.setTextColor(Color.BLACK);
                ((View)findViewById(R.id.indicator_photos)).setVisibility(View.VISIBLE);
                contain.removeAllViews();
                getPictures(Commons.thisUser.get_idx());
            }
        });

        posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearTabColors();
                posts.setTextColor(Color.BLACK);
                ((View)findViewById(R.id.indicator_posts)).setVisibility(View.VISIBLE);
                getPosts();
                photoLayout.setVisibility(View.GONE);
            }
        });

        aboutme.setTypeface(font);
        posts.setTypeface(font);
        photos.setTypeface(font);

        appbarLayout.addOnOffsetChangedListener(this);
        mMaxScrollSize = appbarLayout.getTotalScrollRange();

        getActives(Commons.thisUser.get_idx());
        getSettings(Commons.thisUser.get_email());

        new Thread(new Runnable() {
            @Override
            public void run() {
                getNotification(String.valueOf(Commons.thisUser.get_idx()));
                getOnline();
                getDynamicalUpdateNotification();
                getMembershipUpdateNotification();
                getAdminNotification();
            }
        }).start();

        if(Commons.premium_expired_flag){
            long expired = 0; int max_dates = Commons.plan.getDates();
            int[] max_dates_list = new int[]{0, Commons.plan.getDates1(), Commons.plan.getDates2(), Commons.plan.getDates3()};
            String premium = Commons.thisUser.get_premium();
            if(premium.contains("_")){
                expired = Long.parseLong(premium.substring(0, premium.indexOf("_")));
                max_dates = max_dates_list[Integer.parseInt(premium.substring(premium.indexOf("_") + 1, premium.length()))];
            }
            ((FrameLayout)findViewById(R.id.layout)).setVisibility(View.VISIBLE);
            LinearLayout alert = (LinearLayout)findViewById(R.id.alert);
            alert.setVisibility(View.VISIBLE);
            TextView warningText = alert.findViewById(R.id.warning_text);
//            warningText.setText("Your membership has been expired.\nExpired date: " + dateFormat.format(new Date(expired)) + "\n" + "Max matches: " + String.valueOf(max_dates));
            warningText.setText("Membership expired!!!");
            ((TextView)alert.findViewById(R.id.ok_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.setVisibility(View.GONE);
                    ((FrameLayout)findViewById(R.id.layout)).setVisibility(View.GONE);
                    Intent intent = new Intent(getApplicationContext(), PremiumGuideActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
            });
            ((ImageView)alert.findViewById(R.id.close)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.setVisibility(View.GONE);
                    ((FrameLayout)findViewById(R.id.layout)).setVisibility(View.GONE);
                    resetPremium(Commons.thisUser.get_idx());
                }
            });
        }

//        if(Commons.thisUser.get_phone().equals("updated")){

//        }
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
                Log.d("AAA===>", String.valueOf(e));
                System.exit(1);
            }
        });

    }

    public void closeAlert(View view){
        ((LinearLayout)findViewById(R.id.adminAlertbox)).setVisibility(View.GONE);
        ((View)findViewById(R.id.background)).setVisibility(View.GONE);
    }

    public void editProfile(View view){
        Commons.profileViews = new TextView[]{name, name2, age, gender, location};
        Commons.profileImage = profileImage;
        Commons.backgroundImage = ((ImageView)findViewById(R.id.backgroundImage));
        Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    public void logout(View view){
        Preference.getInstance().put(getApplicationContext(), PrefConst.PREFKEY_USEREMAIL, "");
//        Preference.getInstance().put(getApplicationContext(), PrefConst.SETTING_MAX_AGE, "");
//        Preference.getInstance().put(getApplicationContext(), PrefConst.SETTING_MIN_AGE, "");
//        Preference.getInstance().put(getApplicationContext(), PrefConst.SETTING_GENDER, "");
//        Preference.getInstance().put(getApplicationContext(), PrefConst.SETTING_PROFILE, "");
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    private void clearTabColors(){
        aboutme.setTextColor(Color.GRAY);
        photos.setTextColor(Color.GRAY);
        posts.setTextColor(Color.GRAY);
        ((View)findViewById(R.id.indicator_aboutme)).setVisibility(View.GONE);
        ((View)findViewById(R.id.indicator_posts)).setVisibility(View.GONE);
        ((View)findViewById(R.id.indicator_photos)).setVisibility(View.GONE);
    }

    public void parseQAJson(String json, String json2){

        contain.removeAllViews();
        dynamical.removeAllViews();
        staticq.removeAllViews();
//        Commons.questionnaires.clear();
        Commons.questionnaires2.clear();
        ilayout.removeAllViews();
        ilikelayout.removeAllViews();
        iamherelayout.removeAllViews();
        iamlayout.removeAllViews();

//        try {
//            JSONObject jsonObject = new JSONObject(json);
//
//            JSONArray jsonArray = jsonObject.getJSONArray("questionnaires");
//
//            for (int i = 0; i < jsonArray.length(); i++) {
//
//                JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
//
//                String question=jsonObject1.getString("question");
//                String answer=jsonObject1.getString("answer");
//
//                Questionnaire questionnaire = new Questionnaire();
//                questionnaire.set_text(question);
//                questionnaire.set_isactive("active");
//                questionnaire.set_answer(answer);
//                Commons.questionnaires.add(questionnaire);
//
//                View qa_dynamical = getLayoutInflater().inflate(R.layout.qa_layout, dynamical, false);
//                final TextView questionHolder = (TextView) qa_dynamical.findViewById(R.id.question);
//                final TextView answerHolder = (TextView) qa_dynamical.findViewById(R.id.answer);
//                questionHolder.setText(question);
//                final ImageButton editButton = (ImageButton) qa_dynamical.findViewById(R.id.editButton);
//                answerHolder.setText(StringEscapeUtils.unescapeJava(answer));
//                answerHolder.setTypeface(font2);
//                editButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Commons.textView = answerHolder;
//                        Intent intent = new Intent(getApplicationContext(), EditDynamicalActivity.class);
//                        intent.putExtra("question", question);
//                        intent.putExtra("answer", answerHolder.getText().toString());
//                        startActivity(intent);
//                        overridePendingTransition(0,0);
//                    }
//                });
//                dynamical.addView(qa_dynamical);
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        View onFocusView = null;

        for(int k=0; k<Commons.questionnaires.size(); k++){
            String question = Commons.questionnaires.get(k).get_text();
            String answer = Commons.questionnaires.get(k).get_answer();
            View qa_dynamical = getLayoutInflater().inflate(R.layout.qa_layout, dynamical, false);
            final TextView questionHolder = (TextView) qa_dynamical.findViewById(R.id.question);
            final TextView answerHolder = (TextView) qa_dynamical.findViewById(R.id.answer);
            questionHolder.setText(question);
            if(answer.length() == 0){
                qa_dynamical.findViewById(R.id.qasublayout).setBackgroundResource(R.drawable.red_stroke_round);
                questionHolder.setFocusable(true);
                if(onFocusView == null)
                    onFocusView = qa_dynamical;
            }
            final ImageButton editButton = (ImageButton) qa_dynamical.findViewById(R.id.editButton);
            answerHolder.setText(StringEscapeUtils.unescapeJava(answer));
            answerHolder.setTypeface(font2);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Commons.textView = answerHolder;
                    Commons.qa_sublayout = qa_dynamical.findViewById(R.id.qasublayout);
                    Intent intent = new Intent(getApplicationContext(), EditDynamicalActivity.class);
                    intent.putExtra("question", question);
                    intent.putExtra("answer", answerHolder.getText().toString());
                    startActivity(intent);
                    overridePendingTransition(0,0);
                }
            });
            dynamical.addView(qa_dynamical);
        }

        try {
            JSONObject jsonObject = new JSONObject(json2);

            JSONArray jsonArray = jsonObject.getJSONArray("questionnaires");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);

                String question=jsonObject1.getString("question");
                String answer=jsonObject1.getString("answer");

                Questionnaire questionnaire = new Questionnaire();
                questionnaire.set_text(question);
                questionnaire.set_isactive("active");
                questionnaire.set_answer(answer);
                Commons.questionnaires2.add(questionnaire);

                if(answer.startsWith("I am here for")){
                    View qa_iamhere = getLayoutInflater().inflate(R.layout.quickpeek_layout, iamherelayout, false);
                    final TextView questionHolder = (TextView) qa_iamhere.findViewById(R.id.question);
                    final TextView answerHolder = (TextView) qa_iamhere.findViewById(R.id.answer);
                    questionHolder.setText(question);
                    final ImageButton editButton = (ImageButton) qa_iamhere.findViewById(R.id.editButton);
                    editButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Commons.edit_profile_flag = true;
                            Intent intent = new Intent(getApplicationContext(), Questionnaire2Activity.class);
                            intent.putExtra("question", question);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                        }
                    });
                    answerHolder.setText(answer.replace("I am here for ", ""));
                    answerHolder.setTypeface(font2);
                    iamherelayout.addView(qa_iamhere);
                }else if(answer.startsWith("I like")){
                    View qa_ilike = getLayoutInflater().inflate(R.layout.quickpeek_layout, ilikelayout, false);
                    final TextView questionHolder = (TextView) qa_ilike.findViewById(R.id.question);
                    final TextView answerHolder = (TextView) qa_ilike.findViewById(R.id.answer);
                    questionHolder.setText(question);
                    final ImageButton editButton = (ImageButton) qa_ilike.findViewById(R.id.editButton);
                    editButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Commons.edit_profile_flag = true;
                            Intent intent = new Intent(getApplicationContext(), Questionnaire2Activity.class);
                            intent.putExtra("question", question);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                        }
                    });
                    answerHolder.setText(answer.replace("I like ", ""));
                    answerHolder.setTypeface(font2);
                    ilikelayout.addView(qa_ilike);
                }else if(answer.startsWith("I am")){
                    View qa_iam = getLayoutInflater().inflate(R.layout.quickpeek_layout, iamlayout, false);
                    final TextView questionHolder = (TextView) qa_iam.findViewById(R.id.question);
                    final TextView answerHolder = (TextView) qa_iam.findViewById(R.id.answer);
                    questionHolder.setText(question);
                    final ImageButton editButton = (ImageButton) qa_iam.findViewById(R.id.editButton);
                    editButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Commons.edit_profile_flag = true;
                            Intent intent = new Intent(getApplicationContext(), Questionnaire2Activity.class);
                            intent.putExtra("question", question);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                        }
                    });
                    answerHolder.setText(answer.replace("I am ", ""));
                    answerHolder.setTypeface(font2);
                    iamlayout.addView(qa_iam);
                }else if(answer.startsWith("I ")){
                    View qa_i = getLayoutInflater().inflate(R.layout.quickpeek_layout, ilayout, false);
                    final TextView questionHolder = (TextView) qa_i.findViewById(R.id.question);
                    final TextView answerHolder = (TextView) qa_i.findViewById(R.id.answer);
                    questionHolder.setText(question);
                    final ImageButton editButton = (ImageButton) qa_i.findViewById(R.id.editButton);
                    editButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Commons.edit_profile_flag = true;
                            Intent intent = new Intent(getApplicationContext(), Questionnaire2Activity.class);
                            intent.putExtra("question", question);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                        }
                    });
                    answerHolder.setText(answer.replace("I ", "").replace("can't", "Can't"));
                    answerHolder.setTypeface(font2);
                    ilayout.addView(qa_i);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        contain.addView(aboutmeView);
        if(onFocusView != null){
            View finalOnFocusView = onFocusView;
            contain.post(new Runnable() {
                @Override
                public void run() {
                    contain.scrollTo(0, finalOnFocusView.getTop());
                }
            });
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int percentage = (Math.abs(i)) * 100 / mMaxScrollSize;

        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;

            profileImage.animate()
                    .scaleY(0).scaleX(0)
                    .setDuration(200)
                    .start();

            name.setVisibility(View.GONE);
            nameLayout.setVisibility(View.VISIBLE);
        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;

            mProfileImage.setVisibility(View.VISIBLE);

            profileImage.animate()
                    .scaleY(1).scaleX(1)
                    .start();

            name.setVisibility(View.VISIBLE);
            nameLayout.setVisibility(View.GONE);
        }
    }

    public void getPosts() {

            String url = ReqConst.SERVER_URL + "getposts";

            progress_bar = (LoadingDots)findViewById(R.id.progress_bar);
            progress_bar.setVisibility(View.VISIBLE);

            StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    VolleyLog.v("Response:%n %s", response.toString()); Log.d("GETPOSTS===>",response.toString());

                    parseGetUsersResponse(response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.d("debug", error.toString());

                    progress_bar.setVisibility(View.GONE);
                    progress_bar = null;

                    showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();

                    params.put("email", Commons.thisUser.get_email());

                    return params;
                }
            };

            post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                    0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            MyApplication.getInstance().addToRequestQueue(post, url);
        }

        public void parseGetUsersResponse(String json) {

            progress_bar.setVisibility(View.GONE);
            progress_bar = null;

            try{

                JSONObject response = new JSONObject(json);  Log.d("RESPONSE===>",response.toString());

                int result_code = response.getInt("result_code");

            if(result_code == 0){

                contain.removeAllViews();
                postLayout.removeAllViews();

                JSONArray posts = response.getJSONArray("posts");
                Log.d("POSTS===",posts.toString());

                for (int i = 0; i < posts.length(); i++) {

                    JSONObject jsonPost = (JSONObject) posts.get(i);

                    Post post = new Post();

                    post.set_idx(jsonPost.getInt("id"));
                    post.set_userID(jsonPost.getInt("user_id"));
                    post.set_photo(jsonPost.getString("photo"));
                    post.set_text(jsonPost.getString("text"));
                    post.set_datetime(jsonPost.getString("datetime"));

                    View post_layout = getLayoutInflater().inflate(R.layout.posts_layout, postLayout, false);
                    final TextView textHolder = (TextView) post_layout.findViewById(R.id.text);
                    final TextView datetimeHolder = (TextView) post_layout.findViewById(R.id.datetime);
                    final ImageButton editButton = (ImageButton) post_layout.findViewById(R.id.editButton);
                    editButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Commons.textView = textHolder;
                            Commons.postDate = datetimeHolder;
                            Intent intent = new Intent(getApplicationContext(), EditPostActivity.class);
                            intent.putExtra("idx", post.get_idx());
                            startActivity(intent);
                            overridePendingTransition(0,0);
                        }
                    });
                    final ImageButton delButton = (ImageButton) post_layout.findViewById(R.id.deleteButton);
                    delButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            show_alert_delet_post(String.valueOf(post.get_idx()));
                        }
                    });
                    textHolder.setText(StringEscapeUtils.unescapeJava(post.get_text()));
                    textHolder.setTypeface(font2);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                    String myDate = dateFormat.format(new Date(Long.parseLong(post.get_datetime())));
                    datetimeHolder.setText(myDate);
                    postLayout.addView(post_layout);
                }

                if(posts.length() == 0)no_result.setVisibility(View.VISIBLE);
                else no_result.setVisibility(View.GONE);

                contain.addView(postView);

            } else {
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
            }
        }catch (JSONException e){
            e.printStackTrace();
            showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
        }
    }

    public void show_alert_delet_post(String idx){
        alert_post.setVisibility(View.VISIBLE);
        ((FrameLayout)findViewById(R.id.postdeletebackground)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.yes_post_delete)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePost(idx);
                alert_post.setVisibility(View.GONE);
                ((FrameLayout)findViewById(R.id.postdeletebackground)).setVisibility(View.GONE);
            }
        });
        ((TextView)findViewById(R.id.no_post_delete)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert_post.setVisibility(View.GONE);
                ((FrameLayout)findViewById(R.id.postdeletebackground)).setVisibility(View.GONE);
            }
        });
    }

    private String rename(String name){
        String firstName = "", lastName = "";
        if(name.contains(" ")){
            if(name.indexOf(" ") >= 1) {
                firstName = name.substring(0, name.indexOf(" "));
                lastName=name.substring(name.indexOf(" ")+1,name.length());
            }
            else {
                firstName=name;
                lastName="";
            }
        }else {
            firstName=name;
            lastName="";
        }
        if(lastName.length() != 0)
            return firstName + " " + lastName.substring(0, 1) + ".";
        else return firstName;
    }

    public void getActives(int user_id) {

        String url = ReqConst.SERVER_URL + "getNotifications";
        progressBar.setVisibility(View.VISIBLE);

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());
                parseGetNotificationsResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                progressBar.setVisibility(View.GONE);
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("user_id", String.valueOf(user_id));

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);
    }

    public void showToast(String content){
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_toast, null);
        TextView textView=(TextView)dialogView.findViewById(R.id.text);
        textView.setText(content);
        Toast toast=new Toast(getApplicationContext());
        toast.setView(dialogView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    public void parseGetNotificationsResponse(String json) {
        progressBar.setVisibility(View.GONE);
        try{

            JSONObject response = new JSONObject(json);  Log.d("RESPONSE===>",response.toString());

            int result_code = response.getInt("result_code");
            if(result_code == 0) {
                int acts = 0;
                JSONArray data = response.getJSONArray("data");
                Log.d("Notis===",data.toString());
                for(int i=0; i<data.length(); i++){
                    JSONObject jsonData = (JSONObject) data.get(i);
                    if(jsonData.getString("active").equals("1")){
                        acts = acts + 1;
                    }
                }
                Commons.thisUser.set_actives(acts);

            }else
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void getPhotos(){
        list.setVisibility(View.VISIBLE);
        String[] photoUrls = {
                "https://www.planwallpaper.com/static/images/94d1b9fea127b65c208ce54ab94662ff_large.jpg",
                "https://imgix.bustle.com/elite-daily/2017/05/08091733/blair-waldorf-gossip-girl.jpg?w=998&h=598&fit=crop&crop=faces&auto=format&q=70",
                "https://i.pinimg.com/originals/77/fa/e3/77fae39266d4bfa0495f838866427beb.jpg",
                "http://wpnature.com/wp-content/uploads/2016/08/lakes-emerald-lake-serenity-mountain-quiet-rocks-calmness-sky-nice-water-summer-nature-tranquil-reflection-beautiful-lovely-river-pretty-green-riverbank-shore-backgrounds.jpg",
                "http://uaab.tntsj.com/images/4006-2.jpg",
                "https://media.glamour.com/photos/56965c6416d0dc3747efeca2/master/pass/fashion-2012-12-blair-waldorf-headband-main.jpg",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRHrlz-YULgPhWz7HF5qTw01OZwjvI2Ga7fRQeKBjtAP_Q_R-jC",
                "https://ak5.picdn.net/shutterstock/videos/30765385/thumb/1.jpg?i10c=img.resize(height:160)",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR5d1wjhNWu927vKG-ADrJIobJJ8Cj9tHCXJSk7VMFdg5Hv9P1n",
                "https://i.ytimg.com/vi/5dRZK_P5D3c/maxresdefault.jpg",
                "https://cdn.lifehack.org/wp-content/uploads/2014/04/friends-tv-versability-lifehack-Brian-Penny.jpg",
                "http://getwallpapers.com/wallpaper/full/c/6/0/833649-best-pretty-nature-backgrounds-2000x1159-for-1080p.jpg",
                "https://cloud.netlifyusercontent.com/assets/344dbf88-fdf9-42bb-adb4-46f01eedd629/6abdc20c-a9a3-4986-b3a7-19699775505d/27-3.jpg",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTi4-zJcuzeRgJ31IKB8iobfV8q7GMLvyKilF85ZKNQInSJQmkvxg",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRBjvGVFuSpEekuli2nCU6H1ypPViKqSv5P3wAClAVMOCpQFd8z",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQrWQiuMIj1LAF12rgg9jWX9uk4CPgQuPXviPQ_eYOo5jvCJJJe",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS6Hj6MQYLwEL7EixuR2N43pleKerhnlaTZEODuEpC2fAnFSiWACw",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTFUPeq-1TRveO0FsSIrDIgtFAae-iVyBTihtR6Txs-Ll43jOPNdA",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS378OOKrbMGxn1z-DJLo19jJDXcy1m_OfcXFCQB8gPsq4WuHuY",
                "http://www.hotelcomtedenice.com/img/news-deal/news-deal-1.jpg",
                "https://wallpapersfind.com/wp-content/uploads/2017/10/nature-scene-landscape-wallpaper-650x400.jpg",
                "https://s-media-cache-ak0.pinimg.com/originals/4c/e3/43/4ce343c170be650ebc33b54276aa7b6d.jpg",
                "http://kb4images.com/images/good-morning-wallpapers/36846126-good-morning-wallpapers.jpg",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSwFs4GeRzFU0dVtR-PT9QL_a2zi-BHWP7v6Bbershd_6-NA_6k",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRBA_MIcLH5KPH3XxWwE5HGhKQCjPziZjaQgxiWPS4iCUN1p6qbDg",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRZLXeDvomm-8FRH8zHw5nt60w5jaQlGPdQY6dPasJLneqgd6gM",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRC6Kf9M2mzxfb4qWGex5Eg3A6op0bWdnyA9s3sh3f-GoC199AZ",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRowtMwwIJYQDppsR2vLr2KwXzzvOF5XT_gIjYEW3njrPfwBil8",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT50cd48JX_nbLI4y5IUGuhucDz8u1iVXzZe5hsvvcC6JL2I8D_",
                "https://80lv-cdn.akamaized.net/80.lv/uploads/2017/03/jhosep-chevarria-capacoila-highresscreenshot00100.jpg",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTu8hYyt3ZDa2RzhiNWtvpGFd8gMJZVUQvGye1b4kjLH9JPHhBWyw",
                "http://3.bp.blogspot.com/-HMiNxwWLAEw/TkqqPvjcuxI/AAAAAAAAIxg/1qYBFaTt9wU/s1600/Free_High%2Bhigh%2Bquality%2Bwidescreen%2Bwallpapers.jpg",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSa6ehrM62ojSG-QRSTyxHYXz3s3MuNBwj7TMcW-p2JuORyOrNK",
                "http://old.wallcoo.net/1440x900/1440_900_nature_scene_wallpapers_02/images/Free_High_resolution_nature_wallpaper_343847.jpg",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR6sWiCaOy5Vv8ZtIx53XtvJ-X2czkhUyKkoesSXoQBMIExfu4a",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRnu5QNwzN5h6ipS4YXrJV7GNGggdR04vPj6hWC3grSrj8T-RuehQ"
        };

    }

    private boolean compareDates(String datetime1, String datetime2){

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        Date date1 = new Date(Long.parseLong(datetime1));
        Date date2 = new Date(Long.parseLong(datetime2));

        c1.setTime(date1);
        c2.setTime(date2);

        int yearDiff = c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
        int monthDiff = c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
        int dayDiff = c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH);

        if(yearDiff == 0 && monthDiff == 0 && dayDiff == 0) {
            return true;
        }

        return false;
    }

    public void getNotification(final String me_id) {

        Commons.notifications.clear();

        final Firebase ref = new Firebase(ReqConst.FIREBASE_URL + "notification/" + me_id);

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Log.d("Count===>", String.valueOf(dataSnapshot.getChildrenCount()));

                final Firebase ref2 = ref.child(dataSnapshot.getKey());
                Log.d("Reference===>", ref2.toString());

                ref2.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        Map map = dataSnapshot.getValue(Map.class);

                        String message, senderName, senderEmail, senderPhoto, dateTime;
                        int senderId = 1;

                        try{
                            message = map.get("msg").toString();
                            senderId = Integer.parseInt(map.get("sender_id").toString());
                            senderEmail = map.get("sender_email").toString();
                            senderPhoto = map.get("sender_photo").toString();
                            senderName = rename(map.get("sender_name").toString());
                            dateTime = map.get("time").toString();

                            Notification notification = new Notification();
                            notification.setSender_id(senderId);
                            notification.setSender_name(senderName);
                            notification.setSender_photo(senderPhoto);
                            notification.setDate_time(dateTime);
                            notification.setMessage(message);
                            notification.setFirebase(ref2.child(dataSnapshot.getKey()));
                            notification.setKey(dataSnapshot.getKey());

                            if(Commons.notifications.size() == 0) {
                                Commons.notifications.add(notification);
                                notiMap.put(senderId, notification);
                                ShortcutBadger.applyCount(getApplicationContext(), Commons.notifications.size());
                                noticon.setVisibility(View.VISIBLE);
                                count.setText(String.valueOf(Commons.notifications.size()));
                                shownot(senderPhoto, senderName, message);
                            }

                            for(int i=0; i < Commons.notifications.size(); i++){
                                if(Commons.notifications.get(i).getDate_time().equals(notification.getDate_time()) && Commons.notifications.get(i).getSender_id() == notification.getSender_id())
                                    break;
                                else if(i == Commons.notifications.size()-1){
                                    if(!notiMap.containsKey(senderId) && Commons.notifications.get(i).getSender_id() != notification.getSender_id()){
                                        Commons.notifications.add(0, notification);
                                        notiMap.put(senderId, notification);
                                        ShortcutBadger.applyCount(getApplicationContext(), Commons.notifications.size());
                                        noticon.setVisibility(View.VISIBLE);
                                        count.setText(String.valueOf(Commons.notifications.size()));
                                        shownot(senderPhoto, senderName, message);
                                    }
                                }
                            }

                            Commons.event = true;
                            Log.d("new noti+++", "yes");

                        }catch (NullPointerException e){}
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Map map = dataSnapshot.getValue(Map.class);

                        String message, senderName, senderEmail, senderPhoto, dateTime;
                        int senderId = 1;

                        try{
                            message = map.get("msg").toString();
                            senderId = Integer.parseInt(map.get("sender_id").toString());
                            senderEmail = map.get("sender_email").toString();
                            senderPhoto = map.get("sender_photo").toString();
                            senderName = rename(map.get("sender_name").toString());
                            dateTime = map.get("time").toString();

                            Notification notification = new Notification();
                            notification.setSender_id(senderId);
                            notification.setSender_name(senderName);
                            notification.setSender_photo(senderPhoto);
                            notification.setDate_time(dateTime);
                            notification.setMessage(message);
                            notification.setFirebase(ref2.child(dataSnapshot.getKey()));
                            notification.setKey(dataSnapshot.getKey());

                            Commons.notifications.remove((Notification) notiMap.get(senderId));
                            ShortcutBadger.applyCount(getApplicationContext(), Commons.notifications.size());
                            if(Commons.notifications.size() <= 0){
                                Commons.notificationManager.cancelAll();
                                ShortcutBadger.removeCount(getApplicationContext());
                                noticon.setVisibility(View.GONE);
                                count.setText("0");
                            }

                            Commons.event = true;
                            Log.d("new noti---", "yes");

                        }catch (NullPointerException e){}
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void shownot(String senderPhoto, String senderName, String message) {

        android.app.Notification.Builder n;
        NotificationManager notification_manager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String chanel_id = "3000";
            CharSequence name = "Channel Name";
            String description = "Chanel Description";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(chanel_id, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.BLUE);
            notification_manager.createNotificationChannel(mChannel);
            n = new android.app.Notification.Builder(this, chanel_id);
        } else {
            n = new android.app.Notification.Builder(this);
        }

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] v = {500,1000};

        Bitmap bitmapPhoto = null;

        if(senderPhoto.length() > 0){
            try {
                bitmapPhoto = BitmapFactory.decodeStream((InputStream) new URL(senderPhoto).getContent());
            } catch (IOException e) {
                e.printStackTrace();
                bitmapPhoto = BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.questionicon);
            }
        }

        bitmapPhoto = BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.notiusericon);

        Intent intent = new Intent(this, NotificationsActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 113, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        n.setContentTitle(senderName)
//                .setFullScreenIntent(pIntent,false)
                .setContentText(message)
                .setSmallIcon(R.mipmap.appicon).setLargeIcon(bitmapPhoto)
                .setContentIntent(pIntent)
                .setSound(uri)
                .setVibrate(v)
                .setAutoCancel(true).build();

        notification_manager.notify(0, n.build());
        Commons.notificationManager = notification_manager;
    }

    private void getOnline(){

        Commons.onlines.clear();

        Firebase ref = new Firebase(ReqConst.FIREBASE_URL + "sts");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                final Firebase ref2 = new Firebase(ReqConst.FIREBASE_URL + "sts/" + dataSnapshot.getKey());
                ref2.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        Map map = dataSnapshot.getValue(Map.class);
                        try{
                            String status = map.get("status").toString();
                            String time = map.get("time").toString();
                            String user = map.get("user").toString();

                            Online online = new Online();
                            online.setStatus(status);
                            online.setTime(time);
                            online.setUser(user);

                            if(Commons.onlines.size() == 0) {
                                Commons.onlines.add(online);
                                onlineMap.put(online.getUser(), online);
                            }

                            for(int i=0; i < Commons.onlines.size(); i++){
                                if(Commons.onlines.get(i).getTime().equals(online.getTime()) && Commons.onlines.get(i).getUser().equals(online.getUser()))
                                    break;
                                else if(i == Commons.onlines.size()-1){
                                    if(!onlineMap.containsKey(online.getUser()) && !Commons.onlines.get(i).getUser().equals(online.getUser())){
                                        Commons.onlines.add(0, online);
                                        onlineMap.put(online.getUser(), online);
                                    }
                                }
                            }

                            Commons.event2 = true;
                            Log.d("new online+++", "yes");

                        }catch (NullPointerException e){}
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Map map = dataSnapshot.getValue(Map.class);
                        try{
                            String status = map.get("status").toString();
                            String time = map.get("time").toString();
                            String user = map.get("user").toString();

                            Online online = new Online();
                            online.setStatus(status);
                            online.setTime(time);
                            online.setUser(user);

                            Commons.onlines.remove((Online) onlineMap.get(online.getUser()));

                            Commons.event2 = true;
                            Log.d("new online---", "yes");

                        }catch (NullPointerException e){}
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void deletePost(String post_id) {

        String url = ReqConst.SERVER_URL + "delpost";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString()); Log.d("GETPOSTS===>",response.toString());

                parseDelPostResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("post_id", post_id);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);
    }

    public void parseDelPostResponse(String json) {

        try{

            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");

            if(result_code == 0) {
                getPosts();
            }else {
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void resetPremium(int user_id) {

        String url = ReqConst.SERVER_URL + "resetPremium";
        progressBar.setVisibility(View.VISIBLE);

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());
                parseResetPremiumResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                progressBar.setVisibility(View.GONE);
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("me_id", String.valueOf(user_id));

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);
    }

    public void parseResetPremiumResponse(String json) {
        progressBar.setVisibility(View.GONE);
        try{
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");
            if(result_code == 0) {
                Commons.thisUser.set_premium("");
                membership.setText("Bronze member");
                maxdatesbox.setText("Max dates: " + String.valueOf(Commons.max_dates));
                membership.setVisibility(View.VISIBLE);
                premium.setVisibility(View.GONE);

            }else
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void checkAllPermission() {

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (hasPermissions(this, PERMISSIONS)){

        }else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 101);
        }
    }
    public static boolean hasPermissions(Context context, String... permissions) {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {

            for (String permission : permissions) {

                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void okayadminalert(View view){

        ((LinearLayout)findViewById(R.id.adminAlertbox)).setVisibility(View.GONE);
        ((View)findViewById(R.id.background)).setVisibility(View.GONE);

        reference_admin_alert.removeValue();
    }

    ArrayList<Questionnaire> oldQuestions = new ArrayList<>();

    public void getDynamicals(String json) {
        oldQuestions.clear();
        try {
            JSONObject jsonObject = new JSONObject(json);

            JSONArray jsonArray = jsonObject.getJSONArray("questionnaires");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);

                String question=jsonObject1.getString("question");
                String answer=jsonObject1.getString("answer");

                Questionnaire questionnaire = new Questionnaire();
                questionnaire.set_text(question);
                questionnaire.set_isactive("active");
                questionnaire.set_answer(answer);
                oldQuestions.add(questionnaire);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        updatedQuestions.clear();

        progressBar.setVisibility(View.VISIBLE);

        String url = ReqConst.SERVER_URL + "getDynamicals";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("Dynamicals====>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseGetDynamicalsResponse1(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                progressBar.setVisibility(View.GONE);
            }
        }) {

        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseGetDynamicalsResponse1(String json) {

        try {
            JSONObject response = new JSONObject(json);

            String result_code = response.getString("result_code");

            if (result_code.equals("0")) {

                //==========================================================

                JSONArray dynamicalsInfo = response.getJSONArray("data");

                if(dynamicalsInfo.length() > 0){
                    for(int i=0; i<dynamicalsInfo.length(); i++) {
                        JSONObject jsonDynamical = (JSONObject) dynamicalsInfo.get(i);
                        Questionnaire questionnaire = new Questionnaire();
                        questionnaire.set_text(jsonDynamical.getString("dynamical_question"));
                        questionnaire.set_isactive("inactive");
                        questionnaire.set_answer("");
                        for(int j=0; j<oldQuestions.size(); j++){
                            if(questionnaire.get_text().equals(oldQuestions.get(j).get_text())){
                                updatedQuestions.add(oldQuestions.get(j));
                                break;
                            }
                            else{
                                if(j == oldQuestions.size()-1)
                                    updatedQuestions.add(questionnaire);
                            }
                        }
                    }
                }else {
                    String[] texts = {"Other than appearance, what is the first thing that people notice about you?",
                            "What’s the most important thing you’re looking for in another person?",
                            "If you could travel anywhere, where would you go?",
                            "What are your favourite books/movies/music/quotes?",
                            "If you won the lottery tomorrow, what’s the first thing you’d buy?"};

                    for(int i=0; i<texts.length; i++){
                        Questionnaire questionnaire = new Questionnaire();
                        questionnaire.set_text(texts[i]);
                        questionnaire.set_isactive("inactive");
                        questionnaire.set_answer("");
                        for(int j=0; j<oldQuestions.size(); j++){
                            if(questionnaire.get_text().equals(oldQuestions.get(j).get_text())){
                                updatedQuestions.add(oldQuestions.get(j));
                                break;
                            }
                            else{
                                if(j == oldQuestions.size()-1)
                                    updatedQuestions.add(questionnaire);
                            }
                        }
                    }
                }
                Commons.questionnaires.clear();
                Commons.questionnaires.addAll(updatedQuestions);
                uploadQuestionnaireInfo(createAnswerJsonString());
            }
            else {
                progressBar.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
        }

    }

    String dynamicalNotiTime = "0";

    public void closeDynamicalAlert(View view){
        ((LinearLayout)findViewById(R.id.dynamicalUpdatedAlertbox)).setVisibility(View.GONE);
        ((View)findViewById(R.id.dynamicalbackground)).setVisibility(View.GONE);
    }

    public void okayDynamicalAlert(View view){
        ((LinearLayout)findViewById(R.id.dynamicalUpdatedAlertbox)).setVisibility(View.GONE);
        ((View)findViewById(R.id.dynamicalbackground)).setVisibility(View.GONE);
        Preference.getInstance().put(getApplicationContext(), PrefConst.DYNAMICAL_UPDATED, dynamicalNotiTime);
        getDynamicals(Commons.thisUser.get_answers());
    }

    private void getDynamicalUpdateNotification(){
        Firebase ref = new Firebase(ReqConst.FIREBASE_URL + "dynamical");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Map map = dataSnapshot.getValue(Map.class);
                try{
                    String noti = map.get("noti").toString();
                    String time = map.get("time").toString();
                    processNoti(time);


                }catch (NullPointerException e){}
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void getMembershipUpdateNotification(){
        Firebase ref = new Firebase(ReqConst.FIREBASE_URL + "updatedmembership" + "/" + String.valueOf(Commons.thisUser.get_idx()));
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Map map = dataSnapshot.getValue(Map.class);
                try{
                    String noti = map.get("noti").toString();
                    String time = map.get("time").toString();
                    ref.removeValue();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0,0);

                }catch (NullPointerException e){}
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void processNoti(String time){
        String oldTime = Preference.getInstance().getValue(getApplicationContext(), PrefConst.DYNAMICAL_UPDATED, "");
        if(oldTime.length() == 0) oldTime = "0";
        if(Long.parseLong(oldTime) < Long.parseLong(time)) {
            Preference.getInstance().put(getApplicationContext(), PrefConst.DYNAMICAL_UPDATED, time);
            getDynamicals(Commons.thisUser.get_answers());
        }
        dynamicalNotiTime = time;
    }

    public String createAnswerJsonString()throws JSONException {

        String answers = "";
        JSONObject jsonObj = null;
        JSONArray jsonArr = new JSONArray();
        if (Commons.questionnaires.size()>0){
            for(int i=0; i<Commons.questionnaires.size(); i++){

                String question = Commons.questionnaires.get(i).get_text();
                String answer = Commons.questionnaires.get(i).get_answer();
                String isactive = Commons.questionnaires.get(i).get_isactive();

                jsonObj=new JSONObject();

                try {
                    jsonObj.put("question",question);
                    jsonObj.put("answer",answer);
                    jsonObj.put("isactive",isactive);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                jsonArr.put(jsonObj);
            }
            JSONObject questionnaireObj = new JSONObject();
            questionnaireObj.put("questionnaires", jsonArr);
            answers = questionnaireObj.toString();
            return answers;
        }
        return answers;
    }

    public void uploadQuestionnaireInfo(String questionnaireInfo) {

        String url = ReqConst.SERVER_URL + "uploadQuestionnaireInfo";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseRestUrlsResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                Toast.makeText(getApplicationContext(), "We can not detect any internet connectivity. Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("email", Commons.thisUser.get_email());
                params.put("questionnaireinfo", questionnaireInfo);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseRestUrlsResponse(String json) {
        progressBar.setVisibility(View.GONE);
        try {

            JSONObject response = new JSONObject(json);   Log.d("RESPONSE=====> :",response.toString());

            String success = response.getString("result_code");

            Log.d("Result_Code===> :",success);

            if (success.equals("0")) {
                parseQAJson(Commons.thisUser.get_answers(), Commons.thisUser.get_answers2());
            }
            else {
                Toast.makeText(getApplicationContext(), "We can not detect any internet connectivity. Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "We can not detect any internet connectivity. Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void getPictures(int user_id) {

        photoLayout.setVisibility(View.VISIBLE);
        progress_bar = (LoadingDots)findViewById(R.id.progress_bar);
        progress_bar.setVisibility(View.VISIBLE);

        String url = ReqConst.SERVER_URL + "getPictures";
        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());
                parseGetPicturesResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
                progress_bar.setVisibility(View.GONE);
                progress_bar = null;
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("member_id", String.valueOf(user_id));

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);
    }

    public void parseGetPicturesResponse(String json) {
        progress_bar.setVisibility(View.GONE);
        progress_bar = null;
        try{

            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");
            if(result_code == 0) {
                showPictureJsonData(response);
            }else
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void pickPhoto(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), 100);
    }

    int pictureID = 0;

    public void editPicture(int picture_id){
        pictureID = picture_id;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    onSelectFromGalleryResult(data, 0);
                }
                break;
            case 200:
                if (resultCode == RESULT_OK) {
                    onSelectFromGalleryResult(data, 1);
                }
                break;
        }
    }

    File imageFile = null;

    private void onSelectFromGalleryResult(Intent data, int cat) {

        if (data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getApplicationContext().getContentResolver(), data.getData());
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                imageFile = new File(Environment.getExternalStorageDirectory()+"/Pictures",
                        System.currentTimeMillis() + ".jpg");

                FileOutputStream fo;
                try {
                    imageFile.createNewFile();
                    fo = new FileOutputStream(imageFile);
                    fo.write(byteArrayOutputStream.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(cat == 0)
                    uploadPicture(Commons.thisUser.get_idx(), imageFile);
                else if(cat == 1)
                    updatePicture(pictureID, imageFile);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadPicture(int user_id, File file) {

        try {

            final Map<String, String> params = new HashMap<>();
            params.put("member_id", String.valueOf(user_id));

            progress_bar = (LoadingDots)findViewById(R.id.progress_bar);
            progress_bar.setVisibility(View.VISIBLE);

            String url = ReqConst.SERVER_URL + "uploadPicture";

            MultiPartRequest reqMultiPart = new MultiPartRequest(url, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    progress_bar.setVisibility(View.GONE);
                    progress_bar = null;
                    showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
                }
            }, new Response.Listener<String>() {

                @Override
                public void onResponse(String json) {

                    parseUploadPictureResponse(json);
                }
            }, file, "file", params);

            reqMultiPart.setRetryPolicy(new DefaultRetryPolicy(
                    Constants.VOLLEY_TIME_OUT, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            MyApplication.getInstance().addToRequestQueue(reqMultiPart, url);

        } catch (Exception e) {
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(),"We can not detect any internet connectivity. Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
        }
    }

    public void parseUploadPictureResponse(String json) {

        progress_bar.setVisibility(View.GONE);
        progress_bar = null;

        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");

            if (result_code == 0) {
                imageFile=null;
                showToast("Successfully uploaded");
                showPictureJsonData(response);
            }
            else if (result_code == 1) {
                imageFile=null;
                showToast("You can feature only up to 4 photos.");
            }
            else {
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
            }

        } catch (JSONException e) {
            showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
            e.printStackTrace();
        }
    }

    public void updatePicture(int picture_id, File file) {

        try {

            final Map<String, String> params = new HashMap<>();
            params.put("picture_id", String.valueOf(picture_id));

            progress_bar = (LoadingDots)findViewById(R.id.progress_bar);
            progress_bar.setVisibility(View.VISIBLE);

            String url = ReqConst.SERVER_URL + "updatePicture";

            MultiPartRequest reqMultiPart = new MultiPartRequest(url, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    progress_bar.setVisibility(View.GONE);
                    progress_bar = null;
                    showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
                }
            }, new Response.Listener<String>() {

                @Override
                public void onResponse(String json) {

                    parseEditPictureResponse(json);
                }
            }, file, "file", params);

            reqMultiPart.setRetryPolicy(new DefaultRetryPolicy(
                    Constants.VOLLEY_TIME_OUT, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            MyApplication.getInstance().addToRequestQueue(reqMultiPart, url);

        } catch (Exception e) {
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
            showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
        }
    }

    public void parseEditPictureResponse(String json) {

        progress_bar.setVisibility(View.GONE);
        progress_bar = null;

        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");

            if (result_code == 0) {
                imageFile = null;
                showToast("Updated");
                showPictureJsonData(response);
            }
            else {
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
            }

        } catch (JSONException e) {
            showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
            e.printStackTrace();
        }
    }

    public void showDelPictureAlert(int picture_id){
        pictureID = picture_id;
        ((LinearLayout)findViewById(R.id.alert_picture)).setVisibility(View.VISIBLE);
        ((FrameLayout)findViewById(R.id.picturedeletebackground)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.yes_picture_delete)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LinearLayout)findViewById(R.id.alert_picture)).setVisibility(View.GONE);
                ((FrameLayout)findViewById(R.id.picturedeletebackground)).setVisibility(View.GONE);
                if(pictureCount == 2){
                    showToast("You need to keep at least 2 pictures.");
                    return;
                }
                deletePicture(pictureID);
            }
        });
    }

    public void closePictureDeleteAlert(View view){
        ((LinearLayout)findViewById(R.id.alert_picture)).setVisibility(View.GONE);
        ((FrameLayout)findViewById(R.id.picturedeletebackground)).setVisibility(View.GONE);
    }

    public void deletePicture(int picture_id) {

        progress_bar = (LoadingDots)findViewById(R.id.progress_bar);
        progress_bar.setVisibility(View.VISIBLE);
        String url = ReqConst.SERVER_URL + "deletePicture";
        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());
                parseDelPictureResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress_bar.setVisibility(View.GONE);
                progress_bar = null;
                Log.d("debug", error.toString());
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("picture_id", String.valueOf(picture_id));
                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);
    }

    public void parseDelPictureResponse(String json) {
        progress_bar.setVisibility(View.GONE);
        progress_bar = null;
        try{

            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");
            if(result_code == 0) {
                showToast("Deleted");
                showPictureJsonData(response);
            }else
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void showPictureJsonData(JSONObject response){
        try{
            pictures.clear();
            JSONArray data = response.getJSONArray("pictures");
            for(int i=0; i<data.length(); i++){
                JSONObject jsonData = (JSONObject) data.get(i);
                Picture picture = new Picture();
                picture.setIdx(jsonData.getInt("id"));
                picture.setMember_id(jsonData.getInt("member_id"));
                picture.setPicture_url(jsonData.getString("picture_url"));
                pictures.add(picture);
            }
            pictureCount = pictures.size();
            if(pictures.isEmpty()){
                photoLayout.findViewById(R.id.no_result).setVisibility(View.VISIBLE);
            }
            else {
                photoLayout.findViewById(R.id.no_result).setVisibility(View.GONE);
            }
            adapter.setDatas(pictures);
            adapter.notifyDataSetChanged();
            list.setAdapter(adapter);
        }catch (JSONException e){}
    }

    Firebase reference_admin_alert;

    private void getAdminNotification(){
        Firebase ref = new Firebase(ReqConst.FIREBASE_URL + "adminnotification" + "/" + String.valueOf(Commons.thisUser.get_idx()));
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Map map = dataSnapshot.getValue(Map.class);
                try{
                    String noti = map.get("noti").toString();
                    String time = map.get("time").toString();
                    reference_admin_alert = ref;
                    ((LinearLayout)findViewById(R.id.adminAlertbox)).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.notification)).setText(noti);
                    ((View)findViewById(R.id.background)).setVisibility(View.VISIBLE);

                }catch (NullPointerException e){}
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void getSettings(String email) {

        String url = ReqConst.SERVER_URL + "getSettings";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseGetSettingsResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("member_email", email);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseGetSettingsResponse(String json) {

        try {

            JSONObject response = new JSONObject(json);

            String success = response.getString("result_code");

            Log.d("Result_Code===> :",success);

            if (success.equals("0")) {
                JSONObject jsonObject = response.getJSONObject("data");
                Preference.getInstance().put(getApplicationContext(), PrefConst.SETTING_MAX_AGE, jsonObject.getInt("max_age"));
                Preference.getInstance().put(getApplicationContext(), PrefConst.SETTING_MIN_AGE, jsonObject.getInt("min_age"));
                Preference.getInstance().put(getApplicationContext(), PrefConst.SETTING_GENDER, jsonObject.getInt("gender"));
                Preference.getInstance().put(getApplicationContext(), PrefConst.SETTING_PROFILE, jsonObject.getInt("profile"));
            }
            else if(success.equals("1")){
                Preference.getInstance().put(getApplicationContext(), PrefConst.SETTING_MAX_AGE, "");
                Preference.getInstance().put(getApplicationContext(), PrefConst.SETTING_MIN_AGE, "");
                Preference.getInstance().put(getApplicationContext(), PrefConst.SETTING_GENDER, "");
                Preference.getInstance().put(getApplicationContext(), PrefConst.SETTING_PROFILE, "");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finish() {
        super.finish();
        if(progress_bar != null) {
            if(progress_bar.getVisibility() == View.VISIBLE)
                progress_bar.setVisibility(View.GONE);
            progress_bar = null;
        }
    }

}




























