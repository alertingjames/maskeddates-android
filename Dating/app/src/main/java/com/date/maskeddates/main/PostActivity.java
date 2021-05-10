package com.date.maskeddates.main;

import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.date.maskeddates.MyApplication;
import com.date.maskeddates.R;
import com.date.maskeddates.commons.Commons;
import com.date.maskeddates.commons.Constants;
import com.date.maskeddates.commons.ReqConst;
import com.date.maskeddates.models.Post;
import com.marozzi.roundbutton.RoundButton;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {

    CircleImageView imageView;
    EditText editText;
    RoundButton postButton;
    Typeface font2;
    LinearLayout postLayout;
    View postView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postButton = (RoundButton)findViewById(R.id.submit);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Comfortaa_Bold.ttf");
        font2 = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Comfortaa_Regular.ttf");
        ((TextView)findViewById(R.id.title)).setTypeface(font);

        imageView = (CircleImageView)findViewById(R.id.picture);
        editText = (EditText)findViewById(R.id.postBox);
        Picasso.with(getApplicationContext())
                .load(Commons.thisUser.get_photoUrl())
                .error(R.mipmap.appicon)
                .placeholder(R.mipmap.appicon)
                .into(imageView);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(editText.getText().toString().trim().length() > 0) postButton.setVisibility(View.VISIBLE);
                else postButton.setVisibility(View.GONE);
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postButton.startAnimation();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        post();
                    }
                }, 1200);
            }
        });

        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        postView = inflater.inflate(R.layout.fragment_posts, null);
        postLayout = (LinearLayout)postView.findViewById(R.id.posts);
        Button postButton = (Button) postView.findViewById(R.id.newpost);
        postButton.setTypeface(font);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
    }

    public void post(){
        if(editText.getText().toString().trim().length() > 0)
            submit();
        else {
            postButton.revertAnimation();
            showToast("Please write something...");
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

    public void submit() {

        String url = ReqConst.SERVER_URL + "post";

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
                postButton.revertAnimation();
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("email", Commons.thisUser.get_email());
                params.put("post", StringEscapeUtils.escapeJava(editText.getText().toString().trim()));

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseRestUrlsResponse(String json) {
        try {

            JSONObject response = new JSONObject(json);   Log.d("POSTRESPONSE=====> :",response.toString());

            String success = response.getString("result_code");

            Log.d("Result_Code===> :",success);

            if (success.equals("0")) {
                editText.setText("");
                showToast("Posted!");
                getPosts();
            }
            else {
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
                postButton.revertAnimation();
            }

        } catch (JSONException e) {
            showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
            postButton.revertAnimation();
            e.printStackTrace();
        }
    }

    public void getPosts() {

        String url = ReqConst.SERVER_URL + "getposts";

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
                postButton.revertAnimation();
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
        postButton.revertAnimation();
        try{

            JSONObject response = new JSONObject(json);  Log.d("RESPONSE===>",response.toString());

            int result_code = response.getInt("result_code");

            if(result_code == 0){

                Commons.container.removeAllViews();
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

                Commons.container.addView(postView);
                finish();
                overridePendingTransition(0,0);

            } else {
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
            }
        }catch (JSONException e){
            e.printStackTrace();
            showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
        }
    }

    public void show_alert_delet_post(String idx){
        Commons.alert_post.setVisibility(View.VISIBLE);
        Commons.deletepostbackground.setVisibility(View.VISIBLE);
        ((TextView)Commons.alert_post.findViewById(R.id.yes_post_delete)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePost(idx);
                Commons.alert_post.setVisibility(View.GONE);
                Commons.deletepostbackground.setVisibility(View.GONE);
            }
        });
        ((ImageView)Commons.alert_post.findViewById(R.id.cancelButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Commons.alert_post.setVisibility(View.GONE);
                Commons.deletepostbackground.setVisibility(View.GONE);
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

    public void back(View view){
        onBackPressed();
    }
}



























