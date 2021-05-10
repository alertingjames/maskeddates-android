package com.date.maskeddates.main;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.date.maskeddates.Config.MapUtils;
import com.date.maskeddates.MyApplication;
import com.date.maskeddates.R;
import com.date.maskeddates.adapters.NotificationAdapter;
import com.date.maskeddates.commons.Commons;
import com.date.maskeddates.commons.Constants;
import com.date.maskeddates.commons.ReqConst;
import com.date.maskeddates.models.Conversation;
import com.date.maskeddates.models.Unravel;
import com.date.maskeddates.models.User;
import com.eyalbira.loadingdots.LoadingDots;
import com.firebase.client.Firebase;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationsActivity extends AppCompatActivity implements SwipyRefreshLayout.OnRefreshListener {

    ImageView searchButton, cancelButton;
    LinearLayout searchBar;
    EditText ui_edtsearch;
    TextView title;
    SwipyRefreshLayout ui_RefreshLayout;
    private LoadingDots progressBar;
    ListView list;
    NotificationAdapter adapter = new NotificationAdapter(this);
    ArrayList<Conversation> conversations = new ArrayList<>();

    Conversation conversation = new Conversation();

    TextView enddatesbutton, premiumbutton;
    LinearLayout alertbox;
    View layout;
    Unravel unravel = null;
    Firebase ref;
    FrameLayout userButton, chatButton, usersButton;
    int exitF = 0;

    FrameLayout noticon;
    TextView count;

    private Handler mHandler = new Handler();
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_notifications);

        Firebase.setAndroidContext(getApplicationContext());

        noticon = (FrameLayout) findViewById(R.id.badge);
        count = (TextView)findViewById(R.id.count);

        ref = new Firebase(ReqConst.FIREBASE_URL + "sts/" + Commons.thisUser.get_idx());

        online("true", exitF);

        progressBar = (LoadingDots) findViewById(R.id.progressBar);

        title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Comfortaa_Bold.ttf");
        title.setTypeface(font);

        ui_RefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        ui_RefreshLayout.setOnRefreshListener(this);
        ui_RefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);

        searchBar = (LinearLayout)findViewById(R.id.search_bar);
        searchButton = (ImageView)findViewById(R.id.searchButton);
        cancelButton = (ImageView)findViewById(R.id.cancelButton);

        userButton = (FrameLayout) findViewById(R.id.userbutton);
        chatButton = (FrameLayout) findViewById(R.id.chatbutton);
        usersButton = (FrameLayout) findViewById(R.id.usersbutton);
        usersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitF = 2;
                online("false", exitF);
            }
        });
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitF = 1;
                online("false", exitF);
            }
        });

        layout = (View) findViewById(R.id.layout);

        ui_edtsearch = (EditText)findViewById(R.id.edt_search);
        ui_edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = ui_edtsearch.getText().toString().toLowerCase(Locale.getDefault());
                if (text.length() != 0) {
                    adapter.filter(text);
                }else  {
                    adapter.setDatas(conversations);
                    list.setAdapter(adapter);
                }

            }
        });

        list = (ListView) findViewById(R.id.list);

        alertbox = (LinearLayout) findViewById(R.id.alertbox);

        enddatesbutton = (TextView) findViewById(R.id.enddates);
        enddatesbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertbox.setVisibility(View.GONE);
                layout.setVisibility(View.GONE);
                Intent intent = new Intent(getApplicationContext(), EndDatesActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
        premiumbutton = (TextView) findViewById(R.id.premium);
        if(Commons.thisUser.get_premium().contains("_3"))
            premiumbutton.setVisibility(View.GONE);
        premiumbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertbox.setVisibility(View.GONE);
                layout.setVisibility(View.GONE);
                Intent intent = new Intent(getApplicationContext(), PremiumGuideActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

        getActives(Commons.thisUser.get_idx());

        timer.schedule(doAsynchronousTask2, 0, 2000);

        getConversations();

    }

    int cnt = 0;
    TimerTask doAsynchronousTask2 = new TimerTask() {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    cnt++;
                    if(Commons.notifications.size() > 0) {
                        noticon.setVisibility(View.VISIBLE);
                        count.setText(String.valueOf(Commons.notifications.size()));
                    }else {
                        noticon.setVisibility(View.GONE);
                    }
                    if(Commons.event){
                        getConversationsForNotis();
                    }
                    if(Commons.event2){
                        getConversationsForOnlines();
                    }
                    if(Commons.event3){
                        updateConversation();
                    }
                    if(cnt % 2 == 0)
                        getActives(Commons.thisUser.get_idx());
                    if(cnt >= 100)
                        cnt = 0;
                }
            });
        }
    };

    public void closeAlert(View view){
        alertbox.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);
    }

    public void activeDates(View view){
        Intent intent = new Intent(getApplicationContext(), EndDatesActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0,0);
    }

    public void online(String status, int exitf){
        Map<String, String> map = new HashMap<String, String>();
        if(status.equals("true"))
            map.put("status", "online");
        else map.put("status", "offline");
        map.put("time", String.valueOf(new Date().getTime()));
        map.put("user", Commons.thisUser.get_email());
        ref.removeValue();
        ref.push().setValue(map);
        if(status.equals("false")){
            if(exitf == 1){
                Intent intent = new Intent(getApplicationContext(), MyWallActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
            }else if(exitf == 2){
                Intent intent = new Intent(getApplicationContext(), UsersActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
            }
        }
    }

    public void searchNoti(View view){
        cancelButton.setVisibility(View.VISIBLE);
        searchButton.setVisibility(View.GONE);
        searchBar.setVisibility(View.VISIBLE);
        title.setVisibility(View.GONE);
    }

    public void cancelSearch(View view){
        cancelButton.setVisibility(View.GONE);
        searchButton.setVisibility(View.VISIBLE);
        searchBar.setVisibility(View.GONE);
        title.setVisibility(View.VISIBLE);
    }

    public void getConversations() {

        conversations.clear();
        ui_RefreshLayout.setRefreshing(true);

        String url = ReqConst.SERVER_URL + "getNotifications";

        progressBar = (LoadingDots) findViewById(R.id.progressBar);
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
                progressBar = null;
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again..");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("user_id", String.valueOf(Commons.thisUser.get_idx()));

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);
    }

    public void parseGetNotificationsResponse(String json) {

        ui_RefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
        progressBar = null;

        try{

            JSONObject response = new JSONObject(json);  Log.d("RESPONSE===>",response.toString());

            int result_code = response.getInt("result_code");

            if(result_code == 0){
                JSONArray data = response.getJSONArray("data");
//                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFO);
                Log.d("DATA===",data.toString());

                for (int i = 0; i < data.length(); i++) {

                    JSONObject jsonData = (JSONObject) data.get(i);

                    Conversation notification = new Conversation();

                    notification.setIdx(jsonData.getInt("id"));
                    notification.setUser_id(jsonData.getInt("user_id"));
                    notification.setSender_id(jsonData.getInt("sender_id"));
                    notification.setSender_name(jsonData.getString("sender_name"));
                    notification.setSender_email(jsonData.getString("sender_email"));
                    notification.setSender_photo(jsonData.getString("sender_photo"));
                    notification.setNotitext(jsonData.getString("notitext"));
                    notification.setNotitime(jsonData.getString("notitime"));
                    notification.setEnteredtime(jsonData.getString("enteredtime"));
                    notification.setExitedtime(jsonData.getString("exitedtime"));
                    if(jsonData.getString("unraveled").length() > 0)
                        notification.setUnraveled(jsonData.getInt("unraveled"));
                    else notification.setUnraveled(0);
                    notification.setOption(jsonData.getString("option"));
                    notification.setStatus(jsonData.getString("status"));
                    notification.setActive(jsonData.getInt("active"));

                    conversations.add(0, notification);
                }

                if(conversations.isEmpty()){
                    ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
                }else {
                    ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);
                    if(Commons.notifications.size() > 0) {
                        getConversationsForNotis();
                        getConversationsForOnlines();
                    }
                    else {
                        adapter.setDatas(conversations);
                        adapter.notifyDataSetChanged();
                        list.setAdapter(adapter);
//                        setNotis(createNotiJsonString());
                    }
                }

            } else {
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again..");
            }
        }catch (JSONException e){
            e.printStackTrace();
            showToast("We can not detect any internet connectivity. Please check your internet connection and try again..");
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void readNotification(Conversation noti){
        conversation = noti;
        getUser();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    public void getUser() {

        progressBar = (LoadingDots) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        String url = ReqConst.SERVER_URL + "getUser";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());

                parseGetUserResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                progressBar = null;
                Log.d("debug", error.toString());
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again..");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("user_id", String.valueOf(conversation.getSender_id()));

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);
    }

    public void parseGetUserResponse(String json) {

        if(progressBar != null){
            progressBar.setVisibility(View.GONE);
            progressBar = null;
        }

        try{

            JSONObject response = new JSONObject(json);  Log.d("RESPONSE===>",response.toString());

            int result_code = response.getInt("result_code");

            if(result_code == 0){
                JSONArray users = response.getJSONArray("users");

                Log.d("USERS===",users.toString());
                JSONObject jsonUser = (JSONObject) users.get(0);

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

                Commons.user = user;

                Log.d("Current/Max===>", String.valueOf(Commons.thisUser.get_actives()) + "/" + String.valueOf(Commons.max_dates));
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                if(conversation.getOption().equals("requested")){
                    if(Commons.thisUser.get_actives() >= Commons.max_dates) {
                        alertbox.setVisibility(View.VISIBLE);
                        layout.setVisibility(View.VISIBLE);
                        return;
                    }
                    if(conversation.getUnraveled() == 0) {
                        Intent intent = new Intent(getApplicationContext(), AcceptDeclineActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_in,R.anim.left_out);
                    }
                }else if(conversation.getOption().equals("declined")){
                    Firebase decilinedRef = new Firebase(ReqConst.FIREBASE_URL + "notification/" + Commons.thisUser.get_idx() + "/" + conversation.getSender_id());
                    decilinedRef.removeValue();
                    showToast("That user is unavaliable.");
                }
                else if(conversation.getOption().equals("blocked")){
                    if(Commons.thisUser.get_actives() >= Commons.max_dates) {
                        if(conversation.getActive() != 1) {
                            alertbox.setVisibility(View.VISIBLE);
                            layout.setVisibility(View.VISIBLE);
                            return;
                        }
                    }
                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                    intent.putExtra("old_entered", conversation.getEnteredtime());
                    intent.putExtra("unraveled", conversation.getUnraveled());
                    intent.putExtra("option", conversation.getOption());
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in,R.anim.left_out);
                }else {
                    if(Commons.thisUser.get_actives() >= Commons.max_dates) {
                        if(conversation.getActive() != 1) {
                            alertbox.setVisibility(View.VISIBLE);
                            layout.setVisibility(View.VISIBLE);
                            return;
                        }
                    }
                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                    intent.putExtra("old_entered", conversation.getEnteredtime());
                    intent.putExtra("unraveled", conversation.getUnraveled());
                    intent.putExtra("option", conversation.getOption());
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in,R.anim.left_out);
                }

                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


            } else {
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again..");
            }
        }catch (JSONException e){
            e.printStackTrace();
            showToast("We can not detect any internet connectivity. Please check your internet connection and try again..");
        }
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

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());
                parseGetActivesResponse(response);
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

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);
    }

    public void parseGetActivesResponse(String json) {
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
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public String createNotiJsonString()throws JSONException {

        String jsonNoti = "";
        JSONObject jsonObj = null;
        JSONArray jsonArr = new JSONArray();
        if (Commons.notifications.size()>0){
            for(int i=0; i<Commons.notifications.size(); i++){

                String idx = String.valueOf(Commons.notifications.get(i).getIdx());
                String notitext = Commons.notifications.get(i).getMessage();
                String datetime = Commons.notifications.get(i).getDate_time();

                jsonObj=new JSONObject();

                try {
                    jsonObj.put("noti_id",idx);
                    jsonObj.put("notitext",notitext);
                    jsonObj.put("datetime",datetime);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                jsonArr.put(jsonObj);
            }
            JSONObject notiObj = new JSONObject();
            notiObj.put("notijson", jsonArr);
            jsonNoti = notiObj.toString();
            return jsonNoti;
        }
        return jsonNoti;
    }

    public void setNotis(String notiJsonStr) {

        String url = ReqConst.SERVER_URL + "setNotifications";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseSetNotisResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again..");

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("notifications", notiJsonStr);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseSetNotisResponse(String json) {

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

    public void exitChat() {

        String url = ReqConst.SERVER_URL + "exitChat";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());
                parseExitChatResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again..");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("me_id", String.valueOf(Commons.thisUser.get_idx()));

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);
    }

    public void parseExitChatResponse(String json) {

        try{

            JSONObject response = new JSONObject(json);  Log.d("RESPONSE===>",response.toString());

            int result_code = response.getInt("result_code");

            if(result_code == 0){
                online("false", exitF);
                Commons.active_userids.clear();
            } else {
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again..");
            }
        }catch (JSONException e){
            e.printStackTrace();
            showToast("We can not detect any internet connectivity. Please check your internet connection and try again..");
        }
    }

    @Override
    public void onBackPressed() {
        exitF = 1;
        online("false", exitF);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doAsynchronousTask2.cancel();
    }

    @Override
    protected void onPause() {
        super.onPause();
        online("false", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        online("true", 0);
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {

    }

    private void getConversationsForNotis(){
        Commons.notifiedConversationIds.clear();
        for(int i=0; i<Commons.notifications.size(); i++){
            Commons.notifiedConversationIds.add(Commons.notifications.get(i).getSender_id());
            MapUtils.notiMap.put(Commons.notifications.get(i).getSender_id(), Commons.notifications.get(i).getMessage());
        }
        adapter.setDatas(conversations);
        adapter.notifyDataSetChanged();
        list.setAdapter(adapter);
        Commons.event = false;
    }

    private void getConversationsForOnlines(){
        Commons.onlineConversationIds.clear();
        for(int i=0; i<Commons.onlines.size(); i++){
            Commons.onlineConversationIds.add(Commons.onlines.get(i).getUser());
        }
        adapter.setDatas(conversations);
        adapter.notifyDataSetChanged();
        list.setAdapter(adapter);
        Commons.event2 = false;
    }

    private void updateConversation(){
        adapter.setDatas(conversations);
        adapter.notifyDataSetChanged();
        list.setAdapter(adapter);
        Commons.event3 = false;
    }

    @Override
    public void finish() {
        super.finish();
        doAsynchronousTask2.cancel();
        if(progressBar != null) {
            if(progressBar.getVisibility() == View.VISIBLE)
                progressBar.setVisibility(View.GONE);
            progressBar = null;
        }
    }
}




































