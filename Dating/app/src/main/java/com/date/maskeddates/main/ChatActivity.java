package com.date.maskeddates.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.ayz4sci.androidfactory.permissionhelper.PermissionHelper;
import com.date.maskeddates.Config.MapUtils;
import com.date.maskeddates.MyApplication;
import com.date.maskeddates.R;
import com.date.maskeddates.commons.Commons;
import com.date.maskeddates.commons.Constants;
import com.date.maskeddates.commons.ReqConst;
import com.date.maskeddates.utils.BitmapUtils;
import com.date.maskeddates.video.VideoCaptureActivity;
import com.date.maskeddates.video.VideoPlayActivity;
import com.eyalbira.loadingdots.LoadingDots;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.skyfishjy.library.RippleBackground;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
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
import jp.wasabeef.picasso.transformations.BlurTransformation;
import pl.tajchert.nammu.PermissionCallback;

import static android.os.Environment.getExternalStorageDirectory;


public class ChatActivity extends AppCompatActivity{

    LinearLayout layout;
    LinearLayout layout_2;
    ImageView sendButton, attachbutton, location;
    EditText messageArea;
    SeekBar seekBar;
    ScrollView scrollView;
    TextView statusView,ok, cancel, typeStatus, sendAudio, cancelAudio;
    ImageView record, play;
    Firebase reference1, reference2, reference3, reference4, reference5, reference6, reference7, reference22, reference8;
    public ChildEventListener mListener1, mListener2;
    CircleImageView photo;
    int is_talking=0;
    int is_talkingR=0;
    boolean is_typing=false;

    boolean recordFlag=false;
    boolean playFlag=false;

    private MediaRecorder myAudioRecorder=null, myAudioRecorder2=null;
    private MediaPlayer myPlayer;
    private String outputFile = "", vOutputFile="";
    private Handler mHandler = new Handler();

    ImageView image;
    private Uri _imageCaptureUri;
    String _photoPath = "";
    Bitmap bitmap;
    LinearLayout imagePortion, audioPortion, dotsmenuButton;
    LinearLayout voicePortion;
    Bitmap thumbnail;
    String imageFile="";
    Uri downloadUrl=null;
    Vibrator vibrator=null;
    LinearLayout unravelbutton;
    RippleBackground rippleBackground;

    private LoadingDots mProgresDialog;
    Timer timer = new Timer();
    Timer timer2 = new Timer();

    String imageStr = "", videoThumbStr = "";

    PermissionHelper permissionHelper;

    boolean is_speech=false;
    private FrameLayout mFrameLayout;
    boolean startTalking=false;
    boolean isCrop=true;
    String sts = "", option = "";
    String old_entered = "";
    int unraveled = 0;
    Typeface font, font2;
    TextView clock, date;
    boolean chatBlockF = false;
    boolean isChatBlockedF = false;
    String lastMessage = "";
    boolean messageReceivedF = false;
    boolean readF = false;
    View unreadView = null;

    Map<String, Object> chatKeyViewMap = new HashMap<>();
    ArrayList<CircleImageView> chatPhotos = new ArrayList<>();

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = new Date().getTime();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM/dd hh:mm a");
            String datetime = dateFormat.format(new Date(millis));
            String datetime2 = dateFormat2.format(new Date(millis));

