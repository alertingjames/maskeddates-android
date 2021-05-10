package com.date.maskeddates.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.eyalbira.loadingdots.LoadingDots;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ViewUnraveledScoreActivity extends AppCompatActivity {

    ProgressBar scoreBar = null;
    LoadingDots progressBar;
    TextView scoreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_unraveled_score);

        progressBar = (LoadingDots) findViewById(R.id.dots);
        scoreBar = (ProgressBar)findViewById(R.id.scorebar);
        scoreText = (TextView)findViewById(R.id.txtProgress);

        getUnravel();
    }

    public void getUnravel() {

        String url = ReqConst.SERVER_URL + "getNoti";

        progressBar.setVisibility(View.VISIBLE);

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());
                parseCheckActiveResponse(response);
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

                params.put("user_id", String.valueOf(Commons.user.get_idx()));
                params.put("me_id", String.valueOf(Commons.thisUser.get_idx()));

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

    public void parseCheckActiveResponse(String json) {

        progressBar.setVisibility(View.GONE);

        try{

            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");
            if(result_code == 0) {
                JSONArray data = response.getJSONArray("noti");

                Log.d("UNRAVELS===",data.toString());
                JSONObject jsonData = (JSONObject) data.get(0);

                int unraveled = Integer.parseInt(jsonData.getString("unraveled"));

                int percent = 0;

                if(unraveled == 1) percent = 25;
                else if(unraveled == 2) percent = 50;
                else if(unraveled == 3) percent = 75;
                else if(unraveled == 4) percent = 100;

                scoreBar.setProgress(percent);
                scoreText.setText("Unlocked " + String.valueOf(percent) + " %");

            }else
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void closeActivity(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0,0);
    }
}
