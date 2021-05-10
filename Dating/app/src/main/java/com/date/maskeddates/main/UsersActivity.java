package com.date.maskeddates.main;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.RotateDownTransformer;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.cunoraz.gifview.library.GifView;
import com.date.maskeddates.MyApplication;
import com.date.maskeddates.R;
import com.date.maskeddates.classes.ViewPagerCustomDuration;
import com.date.maskeddates.commons.Commons;
import com.date.maskeddates.commons.Constants;
import com.date.maskeddates.commons.ReqConst;
import com.date.maskeddates.models.Conversation;
import com.date.maskeddates.models.Online;
import com.date.maskeddates.models.Post;
import com.date.maskeddates.models.User;
import com.date.maskeddates.preference.PrefConst;
import com.date.maskeddates.preference.Preference;
import com.eyalbira.loadingdots.LoadingDots;
import com.firebase.client.Firebase;
import com.github.mmin18.widget.RealtimeBlurView;
import com.google.android.flexbox.FlexboxLayout;
import com.skyfishjy.library.RippleBackground;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import io.apptik.widget.MultiSlider;
import jp.wasabeef.picasso.transformations.BlurTransformation;

public class UsersActivity extends AppCompatActivity {

    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 50;
    private static boolean mIsAvatarShown = true;
    static ArrayList<User> userList = new ArrayList<>();
    ArrayList<User> userList2 = new ArrayList<>();
    static ArrayList<User> allUserList = new ArrayList<>();
    ArrayList<User> allUserList2 = new ArrayList<>();
    static ArrayList<User> connectedUserList = new ArrayList<>();
    ArrayList<User> connectedUserList2 = new ArrayList<>();
    public ViewPagerCustomDuration pager;
    private PageAdapter adapter;
    EditText searchBox;
    FrameLayout userButton, chatButton, usersButton;
    LoadingDots loadingDots;
    LinearLayout settings, searchBar;

    Button all, connected, newUsers, okay, male, female, both;
    int userF = 0, genderF = 2;
    int maxAge = 70, minAge = 18;
    FrameLayout layout;
    MultiSlider multiSlider;
    TextView minVal, maxVal;

