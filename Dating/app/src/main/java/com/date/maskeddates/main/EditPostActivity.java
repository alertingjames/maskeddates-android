package com.date.maskeddates.main;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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
import com.marozzi.roundbutton.RoundButton;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditPostActivity extends AppCompatActivity {

    CircleImageView imageView;
    EditText editText;
    RoundButton postButton;
    int idx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        postButton = (RoundButton)findViewById(R.id.submit);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Comfortaa_Bold.ttf");
        ((TextView)findViewById(R.id.title)).setTypeface(font);

        imageView = (CircleImageView)findViewById(R.id.picture);
        editText = (EditText)findViewById(R.id.postBox);
        Picasso.with(getApplicationContext())
                .load(Commons.thisUser.get_photoUrl())
                .error(R.mipmap.appicon)
                .placeholder(R.mipmap.appicon)
                .into(imageView);

        editText.setText(StringEscapeUtils.unescapeJava(Commons.textView.getText().toString()));
    //    editText.setTypeface(font);

        idx = getIntent().getIntExtra("idx", 0);

        String oldAnswer = editText.getText().toString();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(editText.getText().toString().trim().length() > 0 && !editText.getText().toString().trim().equals(oldAnswer)) postButton.setVisibility(View.VISIBLE);
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
    }

    public void post(){
        if(editText.getText().toString().trim().length() > 0) {
            submit();
        }
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

        String url = ReqConst.SERVER_URL + "updatePost";

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

                params.put("id", String.valueOf(idx));
                params.put("post", StringEscapeUtils.escapeJava(editText.getText().toString().trim()));

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseRestUrlsResponse(String json) {

        postButton.revertAnimation();

        try {

            JSONObject response = new JSONObject(json);   Log.d("POSTRESPONSE=====> :",response.toString());

            String success = response.getString("result_code");

            Log.d("Result_Code===> :",success);

            if (success.equals("0")) {
                Commons.textView.setText(StringEscapeUtils.unescapeJava(editText.getText().toString()));
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                String myDate = dateFormat.format(new Date());
                Commons.postDate.setText(myDate);
                editText.setText("");
                showToast("Updated!");
                finish();
                overridePendingTransition(0,0);
            }
            else {
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
            }

        } catch (JSONException e) {
            showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0,0);
    }
}
