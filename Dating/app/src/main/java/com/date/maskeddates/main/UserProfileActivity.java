package com.date.maskeddates.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.AppBarLayout;

import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.date.maskeddates.commons.Commons;
import com.date.maskeddates.commons.Constants;
import com.date.maskeddates.commons.ReqConst;
import com.date.maskeddates.models.Post;
import com.eyalbira.loadingdots.LoadingDots;
import com.firebase.client.Firebase;
import com.github.mmin18.widget.RealtimeBlurView;
import com.google.android.flexbox.FlexboxLayout;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.BlurTransformation;

public class UserProfileActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener  {

    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 30;
    private static boolean mIsAvatarShown = true;

    private ImageView mProfileImage, datinglock, no_result;
    private CircleImageView profileImage;
    private int mMaxScrollSize;
    TextView name, name2, gender, age, location, progressText, aboutme, posts, photos;
    RealtimeBlurView blurView;
    ProgressBar progressBar;
    LoadingDots progress_bar;
    int percent = 0;
    LinearLayout nameLayout, dynamical, staticq, postLayout, commonq;
    TextView match, viewalltitle, lastlogin, actives;
    NestedScrollView contain;
    View aboutmeView, postView;
    Firebase ref, ref2, ref3;
    Button viewAll;
    View aboutmeIndicator, photosIndicator, postsIndicator;
    String userStaticJson = "";
    Typeface font, font2;
    FlexboxLayout iamlayout, iamherelayout, ilikelayout, ilayout;
    int userActs = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Firebase.setAndroidContext(this);

        int unraveled = getIntent().getIntExtra("unraveled", 0);

        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Comfortaa_Bold.ttf");
        font2 = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Comfortaa_Regular.ttf");

        progressBar=(ProgressBar) findViewById(R.id.progressBar);
        progress_bar=(LoadingDots) findViewById(R.id.progress_bar);

        contain = (NestedScrollView) findViewById(R.id.contain);
        aboutme = (TextView) findViewById(R.id.aboutme);
        posts = (TextView) findViewById(R.id.posts);
        photos = (TextView) findViewById(R.id.photos);

        aboutmeIndicator = ((View)findViewById(R.id.indicator_aboutme));
        photosIndicator = ((View)findViewById(R.id.indicator_photos));
        postsIndicator = ((View)findViewById(R.id.indicator_posts));

        lastlogin = (TextView) findViewById(R.id.lastlogin);
        actives = (TextView) findViewById(R.id.actives);

        AppBarLayout appbarLayout = (AppBarLayout) findViewById(R.id.materialup_appbar);
        mProfileImage = (ImageView) findViewById(R.id.materialup_profile_image);
        profileImage = (CircleImageView) findViewById(R.id.materialup_profile_image2);

        match = (TextView) findViewById(R.id.match);
        nameLayout = (LinearLayout) findViewById(R.id.nameLayout);

        name = (TextView) findViewById(R.id.name);
        location = (TextView) findViewById(R.id.location);
        name2 = (TextView) findViewById(R.id.name2);
        gender = (TextView) findViewById(R.id.gender);
        age = (TextView) findViewById(R.id.age);

        name.setTypeface(font);
        location.setTypeface(font2);
        name2.setTypeface(font);
        gender.setTypeface(font);
        age.setTypeface(font);
        lastlogin.setTypeface(font2);
        ((TextView)findViewById(R.id.lastseen)).setTypeface(font2);

        datinglock = (ImageView) findViewById(R.id.datinglock);
        datinglock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PremiumGuideActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        getActives(Commons.user.get_idx());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        String myDate = dateFormat.format(new Date(Long.parseLong(Commons.user.get_lastlogin())));
        lastlogin.setText(myDate);

        name.setText(rename(Commons.user.get_name()));
        name2.setText(rename(Commons.user.get_name()));

        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Comfortaa_Bold.ttf");
        font2 = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Comfortaa_Regular.ttf");
//        name.setTypeface(font);
//        name2.setTypeface(font);