    ArrayList<Conversation> notifications = new ArrayList<>();
    ArrayList<Online> onlines = new ArrayList<>();
    ArrayList<String> emailList = new ArrayList<>();
    ArrayList<String> onlineEmailList = new ArrayList<>();
    FrameLayout noticon;
    TextView count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_users);

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Comfortaa_Bold.ttf");
        ((TextView)findViewById(R.id.title)).setTypeface(font);

        Firebase.setAndroidContext(this);
        notifications.clear();
        emailList.clear();
        onlineEmailList.clear();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });

        noticon = (FrameLayout) findViewById(R.id.badge);
        count = (TextView)findViewById(R.id.count);

        layout = (FrameLayout)findViewById(R.id.layout);

        userF = (int) Preference.getInstance().getValue(getApplicationContext(), PrefConst.SETTING_PROFILE, 0);

        if(Commons.thisUser.get_gender().equals("Male"))
            genderF = (int) Preference.getInstance().getValue(getApplicationContext(), PrefConst.SETTING_GENDER, 1);
        else if(Commons.thisUser.get_gender().equals("Female"))
            genderF = (int) Preference.getInstance().getValue(getApplicationContext(), PrefConst.SETTING_GENDER, 0);
        else
            genderF = (int) Preference.getInstance().getValue(getApplicationContext(), PrefConst.SETTING_GENDER, 2);

        minAge = (int) Preference.getInstance().getValue(getApplicationContext(), PrefConst.SETTING_MIN_AGE, 18);
        maxAge = (int) Preference.getInstance().getValue(getApplicationContext(), PrefConst.SETTING_MAX_AGE, 70);

        multiSlider = (MultiSlider) findViewById(R.id.range_slider);
        multiSlider.setMin(18);
        multiSlider.setMax(70);

        minVal = (TextView)findViewById(R.id.minVal);
        maxVal = (TextView)findViewById(R.id.maxVal);

        minVal.setText(String.valueOf(minAge));
        maxVal.setText(String.valueOf(maxAge));

        multiSlider.getThumb(0).setValue(minAge);
        multiSlider.getThumb(1).setValue(maxAge);

        searchBox = (EditText)findViewById(R.id.search);
        searchBox.setTypeface(font);

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = searchBox.getText().toString().toLowerCase(Locale.getDefault());
                if(userF == 0)
                    filterBySettings(genderF, minAge, maxAge, text, userList2);
                else if(userF == 1)
                    filterBySettings(genderF, minAge, maxAge, text, connectedUserList2);
                else if(userF == 2)
                    filterBySettings(genderF, minAge, maxAge, text, allUserList2);
            }
        });

        settings = ((LinearLayout)findViewById(R.id.settings));
        searchBar = ((LinearLayout)findViewById(R.id.searchBar));

        all = (Button)findViewById(R.id.all_users);
        connected = (Button)findViewById(R.id.connected);
        newUsers = (Button)findViewById(R.id.new_users);

        male = (Button)findViewById(R.id.male);
        female = (Button)findViewById(R.id.female);
        both = (Button)findViewById(R.id.both);

        ((TextView)findViewById(R.id.ageCaption)).setTypeface(font);
        ((TextView)findViewById(R.id.usersCaption)).setTypeface(font);
        ((TextView)findViewById(R.id.genderCaption)).setTypeface(font);

        okay = (Button)findViewById(R.id.okay);
        okay.setTypeface(font);
        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.setVisibility(View.GONE);
                layout.setVisibility(View.GONE);
                Preference.getInstance().put(getApplicationContext(), PrefConst.SETTING_MAX_AGE, maxAge);
                Preference.getInstance().put(getApplicationContext(), PrefConst.SETTING_MIN_AGE, minAge);
                Preference.getInstance().put(getApplicationContext(), PrefConst.SETTING_GENDER, genderF);
                Preference.getInstance().put(getApplicationContext(), PrefConst.SETTING_PROFILE, userF);
                if(userF == 0)
                    filterBySettings(genderF, minAge, maxAge, "", userList2);
                else if(userF == 1)
                    filterBySettings(genderF, minAge, maxAge, "", connectedUserList2);
                else if(userF == 2)
                    filterBySettings(genderF, minAge, maxAge, "", allUserList2);
                saveSettings();
            }
        });

        Button[] buttons = {newUsers, connected, all};

        for(int i=0; i<buttons.length; i++){
            int finalI = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(finalI ==0){
                        buttons[0].setBackgroundResource(R.drawable.red_radio_fill_round);
                        buttons[1].setBackgroundResource(R.drawable.red_light_fill_radio_round);
                        buttons[2].setBackgroundResource(R.drawable.red_light_fill_radio_round);
                        buttons[0].setTextColor(Color.WHITE);
                        buttons[1].setTextColor(getResources().getColor(R.color.colorAccent));
                        buttons[2].setTextColor(getResources().getColor(R.color.colorAccent));
                        userF = 0;
                    }else if(finalI ==1){
                        buttons[1].setBackgroundResource(R.drawable.red_radio_fill_round);
                        buttons[0].setBackgroundResource(R.drawable.red_light_fill_radio_round);
                        buttons[2].setBackgroundResource(R.drawable.red_light_fill_radio_round);
                        buttons[1].setTextColor(Color.WHITE);
                        buttons[0].setTextColor(getResources().getColor(R.color.colorAccent));
                        buttons[2].setTextColor(getResources().getColor(R.color.colorAccent));
                        userF = 1;
                    }else if(finalI ==2){
                        buttons[2].setBackgroundResource(R.drawable.red_radio_fill_round);
                        buttons[0].setBackgroundResource(R.drawable.red_light_fill_radio_round);
                        buttons[1].setBackgroundResource(R.drawable.red_light_fill_radio_round);
                        buttons[2].setTextColor(Color.WHITE);
                        buttons[0].setTextColor(getResources().getColor(R.color.colorAccent));
                        buttons[1].setTextColor(getResources().getColor(R.color.colorAccent));
                        userF = 2;
                    }
                }
            });
        }

        if(userF == 0){
            buttons[0].setBackgroundResource(R.drawable.red_radio_fill_round);
            buttons[1].setBackgroundResource(R.drawable.red_light_fill_radio_round);
            buttons[2].setBackgroundResource(R.drawable.red_light_fill_radio_round);
            buttons[0].setTextColor(Color.WHITE);
            buttons[1].setTextColor(getResources().getColor(R.color.colorAccent));
            buttons[2].setTextColor(getResources().getColor(R.color.colorAccent));
        }else if(userF == 1){
            buttons[1].setBackgroundResource(R.drawable.red_radio_fill_round);
            buttons[0].setBackgroundResource(R.drawable.red_light_fill_radio_round);
            buttons[2].setBackgroundResource(R.drawable.red_light_fill_radio_round);
            buttons[1].setTextColor(Color.WHITE);
            buttons[0].setTextColor(getResources().getColor(R.color.colorAccent));
            buttons[2].setTextColor(getResources().getColor(R.color.colorAccent));
        }else if(userF == 2){
            buttons[2].setBackgroundResource(R.drawable.red_radio_fill_round);
            buttons[0].setBackgroundResource(R.drawable.red_light_fill_radio_round);
            buttons[1].setBackgroundResource(R.drawable.red_light_fill_radio_round);
            buttons[2].setTextColor(Color.WHITE);
            buttons[0].setTextColor(getResources().getColor(R.color.colorAccent));
            buttons[1].setTextColor(getResources().getColor(R.color.colorAccent));
        }

        Button[] buttons2 = {male, female, both};

        for(int i=0; i<buttons2.length; i++){
            int finalI = i;
            buttons2[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(finalI ==0){
                        buttons2[0].setBackgroundResource(R.drawable.red_radio_fill_round);
                        buttons2[1].setBackgroundResource(R.drawable.red_light_fill_radio_round);
                        buttons2[2].setBackgroundResource(R.drawable.red_light_fill_radio_round);
                        buttons2[0].setTextColor(Color.WHITE);
                        buttons2[1].setTextColor(getResources().getColor(R.color.colorAccent));
                        buttons2[2].setTextColor(getResources().getColor(R.color.colorAccent));
                        genderF = 0;
                    }else if(finalI ==1){
                        buttons2[1].setBackgroundResource(R.drawable.red_radio_fill_round);
                        buttons2[0].setBackgroundResource(R.drawable.red_light_fill_radio_round);
                        buttons2[2].setBackgroundResource(R.drawable.red_light_fill_radio_round);
                        buttons2[1].setTextColor(Color.WHITE);
                        buttons2[0].setTextColor(getResources().getColor(R.color.colorAccent));
                        buttons2[2].setTextColor(getResources().getColor(R.color.colorAccent));
                        genderF = 1;
                    }else if(finalI ==2){
                        buttons2[2].setBackgroundResource(R.drawable.red_radio_fill_round);
                        buttons2[0].setBackgroundResource(R.drawable.red_light_fill_radio_round);
                        buttons2[1].setBackgroundResource(R.drawable.red_light_fill_radio_round);
                        buttons2[2].setTextColor(Color.WHITE);
                        buttons2[0].setTextColor(getResources().getColor(R.color.colorAccent));
                        buttons2[1].setTextColor(getResources().getColor(R.color.colorAccent));
                        genderF = 2;
                    }
                }
            });
        }

        if(genderF == 0){
            buttons2[0].setBackgroundResource(R.drawable.red_radio_fill_round);
            buttons2[1].setBackgroundResource(R.drawable.red_light_fill_radio_round);
            buttons2[2].setBackgroundResource(R.drawable.red_light_fill_radio_round);
            buttons2[0].setTextColor(Color.WHITE);
            buttons2[1].setTextColor(getResources().getColor(R.color.colorAccent));
            buttons2[2].setTextColor(getResources().getColor(R.color.colorAccent));
        }else if(genderF == 1){
            buttons2[1].setBackgroundResource(R.drawable.red_radio_fill_round);
            buttons2[0].setBackgroundResource(R.drawable.red_light_fill_radio_round);
            buttons2[2].setBackgroundResource(R.drawable.red_light_fill_radio_round);
            buttons2[1].setTextColor(Color.WHITE);
            buttons2[0].setTextColor(getResources().getColor(R.color.colorAccent));
            buttons2[2].setTextColor(getResources().getColor(R.color.colorAccent));
        }else if(genderF == 2){
            buttons2[2].setBackgroundResource(R.drawable.red_radio_fill_round);
            buttons2[0].setBackgroundResource(R.drawable.red_light_fill_radio_round);
            buttons2[1].setBackgroundResource(R.drawable.red_light_fill_radio_round);
            buttons2[2].setTextColor(Color.WHITE);
            buttons2[0].setTextColor(getResources().getColor(R.color.colorAccent));
            buttons2[1].setTextColor(getResources().getColor(R.color.colorAccent));
        }

        userButton = (FrameLayout) findViewById(R.id.userbutton);
        chatButton = (FrameLayout) findViewById(R.id.chatbutton);
        usersButton = (FrameLayout) findViewById(R.id.usersbutton);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NotificationsActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
            }
        });
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyWallActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
            }
        });

        loadingDots = (LoadingDots)findViewById(R.id.progressBar);

        userList.clear();
        allUserList.clear();
        connectedUserList.clear();

        adapter = new PageAdapter(getFragmentManager());
        pager = (ViewPagerCustomDuration) findViewById(R.id.viewpager);
        pager.setScrollDuration(800);
        pager.setAdapter(adapter);
        pager.setPageTransformer(true, new RotateDownTransformer());

        ((ImageView)findViewById(R.id.left_arrow)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pager.getCurrentItem() > 0) pager.setCurrentItem(pager.getCurrentItem() - 1);
            }
        });

        ((ImageView)findViewById(R.id.right_arrow)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pager.getCurrentItem() < userList.size()) pager.setCurrentItem(pager.getCurrentItem() + 1);
            }
        });

        multiSlider.setOnThumbValueChangeListener(new MultiSlider.SimpleChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int
                    thumbIndex, int value) {
                if (thumbIndex == 0) {
                    minVal.setText(String.valueOf(value));
                    minAge = value;
                } else {
                    maxVal.setText(String.valueOf(value));
                    maxAge = value;
                }
            }
        });

        Timer timer = new Timer();
        timer.schedule(doAsynchronousTask3, 0, 2000);


        getAllUsers();
        getActives(Commons.thisUser.get_idx());

    }

    Handler mHandler = new Handler();

    TimerTask doAsynchronousTask3 = new TimerTask() {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(Commons.notifications.size() > 0) {
                        noticon.setVisibility(View.VISIBLE);
                        count.setText(String.valueOf(Commons.notifications.size()));
                    }else {
                        noticon.setVisibility(View.GONE);
                    }
                }
            });
        }
    };

    public void showSettings(View view){
        settings.setVisibility(View.VISIBLE);
    }

    public static class UserFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener  {

        private static final String EXTRA_POSITION = "EXTRA_POSITION";
        private ImageView mProfileImage, profileImage, datinglock, no_result;
        private int mMaxScrollSize;
        TextView name, name2, gender, age, location, progressText, aboutme, posts, photos;
        RealtimeBlurView blurView;
        ProgressBar progressBar;
        LoadingDots progress_bar;
        int percent = 0;
        LinearLayout nameLayout, dynamical, staticq, postLayout, commonq;
        TextView match, viewalltitle, lastlogin, actives;
        EditText inputbox;
        Button viewAll;
        NestedScrollView contain;
        View aboutmeView, postView, layout;
        String currentUserEmail = "";
        ImageButton loveButton;
        Firebase ref, ref2, ref3;
        String otherID, thisID;
        CircleImageView picture;
        FrameLayout notibox;
        TextView sendbutton, cancelbutton;
        LinearLayout alertbox;
        TextView premium, enddates;
        TextView matchlabel, matchlabel2;
        boolean _isActive = false;
        String userStaticJson = "";
        RelativeLayout matchframe;
        View aboutmeIndicator, photosIndicator, postsIndicator;
        RippleBackground rippleBackground, rippleBackground2;
        GifView love;
        Typeface font, font2;
        FlexboxLayout iamlayout, iamherelayout, ilikelayout, ilayout;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            Firebase.setAndroidContext(getActivity());

            final int position = getArguments().getInt(EXTRA_POSITION);
            final FrameLayout parent = (FrameLayout) inflater.inflate(R.layout.fragment_profile, container, false);

            font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Comfortaa_Bold.ttf");
            font2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Comfortaa_Regular.ttf");

            love = (GifView)parent.findViewById(R.id.love);
            rippleBackground2 = (RippleBackground)parent.findViewById(R.id.content2);

            contain = (NestedScrollView) parent.findViewById(R.id.contain);
            aboutme = (TextView) parent.findViewById(R.id.aboutme);
            posts = (TextView) parent.findViewById(R.id.posts);
            photos = (TextView) parent.findViewById(R.id.photos);

            aboutmeIndicator = ((View)parent.findViewById(R.id.indicator_aboutme));
            photosIndicator = ((View)parent.findViewById(R.id.indicator_photos));
            postsIndicator = ((View)parent.findViewById(R.id.indicator_posts));

            matchframe = (RelativeLayout)parent.findViewById(R.id.matchframe);
            datinglock = (ImageView)parent.findViewById(R.id.datinglock);
            datinglock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), PremiumGuideActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
            });

            matchlabel = (TextView) parent.findViewById(R.id.matchlabel);
            matchlabel2 = (TextView) parent.findViewById(R.id.matchlabel2);

            progress_bar = (LoadingDots) parent.findViewById(R.id.progress_bar);

            loveButton = (ImageButton) parent.findViewById(R.id.loveButton);
            checkLove(userList.get(position - 1).get_idx());

            lastlogin = (TextView) parent.findViewById(R.id.lastlogin);
            actives = (TextView) parent.findViewById(R.id.actives);

            AppBarLayout appbarLayout = (AppBarLayout) parent.findViewById(R.id.materialup_appbar);
            mProfileImage = (ImageView) parent.findViewById(R.id.materialup_profile_image);
            profileImage = (CircleImageView) parent.findViewById(R.id.materialup_profile_image2);
            getUnravel(userList.get(position - 1).get_idx(), position);

            Log.d("USERID===>", String.valueOf(userList.get(position - 1).get_idx()));

            match = (TextView) parent.findViewById(R.id.match);
            nameLayout = (LinearLayout) parent.findViewById(R.id.nameLayout);

            name = (TextView) parent.findViewById(R.id.name);
            location = (TextView) parent.findViewById(R.id.location);
            name2 = (TextView) parent.findViewById(R.id.name2);
            gender = (TextView) parent.findViewById(R.id.gender);
            age = (TextView) parent.findViewById(R.id.age);

            name.setTypeface(font);
            location.setTypeface(font2);
            name2.setTypeface(font);
            gender.setTypeface(font);
            age.setTypeface(font);
            lastlogin.setTypeface(font2);
            ((TextView)parent.findViewById(R.id.lastseen)).setTypeface(font2);

            Log.d("DATE===>", userList.get(position - 1).get_lastlogin());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
            String myDate = dateFormat.format(new Date(Long.parseLong(userList.get(position - 1).get_lastlogin())));
            lastlogin.setText(myDate);

            getActives(userList.get(position - 1).get_idx());

            name.setText(rename(userList.get(position - 1).get_name()));
            name2.setText(rename(userList.get(position - 1).get_name()));

            currentUserEmail = userList.get(position - 1).get_email();

            gender.setText(userList.get(position - 1).get_gender());
            age.setText(userList.get(position - 1).get_age());
            location.setText(userList.get(position - 1).get_address());

            progressBar = (ProgressBar) parent.findViewById(R.id.progressBar);
            progressText = (TextView) parent.findViewById(R.id.txtProgress);

            userStaticJson = userList.get(position - 1).get_answers2();
            getMatchScore(Commons.thisUser.get_answers2(), userList.get(position - 1).get_answers2());

            Toolbar toolbar = (Toolbar) parent.findViewById(R.id.materialup_toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });

            inflater = LayoutInflater.from(getActivity());
            aboutmeView = inflater.inflate(R.layout.fragment_aboutme, null);
            dynamical = (LinearLayout)aboutmeView.findViewById(R.id.dynamical);
            staticq = (LinearLayout)aboutmeView.findViewById(R.id.staticq);
            commonq = (LinearLayout)aboutmeView.findViewById(R.id.commonq);
            viewAll = (Button)aboutmeView.findViewById(R.id.viewall);
            viewalltitle = (TextView) aboutmeView.findViewById(R.id.viewalltitle);

            iamlayout = (FlexboxLayout)aboutmeView.findViewById(R.id.iamlayout);
            ilikelayout = (FlexboxLayout)aboutmeView.findViewById(R.id.ilikelayout);
            iamherelayout = (FlexboxLayout)aboutmeView.findViewById(R.id.iamherelayout);
            ilayout = (FlexboxLayout)aboutmeView.findViewById(R.id.ilayout);

            viewAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    staticq.setVisibility(View.VISIBLE);
                    ((TextView)parent.findViewById(R.id.quickpeektitle)).setVisibility(View.VISIBLE);
                    viewAll.setVisibility(View.GONE);
