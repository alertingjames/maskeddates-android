package com.date.maskeddates.main;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
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
import com.date.maskeddates.MyApplication;
import com.date.maskeddates.R;
import com.date.maskeddates.adapters.ActiveDatesAdapter;
import com.date.maskeddates.commons.Commons;
import com.date.maskeddates.commons.Constants;
import com.date.maskeddates.commons.ReqConst;
import com.date.maskeddates.models.Conversation;
import com.eyalbira.loadingdots.LoadingDots;
import com.firebase.client.Firebase;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EndDatesActivity extends AppCompatActivity implements SwipyRefreshLayout.OnRefreshListener {

    ImageView searchButton, cancelButton;
    LinearLayout searchBar;
    EditText ui_edtsearch;
    TextView title;
    SwipyRefreshLayout ui_RefreshLayout;
    private LoadingDots progressBar;
    ListView list;
    ActiveDatesAdapter adapter = new ActiveDatesAdapter(this);
    ArrayList<Conversation> notifications = new ArrayList<>();

    Firebase reference1, reference2, reference3, reference4;
    Conversation notification = new Conversation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_end_dates);

        progressBar = (LoadingDots) findViewById(R.id.progressBar);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
//                onBackPressed();
                Intent intent = new Intent(getApplicationContext(), NotificationsActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
            }
        });

        ((TextView)findViewById(R.id.maxdates)).setText(String.valueOf(Commons.max_dates));    Log.d("MaxDates===>", String.valueOf(Commons.max_dates));

        title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Comfortaa_Bold.ttf");
        title.setTypeface(font);

        ui_RefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        ui_RefreshLayout.setOnRefreshListener(this);
        ui_RefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);

        searchBar = (LinearLayout)findViewById(R.id.search_bar);
        searchButton = (ImageView)findViewById(R.id.searchButton);
        cancelButton = (ImageView)findViewById(R.id.cancelButton);

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
                    adapter.setDatas(notifications);
                    list.setAdapter(adapter);
                }

            }
        });
        list = (ListView) findViewById(R.id.list);

        getActiveDates();

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

    private void showLoading(boolean isShow){
        if (isShow){
            progressBar = (LoadingDots)findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            if(progressBar != null){
                progressBar.setVisibility(View.GONE);
                progressBar = null;
            }
        }
    }

    public void getActiveDates() {

        ui_RefreshLayout.setRefreshing(true);

        String url = ReqConst.SERVER_URL + "getActiveDates";

        showLoading(true);

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
                showLoading(false);
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
        showLoading(false);

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
                    notification.setUnraveled(jsonData.getInt("unraveled"));
                    notification.setOption(jsonData.getString("option"));
                    notification.setStatus(jsonData.getString("status"));
                    notification.setActive(jsonData.getInt("active"));

                    notifications.add(0, notification);
                }

                if(notifications.isEmpty()){
                    ((ImageView)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
                    list.setVisibility(View.GONE);
                    showToast("No active date(s).");
                }else {
                    ((ImageView)findViewById(R.id.no_result)).setVisibility(View.GONE);
                    list.setVisibility(View.VISIBLE);
                }

                adapter.setDatas(notifications);
                adapter.notifyDataSetChanged();
                list.setAdapter(adapter);

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


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void endDate(Conversation noti){
        notification = noti;
        sendNoti(Commons.thisUser.get_idx());
    }

    public void dateAgain(Conversation noti){
        notification = noti;
        releaseBlocked(String.valueOf(notification.getSender_id()));
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void sendNoti(int user_id){
        Firebase.setAndroidContext(this);
        reference1 = new Firebase(ReqConst.FIREBASE_URL + "message/" + user_id + "_" + notification.getSender_id());
        reference2 = new Firebase(ReqConst.FIREBASE_URL + "message/" + notification.getSender_id() + "_" + user_id);
        reference3 = new Firebase(ReqConst.FIREBASE_URL + "notification/" + notification.getSender_id() + "/" + user_id);
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
        sendNotification(String.valueOf(notification.getSender_id()), messageText, "blocked");
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
                showToast("Date ended");
                notifications.clear();
                getActiveDates();
            }
            else {
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.. ");
            }

        } catch (JSONException e) {
            showToast("We can not detect any internet connectivity. Please check your internet connection and try again.. ");
            e.printStackTrace();
        }
    }

    public void releaseBlocked(String user_id) {

        String url = ReqConst.SERVER_URL + "releaseBlock";
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
                showToast("Date allowed");
                notifications.clear();
                getActiveDates();
            }
            else {
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.. ");
            }

        } catch (JSONException e) {
            showToast("We can not detect any internet connectivity. Please check your internet connection and try again.. ");
            e.printStackTrace();
        }
    }

    public void online(String status){
        reference4 = new Firebase(ReqConst.FIREBASE_URL + "status/" + notification.getSender_id() + "_" + Commons.thisUser.get_idx());
        Map<String, String> map = new HashMap<String, String>();
        if(status.equals("true"))
            map.put("online", "online");
        else map.put("online", "offline");
        map.put("time", String.valueOf(new Date().getTime()));
        map.put("user", Commons.thisUser.get_email());
        reference4.removeValue();
        reference4.push().setValue(map);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), NotificationsActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    @Override
    public void finish() {
        super.finish();
        if(progressBar != null) {
            if(progressBar.getVisibility() == View.VISIBLE)
                progressBar.setVisibility(View.GONE);
            progressBar = null;
        }
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {

    }
}




































