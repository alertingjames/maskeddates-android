package com.date.maskeddates.main;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
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
import com.date.maskeddates.adapters.PhotosAdapter;
import com.date.maskeddates.commons.Commons;
import com.date.maskeddates.commons.Constants;
import com.date.maskeddates.commons.ReqConst;
import com.date.maskeddates.models.Picture;
import com.eyalbira.loadingdots.LoadingDots;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PhotosActivity extends AppCompatActivity implements SwipyRefreshLayout.OnRefreshListener {

    private static final String LOG_TAG = "imageTouch";
    TextView title;
    SwipyRefreshLayout ui_RefreshLayout;
    GridView list;
    LoadingDots progressBar;
    PhotosAdapter adapter = new PhotosAdapter(this);
    ArrayList<Picture> photos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });

        progressBar = (LoadingDots)findViewById(R.id.progressBar);

        title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Comfortaa_Bold.ttf");
        title.setTypeface(font);

        ui_RefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        ui_RefreshLayout.setOnRefreshListener(this);
        ui_RefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);

        list = (GridView)findViewById(R.id.list);
        getPictures(Commons.user.get_idx());
    }

    public void getPictures(int user_id) {

        progressBar.setVisibility(View.VISIBLE);
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
                progressBar.setVisibility(View.GONE);
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
        progressBar.setVisibility(View.GONE);
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

    private void showPictureJsonData(JSONObject response){
        try{
            photos.clear();
            JSONArray data = response.getJSONArray("pictures");
            for(int i=0; i<data.length(); i++){
                JSONObject jsonData = (JSONObject) data.get(i);
                Picture picture = new Picture();
                picture.setIdx(jsonData.getInt("id"));
                picture.setMember_id(jsonData.getInt("member_id"));
                picture.setPicture_url(jsonData.getString("picture_url"));
                photos.add(picture);
            }
            if(photos.isEmpty()){
                ((ImageView)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
                showToast("No image uploaded by this user.");
            }else {
                ((ImageView)findViewById(R.id.no_result)).setVisibility(View.GONE);
            }
            adapter.setDatas(photos);
            adapter.notifyDataSetChanged();
            list.setAdapter(adapter);
        }catch (JSONException e){}
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {

    }
}






