//                    viewalltitle.setVisibility(View.GONE);
                }
            });

            parseQAJson(userList.get(position - 1).get_answers(), userList.get(position - 1).get_answers2());

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
                    parseQAJson(userList.get(position - 1).get_answers(), userList.get(position - 1).get_answers2());
                }
            });

            photos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Commons.thisUser.get_premium().length() == 0){
                        Intent intent = new Intent(getActivity(), PremiumGuideActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                        return;
                    }
                    clearTabColors();
                    photos.setTextColor(Color.BLACK);
                    photosIndicator.setVisibility(View.VISIBLE);
                }
            });

            inflater = LayoutInflater.from(getActivity());
            postView = inflater.inflate(R.layout.fragment_posts, null);
            postLayout = (LinearLayout)postView.findViewById(R.id.posts);
            no_result = (ImageView) postView.findViewById(R.id.no_result);
            Button postButton = (Button) postView.findViewById(R.id.newpost);
            postButton.setVisibility(View.GONE);

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

            notibox = (FrameLayout) parent.findViewById(R.id.notibox);
            layout = ((View)parent.findViewById(R.id.layout));
            inputbox = (EditText) parent.findViewById(R.id.inputBox);
            picture = (CircleImageView) parent.findViewById(R.id.picture);

            Picasso.with(getActivity())
                    .load(userList.get(position - 1).get_photoUrl())
                    .transform(new BlurTransformation(getActivity(), 25, 1))
                    .error(R.drawable.q9)
                    .placeholder(R.drawable.q9)
                    .into(picture);

            sendbutton = (TextView) parent.findViewById(R.id.send_button);
            inputbox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(inputbox.getText().toString().trim().length() > 0) sendbutton.setVisibility(View.VISIBLE);
                    else sendbutton.setVisibility(View.GONE);
                }
            });

            sendbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(inputbox.getText().length() > 0){
                        ref = new Firebase(ReqConst.FIREBASE_URL + "notification/" + String.valueOf(userList.get(position - 1).get_idx()) + "/" + String.valueOf(Commons.thisUser.get_idx()));
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("sender_id", String.valueOf(Commons.thisUser.get_idx()));
                        map.put("sender_name", Commons.thisUser.get_name());
                        map.put("sender_email", Commons.thisUser.get_email());
                        map.put("sender_photo", Commons.thisUser.get_photoUrl());
                        map.put("time", String.valueOf(new Date().getTime()));
                        map.put("msg", inputbox.getText().toString());
                        ref.removeValue();
                        ref.push().setValue(map);

                        ref2 = new Firebase(ReqConst.FIREBASE_URL + "message/" + String.valueOf(userList.get(position - 1).get_idx()) + "_" + String.valueOf(Commons.thisUser.get_idx()));
                        ref3 = new Firebase(ReqConst.FIREBASE_URL + "message/" + String.valueOf(Commons.thisUser.get_idx()) + "_" +  String.valueOf(userList.get(position - 1).get_idx()));
                        Map<String, String> map2 = new HashMap<String, String>();
                        map2.put("message", inputbox.getText().toString());
                        map2.put("istyping", "false");
                        map2.put("online", "true");
                        map2.put("time", String.valueOf(new Date().getTime()));
                        map2.put("image", "");
                        map2.put("video", "");
                        map2.put("lat", "");
                        map2.put("lon", "");
                        map2.put("email", Commons.thisUser.get_email());
                        map2.put("name", Commons.thisUser.get_name());
                        map2.put("photo", Commons.thisUser.get_photoUrl());

                        ref2.push().setValue(map2);
                        ref3.push().setValue(map2);

                        sendNotification(String.valueOf(userList.get(position - 1).get_idx()));
                    }else {
                        showToast("Please write something...");
                    }
                }
            });
            cancelbutton = (TextView) parent.findViewById(R.id.cancel_button);
            cancelbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            layout.setVisibility(View.GONE);
                        }
                    }, 500);
                    notibox.setVisibility(View.GONE);
                    Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_off);
                    notibox.startAnimation(animation);
                    inputbox.setText("");
                }
            });

            alertbox = (LinearLayout)parent.findViewById(R.id.alertbox);
            premium = (TextView) parent.findViewById(R.id.premium) ;
            enddates = (TextView) parent.findViewById(R.id.enddates);
            enddates.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertbox.setVisibility(View.GONE);
                    layout.setVisibility(View.GONE);
                    Intent intent = new Intent(getActivity(), EndDatesActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(0,0);
                }
            });

            premium.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertbox.setVisibility(View.GONE);
                    layout.setVisibility(View.GONE);
                    Intent intent = new Intent(getActivity(), PremiumGuideActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(0,0);
                }
            });

            rippleBackground=(RippleBackground)parent.findViewById(R.id.content);

            loveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rippleBackground.startRippleAnimation();
                    rippleBackground.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            checkNotification(userList.get(position - 1).get_idx());
                        }
                    },1000);
                }
            });

            appbarLayout.addOnOffsetChangedListener(this);
            mMaxScrollSize = appbarLayout.getTotalScrollRange();

            return parent;
        }

        private void getMatchScore(String me_json, String user_json){
            try {
                ArrayList<String> commonlist = new ArrayList<>();
                JSONObject jsonObject1 = new JSONObject(me_json);
                JSONObject jsonObject2 = new JSONObject(user_json);

                JSONArray jsonArray1 = jsonObject1.getJSONArray("questionnaires");
                JSONArray jsonArray2 = jsonObject2.getJSONArray("questionnaires");

                Log.d("CommonSize===>", String.valueOf(jsonArray1.length()) + "/" + String.valueOf(jsonArray2.length()));

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

                    View qa_dynamical = getActivity().getLayoutInflater().inflate(R.layout.qa_layout, dynamical, false);
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
                        View qa_iamhere = getActivity().getLayoutInflater().inflate(R.layout.quickpeek_layout, iamherelayout, false);
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
                        View qa_ilike = getActivity().getLayoutInflater().inflate(R.layout.quickpeek_layout, ilikelayout, false);
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
                        View qa_iam = getActivity().getLayoutInflater().inflate(R.layout.quickpeek_layout, iamlayout, false);
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
                        View qa_i = getActivity().getLayoutInflater().inflate(R.layout.quickpeek_layout, ilayout, false);
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

            contain.addView(aboutmeView);
        }

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
            if (mMaxScrollSize == 0)
                mMaxScrollSize = appBarLayout.getTotalScrollRange();

            int percentage = (Math.abs(i)) * 100 / mMaxScrollSize;

            if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR - 20 && mIsAvatarShown) {
                mIsAvatarShown = false;

                mProfileImage.setVisibility(View.GONE);
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_off);
                mProfileImage.startAnimation(animation);

                name.setVisibility(View.GONE);
                matchframe.setVisibility(View.GONE);
                matchlabel.setVisibility(View.GONE);
                nameLayout.setVisibility(View.VISIBLE);
            }

            if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR  - 10 && !mIsAvatarShown) {
                mIsAvatarShown = true;

                mProfileImage.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade);
                mProfileImage.startAnimation(animation);

                name.setVisibility(View.VISIBLE);
                matchframe.setVisibility(View.VISIBLE);
                matchlabel.setVisibility(View.VISIBLE);
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

                    parseGetPostsResponse(response);

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

                    params.put("email", currentUserEmail);

                    return params;
                }
            };

            post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                    0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            MyApplication.getInstance().addToRequestQueue(post, url);
        }

        public void parseGetPostsResponse(String json) {

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

                        View post_layout = getActivity().getLayoutInflater().inflate(R.layout.posts_layout, postLayout, false);
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

        public void checkNotification(int user_id) {

            String url = ReqConst.SERVER_URL + "checkNotification";

    //        progress_bar.setVisibility(View.VISIBLE);

            StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    VolleyLog.v("Response:%n %s", response.toString()); Log.d("GETPOSTS===>",response.toString());
                    parseCheckNotificationResponse(response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.d("debug", error.toString());
       //             progress_bar.setVisibility(View.GONE);
                    rippleBackground.stopRippleAnimation();
                    rippleBackground.setVisibility(View.GONE);
                    showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();

                    params.put("user_id", String.valueOf(user_id));
                    params.put("me_id", String.valueOf(Commons.thisUser.get_idx()));

                    return params;
                }
            };

            post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                    0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            MyApplication.getInstance().addToRequestQueue(post, url);
        }

        public void parseCheckNotificationResponse(String json) {

   //         progress_bar.setVisibility(View.GONE);
            rippleBackground.stopRippleAnimation();
            rippleBackground.setVisibility(View.GONE);

            try{

                JSONObject response = new JSONObject(json);  Log.d("RESPONSE===>",response.toString());

                int result_code = response.getInt("result_code");

                if(result_code == 0){
                    if(response.getString("status").equals("accepted") || response.getString("status").equals("normal"))
                        showToast("You are on date with this person.");
                    else showToast("You have already contacted this person.");

                }else if(result_code == 1){

                    love.setVisibility(View.VISIBLE);
                    rippleBackground2.setVisibility(View.VISIBLE);
                    rippleBackground2.startRippleAnimation();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rippleBackground2.stopRippleAnimation();
                            rippleBackground2.setVisibility(View.GONE);
                            love.setVisibility(View.GONE);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    layout.setVisibility(View.VISIBLE);
                                }
                            }, 100);
                            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_in);
                            notibox.startAnimation(animation);
                            notibox.setVisibility(View.VISIBLE);
                            inputbox.setFocusable(true);
                            inputbox.setCursorVisible(true);
                            inputbox.setFocusedByDefault(true);
                            inputbox.requestFocus();
                        }
                    }, 1500);
                }
                else {
                    showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
                }
            }catch (JSONException e){
                e.printStackTrace();
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
            }
        }

        public void checkLove(int user_id) {

            String url = ReqConst.SERVER_URL + "checkNotification";

            StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    VolleyLog.v("Response:%n %s", response.toString()); Log.d("GETPOSTS===>",response.toString());
                    parseCheckLoveResponse(response);

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

                    params.put("user_id", String.valueOf(user_id));
                    params.put("me_id", String.valueOf(Commons.thisUser.get_idx()));

                    return params;
                }
            };

            post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                    0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            MyApplication.getInstance().addToRequestQueue(post, url);
        }

        public void parseCheckLoveResponse(String json) {

            try{

                JSONObject response = new JSONObject(json);

                int result_code = response.getInt("result_code");

                if(result_code == 0){
                    if(response.getString("status").equals("declined"))
                        loveButton.setImageResource(R.drawable.love_gray);
                    else loveButton.setImageResource(R.drawable.love);
                }else if(result_code == 1){
                    loveButton.setImageResource(R.drawable.love_red);
                }
                else {

                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }


        public void showToast(String content){
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.custom_toast, null);
            TextView textView=(TextView)dialogView.findViewById(R.id.text);
            textView.setText(content);
            Toast toast=new Toast(getActivity());
            toast.setView(dialogView);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        }

        public void sendNotification(String user_id) {

            String url = ReqConst.SERVER_URL + "sendNotification";

            progress_bar.setVisibility(View.VISIBLE);

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
                    progress_bar.setVisibility(View.GONE);
                    showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();

                    params.put("user_id", user_id);
                    params.put("sender_id", String.valueOf(Commons.thisUser.get_idx()));
                    params.put("sender_name", Commons.thisUser.get_name());
                    params.put("sender_email", Commons.thisUser.get_email());
                    params.put("sender_photo", Commons.thisUser.get_photoUrl());
                    params.put("notitext", StringEscapeUtils.escapeJava(inputbox.getText().toString()));
                    params.put("notitime", String.valueOf(new Date().getTime()));
                    params.put("option", "requested");

                    return params;
                }
            };

            post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                    0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            MyApplication.getInstance().addToRequestQueue(post, url);

        }

        public void parseRestUrlsResponse(String json) {

            progress_bar.setVisibility(View.GONE);

            try {

                JSONObject response = new JSONObject(json);   Log.d("RESPONSE=====> :",response.toString());

                String success = response.getString("result_code");

                Log.d("Result_Code===> :",success);

                if (success.equals("0")) {
                    notibox.setVisibility(View.GONE);
                    Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_off);
                    notibox.startAnimation(animation);
                    layout.setVisibility(View.GONE);
                    loveButton.setImageResource(R.drawable.love);
                    inputbox.setText("");
                    showToast("Sent!");
                }
                else {
                    showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
                }

            } catch (JSONException e) {
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
                e.printStackTrace();
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

                JSONObject response = new JSONObject(json);  Log.d("RESPONSE===>",response.toString());

                int result_code = response.getInt("result_code");
                if(result_code == 0) {
                    int acts = 0;
                    JSONArray data = response.getJSONArray("data");
                    for(int i=0; i<data.length(); i++){
                        JSONObject jsonData = (JSONObject) data.get(i);
                        if(jsonData.getString("active").equals("1")){
                            acts = acts + 1;
                        }
                    }
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

        public void getUnravel(int user_id, int position) {

            String url = ReqConst.SERVER_URL + "getNoti";

            StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    VolleyLog.v("Response:%n %s", response.toString());
                    parseCheckActiveResponse(response, position);
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

                    params.put("user_id", String.valueOf(user_id));
                    params.put("me_id", String.valueOf(Commons.thisUser.get_idx()));

                    return params;
                }
            };

            post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                    0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            MyApplication.getInstance().addToRequestQueue(post, url);
        }

        public void parseCheckActiveResponse(String json, int position) {

            int score = 0;
            try{

                JSONObject response = new JSONObject(json);

                int result_code = response.getInt("result_code");
                if(result_code == 0) {
                    JSONArray data = response.getJSONArray("noti");

                    Log.d("UNRAVELS===",data.toString());
                    JSONObject jsonData = (JSONObject) data.get(0);

                    int unraveled = Integer.parseInt(jsonData.getString("unraveled"));
                    if(unraveled == 4){
                        Picasso.with(getActivity())
                                .load(userList.get(position - 1).get_photoUrl())
                                .error(R.drawable.q9)
                                .placeholder(R.drawable.q9)
                                .into(profileImage);
                        score = 4;
                    }else {
                        Picasso.with(getActivity())
                                .load(userList.get(position - 1).get_photoUrl())
                                .transform(new BlurTransformation(getActivity(), 25, 1))
                                .error(R.drawable.q9)
                                .placeholder(R.drawable.q9)
                                .into(profileImage);
                        score = unraveled;
                    }

                }

            }catch (JSONException e){
                e.printStackTrace();
                try{
                    Picasso.with(getActivity())
                            .load(userList.get(position - 1).get_photoUrl())
                            .transform(new BlurTransformation(getActivity(), 25, 1))
                            .error(R.drawable.q9)
                            .placeholder(R.drawable.q9)
                            .into(profileImage);
                }catch (IndexOutOfBoundsException ex){
                    ex.printStackTrace();
                }catch (NullPointerException ex){
                    ex.printStackTrace();
                }
                score = 0;
            }

            int finalScore = score;
            profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ViewProfilePictureActivity.class);
                    intent.putExtra("photo_url", userList.get(position - 1).get_photoUrl());
                    intent.putExtra("score", finalScore);
                    startActivity(intent);
                    getActivity().overridePendingTransition(0,0);
                }
            });
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
    }

    private static final class PageAdapter extends FragmentStatePagerAdapter {

        private PageAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }


        @Override
        public Fragment getItem(int position) {
            final Bundle bundle = new Bundle();
            bundle.putInt(UserFragment.EXTRA_POSITION, position + 1);

            final UserFragment fragment = new UserFragment();
            fragment.setArguments(bundle);

            return fragment;
        }

        @Override
        public int getCount() {
            return userList.size();
        }
    }

    private void filterBySettings(final int gender, final int minAge, final int maxAge, String charText, ArrayList<User> datas){
        ArrayList<User> _datas = new ArrayList<>();
        ArrayList<User> _alldatas = new ArrayList<>();

        _alldatas.addAll(datas);
        charText = charText.toLowerCase();
        userList.clear();
        _datas.clear();

        if(charText.length() == 0){
//            userList.addAll(_alldatas);
            for (User user : _alldatas){
                if (user instanceof User) {
                    int age = Integer.parseInt(user.get_age());
                    if (age >= minAge && age <= maxAge)
                        if(gender == 2) {
                            _datas.add(user);
                        }else if(gender == 0){
                            if(user.get_gender().equals("Male"))
                                _datas.add(user);
                        }else if(gender == 1){
                            if(user.get_gender().equals("Female"))
                                _datas.add(user);
                        }
                }
            }
            userList.addAll(_datas);
        }else {
            for (User user : _alldatas){

                if (user instanceof User) {
                    int age = Integer.parseInt(user.get_age());
                    String value = ((User) user).get_name().toLowerCase();
                    if (value.contains(charText)) {
                        if (age >= minAge && age <= maxAge)
                            if(gender == 2) {
                                _datas.add(user);
                            }else if(gender == 0){
                                if(user.get_gender().equals("Male"))
                                    _datas.add(user);
                            }else if(gender == 1){
                                if(user.get_gender().equals("Female"))
                                    _datas.add(user);
                            }
                    }
                    else {
                        value = ((User) user).get_address().toLowerCase();
                        if (value.contains(charText)) {
                            if (age >= minAge && age <= maxAge)
                                if(gender == 2) {
                                    _datas.add(user);
                                }else if(gender == 0){
                                    if(user.get_gender().equals("Male"))
                                        _datas.add(user);
                                }else if(gender == 1){
                                    if(user.get_gender().equals("Female"))
                                        _datas.add(user);
                                }
                        }
                    }
                }
            }
            userList.addAll(_datas);
        }

        if(userList.isEmpty()){
            ((ImageView)findViewById(R.id.left_arrow)).setVisibility(View.GONE);
            ((ImageView)findViewById(R.id.right_arrow)).setVisibility(View.GONE);
            ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
        }else {
            ((ImageView)findViewById(R.id.left_arrow)).setVisibility(View.VISIBLE);
            ((ImageView)findViewById(R.id.right_arrow)).setVisibility(View.VISIBLE);
            ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);
        }

        adapter = new PageAdapter(getFragmentManager());
        pager = (ViewPagerCustomDuration) findViewById(R.id.viewpager);
        pager.setAdapter(adapter);
        pager.setPageTransformer(true, new RotateDownTransformer());

    }


    public void getAllUsers() {

        String url = ReqConst.SERVER_URL + "getAllUsers";

        loadingDots.setVisibility(View.VISIBLE);

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString()); Log.d("GETALL===>",response.toString());

                parseGetUsersResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());

                loadingDots.setVisibility(View.GONE);
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again. ... ");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                Log.d("userId+++", String.valueOf(Commons.thisUser.get_idx()));
                params.put("user_id", String.valueOf(Commons.thisUser.get_idx()));

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);
    }

    public void parseGetUsersResponse(String json) {

        loadingDots.setVisibility(View.GONE);

        try{

            JSONObject response = new JSONObject(json);  Log.d("ALLUSERS+++",response.toString());

            int result_code = response.getInt("result_code");

            if(result_code == 0){

                JSONArray users = response.getJSONArray("users");
//                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFO);
                Log.d("USERS===",users.toString());

                for (int i = 0; i < users.length(); i++) {

                    JSONObject jsonUser = (JSONObject) users.get(i);

                    User user = new User();

                    user.set_idx(jsonUser.getInt("id"));
                    user.set_name(jsonUser.getString("name"));
                    user.set_email(jsonUser.getString("email"));
                    user.set_gender(jsonUser.getString("gender"));
                    user.set_age(jsonUser.getString("age"));
                    user.set_photoUrl(jsonUser.getString("photo_url"));
                    user.set_fbPhoto(jsonUser.getString("fb_photo"));
                    user.set_phone(jsonUser.getString("phone"));
                    user.set_address(jsonUser.getString("address"));
                    user.set_lat(jsonUser.getString("lat"));
                    user.set_lng(jsonUser.getString("lng"));
                    user.set_answers(jsonUser.getString("answers"));
                    user.set_answers2(jsonUser.getString("answers2"));
                    user.set_premium(jsonUser.getString("premium"));
                    user.set_photos(jsonUser.getString("photos"));
                    user.set_photo_unlock(jsonUser.getString("photo_unlock"));
                    user.set_selfie_approved(jsonUser.getString("selfie_approved"));

                    user.set_lastlogin(jsonUser.getString("last_login"));
                    user.set_actives(jsonUser.getInt("actives"));

                    // except me
                    if (user.get_idx() == Commons.thisUser.get_idx())
                        continue;

                    if(jsonUser.getString("password").equals("deactivated")) continue;

                    if(user.get_address().length() == 0 || user.get_answers().length() == 0 || user.get_answers2().length() == 0)
                        continue;

                    userList.add(user);
                }

                userList2.clear();
                userList2.addAll(userList);

//                if(userList.isEmpty()){
//                    ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
//                    ((ImageView)findViewById(R.id.left_arrow)).setVisibility(View.GONE);
//                    ((ImageView)findViewById(R.id.right_arrow)).setVisibility(View.GONE);
//                }else {
//                    ((ImageView)findViewById(R.id.left_arrow)).setVisibility(View.VISIBLE);
//                    ((ImageView)findViewById(R.id.right_arrow)).setVisibility(View.VISIBLE);
//                    ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);
//                }
//                adapter = new PageAdapter(getFragmentManager());
//                pager = (ViewPagerCustomDuration) findViewById(R.id.viewpager);
//                pager.setAdapter(adapter);
//                pager.setPageTransformer(true, new RotateDownTransformer());

                JSONArray allusers = response.getJSONArray("allusers");
//                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFO);
                Log.d("USERS===",allusers.toString());

                for (int i = 0; i < allusers.length(); i++) {

                    JSONObject jsonUser = (JSONObject) allusers.get(i);

                    User user = new User();

                    user.set_idx(jsonUser.getInt("id"));
                    user.set_name(jsonUser.getString("name"));   Log.d("NAME+++++", user.get_name());
                    user.set_email(jsonUser.getString("email"));
                    user.set_gender(jsonUser.getString("gender"));
                    user.set_age(jsonUser.getString("age"));
                    user.set_photoUrl(jsonUser.getString("photo_url"));
                    user.set_fbPhoto(jsonUser.getString("fb_photo"));
                    user.set_phone(jsonUser.getString("phone"));
                    user.set_address(jsonUser.getString("address"));
                    user.set_lat(jsonUser.getString("lat"));
                    user.set_lng(jsonUser.getString("lng"));
                    user.set_answers(jsonUser.getString("answers"));
                    user.set_answers2(jsonUser.getString("answers2"));
                    user.set_premium(jsonUser.getString("premium"));
                    user.set_photos(jsonUser.getString("photos"));
                    user.set_photo_unlock(jsonUser.getString("photo_unlock"));
                    user.set_selfie_approved(jsonUser.getString("selfie_approved"));

                    user.set_lastlogin(jsonUser.getString("last_login"));
                    user.set_actives(jsonUser.getInt("actives"));

                    // except me
                    if (user.get_idx() == Commons.thisUser.get_idx())
                        continue;

                    if(jsonUser.getString("password").equals("deactivated")) continue;

                    if(user.get_address().length() == 0 || user.get_answers().length() == 0 || user.get_answers2().length() == 0)
                        continue;

                    allUserList.add(user);
                }

                allUserList2.clear();
                allUserList2.addAll(allUserList);

                JSONArray connecteds = response.getJSONArray("contacted");
//                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFO);
                Log.d("USERS===",connecteds.toString());

                for (int i = 0; i < connecteds.length(); i++) {

                    JSONObject jsonUser = (JSONObject) connecteds.get(i);

                    User user = new User();

                    user.set_idx(jsonUser.getInt("id"));
                    user.set_name(jsonUser.getString("name"));
                    user.set_email(jsonUser.getString("email"));
                    user.set_gender(jsonUser.getString("gender"));
                    user.set_age(jsonUser.getString("age"));
                    user.set_photoUrl(jsonUser.getString("photo_url"));
                    user.set_fbPhoto(jsonUser.getString("fb_photo"));
                    user.set_phone(jsonUser.getString("phone"));
                    user.set_address(jsonUser.getString("address"));
                    user.set_lat(jsonUser.getString("lat"));
                    user.set_lng(jsonUser.getString("lng"));
                    user.set_answers(jsonUser.getString("answers"));
                    user.set_answers2(jsonUser.getString("answers2"));
                    user.set_premium(jsonUser.getString("premium"));
                    user.set_photos(jsonUser.getString("photos"));
                    user.set_photo_unlock(jsonUser.getString("photo_unlock"));
                    user.set_selfie_approved(jsonUser.getString("selfie_approved"));

                    user.set_lastlogin(jsonUser.getString("last_login"));
                    user.set_actives(jsonUser.getInt("actives"));

                    // except me
                    if (user.get_idx() == Commons.thisUser.get_idx())
                        continue;

                    if(jsonUser.getString("password").equals("deactivated")) continue;

                    if(user.get_address().length() == 0 || user.get_answers().length() == 0 || user.get_answers2().length() == 0)
                        continue;

                    connectedUserList.add(user);
                }

                connectedUserList2.clear();
                connectedUserList2.addAll(connectedUserList);

                if(userF == 0)
                    filterBySettings(genderF, minAge, maxAge, "", userList2);
                else if(userF == 1)
                    filterBySettings(genderF, minAge, maxAge, "", connectedUserList2);
                else if(userF == 2)
                    filterBySettings(genderF, minAge, maxAge, "", allUserList2);

            } else {
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again. ... ");
            }
        }catch (JSONException e){
            e.printStackTrace();
            showToast("We can not detect any internet connectivity. Please check your internet connection and try again. ... ");
        }
    }

    public void showToast(String content){
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_toast, null);
        TextView textView=(TextView)dialogView.findViewById(R.id.text);
        textView.setText(content);
        Toast toast=new Toast(this);
        toast.setView(dialogView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    public void getActives(int user_id) {

        String url = ReqConst.SERVER_URL + "getNotifications";
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
                Log.d("ACTIVES===>", String.valueOf(acts));
                Commons.thisUser.set_actives(acts);

            }else
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");

        }catch (JSONException e){
            e.printStackTrace();
        }
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

    public void saveSettings() {

        String url = ReqConst.SERVER_URL + "saveSettings";

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

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("member_email", Commons.thisUser.get_email());
                params.put("max_age", String.valueOf(maxAge));
                params.put("min_age", String.valueOf(minAge));
                params.put("profile", String.valueOf(userF));
                params.put("gender", String.valueOf(genderF));

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseRestUrlsResponse(String json) {

        try {

            JSONObject response = new JSONObject(json);

            String success = response.getString("result_code");

            Log.d("Result_Code===> :",success);

            if (success.equals("0")) {

            }
            else {

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if(settings.getVisibility()== View.VISIBLE){
            settings.setVisibility(View.GONE);
        }else {
            Intent intent = new Intent(getApplicationContext(), NotificationsActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(0,0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doAsynchronousTask3.cancel();
    }

    @Override
    public void finish() {
        super.finish();
        doAsynchronousTask3.cancel();
    }
}

































