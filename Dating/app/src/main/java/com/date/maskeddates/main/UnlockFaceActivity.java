package com.date.maskeddates.main;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.eyalbira.loadingdots.LoadingDots;
import com.github.mmin18.widget.RealtimeBlurView;
import com.skyfishjy.library.RippleBackground;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class UnlockFaceActivity extends AppCompatActivity {

    ProgressBar scoreBar = null;
    LoadingDots progressBar;
    ImageView picture;
    TextView scoreText;
    RippleBackground rippleBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_face);

        rippleBackground = (RippleBackground)findViewById(R.id.content);

        progressBar = (LoadingDots) findViewById(R.id.dots);
        scoreBar = (ProgressBar)findViewById(R.id.scorebar);
        scoreText = (TextView)findViewById(R.id.txtProgress);

        picture = (ImageView) findViewById(R.id.picture);
        Picasso.with(getApplicationContext())
                .load(Commons.user.get_photoUrl())
                .transform(new RoundedCornersTransformation(30, 0,
                        RoundedCornersTransformation.CornerType.ALL))
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)
                .into(picture);

        getUnravel();
    }

    public void back(View view){
        onBackPressed();
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
                if(unraveled == 1){
                    ((RealtimeBlurView)findViewById(R.id.blur1)).setVisibility(View.INVISIBLE);
                    ((RealtimeBlurView)findViewById(R.id.blur9)).setVisibility(View.INVISIBLE);
                }else if(unraveled == 2){
                    ((RealtimeBlurView)findViewById(R.id.blur1)).setVisibility(View.INVISIBLE);
                    ((RealtimeBlurView)findViewById(R.id.blur9)).setVisibility(View.INVISIBLE);
                    ((RealtimeBlurView)findViewById(R.id.blur3)).setVisibility(View.INVISIBLE);
                    ((RealtimeBlurView)findViewById(R.id.blur7)).setVisibility(View.INVISIBLE);
                }else if(unraveled == 3){
                    ((RealtimeBlurView)findViewById(R.id.blur1)).setVisibility(View.INVISIBLE);
                    ((RealtimeBlurView)findViewById(R.id.blur9)).setVisibility(View.INVISIBLE);
                    ((RealtimeBlurView)findViewById(R.id.blur3)).setVisibility(View.INVISIBLE);
                    ((RealtimeBlurView)findViewById(R.id.blur7)).setVisibility(View.INVISIBLE);
                    ((RealtimeBlurView)findViewById(R.id.blur4)).setVisibility(View.INVISIBLE);
                    ((RealtimeBlurView)findViewById(R.id.blur6)).setVisibility(View.INVISIBLE);
                }else if(unraveled == 4){
                    ((LinearLayout)findViewById(R.id.blurFrame)).setVisibility(View.GONE);
//                    ((RealtimeBlurView)findViewById(R.id.blur2)).setVisibility(View.INVISIBLE);
//                    ((RealtimeBlurView)findViewById(R.id.blur5)).setVisibility(View.INVISIBLE);
//                    ((RealtimeBlurView)findViewById(R.id.blur8)).setVisibility(View.INVISIBLE);
                }

                int percent = 0;

                if(unraveled == 1) percent = 25;
                else if(unraveled == 2) percent = 50;
                else if(unraveled == 3) percent = 75;
                else if(unraveled == 4) percent = 100;

                scoreBar.setProgress(percent);
                scoreText.setText(String.valueOf(percent) + " %");

                if(percent == 100){
                    rippleBackground.setVisibility(View.VISIBLE);
                    rippleBackground.startRippleAnimation();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rippleBackground.stopRippleAnimation();
                            rippleBackground.setVisibility(View.GONE);
                        }
                    }, 5000);
                }

            }else
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0,0);
    }
}





