        gender.setText(Commons.user.get_gender());
        age.setText(Commons.user.get_age());
        location.setText(Commons.user.get_address());

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressText = (TextView) findViewById(R.id.txtProgress);

        percent = Integer.parseInt(Commons.user.get_photo_unlock());

        progressBar.setProgress(percent);
        progressText.setText(String.valueOf(percent) + " %");
        match.setText(String.valueOf(percent) + " %");

        if(unraveled < 4)
            Picasso.with(getApplicationContext())
                .load(Commons.user.get_photoUrl())
                .transform(new BlurTransformation(getApplicationContext(), 25, 1))
                .error(R.drawable.q9)
                .placeholder(R.drawable.q9)
                .into(profileImage);
        else Picasso.with(getApplicationContext())
                .load(Commons.user.get_photoUrl())
                .error(R.drawable.q9)
                .placeholder(R.drawable.q9)
                .into(profileImage);

        Toolbar toolbar = (Toolbar) findViewById(R.id.materialup_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });

        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        aboutmeView = inflater.inflate(R.layout.fragment_aboutme, null);
        dynamical = (LinearLayout)aboutmeView.findViewById(R.id.dynamical);
        staticq = (LinearLayout)aboutmeView.findViewById(R.id.staticq);
        commonq = (LinearLayout)aboutmeView.findViewById(R.id.commonq);

        iamlayout = (FlexboxLayout)aboutmeView.findViewById(R.id.iamlayout);
        ilikelayout = (FlexboxLayout)aboutmeView.findViewById(R.id.ilikelayout);
        iamherelayout = (FlexboxLayout)aboutmeView.findViewById(R.id.iamherelayout);
        ilayout = (FlexboxLayout)aboutmeView.findViewById(R.id.ilayout);

        userStaticJson = Commons.user.get_answers2();
        getMatchScore(Commons.thisUser.get_answers2(), Commons.user.get_answers2());

        aboutme.setTextColor(Color.BLACK);
        aboutme.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        photos.setTextColor(Color.GRAY);
        posts.setTextColor(Color.GRAY);

        aboutme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearTabColors();
                aboutme.setTextColor(Color.BLACK);
                aboutmeIndicator.setVisibility(View.VISIBLE);
                parseQAJson(Commons.user.get_answers(), Commons.user.get_answers2());
            }
        });

        parseQAJson(Commons.user.get_answers(), Commons.user.get_answers2());

        viewAll = (Button)aboutmeView.findViewById(R.id.viewall);
        viewAll.setVisibility(View.GONE);
        staticq.setVisibility(View.VISIBLE);

        viewalltitle = (TextView) aboutmeView.findViewById(R.id.viewalltitle);
        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                staticq.setVisibility(View.VISIBLE);
                viewAll.setVisibility(View.GONE);
                viewalltitle.setVisibility(View.GONE);
            }
        });

        inflater = LayoutInflater.from(getApplicationContext());
        postView = inflater.inflate(R.layout.fragment_posts, null);
        postLayout = (LinearLayout)postView.findViewById(R.id.posts);
        no_result = (ImageView)postView.findViewById(R.id.no_result);
        Button postButton = (Button) postView.findViewById(R.id.newpost);
        postButton.setVisibility(View.GONE);

        photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Commons.thisUser.get_premium().length() == 0){
                    Intent intent = new Intent(getApplicationContext(), PremiumGuideActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    return;
                }
                clearTabColors();
                photos.setTextColor(Color.BLACK);
                photosIndicator.setVisibility(View.VISIBLE);
            }
        });

        posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearTabColors();
                posts.setTextColor(Color.BLACK);
                postsIndicator.setVisibility(View.VISIBLE);
                contain.removeAllViews();
                postLayout.removeAllViews();
                getPosts();
            }
        });

        aboutme.setTypeface(font);
        posts.setTypeface(font);
        photos.setTypeface(font);

        appbarLayout.addOnOffsetChangedListener(this);
        mMaxScrollSize = appbarLayout.getTotalScrollRange();

        if(Commons.user.get_premium().length() > 0)
            parsePremium(Commons.user.get_premium());

    }

    int userMaxDates = 1;     ///////////////   3   ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void parsePremium(String premium){
        long expired = 0; int max_dates = 3;
        if(premium.contains("_")){
            expired = Long.parseLong(premium.substring(0, premium.indexOf("_")));
            max_dates = Integer.parseInt(premium.substring(premium.indexOf("_") + 1, premium.length()));
        }
        userMaxDates = max_dates;
    }

    private void getMatchScore(String me_json, String user_json){
        try {
            ArrayList<String> commonlist = new ArrayList<>();
            JSONObject jsonObject1 = new JSONObject(me_json);
            JSONObject jsonObject2 = new JSONObject(user_json);

            JSONArray jsonArray1 = jsonObject1.getJSONArray("questionnaires");
            JSONArray jsonArray2 = jsonObject2.getJSONArray("questionnaires");

            for (int i = 0; i < jsonArray1.length(); i++) {

                JSONObject jsonObject11 = (JSONObject) jsonArray1.get(i);
                JSONObject jsonObject22 = (JSONObject) jsonArray2.get(i);

                String question1=jsonObject11.getString("question");
                String answer1=jsonObject11.getString("answer");

                String question2=jsonObject22.getString("question");
                String answer2=jsonObject22.getString("answer");

                if(answer1.equals(answer2))commonlist.add(answer1);
            }
            int matched = (int) commonlist.size()*100/jsonArray1.length();
            progressBar.setProgress(matched);
            progressText.setText(String.valueOf(matched) + " %");
            match.setText(String.valueOf(matched) + " %");

        } catch (JSONException e) {
            e.printStackTrace();
            progressBar.setProgress(0);
            progressText.setText(String.valueOf(0) + " %");
            match.setText(String.valueOf(0) + " %");
        }
    }

    private void clearTabColors(){
        aboutme.setTextColor(Color.GRAY);
        photos.setTextColor(Color.GRAY);
        posts.setTextColor(Color.GRAY);
        aboutmeIndicator.setVisibility(View.GONE);
        photosIndicator.setVisibility(View.GONE);
        postsIndicator.setVisibility(View.GONE);
    }

    public void parseQAJson(String json, String json2){

        contain.removeAllViews();
        dynamical.removeAllViews();
        staticq.removeAllViews();
        commonq.removeAllViews();
        ilayout.removeAllViews();
        ilikelayout.removeAllViews();
        iamherelayout.removeAllViews();
        iamlayout.removeAllViews();

        try {
            JSONObject jsonObject = new JSONObject(json);

            JSONArray jsonArray = jsonObject.getJSONArray("questionnaires");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);

                String question=jsonObject1.getString("question");
                String answer=jsonObject1.getString("answer");

                View qa_dynamical = getLayoutInflater().inflate(R.layout.qa_layout, dynamical, false);
                final TextView questionHolder = (TextView) qa_dynamical.findViewById(R.id.question);
                final TextView answerHolder = (TextView) qa_dynamical.findViewById(R.id.answer);
                final ImageButton editButton = (ImageButton) qa_dynamical.findViewById(R.id.editButton);
                editButton.setVisibility(View.GONE);
                questionHolder.setText(question);
                answerHolder.setText(StringEscapeUtils.unescapeJava(answer));
                answerHolder.setTypeface(font2);
                dynamical.addView(qa_dynamical);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<String> commonAnswers = new ArrayList<>();

        try {
            JSONObject jsonObject1 = new JSONObject(Commons.thisUser.get_answers2());
            JSONObject jsonObject2 = new JSONObject(userStaticJson);

            JSONArray jsonArray1 = jsonObject1.getJSONArray("questionnaires");
            JSONArray jsonArray2 = jsonObject2.getJSONArray("questionnaires");

            for (int i = 0; i < jsonArray1.length(); i++) {

                JSONObject jsonObject11 = (JSONObject) jsonArray1.get(i);
                JSONObject jsonObject22 = (JSONObject) jsonArray2.get(i);

                String question1=jsonObject11.getString("question");
                String answer1=jsonObject11.getString("answer");

                String question2=jsonObject22.getString("question");
                String answer2=jsonObject22.getString("answer");

                if(answer1.equals(answer2)){
                    commonAnswers.add(answer1);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject(json2);

            JSONArray jsonArray = jsonObject.getJSONArray("questionnaires");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);

                String question=jsonObject1.getString("question");
                String answer=jsonObject1.getString("answer");

                if(answer.startsWith("I am here for")){
                    View qa_iamhere = getLayoutInflater().inflate(R.layout.quickpeek_layout, iamherelayout, false);
                    final TextView questionHolder = (TextView) qa_iamhere.findViewById(R.id.question);
                    final TextView answerHolder = (TextView) qa_iamhere.findViewById(R.id.answer);
                    questionHolder.setText(question);
                    final ImageButton editButton = (ImageButton) qa_iamhere.findViewById(R.id.editButton);
                    editButton.setVisibility(View.GONE);
                    answerHolder.setText(answer.replace("I am here for ", ""));
                    answerHolder.setTypeface(font2);
                    if(commonAnswers.contains(answer))answerHolder.setTextColor(getResources().getColor(R.color.colorAccent));
                    iamherelayout.addView(qa_iamhere);
                }else if(answer.startsWith("I like")){
                    View qa_ilike = getLayoutInflater().inflate(R.layout.quickpeek_layout, ilikelayout, false);
                    final TextView questionHolder = (TextView) qa_ilike.findViewById(R.id.question);
                    final TextView answerHolder = (TextView) qa_ilike.findViewById(R.id.answer);
                    questionHolder.setText(question);
                    final ImageButton editButton = (ImageButton) qa_ilike.findViewById(R.id.editButton);
                    editButton.setVisibility(View.GONE);
                    answerHolder.setText(answer.replace("I like ", ""));
                    answerHolder.setTypeface(font2);
                    if(commonAnswers.contains(answer))answerHolder.setTextColor(getResources().getColor(R.color.colorAccent));
                    ilikelayout.addView(qa_ilike);
                }else if(answer.startsWith("I am")){
                    View qa_iam = getLayoutInflater().inflate(R.layout.quickpeek_layout, iamlayout, false);
                    final TextView questionHolder = (TextView) qa_iam.findViewById(R.id.question);
                    final TextView answerHolder = (TextView) qa_iam.findViewById(R.id.answer);
                    questionHolder.setText(question);
                    final ImageButton editButton = (ImageButton) qa_iam.findViewById(R.id.editButton);
                    editButton.setVisibility(View.GONE);
                    answerHolder.setText(answer.replace("I am ", ""));
                    answerHolder.setTypeface(font2);
                    if(commonAnswers.contains(answer))answerHolder.setTextColor(getResources().getColor(R.color.colorAccent));
                    iamlayout.addView(qa_iam);
                }else if(answer.startsWith("I ")){
                    View qa_i = getLayoutInflater().inflate(R.layout.quickpeek_layout, ilayout, false);
                    final TextView questionHolder = (TextView) qa_i.findViewById(R.id.question);
                    final TextView answerHolder = (TextView) qa_i.findViewById(R.id.answer);
                    questionHolder.setText(question);
                    final ImageButton editButton = (ImageButton) qa_i.findViewById(R.id.editButton);
                    editButton.setVisibility(View.GONE);
                    answerHolder.setText(answer.replace("I ", "").replace("can't", "Can't"));
                    answerHolder.setTypeface(font2);
                    if(commonAnswers.contains(answer))answerHolder.setTextColor(getResources().getColor(R.color.colorAccent));
                    ilayout.addView(qa_i);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject1 = new JSONObject(Commons.thisUser.get_answers2());
            JSONObject jsonObject2 = new JSONObject(userStaticJson);

            JSONArray jsonArray1 = jsonObject1.getJSONArray("questionnaires");
            JSONArray jsonArray2 = jsonObject2.getJSONArray("questionnaires");

            for (int i = 0; i < jsonArray1.length(); i++) {

                JSONObject jsonObject11 = (JSONObject) jsonArray1.get(i);
                JSONObject jsonObject22 = (JSONObject) jsonArray2.get(i);

                String question1=jsonObject11.getString("question");
                String answer1=jsonObject11.getString("answer");

                String question2=jsonObject22.getString("question");
                String answer2=jsonObject22.getString("answer");

                if(answer1.equals(answer2)){
                    View qa_staticq = getLayoutInflater().inflate(R.layout.matched_layout, commonq, false);
                    final TextView questionHolder = (TextView) qa_staticq.findViewById(R.id.question);
                    final TextView answerHolder = (TextView) qa_staticq.findViewById(R.id.answer);
                    questionHolder.setText(question1);
                    answerHolder.setText(answer1);
                    //           Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
                    answerHolder.setTypeface(font);
                    commonq.addView(qa_staticq);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        contain.addView(aboutmeView);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int percentage = (Math.abs(i)) * 100 / mMaxScrollSize;

        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;

//            mProfileImage.animate()
//                    .scaleY(0).scaleX(0)
//                    .setDuration(200)
//                    .start();

            mProfileImage.setVisibility(View.GONE);
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_off);
            mProfileImage.startAnimation(animation);

            profileImage.animate()
                    .scaleY(0).scaleX(0)
                    .setDuration(200)
                    .start();

            name.setVisibility(View.GONE);
            progressText.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            nameLayout.setVisibility(View.VISIBLE);
        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;

//            mProfileImage.animate()
//                    .scaleY(1).scaleX(1)
//                    .start();
            mProfileImage.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
            mProfileImage.startAnimation(animation);

            profileImage.animate()
                    .scaleY(1).scaleX(1)
                    .start();

            name.setVisibility(View.VISIBLE);
            progressText.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            nameLayout.setVisibility(View.GONE);
        }
    }

    public void getPosts() {

        String url = ReqConst.SERVER_URL + "getposts";

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
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("email", Commons.user.get_email());

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);
    }

    public void parseGetUsersResponse(String json) {

        progress_bar.setVisibility(View.GONE);

        try{

            JSONObject response = new JSONObject(json);  Log.d("RESPONSE===>",response.toString());

            int result_code = response.getInt("result_code");

            if(result_code == 0){

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
                    editButton.setVisibility(View.GONE);
                    final ImageButton delButton = (ImageButton) post_layout.findViewById(R.id.deleteButton);
                    delButton.setVisibility(View.GONE);
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

    public void getActives(int user_id) {

        String url = ReqConst.SERVER_URL + "getNotifications";

        progress_bar.setVisibility(View.VISIBLE);

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());
                parseGetUnravelsResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                progress_bar.setVisibility(View.GONE);
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

    public void parseGetUnravelsResponse(String json) {
        progress_bar.setVisibility(View.GONE);
        try{

            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");
            if(result_code == 0) {
                int acts = 0;
                JSONArray data = response.getJSONArray("data");
                Log.d("UNRAVELS===",data.toString());
                for(int i=0; i<data.length(); i++){
                    JSONObject jsonData = (JSONObject) data.get(i);
                    if(jsonData.getString("active").equals("1")){
                        acts = acts + 1;
                    }
                }
                userActs = acts;
                actives.setText(String.valueOf(acts));
                if(Commons.thisUser.get_premium().length() == 0){
                    actives.setVisibility(View.GONE);
                    datinglock.setVisibility(View.VISIBLE);
                }

            }else
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");

        }catch (JSONException e){
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

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}











