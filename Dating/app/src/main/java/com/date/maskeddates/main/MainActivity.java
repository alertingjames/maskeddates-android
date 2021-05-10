package com.date.maskeddates.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.date.maskeddates.commons.Commons;
import com.date.maskeddates.commons.Constants;
import com.date.maskeddates.commons.ReqConst;
import com.date.maskeddates.models.Album;
import com.date.maskeddates.models.Plan;
import com.date.maskeddates.models.User;
import com.date.maskeddates.preference.PrefConst;
import com.date.maskeddates.preference.Preference;
import com.eyalbira.loadingdots.LoadingDots;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";
    LinearLayout facebookButton;
    LoginButton loginButton;
    public static CallbackManager callbackManager;
    private String FEmail, Name, Firstname, Lastname, Id, Gender="", Image_url, Birthday="";
    AccessToken accessToken=null;
    User user = null;
    GifView progressBar;
    SpotsDialog _progressDlg;
    FrameLayout frameLayout;
    String eml = "";
    LoadingDots loadingDots;
    GoogleSignInClient mGoogleSignInClient;
    int pictureCount = 0;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        checkAllPermission();

        eml = Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREFKEY_USEREMAIL, "");

        progressBar=(GifView) findViewById(R.id.progressBar);
        _progressDlg = (SpotsDialog) new SpotsDialog.Builder()
                .setContext(this)
                .setTheme(R.style.Custom)
                .build();

        loadingDots = (LoadingDots)findViewById(R.id.dots);
        frameLayout = (FrameLayout)findViewById(R.id.background);

        Commons.edit_profile_flag=false;
        Commons.questionnaires.clear();
        Commons.questionnaires2.clear();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        getMembership();

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Comfortaa_Bold.ttf");
        ((TextView)findViewById(R.id.subtitle)).setTypeface(font);

        try {

            PackageInfo info = getPackageManager().getPackageInfo("com.date.maskeddates", PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");

                md.update(signature.toByteArray());
                Log.i("KeyHash::", Base64.encodeToString(md.digest(), Base64.DEFAULT));//will give developer key hash
                //            Toast.makeText(getApplicationContext(), Base64.encodeToString(md.digest(), Base64.DEFAULT), Toast.LENGTH_LONG).show(); //will give app key hash or release key hash

            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        loginButton= (LoginButton)findViewById(R.id.login_button);

        facebookButton=(LinearLayout)findViewById(R.id.lytfacebook);
        facebookButton.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        facebookButton.setBackground(getDrawable(R.drawable.light_green_button_style));
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        facebookButton.setBackground(getDrawable(R.drawable.button_style));
                        loginWithFB();
            //            loginButton.performClick();
                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        facebookButton.getBackground().clearColorFilter();
                        facebookButton.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private static final int RC_SIGN_IN = 9001;

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount acct){
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            Log.d("GAccount===>", personEmail + "/" + personName + "/" + personPhoto + "/" + personFamilyName);
            user = new User();
            if (personGivenName == null) personGivenName = "";
            user.set_firstName(personGivenName);
            if (personName == null) personName = "";
            user.set_name(personName);
            user.set_email(personEmail);
            user.set_gender(Gender);
            user.set_birthday(Birthday);
            if (personFamilyName == null) personFamilyName = "";
            user.set_lastName(personFamilyName);
            if (personPhoto == null) user.set_fbPhoto("");
            else user.set_fbPhoto(personPhoto.toString());
            Commons.thisUser = user;
            login(false);
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

    private void loginWithFB() {
        // set permissions
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();

                // Facebook Email address
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                Log.v("LoginActivity Response ", response.toString());

                                try {

                                    user=new User();

                                    Name = object.getString("name");
                                    Name.trim();
                                    Id = object.getString("id");
                                    Firstname = object.getString("first_name");
                                    Lastname = object.getString("last_name");
                                    FEmail = object.getString("email");
                                    Image_url = "http://graph.facebook.com/(Id)/picture?type=large";
                                    Image_url = URLEncoder.encode(Image_url);
                                    Log.d("Email = ", " " + FEmail);
                                    Log.d("Name======", Name);
                                    Log.d("Image====",Image_url.toString());
                                    Log.d("firstName======", Firstname);
                                    Log.d("lastName======", Lastname);
                                    Log.d("id======", Id);
                                    Log.d("Object=====>", object.toString());
                                    Log.d("photourl======", Image_url.toString());

                                    if (object.has("picture")) {
                                        JSONObject jsonPicture = object.getJSONObject("picture");
                                        if (jsonPicture.has("data")) {
                                            JSONObject jsonData = jsonPicture.getJSONObject("data");
                                            if (jsonData.has("url"))
                                                user.set_fbPhoto(jsonData.getString("url"));
                                        }
                                    }

//                                    Gender = object.getString("gender");
//                                    Log.d("Gender======", Gender);
//
//                                    try{
//                                        Birthday=object.getString("birthday");
//                                        Log.d("Birthday: ",Birthday);
//                                    }catch (JSONException e){
//                                        e.printStackTrace();
//                                    }

//                                    ArrayList<Album> Albums = new ArrayList<>();
//                                    try {
//                                        JSONArray albumsJSArray = object.getJSONObject("albums").getJSONArray("data");
//                                        getAlbumsArray(albumsJSArray, Albums, MainActivity.this);
//
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                        showToast("This test user doesn't get user_photos permission.");
//                                    }

                                    user.set_firstName(Firstname);
                                    user.set_lastName(Lastname);
                                    user.set_name(Name);
                                    user.set_email(FEmail);
                                    user.set_gender(Gender);
                                    user.set_birthday(Birthday);

                                    Commons.thisUser=user;

                                    Log.d("EMAIL===>", Commons.thisUser.get_email());

                                    //  getFBFriendsList(accessToken);
                                    login(false);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, first_name, last_name, email, gender, birthday, picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                LoginManager.getInstance().logOut();

            }

            @Override
            public void onError(FacebookException e) {

            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    // Extract albums' data from JSON Arrays to Album ArrayList
    private void getAlbumsArray(JSONArray albumsJSArray, ArrayList<Album> Albums, Activity activity) {
        for (int i = 0; i < albumsJSArray.length(); i++) {
            //getting Albums names and id
            try {
                Albums.add(new Album(albumsJSArray.getJSONObject(i).getString("name"), albumsJSArray.getJSONObject(i).getString("id")));
                if (albumsJSArray.getJSONObject(i).has("photos")) {
                    JSONArray photos = albumsJSArray.getJSONObject(i).getJSONObject("photos").getJSONArray("data");
                    Log.d("LENGTH===>", String.valueOf(photos.length()));
                    for (int j = 0; j < photos.length(); j++) {
                        //getting Album photos
                        Albums.get(i).getPhotosUrls().add(j, photos.getJSONObject(j).getJSONArray("images").getJSONObject(0).getString("source"));
                        Log.d("PHOTOS===>", photos.getJSONObject(j).getJSONArray("images").getJSONObject(0).getString("source"));
                        Commons.photoUrls.add(photos.getJSONObject(j).getJSONArray("images").getJSONObject(0).getString("source"));
                    }
                } else {
                    //setting a default icon if the Album is empty
                    Albums.get(i).getPhotosUrls().add(activity.getApplicationContext().getResources().getString(R.string.default_album_cover));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public void login(final boolean option) {

        String url = "";
        if(option)url = ReqConst.SERVER_URL + "loginMember";
        else url = ReqConst.SERVER_URL + "login2Member";

 //       frameLayout.setVisibility(View.VISIBLE);
//        _progressDlg.show();
        loadingDots.setVisibility(View.VISIBLE);
        loadingDots.startAnimation();

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseRestUrlsResponse1(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                frameLayout.setVisibility(View.GONE);
//                _progressDlg.dismiss();
                loadingDots.setVisibility(View.GONE);
                loadingDots.stopAnimation();
                Log.d("debug", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("email", Commons.thisUser.get_email());
                params.put("phone_imei", getDeviceIMEI());  Log.d("DEVICE+++", getDeviceIMEI());
                if(!option) params.put("fb_photo", Commons.thisUser.get_fbPhoto());

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseRestUrlsResponse1(String json) {

//        frameLayout.setVisibility(View.GONE);
//        _progressDlg.dismiss();
        loadingDots.setVisibility(View.GONE);
        loadingDots.stopAnimation();
        try {
            JSONObject response = new JSONObject(json);

            String result_code = response.getString("result_code");

            Log.d("result===", String.valueOf(result_code));

            if (result_code.equals("0")) {

                //==========================================================

                JSONArray userInfo = response.getJSONArray("data");

                JSONObject jsonUser = (JSONObject) userInfo.get(0);

                Commons.thisUser.set_idx(jsonUser.getInt("id"));
                Commons.thisUser.set_email(jsonUser.getString("email"));
                Commons.thisUser.set_photoUrl(jsonUser.getString("photo_url"));
                Commons.thisUser.set_fbPhoto(jsonUser.getString("fb_photo"));
                Commons.thisUser.set_gender(jsonUser.getString("gender"));
                Commons.thisUser.set_age(jsonUser.getString("age"));
                Commons.thisUser.set_address(jsonUser.getString("address"));
                Commons.thisUser.set_name(jsonUser.getString("name"));
                Commons.thisUser.set_lat(jsonUser.getString("lat"));
                Commons.thisUser.set_lng(jsonUser.getString("lng"));
                Commons.thisUser.set_answers(jsonUser.getString("answers"));
                Commons.thisUser.set_answers2(jsonUser.getString("answers2"));

                Commons.thisUser.set_premium(jsonUser.getString("premium"));
                Commons.thisUser.set_photos(jsonUser.getString("photos"));
                Commons.thisUser.set_photo_unlock(jsonUser.getString("photo_unlock"));
                Commons.thisUser.set_selfie_approved(jsonUser.getString("selfie_approved"));
                Commons.thisUser.set_lastlogin(jsonUser.getString("last_login"));
                Commons.thisUser.set_actives(jsonUser.getInt("actives"));

                Commons.thisUser.set_phone(jsonUser.getString("phone"));             /////////////////////////////////////////// Updated membership //////////////////////////////////////////
                Commons.thisUser.set_phone_imei(jsonUser.getString("phone_imei"));

                if(jsonUser.getString("password").equals("deactivated")){
                    ((LinearLayout)findViewById(R.id.alertbox)).setVisibility(View.VISIBLE);
                    frameLayout.setVisibility(View.VISIBLE);
                    return;
                }

                if(jsonUser.getString("password").equals("canceled")){
                    ((LinearLayout)findViewById(R.id.alertbox)).setVisibility(View.VISIBLE);
                    ((TextView)((LinearLayout)findViewById(R.id.alertbox)).findViewById(R.id.alertDescription)).setText("Your account have already been deactivated permanently.\nTry another account.");
                    frameLayout.setVisibility(View.VISIBLE);
                    return;
                }

                if(Commons.thisUser.get_premium().length() > 0)
                    parsePremium(Commons.thisUser.get_premium());
                else Commons.max_dates = Commons.plan.getDates();    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                Preference.getInstance().put(getApplicationContext(),
                        PrefConst.PREFKEY_USEREMAIL, Commons.thisUser.get_email());

                getPictures(Commons.thisUser.get_idx());
            }else if(result_code.equals("100")){
                String email = response.getString("email");
                String xx = "";
                for(int i=0; i<email.substring(3, email.indexOf("@")).length(); i++){
                    xx = xx + "X";
                }
                String emailStr = email.substring(0,3) + xx + email.substring(email.indexOf("@"), email.length());
                Log.d("EMAILSTR+++", emailStr);
                showToast("You have already registered with us using " + emailStr + ". Please login with that account.");
                Preference.getInstance().put(getApplicationContext(), PrefConst.PREFKEY_USEREMAIL, "");
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {

                            }
                        });
            }
            else {
                Intent intent=new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void parsePremium(String premium){
        long expired = 0; int max_dates = Commons.plan.getDates();
        int[] max_dates_list = new int[]{0, Commons.plan.getDates1(), Commons.plan.getDates2(), Commons.plan.getDates3()};
        if(premium.contains("_")){
            expired = Long.parseLong(premium.substring(0, premium.indexOf("_")));
            max_dates = max_dates_list[Integer.parseInt(premium.substring(premium.indexOf("_") + 1, premium.length()))];
        }

        Commons.premium_expired = expired;
        Commons.max_dates = max_dates;

        if(new Date().getTime() >= Commons.premium_expired){
            Commons.max_dates = Commons.plan.getDates();   ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            Commons.premium_expired_flag = true;
        }
    }

    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void getMembership() {

        String url = ReqConst.SERVER_URL + "getmembership";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseGetMembershipResponse1(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("debug", error.toString());
            }
        }) {

        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseGetMembershipResponse1(String json) {

        try {
            JSONObject response = new JSONObject(json);

            String result_code = response.getString("result_code");

            Log.d("result===", String.valueOf(result_code));

            if (result_code.equals("0")) {

                //==========================================================

                JSONArray planInfo = response.getJSONArray("data");

                JSONObject jsonPlan = (JSONObject) planInfo.get(0);

                Plan plan = new Plan();
                plan.setIdx(jsonPlan.getInt("id"));
                plan.setMonths(0);
                plan.setDates(jsonPlan.getInt("dates"));
                plan.setPrice(0.0f);

                plan.setMonths1(jsonPlan.getInt("months1"));
                plan.setDates1(jsonPlan.getInt("dates1"));
                plan.setPrice1(jsonPlan.getDouble("price1"));

                plan.setMonths2(jsonPlan.getInt("months2"));
                plan.setDates2(jsonPlan.getInt("dates2"));
                plan.setPrice2(jsonPlan.getDouble("price2"));

                plan.setMonths3(jsonPlan.getInt("months3"));
                plan.setDates3(jsonPlan.getInt("dates3"));
                plan.setPrice3(jsonPlan.getDouble("price3"));

                Commons.plan = plan;
                if(eml.length() == 0){
                    if (AccessToken.getCurrentAccessToken() != null && com.facebook.Profile.getCurrentProfile() != null){
                        //Logged in so show the login button
                        LoginManager.getInstance().logOut();
                    }
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                            new ResultCallback<Status>() {
                                @Override
                                public void onResult(Status status) {

                                }
                            });
                }
                else {
                    Commons.thisUser.set_email(eml);
                    login(true);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void closeAlert(View view){
        ((LinearLayout)findViewById(R.id.alertbox)).setVisibility(View.GONE);
        frameLayout.setVisibility(View.GONE);
        if (AccessToken.getCurrentAccessToken() != null && com.facebook.Profile.getCurrentProfile() != null){
            //Logged in so show the login button
            LoginManager.getInstance().logOut();
        }
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });

    }

    public void terms(View view){
        Intent intent = new Intent(getApplicationContext(), TermsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void privacy(View view){
        Intent intent = new Intent(getApplicationContext(), PrivacyActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void getPictures(int user_id) {

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
        try{

            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");
            if(result_code == 0) {
                Log.d("PICTURES===>", response.toString());
                try{
                    JSONArray data = response.getJSONArray("pictures");
                    pictureCount = data.length();
                    Intent intent;
                    if(pictureCount < 2){
                        intent = new Intent(getApplicationContext(), UploadPicturesActivity.class);
                        startActivity(intent);
                    }
                    else {

                        if(Commons.thisUser.get_address().length() == 0) {
                            intent = new Intent(getApplicationContext(), MapActivity.class);
                            startActivity(intent);
                        }else if(Commons.thisUser.get_answers().length() == 0) {
                            intent = new Intent(getApplicationContext(), QuestionnaireActivity.class);
                            startActivity(intent);
                        }else if(Commons.thisUser.get_answers2().length() == 0) {
                            intent = new Intent(getApplicationContext(), Questionnaire2Activity.class);
                            startActivity(intent);
                        }else if(Commons.thisUser.get_selfie_approved().equals("no")) {
                            String selfie_updated = Preference.getInstance().getValue(getApplicationContext(), PrefConst.SELFIE_UPDATED, "");
                            if(selfie_updated.length() == 0){
                                intent = new Intent(getApplicationContext(), UpdateSelfieActivity.class);
                                startActivity(intent);
                            }else {
                                intent = new Intent(getApplicationContext(), MyWallActivity.class);
                                startActivity(intent);
                            }
                        }else {
                            intent = new Intent(getApplicationContext(), MyWallActivity.class);
                            startActivity(intent);
                        }
                    }
                    finish();
                    overridePendingTransition(R.anim.right_in,R.anim.left_out);
                }catch (JSONException e){}
            }else
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * Returns the unique identifier for the device
     *
     * @return unique identifier for the device
     */
    public String getDeviceIMEI() {
        String deviceUniqueIdentifier = null;
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            deviceUniqueIdentifier = tm.getDeviceId();
        }
        if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
            deviceUniqueIdentifier = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceUniqueIdentifier;
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

    private void showPictureJsonData(JSONObject response){

    }

}





