            clock.setText(datetime.substring(6,8));
            date.setText(datetime2);
            timerHandler.postDelayed(this, 1000);
        }
    };

    public static final boolean NATIVE_WEB_P_SUPPORT = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    public static final int RECORD_AUDIO = 0;
    private InterstitialAd interstitialAd;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        permissionHelper = PermissionHelper.getInstance(this);
        vibrator=(Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        mProgresDialog = (LoadingDots)findViewById(R.id.progressBar);

        date = (TextView)findViewById(R.id.date);
        clock = (TextView)findViewById(R.id.clock);
        timerRunnable.run();

        chatKeyViewMap.clear();
        chatPhotos.clear();

        setActives(Commons.user.get_idx());

        Firebase.setAndroidContext(this);
        reference1 = new Firebase(ReqConst.FIREBASE_URL + "message/" + Commons.thisUser.get_idx() + "_" + Commons.user.get_idx());
        reference2 = new Firebase(ReqConst.FIREBASE_URL + "message/" + Commons.user.get_idx() + "_" + Commons.thisUser.get_idx());
        reference3 = new Firebase(ReqConst.FIREBASE_URL + "notification/" + Commons.user.get_idx() + "/" + Commons.thisUser.get_idx());
        reference4 = new Firebase(ReqConst.FIREBASE_URL + "status/" + Commons.user.get_idx() + "_" + Commons.thisUser.get_idx());
        reference5 = new Firebase(ReqConst.FIREBASE_URL + "status/" + Commons.thisUser.get_idx() + "_" + Commons.user.get_idx());
        reference6 = new Firebase(ReqConst.FIREBASE_URL + "sts/" + Commons.user.get_idx());
        reference7 = new Firebase(ReqConst.FIREBASE_URL + "notification/" + Commons.thisUser.get_idx() + "/" + Commons.user.get_idx());
        reference8 = new Firebase(ReqConst.FIREBASE_URL + "sts/" + Commons.thisUser.get_idx());

        reference22 = new Firebase(ReqConst.FIREBASE_URL + "message/" + Commons.user.get_idx() + "_" + Commons.thisUser.get_idx());

        messageArea = (EditText)findViewById(R.id.messageArea);
        old_entered = getIntent().getStringExtra("old_entered");
        unraveled = getIntent().getIntExtra("unraveled", 0);
        option = getIntent().getStringExtra("option");
        Log.d("OPTION111===>", option);
        if(option.equals("blocked")){
            chatBlockF = true;
            isChatBlockedF = false;
            messageArea.setEnabled(false);
            messageArea.setSingleLine(false);
            messageArea.setHint("You can't send messages because the date has ended.");
            messageArea.setHintTextColor(Color.BLACK);
        }
        else {
            if(option.equals("block")){
                isChatBlockedF = true;
                chatBlockF = true;
                messageArea.setEnabled(false);
                messageArea.setSingleLine(false);
                messageArea.setHint("You can't send messages because the date has ended.");
                messageArea.setHintTextColor(Color.BLACK);
            }else {
                isChatBlockedF = false;
                chatBlockF = false;
                messageArea.setEnabled(true);
                messageArea.setSingleLine(true);
                messageArea.setHint("Write a message");
                messageArea.setHintTextColor(Color.GRAY);
            }
        }

        MobileAds.initialize(this,
                "ca-app-pub-4372722091889992~4297477477");
        //       adView=(AdView)findViewById(R.id.adView);

        AdRequest adRequest1=new AdRequest.Builder().build();
//        adView.loadAd(adRequest1);

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

        rippleBackground = (RippleBackground)findViewById(R.id.content);

        unravelbutton = (LinearLayout) findViewById(R.id.unravelbutton);
        unravelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.space);
                unravelbutton.setAnimation(animation);
                unravelbutton.setVisibility(View.GONE);
                rippleBackground.stopRippleAnimation();
                rippleBackground.setVisibility(View.GONE);
                Intent intent = new Intent(getApplicationContext(), UnlockFaceActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                if(interstitialAd.isLoaded()){
                    interstitialAd.show();
                }else{
//                    showToast2("Please wait for ad to load");
                }
            }
        });

        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Comfortaa_Bold.ttf");
        font2 = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Comfortaa_Regular.ttf");

        timer.schedule(doAsynchronousTask, 0, 3000);
        timer2.schedule(doAsynchronousTask2, 0, 2000);

        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (LinearLayout) findViewById(R.id.layout2);
        sendButton = (ImageView)findViewById(R.id.sendButton);

        dotsmenuButton = (LinearLayout)findViewById(R.id.dotsmenubutton);
        dotsmenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chatBlockF && !isChatBlockedF) return;
                else openMenuItems2();
            }
        });

        scrollView = (ScrollView)findViewById(R.id.scrollView);
        statusView=(TextView)findViewById(R.id.status);

        imagePortion=(LinearLayout)findViewById(R.id.imagePortion);
        Commons.imagePortion=imagePortion;
        image=(ImageView) findViewById(R.id.image);
        Commons.mapImage=image;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO);

        } else {
            seekBar=(SeekBar)findViewById(R.id.seekBar);

            play=(ImageView) findViewById(R.id.play);
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(!playFlag){
                        playFlag=true;
                        try {
                            myPlayer = new MediaPlayer();
                            myPlayer.setDataSource(outputFile);
                            myPlayer.prepare();
                            myPlayer.start();
                            seekBar.setVisibility(View.VISIBLE);
                            seekBar.setMax(myPlayer.getDuration());

//Make sure you update Seekbar on UI thread
                            ChatActivity.this.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    if(myPlayer != null){
                                        int mCurrentPosition = myPlayer.getCurrentPosition();
                                        seekBar.setProgress(mCurrentPosition);
                                        if(mCurrentPosition>=seekBar.getMax()){
                                            record.setEnabled(true);
                                            playFlag=false;
                                            play.setImageResource(R.drawable.audioplayicon);
                                        }
                                    }
                                    mHandler.postDelayed(this, 1000);
                                }
                            });

                            showToast2("Playing audio");

                        } catch (Exception e){
                            e.printStackTrace();
                        }
                        play.setImageResource(R.drawable.audiostopicon);
                        //        play.setTextSize(11.00f);
                        record.setEnabled(false);
                    }else {
                        playFlag=false;
                        try {
                            if (myPlayer != null) {
                                myPlayer.stop();
                                myPlayer.release();
                                myPlayer = null;
                                showToast2("Stop playing the recording...");
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        play.setImageResource(R.drawable.audioplayicon);
                        //        play.setTextSize(11.00f);
                        record.setEnabled(true);
                    }
                }
            });
            record=(ImageView) findViewById(R.id.record);
            record.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(!recordFlag){
                        recordFlag=true;
                        try {
                            //        initAudio();
                            outputFile = Environment.getExternalStorageDirectory().
                                    getAbsolutePath() + "/"+"my_audio"+new Date().getTime()+".3gp";
                            myAudioRecorder = new MediaRecorder();
                            myAudioRecorder.reset();
                            myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                            myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                            myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                            myAudioRecorder.setOutputFile(outputFile);
                            seekBar.setVisibility(View.GONE);
                            myAudioRecorder.prepare();
                            myAudioRecorder.start();
                        } catch (IllegalStateException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        record.setImageResource(R.drawable.audiorecordstopicon);
                        //        record.setTextSize(11.00f);
                        play.setEnabled(false);
                        showToast2("Recording started");
                    }else{
                        recordFlag=false;
                        myAudioRecorder.stop();
                        myAudioRecorder.release();
                        myAudioRecorder = null;

                        record.setImageResource(R.drawable.audiorecordicon);
                        //        record.setTextSize(11.00f);
                        play.setEnabled(true);
                        showToast2("Audio recorded successfully");
                    }

                }
            });
            audioPortion=(LinearLayout)findViewById(R.id.audioPortion);
            sendAudio=(TextView)findViewById(R.id.sendAudio);
            sendAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    File f=new File(outputFile);
                    Log.d("FILExtension===>", FilenameUtils.getExtension(f.getName()));

                    if(f!=null && FilenameUtils.getExtension(f.getName()).length()>0) {
                        showUploadAlert(Uri.fromFile(f));
                        record.setImageResource(R.drawable.audiorecordicon);
                        //        record.setTextSize(11.00f);
                        play.setEnabled(true);
                        record.setEnabled(true);
                        seekBar.setVisibility(View.GONE);
                        audioPortion.setVisibility(View.GONE);
                        recordFlag=false;
                    }
                    else showAlertDialog("Please record audio.");
                }
            });
            cancelAudio=(TextView)findViewById(R.id.cancelA);
            cancelAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    record.setImageResource(R.drawable.audiorecordicon);
                    //        record.setTextSize(11.00f);
                    play.setEnabled(true);
                    play.setImageResource(R.drawable.audioplayicon);
                    //        play.setTextSize(11.00f);
                    record.setEnabled(true);
                    seekBar.setVisibility(View.GONE);

                    audioPortion.setVisibility(View.GONE);
                    recordFlag=false;
                }
            });
        }

        cancel=(TextView)findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePortion.setVisibility(View.GONE);
            }
        });
        ok=(TextView)findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //            Log.d("Bitmap===>",bitmap.toString());

                if(Commons.compressedvideoUrl.length()>0) {
                    imageStr = Commons.videoThumbStr;
                    Commons.videoThumbStr = "";
                    startUploadVideo(Uri.fromFile(new File(Commons.compressedvideoUrl)));
                }
                else {
                    if(Commons.mapScreenshotStr.length() > 0) {
                        imageStr = Commons.mapScreenshotStr;
                        Commons.mapScreenshotStr = "";
                    }
                    uploadImage();
                    imagePortion.setVisibility(View.GONE);
                    mFrameLayout.setVisibility(View.GONE);
                }

            }
        });

        ((LinearLayout)findViewById(R.id.backButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mFrameLayout = (FrameLayout) findViewById(R.id.root_frame_layout);

        voicePortion=(LinearLayout)findViewById(R.id.voiceMessagePortion);
        voicePortion.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        voicePortion.setBackgroundColor(Color.RED);
                        vibrator.vibrate(100);
                        showToast("Hold on and speak something...");
                        try {
                            vOutputFile = Environment.getExternalStorageDirectory().
                                    getAbsolutePath() + "/"+"voice_message"+new Date().getTime()+".3gp";
                            myAudioRecorder2 = new MediaRecorder();
                            myAudioRecorder2.reset();
                            myAudioRecorder2.setAudioSource(MediaRecorder.AudioSource.MIC);
                            myAudioRecorder2.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                            myAudioRecorder2.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                            myAudioRecorder2.setOutputFile(vOutputFile);
                            myAudioRecorder2.prepare();
                            myAudioRecorder2.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        voicePortion.setBackground(null);
                        vibrator.cancel();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(myAudioRecorder2 != null){
                                    myAudioRecorder2.stop();
                                    myAudioRecorder2.release();
                                    myAudioRecorder2 = null;
                                    File f=new File(vOutputFile);
                                    startUploadDocument(Uri.fromFile(f));
                                }
                            }
                        }, 1000);

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        voicePortion.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        photo=(CircleImageView) findViewById(R.id.imv_photo);
        Picasso.with(getApplicationContext())
                .load(Commons.user.get_photoUrl())
                .transform(new BlurTransformation(getApplicationContext(), 25, 1))
                .error(R.mipmap.appicon)
                .placeholder(R.mipmap.appicon)
                .into(photo);

        Picasso.with(getApplicationContext())
                .load(Commons.user.get_photoUrl())
                .transform(new BlurTransformation(getApplicationContext(), 25, 1))
                .error(R.mipmap.appicon)
                .placeholder(R.mipmap.appicon)
                .into((CircleImageView)findViewById(R.id.picture));

        ((TextView)findViewById(R.id.exitbox_title)).setTypeface(font);

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isChatBlockedF || chatBlockF){
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), UnlockFaceActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

        attachbutton=(ImageView)findViewById(R.id.attachbutton);
        attachbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chatBlockF) return;
                openMenuItems();
            }
        });

        TextView name = (TextView)findViewById(R.id.txv_name);
        name.setTypeface(font);

        typeStatus = (TextView)findViewById(R.id.typeStatus);
        name.setText(rename(Commons.user.get_name()));

        messageArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });

        messageArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                is_typing=false;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                is_typing = true;
                if(charSequence.toString().length() > 0){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("online", "typing");
                    map.put("time", String.valueOf(new Date().getTime()));
                    map.put("user", Commons.thisUser.get_email());
                    reference4.removeValue();
                    reference4.push().setValue(map);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                is_typing = false;

//                String messageText = messageArea.getText().toString().trim();
//                if(!is_typing){
//                    is_typing=true;
//                    if(messageText.length() > 0){
//                        Map<String, String> map = new HashMap<String, String>();
//                        map.put("online", "typing");
//                        map.put("time", String.valueOf(new Date().getTime()));
//                        map.put("user", Commons.thisUser.get_email());
//                        reference4.removeValue();
//                        reference4.push().setValue(map);
//                    }
//                }else {
//                    if(messageText.length()==0){
//                        is_typing=false;
//                        Map<String, String> map = new HashMap<String, String>();
//                        map.put("online", "online");
//                        map.put("time", String.valueOf(new Date().getTime()));
//                        map.put("user", Commons.thisUser.get_email());
//                        reference4.removeValue();
//                        reference4.push().setValue(map);
//                    }
//                }
            }
        });

        online("true");

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString().trim();
                if(messageText.length() > 0){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("time", String.valueOf(new Date().getTime()));
                    map.put("image", "");
                    map.put("video", "");
                    map.put("lat", "");
                    map.put("lon", "");
                    map.put("read", "");
                    map.put("email", Commons.thisUser.get_email());
                    map.put("name", Commons.thisUser.get_name());
                    map.put("photo", Commons.thisUser.get_photoUrl());

                    online("true");
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    messageArea.setText("");
                    is_typing=false;

                    Map<String, String> map1 = new HashMap<String, String>();
                    map1.put("sender_id", String.valueOf(Commons.thisUser.get_idx()));
                    map1.put("sender_name", Commons.thisUser.get_name());
                    map1.put("sender_email", Commons.thisUser.get_email());
                    map1.put("sender_photo", Commons.thisUser.get_photoUrl());
                    map1.put("time", String.valueOf(new Date().getTime()));
                    map1.put("msg", messageText);
                    reference3.removeValue();
                    reference3.push().setValue(map1);
                }
                mFrameLayout.setVisibility(View.GONE);
            }
        });

        reference5.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String online = map.get("online").toString();
                String time = map.get("time").toString();

                if(online.equals("online") || online.equals("offline")){
                    typeStatus.setVisibility(View.GONE);
                    if(online.equals("online")){
                        statusView.setVisibility(View.VISIBLE);
                        statusView.setText("Active");
                        typeStatus.setVisibility(View.GONE);
                        chatBlockF = false;
                        sts = "online";
                    }else if(online.equals("offline")){
                        statusView.setVisibility(View.VISIBLE);
                        statusView.setText("Last seen on " + DateFormat.format("MM/dd/yyyy hh:mm a",
                                Long.parseLong(time)));
                        typeStatus.setVisibility(View.GONE);
                        sts = "offline";
                    }
                }
                else if(online.equals("typing")){
                    statusView.setText("is typing . . .");
                    typeStatus.setVisibility(View.VISIBLE);
                    sts = "online";
                    try{
                        if(Commons.user.get_name()!=null){
                            typeStatus.setText(Commons.user.get_name()+" "+"is typing ...");
                        }
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }catch (Exception e){}


                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });

                }

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

        reference6.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String status = map.get("status").toString();
                String time = map.get("time").toString();

                sts = "offline";
                Log.d("ON/OFF_STS===", sts);

                if(status.equals("online")){
                    statusView.setVisibility(View.VISIBLE);
                    statusView.setText("Active");
                    typeStatus.setVisibility(View.GONE);
                }
                else if(status.equals("offline")){
                    statusView.setVisibility(View.VISIBLE);
                    statusView.setText("Last seen on "+DateFormat.format("MM/dd/yyyy hh:mm a",
                            Long.parseLong(time)));
                    typeStatus.setVisibility(View.GONE);
                }

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

        reference7.removeValue();

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String image = map.get("image").toString();
                String video = map.get("video").toString();
                String latStr = map.get("lat").toString();
                String lonStr = map.get("lon").toString();
                String read_on = "";
                try{
                    read_on = map.get("read").toString();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                String email = map.get("email").toString();
                String name = map.get("name").toString();
                String photo = map.get("photo").toString();
                String time = map.get("time").toString();
                LatLng latLng=null;
                String key = dataSnapshot.getKey();

                lastMessage = message;

                if(email.equals(Commons.thisUser.get_email())){
                    addMessageBox(read_on, message, time, image, video, latStr, lonStr, key, 1);
                }
                else{
                    addMessageBox(read_on, message, time, image, video, latStr, lonStr, key, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String email = map.get("email").toString();
                String read_on = map.get("read").toString();
                String key = dataSnapshot.getKey();

                if(email.equals(Commons.thisUser.get_email())){
                    if(read_on.length() > 0){
                        ((ImageView)((View)chatKeyViewMap.get(key)).findViewById(R.id.writed_symb)).setImageResource(R.drawable.ic_chat_read);
                        ((ImageView)((View)chatKeyViewMap.get(key)).findViewById(R.id.read_symb)).setImageResource(R.drawable.ic_chat_read);
                    }
                }
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

        reference22.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                messageReceivedF = true;
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

        scrollView.post(new Runnable(){

            @Override
            public void run() {
                ViewTreeObserver observer = scrollView.getViewTreeObserver();
                observer.addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener(){

                    @Override
                    public void onScrollChanged() {
                        int scrollX = scrollView.getScrollX();
                        int scrollY = scrollView.getScrollY();

                        try{
                            Log.d("ScrollY+++", String.valueOf(scrollY));
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                    }});
            }});

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    int cnt = 0;

    TimerTask doAsynchronousTask = new TimerTask() {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    cnt++;
                    getNoti(Commons.user.get_idx());
                    if(cnt % 5 == 0){
                        if(sts.equals("online") && unraveled < 4 && !chatBlockF){
                            if(!compareDates(old_entered, String.valueOf(new Date().getTime()))){
                                rippleBackground.startRippleAnimation();
                                rippleBackground.setVisibility(View.VISIBLE);
                                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
                                unravelbutton.setAnimation(animation);
                                unravelbutton.setVisibility(View.VISIBLE);
                                setUnraveled(Commons.user.get_idx());
                            }
                        }
                    }
                    if(cnt % 3 == 0){
                        readMessage(messageReceivedF);
                        if(readF){
                            readF = false;
                            try{
                                layout.removeView(unreadView);
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }
                        }
                    }
                    if(cnt >= 150) cnt = 0;
                }
            });
        }
    };

    TimerTask doAsynchronousTask2 = new TimerTask() {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(!is_typing){
                        is_typing = true;
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("online", "online");
                        map.put("time", String.valueOf(new Date().getTime()));
                        map.put("user", Commons.thisUser.get_email());
                        reference4.removeValue();
                        reference4.push().setValue(map);
                    }
                }
            });
        }
    };

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void readMessage(boolean b){
        if(b){
            reference22.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Map map = dataSnapshot.getValue(Map.class);
                    String message = map.get("message").toString();
                    String image = map.get("image").toString();
                    String video = map.get("video").toString();
                    String latStr = map.get("lat").toString();
                    String lonStr = map.get("lon").toString();
                    String read_on = "";
                    try{
                        read_on = map.get("read").toString();
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                    String email = map.get("email").toString();
                    String name = map.get("name").toString();
                    String photo = map.get("photo").toString();
                    String time = map.get("time").toString();
                    LatLng latLng=null;
                    String key = dataSnapshot.getKey();

                    if(!email.equals(Commons.thisUser.get_email())){
                        if(read_on.length() == 0){
                            try{
                                reference22.child(key).child("read").setValue("yes");
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }
                        }
                    }
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
            messageReceivedF = false;
        }
    }

    private String rename(String name){
        String firstName = "", lastName = "";
        if(name.contains(" ")){
            if(name.indexOf(" ") >= 1) {
                firstName = name.substring(0, name.indexOf(" "));
                lastName=name.substring(name.indexOf(" ")+1, name.length());
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
        final View dialogView = inflater.inflate(R.layout.toast_view, null);
        TextView textView=(TextView)dialogView.findViewById(R.id.text);
        textView.setText(content);
        Toast toast=new Toast(this);
        toast.setView(dialogView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    public Bitmap base64ToBitmap(String base64Str){
        Bitmap bitmap;
        final String pureBase64Encoded = base64Str.substring(base64Str.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }

    public void addMessageBox(String readOn, final String message, String time, final String imageFile, final String video, final String latStr, final String lonStr, String key, final int type){

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.chat_area, null);

        FrameLayout photoFrame = (FrameLayout)dialogView.findViewById(R.id.photoFrame);
        final CircleImageView photo=(CircleImageView) dialogView.findViewById(R.id.photo);
        Picasso.with(getApplicationContext())
                .load(Commons.user.get_photoUrl())
                .transform(new BlurTransformation(getApplicationContext(), 25, 1))
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)
                .into(photo);

        chatPhotos.add(photo);

        final LinearLayout read=(LinearLayout)dialogView.findViewById(R.id.read);
        final LinearLayout write=(LinearLayout)dialogView.findViewById(R.id.write);
        final LinearLayout dotrec=(LinearLayout)dialogView.findViewById(R.id.receiverdots);
        final LinearLayout dotsend=(LinearLayout)dialogView.findViewById(R.id.senderdots);
        final TextView text = (TextView) dialogView.findViewById(R.id.text);
        final TextView text2 = (TextView) dialogView.findViewById(R.id.text2);
        final TextView datetime = (TextView) dialogView.findViewById(R.id.datetime);
        final TextView datetime2 = (TextView) dialogView.findViewById(R.id.datetime2);
        final TextView writespace = (TextView) dialogView.findViewById(R.id.writespace);
        ImageView image=(ImageView) dialogView.findViewById(R.id.image);
        ImageView image2=(ImageView) dialogView.findViewById(R.id.image2);
        ImageView writed=(ImageView) dialogView.findViewById(R.id.writed_symb);
        ImageView readed=(ImageView) dialogView.findViewById(R.id.read_symb);
        ImageView play=(ImageView) dialogView.findViewById(R.id.playicon);
        ImageView play2=(ImageView) dialogView.findViewById(R.id.playicon2);

        ImageView receivedownloadicon=(ImageView) dialogView.findViewById(R.id.receivedownloadicon);
        ImageView senddownloadicon=(ImageView) dialogView.findViewById(R.id.senddownloadicon);

        //       if(type==1)photo.setVisibility(View.GONE);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if(type == 1) {

            if(is_talking==1){
                dotsend.setVisibility(View.INVISIBLE);
            }else {
                dotsend.setVisibility(View.VISIBLE);
            }

            photoFrame.setVisibility(View.GONE);
            read.setVisibility(View.GONE);

            dotrec.setVisibility(View.GONE);
            writespace.setVisibility(View.VISIBLE);
            write.setVisibility(View.VISIBLE);
            if(video.length()>0){
                text2.setText("Sent a file:\n" + message);
                senddownloadicon.setVisibility(View.VISIBLE);
            }
            else {
                text2.setText(message);
                senddownloadicon.setVisibility(View.GONE);
            }

            if(readOn.length() > 0){
                readed.setBackgroundResource(R.drawable.ic_chat_read);
                writed.setBackgroundResource(R.drawable.ic_chat_read);
            }else {
                readed.setBackgroundResource(R.drawable.ic_chat_read_gray);
                writed.setBackgroundResource(R.drawable.ic_chat_read_gray);
            }

            text2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(text2.getText().length()>0){

                        if(video.length()>0){
                            if (text2.getText().toString().endsWith(".3gp") || text2.getText().toString().endsWith(".mp3")) {

                                showDownloadAlert(video);
                                myPlayer = new MediaPlayer();
                                try {
                                    myPlayer.setDataSource(video);
                                    myPlayer.prepare();
                                    myPlayer.start();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else
                                showDownloadAlert(video);
                        }
                        else {
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, text2.getText().toString());
                            startActivity(Intent.createChooser(shareIntent, "Share via"));
                        }
                    }
                }
            });

            text2.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    // TODO Auto-generated method stub
                    if(video.length()>0){
                        showDeleteFileAlert(key, dialogView);
                    }
                    else {
                        showEditTextAlert(key, text2, dialogView);
                    }
                    return false;
                }
            });

            datetime2.setText(DateFormat.format("MM/dd/yyyy hh:mm a",
                    Long.parseLong(time)));

            if(imageFile.length()>0){
                image2.setVisibility(View.VISIBLE);

                Picasso.with(getApplicationContext())
                        .load(imageFile)
                        .error(R.drawable.noresult)
                        .placeholder(R.drawable.noresult)
                        .into(image2);

                if(video.length()>0)
                    play2.setVisibility(View.VISIBLE);
                else play2.setVisibility(View.GONE);
                image2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LatLng location = null;
                        if(latStr.length()>0 && lonStr.length()>0){
                            location=new LatLng(Double.parseDouble(latStr),Double.parseDouble(lonStr));
                        }else location=null;
                        Commons.latLng = location;
                        if(video.length()>0){
                            Commons.videouri = Uri.parse(video);
                            Intent intent=new Intent(getApplicationContext(),VideoPlayActivity.class);
                            startActivity(intent);
                        }
                        else {
                            if(location != null){
                                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", location.latitude, location.longitude);
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                startActivity(intent);
                            }else {
                                Intent intent = new Intent(getApplicationContext(), ViewActivity.class);
                                intent.putExtra("image", imageFile);
                                startActivity(intent);
                            }
                        }
                    }
                });
                text2.setVisibility(View.GONE);
                image2.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        showDeleteFileAlert(key, dialogView);
                        return false;
                    }
                });
            }else {
                image2.setVisibility(View.GONE);
                text2.setVisibility(View.VISIBLE);
            }

            if(is_talking==0){
                is_talking=1; is_talkingR=0;
            }
//            lp2.gravity = Gravity.RIGHT;
//            textView.setBackgroundResource(R.drawable.bubble_in);
        }
        else{
            if(is_talkingR==1){
                photoFrame.setVisibility(View.INVISIBLE);
                dotrec.setVisibility(View.INVISIBLE);
            }else {
                photoFrame.setVisibility(View.VISIBLE);
                dotrec.setVisibility(View.VISIBLE);
            }

            if(readOn.length() == 0){
                if(!readF){
                    unreadView = (View) inflater.inflate(R.layout.unread_view, null);
                    ((TextView)unreadView.findViewById(R.id.unreadCaption)).setTypeface(font2);
                    layout.addView(unreadView);
                    readF = true;
                }
                try{
                    reference1.child(key).child("read").setValue("yes");
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

            read.setVisibility(View.VISIBLE);
            dotsend.setVisibility(View.GONE);

            writespace.setVisibility(View.GONE);
            write.setVisibility(View.GONE);

//            textToSpeech = new TextToSpeech(ChatActivity.this, ChatActivity.this);
            //        if(!isCarMode){
            if(video.length()>0){
                text.setText("Shared a file:\n" + message);
                receivedownloadicon.setVisibility(View.VISIBLE);
            }
            else {
                text.setText(message);
                receivedownloadicon.setVisibility(View.GONE);
            }

            //        reference.removeValue();

            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(text.getText().length()>0){

                        if(video.length()>0){
                            if (text.getText().toString().endsWith(".3gp") || text.getText().toString().endsWith(".mp3")) {

                                showDownloadAlert(video);
                                myPlayer = new MediaPlayer();
                                try {
                                    myPlayer.setDataSource(video);
                                    myPlayer.prepare();
                                    myPlayer.start();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            } else
                                showDownloadAlert(video);
                        }
                        else {
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, text.getText().toString());
                            startActivity(Intent.createChooser(shareIntent, "Share via"));
                        }
                    }
                }
            });
            datetime.setText(DateFormat.format("MM/dd/yyyy hh:mm a",
                    Long.parseLong(time)));

            if(imageFile.length()>0){
                image.setVisibility(View.VISIBLE);
                Picasso.with(getApplicationContext())
                        .load(imageFile)
                        .error(R.drawable.noresult)
                        .placeholder(R.drawable.noresult)
                        .into(image);
                if(video.length()>0)
                    play.setVisibility(View.VISIBLE);
                else play.setVisibility(View.GONE);
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LatLng location = null;
                        if(latStr.length()>0 && lonStr.length()>0){
                            location=new LatLng(Double.parseDouble(latStr),Double.parseDouble(lonStr));
                        }else location=null;
                        Commons.latLng=location;
                        if(video.length()>0){
                            Commons.videouri=Uri.parse(video);
                            Intent intent=new Intent(getApplicationContext(),VideoPlayActivity.class);
                            startActivity(intent);
                        }
                        else {
                            if(location != null){
                                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", location.latitude, location.longitude);
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                startActivity(intent);
                            }else {
                                Intent intent = new Intent(getApplicationContext(), ViewActivity.class);
                                intent.putExtra("image", imageFile);
                                startActivity(intent);
                            }
                        }
                    }
                });
                text.setVisibility(View.GONE);
            }else {
                image.setVisibility(View.GONE);
                text.setVisibility(View.VISIBLE);
            }

            if(is_talkingR==0){
                is_talking=0; is_talkingR=1;
            }
        }

        chatKeyViewMap.put(key, dialogView);
        layout.addView(dialogView);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startTalking=true;
            }
        }, 2000);
    }


    AlertDialog alertDialog;
    CharSequence[] editItems = {"Edit","Delete", "Share"};

    public void showEditTextAlert(String key, TextView textView, View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setSingleChoiceItems(editItems, 0, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch(item)
                {
                    case 0:
                        final EditText editText=new EditText(ChatActivity.this);
                        editText.setGravity(Gravity.CENTER);
                        editText.setText(textView.getText().toString());
                        new AlertDialog.Builder(ChatActivity.this)
                                .setTitle("Edit this")
                                .setView(editText)
                                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if(editText.getText().length()>0) {
                                            reference1.child(key).child("message").setValue(editText.getText().toString().trim());
                                            textView.setText(editText.getText().toString().trim());
                                        }else {
                                            showToast2("Write something ...");
                                        }
                                    }
                                })
                                .show();
                        break;
                    case 1:
                        reference1.child(key).removeValue();
                        layout.removeView(view);
                        break;
                    case 2:
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, textView.getText().toString());
                        startActivity(Intent.createChooser(shareIntent, "Share via"));
                        break;
                }
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = 800;
        lp.height = 800;
        alertDialog.getWindow().setAttributes(lp);
    }

    CharSequence[] deleteItems = {"Delete"};

    public void showDeleteFileAlert(String key, View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setSingleChoiceItems(deleteItems, 0, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch(item)
                {
                    case 0:
                        reference1.child(key).removeValue();
                        layout.removeView(view);
                        break;
                }
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = 800;
        lp.height = 280;
        alertDialog.getWindow().setAttributes(lp);
    }

    public void online(String status){
        Map<String, String> map = new HashMap<String, String>();
        if(status.equals("true"))
            map.put("online", "online");
        else map.put("online", "offline");
        map.put("time", String.valueOf(new Date().getTime()));
        map.put("user", Commons.thisUser.get_email());
        reference4.removeValue();
        reference4.push().setValue(map);
    }

    public void showCropDialog() {

        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(this);

        dialogBuilder.setTitle("Hint!");
        dialogBuilder.setIcon(R.drawable.questionicon);
        dialogBuilder.setMessage("Do you want to crop?");

        dialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
                isCrop=false;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);//
                startActivityForResult(Intent.createChooser(intent, "Select File"), Constants.PICK_FROM_ALBUM);
            }
        });
        dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
                isCrop=true;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);//
                startActivityForResult(Intent.createChooser(intent, "Select File"), Constants.PICK_FROM_ALBUM);
            }
        });
        android.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public void fromGallery(MenuItem menuItem) {

        if(Commons.thisUser.get_premium().length() > 0) showCropDialog();
        else gotoMembership();

    }

    public void takePhoto(MenuItem menuItem) {

        if(Commons.thisUser.get_premium().length() > 0) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, Constants.TAKE_FROM_CAMERA);
        }
        else gotoMembership();
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private void gotoMembership(){
        Intent intent = new Intent(getApplicationContext(), PremiumGuideActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        permissionHelper.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.CROP_FROM_CAMERA: {

                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap_buf=null;
                        File saveFile = BitmapUtils.getOutputMediaFile(this);

                        InputStream in = getContentResolver().openInputStream(Uri.fromFile(saveFile));
                        BitmapFactory.Options bitOpt = new BitmapFactory.Options();
                        bitmap = BitmapFactory.decodeStream(in, null, bitOpt);

                        in.close();

                        //set The bitmap data to image View
                        Commons.bitmap=bitmap;

                        imagePortion.setVisibility(View.VISIBLE);
                        image.setImageBitmap(bitmap);
                        //           Constants.userphoto=ui_imvphoto.getDrawable();
                        _photoPath = saveFile.getAbsolutePath();
                        Commons.destination=new File(_photoPath);
                        Commons.imageUrl=_photoPath;    Log.d("PHOTOPATH===",_photoPath.toString());
                        imageToDonwloadUrl(Commons.destination.getPath());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case Constants.PICK_FROM_ALBUM:

                if (resultCode == RESULT_OK) {
                    _imageCaptureUri = data.getData();   Log.d("PHOTOURL===",_imageCaptureUri.toString());
                }

            case Constants.TAKE_FROM_CAMERA: {

                if(!is_speech){
                    try {

                        permissionHelper.verifyPermission(
                                new String[]{"take picture"},
                                new String[]{Manifest.permission.CAMERA},
                                new PermissionCallback() {
                                    @Override
                                    public void permissionGranted() {
                                        //action to perform when permission granteed
                                    }

                                    @Override
                                    public void permissionRefused() {
                                        //action to perform when permission refused
                                    }
                                }
                        );

                        try{
                            onCaptureImageResult(data);
                            return;
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        if(!isCrop){
                            bitmap=getBitmapFromUri(_imageCaptureUri);
                            File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Pictures");
                            if (!dir.exists())
                                dir.mkdirs();
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                            Commons.destination = new File(getExternalStorageDirectory()+"/Pictures",
                                    System.currentTimeMillis() + ".jpg");

                            FileOutputStream fo;
                            try {
                                Commons.destination.createNewFile();
                                fo = new FileOutputStream(Commons.destination);
                                fo.write(bytes.toByteArray());
                                fo.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            imagePortion.setVisibility(View.VISIBLE);
                            image.setImageBitmap(bitmap);
                            imageToDonwloadUrl(Commons.destination.getPath());

                            return;
                        }


                        _imageCaptureUri = data.getData();

//                    _photoPath = BitmapUtils.getRealPathFromURI(this, _imageCaptureUri);  Log.d("PHOTOPATH0000===",_photoPath.toString());
                        //        Commons.imageUrl=_photoPath;

//                    this.grantUriPermission("com.android.camera",_imageCaptureUri,
//                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);


                        Intent intent = new Intent("com.android.camera.action.CROP");
                        intent.setDataAndType(_imageCaptureUri, "image/*");

                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


                        intent.putExtra("crop", true);
                        intent.putExtra("scale", true);
                        intent.putExtra("outputX", Constants.PROFILE_IMAGE_SIZE);
                        intent.putExtra("outputY", Constants.PROFILE_IMAGE_SIZE);
//                    intent.putExtra("outputX", 800);
//                    intent.putExtra("outputY", 800);
                        intent.putExtra("aspectX", 1);
                        intent.putExtra("aspectY", 1);
                        intent.putExtra("noFaceDetection", true);
//                    intent.putExtra("return-data", true);
                        intent.putExtra("output", Uri.fromFile(BitmapUtils.getOutputMediaFile(this)));

                        startActivityForResult(intent, Constants.CROP_FROM_CAMERA);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;
            }
        }

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();

                File f=new File(uri.getPath());

                if(FilenameUtils.getExtension(f.getName()).length()>0)
                    showUploadAlert(uri);
                else showToast2("Invalid file. Try again...");
            }

        }

        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();

                File f=new File(uri.getPath());
                Log.d("FILExtension===>", FilenameUtils.getExtension(f.getName()));

                if(FilenameUtils.getExtension(f.getName()).length()>0 && (FilenameUtils.getExtension(f.getName()).equals("3gp") ||
                        FilenameUtils.getExtension(f.getName()).equals("mp3")))
                    showUploadAlert(uri);
                else showAlertDialog("Please select an Audio file.");
            }

        }

    }

    public void showToast2(String content){
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_toast, null);
        TextView textView=(TextView)dialogView.findViewById(R.id.text);
        textView.setText(content);
        Toast toast=new Toast(getApplicationContext());
        toast.setView(dialogView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    private void onCaptureImageResult(Intent data) {

        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Pictures");
        if (!dir.exists())
            dir.mkdirs();
        thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        Commons.destination = new File(getExternalStorageDirectory()+"/Pictures",
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            Commons.destination.createNewFile();
            fo = new FileOutputStream(Commons.destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imagePortion.setVisibility(View.VISIBLE);
        image.setImageBitmap(thumbnail);
        imageToDonwloadUrl(Commons.destination.getPath());
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void openMenuItems() {
        View view = findViewById(R.id.attachbutton);
//        PopupMenu popup = new PopupMenu(this, view);
//        getMenuInflater().inflate(R.menu.attach_menu, popup.getMenu());
        android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(this, view);
        popupMenu.inflate(R.menu.chat_attach);
        Object menuHelper;
        Class[] argTypes;
        try {
            Field fMenuHelper = android.widget.PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popupMenu);
            argTypes = new Class[]{boolean.class};
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception e) {
            // Possible exceptions are NoSuchMethodError and NoSuchFieldError
            //
            // In either case, an exception indicates something is wrong with the reflection code, or the
            // structure of the PopupMenu class or its dependencies has changed.
            //
            // These exceptions should never happen since we're shipping the AppCompat library in our own apk,
            // but in the case that they do, we simply can't force icons to display, so log the error and
            // show the menu normally.

            Log.w("Error====>", "error forcing menu icons to show", e);
            popupMenu.show();
            return;
        }
        popupMenu.show();

    }

    private void openMenuItems2() {
        View view = findViewById(R.id.dotsmenubutton);
//        PopupMenu popup = new PopupMenu(this, view);
//        getMenuInflater().inflate(R.menu.attach_menu, popup.getMenu());
        android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(this, view);
        popupMenu.inflate(R.menu.dotsmenu);
        if(isChatBlockedF){
            popupMenu.getMenu().findItem(R.id.unlock_face).setVisible(false);
            popupMenu.getMenu().findItem(R.id.block).setVisible(false);
            popupMenu.getMenu().findItem(R.id.unblock).setVisible(true);
            popupMenu.getMenu().findItem(R.id.gallery).setVisible(false);
            popupMenu.getMenu().findItem(R.id.profile).setVisible(false);
        }else {
            popupMenu.getMenu().findItem(R.id.unlock_face).setVisible(true);
            popupMenu.getMenu().findItem(R.id.block).setVisible(true);
            popupMenu.getMenu().findItem(R.id.unblock).setVisible(false);
            popupMenu.getMenu().findItem(R.id.gallery).setVisible(true);
            popupMenu.getMenu().findItem(R.id.profile).setVisible(true);
        }
        Object menuHelper;
        Class[] argTypes;
        try {
            Field fMenuHelper = android.widget.PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popupMenu);
            argTypes = new Class[]{boolean.class};
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception e) {
            // Possible exceptions are NoSuchMethodError and NoSuchFieldError
            //
            // In either case, an exception indicates something is wrong with the reflection code, or the
            // structure of the PopupMenu class or its dependencies has changed.
            //
            // These exceptions should never happen since we're shipping the AppCompat library in our own apk,
            // but in the case that they do, we simply can't force icons to display, so log the error and
            // show the menu normally.

            Log.w("Error====>", "error forcing menu icons to show", e);
            popupMenu.show();
            return;
        }
        popupMenu.show();
    }

    public void viewGallery(MenuItem menuItem){

        if(Commons.thisUser.get_premium().length() == 0){
            Intent intent = new Intent(getApplicationContext(), PremiumGuideActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }else {
            if(unraveled < 4){
                Intent intent = new Intent(getApplicationContext(), ViewUnraveledScoreActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }else {
                Intent intent = new Intent(getApplicationContext(), PhotosActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
            }
        }
    }

    public void viewProfile(MenuItem menuItem){
        Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
        intent.putExtra("unraveled", unraveled);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void unlockFace(MenuItem menuItem){
        Intent intent = new Intent(getApplicationContext(), UnlockFaceActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    public void blockUser(MenuItem menuItem){
        ((FrameLayout)findViewById(R.id.exitbox)).setVisibility(View.VISIBLE);
        ((LinearLayout)findViewById(R.id.layout)).setVisibility(View.VISIBLE);
    }

    public void closeExitbox(View view){
        ((FrameLayout)findViewById(R.id.exitbox)).setVisibility(View.GONE);
        ((LinearLayout)findViewById(R.id.layout)).setVisibility(View.GONE);
    }

    public void sendExitbox(View view){
        ((FrameLayout)findViewById(R.id.exitbox)).setVisibility(View.GONE);
        ((LinearLayout)findViewById(R.id.layout)).setVisibility(View.GONE);
        sendNoti(Commons.thisUser.get_idx());
    }

    public void unblockUser(MenuItem menuItem){
        releaseBlocked(String.valueOf(Commons.user.get_idx()));
    }

    public void releaseBlocked(String user_id) {

        String url = ReqConst.SERVER_URL + "releaseBlockOnChat";
        showLoading(true);

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                VolleyLog.v("Response:%n %s", response.toString());
                parseRestUrlsResponse2(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                showLoading(false);
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("user_id", user_id);
                params.put("me_id", String.valueOf(Commons.thisUser.get_idx()));

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseRestUrlsResponse2(String json) {

        showLoading(false);
        try {

            JSONObject response = new JSONObject(json);

            String success = response.getString("result_code");
            if (success.equals("0")) {
                showToast2("Date allowed");
                MapUtils.optionMap.put(Commons.user.get_idx(), "normal");
            }
            else {
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again..");
            }

        } catch (JSONException e) {
            showToast("We can not detect any internet connectivity. Please check your internet connection and try again..");
            e.printStackTrace();
        }
    }

    public void uploadImage(){

        if(imageStr.length() > 0){

            Map<String, String> map = new HashMap<String, String>();
            map.put("message", "");
            if(downloadUrl!=null){
                map.put("video", downloadUrl.toString());
                downloadUrl=null;
            }else map.put("video", "");
            map.put("time", String.valueOf(new Date().getTime()));
            map.put("email", Commons.thisUser.get_email());
            map.put("name", Commons.thisUser.get_name());
            map.put("photo", Commons.thisUser.get_photoUrl());
            map.put("image", imageStr);
            map.put("read", "");
            if(Commons.requestLatlng!=null){
                map.put("lat", String.valueOf(Commons.requestLatlng.latitude));
                map.put("lon", String.valueOf(Commons.requestLatlng.longitude));
                Commons.requestLatlng=null;
            }else {
                map.put("lat", "");
                map.put("lon", "");
            }

            online("true");
            reference1.push().setValue(map);
            reference2.push().setValue(map);
            is_typing=false;

            Map<String, String> map1 = new HashMap<String, String>();
            map1.put("sender_id", String.valueOf(Commons.thisUser.get_idx()));
            map1.put("sender_name", Commons.thisUser.get_name());
            map1.put("sender_email", Commons.thisUser.get_email());
            map1.put("sender_photo", Commons.thisUser.get_photoUrl());
            map1.put("time", String.valueOf(new Date().getTime()));
            map1.put("msg", "Shared a file");
            reference3.removeValue();
            reference3.push().setValue(map1);

            imageStr = "";
        }
    }

    public void showAlertDialog(String msg) {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setTitle(getString(R.string.app_name));
        alertDialog.setMessage(msg);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Okay",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();
    }

    public void showAlertDialogMenuDocument() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= 19) {
            intent = new Intent("android.intent.action.OPEN_DOCUMENT");
            intent.setType("*/*");

        } else {
            intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("file*//*");
        }
        startActivityForResult(intent, 1);
    }

    public void showAlertDialogMenuAudio() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= 19) {
            intent = new Intent("android.intent.action.OPEN_DOCUMENT");
            intent.setType("*/*");

        } else {
            intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("file*//*");
        }
        startActivityForResult(intent, 2);
    }

    public void pickLocation(MenuItem menuItem){
        Intent intent=new Intent(getApplicationContext(), LocationCaptureActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    public void recordAudio(MenuItem menuItem){
        if(Commons.thisUser.get_premium().length() > 0) audioPortion.setVisibility(View.VISIBLE);
        else gotoMembership();
    }

    public void pickDocument(MenuItem menuItem){
        if(Commons.thisUser.get_premium().length() > 0) showAlertDialogMenuDocument();
        else gotoMembership();
    }

    public void icebreakers(MenuItem menuItem){
        Commons.messageArea = messageArea;
        Intent intent=new Intent(this,IcebreakerActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    public void pickAudio(MenuItem menuItem){
        if(Commons.thisUser.get_premium().length() > 0) showAlertDialogMenuAudio();
        else gotoMembership();
    }

    public void pickVideo(MenuItem menuItem){
        if(Commons.thisUser.get_premium().length() > 0) gotoTakeVideo("pick");
        else gotoMembership();
    }
    public void takeVideo(MenuItem menuItem){
        if(Commons.thisUser.get_premium().length() > 0) gotoTakeVideo("capture");
        else gotoMembership();
    }
    private void gotoTakeVideo(String option) {
        Commons.imagePortion=imagePortion;
        Commons.mapImage = image;
        Intent intent=new Intent(this,VideoCaptureActivity.class);
        intent.putExtra("OPTION",option);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    private void startUploadVideo(Uri uri){

        showLoading(true);

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance("gs://datingapp-206116.appspot.com");

        StorageReference videoReference = firebaseStorage.getReference();

        UploadTask uploadTask = videoReference.child(uri.getLastPathSegment()+ ".mp4").putFile(uri);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                showToast2(exception.getMessage());
                showLoading(false);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                downloadUrl = taskSnapshot.getDownloadUrl(); Log.d("DOWNLOADURL===>",downloadUrl.getPath());
                Commons.videouri=null;
                Commons.compressedvideoUrl="";
                uploadImage();
                imagePortion.setVisibility(View.GONE);
                mFrameLayout.setVisibility(View.GONE);
                showLoading(false);
            }
        });

    }

    private void startUploadDocument(final Uri uri){

        showLoading(true);

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance("gs://datingapp-206116.appspot.com");

        StorageReference videoReference = firebaseStorage.getReference();

        UploadTask uploadTask = videoReference.child(uri.getLastPathSegment()).putFile(uri);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                showToast2(exception.getMessage());
                showLoading(false);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                downloadUrl = taskSnapshot.getDownloadUrl();

                File f=new File(uri.getPath());

                Map<String, String> map = new HashMap<String, String>();
                map.put("message", f.getName());
                map.put("time", String.valueOf(new Date().getTime()));
                map.put("image", "");
                map.put("video", downloadUrl.toString());
                map.put("lat", "");
                map.put("lon", "");
                map.put("read", "");
                map.put("email", Commons.thisUser.get_email());
                map.put("name", Commons.thisUser.get_name());
                map.put("photo", Commons.thisUser.get_photoUrl());

                online("true");
                reference1.push().setValue(map);
                reference2.push().setValue(map);
                messageArea.setText("");
                is_typing=false;

                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("sender_id", String.valueOf(Commons.thisUser.get_idx()));
                map1.put("sender_name", Commons.thisUser.get_name());
                map1.put("sender_email", Commons.thisUser.get_email());
                map1.put("sender_photo", Commons.thisUser.get_photoUrl());
                map1.put("time", String.valueOf(new Date().getTime()));
                map1.put("msg", "Shared a file");
                reference3.removeValue();
                reference3.push().setValue(map1);

                mFrameLayout.setVisibility(View.GONE);
                downloadUrl=null;

                showLoading(false);
            }
        });

    }

    private void showLoading(boolean isShow){
        if (isShow){
            mProgresDialog = (LoadingDots)findViewById(R.id.progressBar);
            mProgresDialog.setVisibility(View.VISIBLE);
        } else {
            if(mProgresDialog != null){
                mProgresDialog.setVisibility(View.GONE);
                mProgresDialog = null;
            }
        }
    }

    private void showDownloadAlert(String link){
        ((LinearLayout)findViewById(R.id.downloadbox)).setVisibility(View.VISIBLE);
        ((LinearLayout)findViewById(R.id.layout)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.yes_down)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LinearLayout)findViewById(R.id.downloadbox)).setVisibility(View.GONE);
                ((LinearLayout)findViewById(R.id.layout)).setVisibility(View.GONE);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(link));
                startActivity(i);
            }
        });

        ((TextView)findViewById(R.id.no_down)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LinearLayout)findViewById(R.id.downloadbox)).setVisibility(View.GONE);
                ((LinearLayout)findViewById(R.id.layout)).setVisibility(View.GONE);
            }
        });
    }

    private void showUploadAlert(final Uri link){
        ((LinearLayout)findViewById(R.id.uploadbox)).setVisibility(View.VISIBLE);
        ((LinearLayout)findViewById(R.id.layout)).setVisibility(View.VISIBLE);

        ((TextView)findViewById(R.id.yesButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startUploadDocument(link);
                ((LinearLayout)findViewById(R.id.uploadbox)).setVisibility(View.GONE);
                ((LinearLayout)findViewById(R.id.layout)).setVisibility(View.GONE);
            }
        });

        ((TextView)findViewById(R.id.noButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LinearLayout)findViewById(R.id.uploadbox)).setVisibility(View.GONE);
                ((LinearLayout)findViewById(R.id.layout)).setVisibility(View.GONE);
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

    public void getNoti(final int user_id) {

        String url = ReqConst.SERVER_URL + "getNoti";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());
                parseGetNotiResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                showToast2("We can not detect any internet connectivity. Please check your internet connection and try again.");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("me_id", String.valueOf(Commons.thisUser.get_idx()));
                params.put("user_id", String.valueOf(user_id));

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);
    }

    public void parseGetNotiResponse(String json) {

        try{

            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");
            if(result_code == 0){
                JSONArray data = response.getJSONArray("noti");
                Log.d("NOTI===",data.toString());
                JSONObject jsonData = (JSONObject) data.get(0);
                option = jsonData.getString("option");
                Log.d("OPTION222===>", option);
                if(option.equals("blocked")){
                    chatBlockF = true;
                    isChatBlockedF = false;
                    messageArea.setEnabled(false);
                    messageArea.setSingleLine(false);
                    messageArea.setHint("You can't send messages because the date has ended.");
                    messageArea.setHintTextColor(Color.BLACK);
                    MapUtils.optionMap.put(Commons.user.get_idx(), "blocked");
                }
                else {
                    if(option.equals("block")){
                        isChatBlockedF = true;
                        chatBlockF = true;
                        messageArea.setEnabled(false);
                        messageArea.setSingleLine(false);
                        messageArea.setHint("You can't send messages because the date has ended.");
                        messageArea.setHintTextColor(Color.BLACK);
                        MapUtils.optionMap.put(Commons.user.get_idx(), "block");
                    }else {
                        isChatBlockedF = false;
                        chatBlockF = false;
                        messageArea.setEnabled(true);
                        messageArea.setSingleLine(true);
                        messageArea.setHint("Write a message");
                        messageArea.setHintTextColor(Color.GRAY);
                        MapUtils.optionMap.put(Commons.user.get_idx(), "normal");
                    }
                }

                int unraveled = Integer.parseInt(jsonData.getString("unraveled"));
                int percent = 0;

                if(unraveled == 1) percent = 25;
                else if(unraveled == 2) percent = 50;
                else if(unraveled == 3) percent = 75;
                else if(unraveled == 4) {
                    percent = 100;
                    Picasso.with(getApplicationContext())
                            .load(Commons.user.get_photoUrl())
                            .error(R.mipmap.appicon)
                            .placeholder(R.mipmap.appicon)
                            .into(photo);

                    if(chatPhotos.size() > 0){
                        for(int k=0; k<chatPhotos.size(); k++){
                            Picasso.with(getApplicationContext())
                                    .load(Commons.user.get_photoUrl())
                                    .error(R.mipmap.appicon)
                                    .placeholder(R.mipmap.appicon)
                                    .into(chatPhotos.get(k));
                        }
                    }

                    Picasso.with(getApplicationContext())
                            .load(Commons.user.get_photoUrl())
                            .error(R.mipmap.appicon)
                            .placeholder(R.mipmap.appicon)
                            .into((CircleImageView)findViewById(R.id.picture));
                }

                ((ProgressBar)findViewById(R.id.scorebar)).setProgress(percent);
                ((TextView)findViewById(R.id.txtProgress)).setText(String.valueOf(percent) + " %");

            } else {
                showToast2("We can not detect any internet connectivity. Please check your internet connection and try again.");
            }
        }catch (JSONException e){
            e.printStackTrace();
            showToast2("We can not detect any internet connectivity. Please check your internet connection and try again.");
        }
    }

    public void setUnraveled(final int user_id) {

        String url = ReqConst.SERVER_URL + "riseUnraveled";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());
                parseSetUnraveledResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                showToast2("We can not detect any internet connectivity. Please check your internet connection and try again.");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("me_id", String.valueOf(Commons.thisUser.get_idx()));
                params.put("user_id", String.valueOf(user_id));

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);
    }

    public void parseSetUnraveledResponse(String json) {

        try{

            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");

            if(result_code == 0){
                old_entered = String.valueOf(new Date().getTime());
            } else {
                showToast2("We can not detect any internet connectivity. Please check your internet connection and try again.");
            }
        }catch (JSONException e){
            e.printStackTrace();
            showToast2("We can not detect any internet connectivity. Please check your internet connection and try again.");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        permissionHelper.finish();
        doAsynchronousTask.cancel();
        doAsynchronousTask2.cancel();
        reference22 = null;
        reference1 = null;
        if(mProgresDialog != null) {
            if(mProgresDialog.getVisibility() == View.VISIBLE)
                mProgresDialog.setVisibility(View.GONE);
            mProgresDialog = null;
        }
        online("true");    // Stop typing
    }

    @Override
    public void onResume() {
        super.onResume();
        reference1 = new Firebase(ReqConst.FIREBASE_URL + "message/" + Commons.thisUser.get_idx() + "_" + Commons.user.get_idx());
        reference22 = new Firebase(ReqConst.FIREBASE_URL + "message/" + Commons.user.get_idx() + "_" + Commons.thisUser.get_idx());
        online("true");     // Not typing and be active now
        onOffline("true");  // Active now
    }

    @Override
    protected void onPause() {
        super.onPause();
        online("false");    // Stop typing and be offline now
        onOffline("false");  // Not active now
    }

    public void onOffline(String status){
        Map<String, String> map = new HashMap<String, String>();
        if(status.equals("true"))
            map.put("status", "online");
        else map.put("status", "offline");
        map.put("time", String.valueOf(new Date().getTime()));
        map.put("user", Commons.thisUser.get_email());
        reference8.removeValue();
        reference8.push().setValue(map);
    }

    @Override
    public void onBackPressed() {
        reference7.removeValue();
        reference22 = null;
        reference1 = null;
        if(lastMessage.length() > 0){
            MapUtils.notiMap.put(Commons.user.get_idx(), lastMessage);
            Commons.event3 = true;
            leaveLastMessage(lastMessage);
        }
        else {
            finish();
            overridePendingTransition(R.anim.left_in,R.anim.right_out);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void imageToDonwloadUrl(String path){
        showLoading(true);
        final String[] url = {""};
        final Uri[] uri = {Uri.fromFile(new File(path))};
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance("gs://datingapp-206116.appspot.com");
        StorageReference fileReference = firebaseStorage.getReference();

        UploadTask uploadTask = fileReference.child(uri[0].getLastPathSegment()+ ".jpg").putFile(uri[0]);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                showLoading(false);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                Uri downloadUri = taskSnapshot.getDownloadUrl();
                imageStr = downloadUri.toString();
                Log.d("IMAGE===>",imageStr);
                showLoading(false);
            }
        });
    }

    public void leaveLastMessage(String message) {
        String url = ReqConst.SERVER_URL + "leaveLastMessage";
        showLoading(true);
        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("LastMsgRESPONSE===>", response);
                VolleyLog.v("Response:%n %s", response.toString());
                showLoading(false);
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("debug", error.toString());
                showLoading(false);
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("receiver_id", String.valueOf(Commons.user.get_idx()));
                params.put("sender_id", String.valueOf(Commons.thisUser.get_idx()));
                params.put("last_message", message);
                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);
    }

    public void setActives(final int user_id) {

        String url = ReqConst.SERVER_URL + "setActive";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());
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

                params.put("me_id", String.valueOf(Commons.thisUser.get_idx()));
                params.put("user_id", String.valueOf(user_id));

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);
    }

    private void sendNoti(int user_id){
        Firebase.setAndroidContext(this);
        reference1 = new Firebase(ReqConst.FIREBASE_URL + "message/" + user_id + "_" + Commons.user.get_idx());
        reference2 = new Firebase(ReqConst.FIREBASE_URL + "message/" + Commons.user.get_idx() + "_" + user_id);
        reference3 = new Firebase(ReqConst.FIREBASE_URL + "notification/" + Commons.user.get_idx() + "/" + user_id);
        String messageText = "I have chosen to end our date. Take care & good luck with your search.";
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", messageText);
        map.put("time", String.valueOf(new Date().getTime()));
        map.put("image", "");
        map.put("video", "");
        map.put("lat", "");
        map.put("lon", "");
        map.put("email", Commons.thisUser.get_email());
        map.put("name", Commons.thisUser.get_name());
        map.put("photo", Commons.thisUser.get_photoUrl());

        reference1.push().setValue(map);
        reference2.push().setValue(map);

        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("sender_id", String.valueOf(Commons.thisUser.get_idx()));
        map1.put("sender_name", Commons.thisUser.get_name());
        map1.put("sender_email", Commons.thisUser.get_email());
        map1.put("sender_photo", Commons.thisUser.get_photoUrl());
        map1.put("time", String.valueOf(new Date().getTime()));
        map1.put("msg", messageText);
        reference3.removeValue();
        reference3.push().setValue(map1);

        MapUtils.optionMap.put(Commons.user.get_idx(), "block");
        sendNotification(String.valueOf(Commons.user.get_idx()), messageText, "blocked");
    }

    public void sendNotification(String user_id, String text, String option) {

        String url = ReqConst.SERVER_URL + "sendNotification";
        showLoading(true);

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseRestUrlsResponse(response, option);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                showLoading(false);
                showToast2("We can not detect any internet connectivity. Please check your internet connection and try again.");

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
                params.put("notitext", text);
                params.put("notitime", String.valueOf(new Date().getTime()));
                params.put("option", option);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseRestUrlsResponse(String json, String option) {

        showLoading(false);
        try {

            JSONObject response = new JSONObject(json);

            String success = response.getString("result_code");

            Log.d("Result_Code===> :",success);

            if (success.equals("0")) {
                showToast2("Date ended");
                onBackPressed();
            }
            else {
                showToast2("We can not detect any internet connectivity. Please check your internet connection and try again.. ");
            }

        } catch (JSONException e) {
            showToast2("We can not detect any internet connectivity. Please check your internet connection and try again.. ");
            e.printStackTrace();
        }
    }
}



























