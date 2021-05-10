package com.date.maskeddates.main;

import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditDynamicalActivity extends AppCompatActivity {

    TextView question;
    EditText answerBox;
    String answers = "", q = "";
    RoundButton submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_dynamical);

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Comfortaa_Bold.ttf");
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_top);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });

        ((TextView)findViewById(R.id.title)).setTypeface(font);

        question = (TextView)findViewById(R.id.question);
        answerBox = (EditText)findViewById(R.id.answerBox);
        submitButton = (RoundButton)findViewById(R.id.submit);

        q = getIntent().getStringExtra("question");
        question.setText(q);
        String  a = getIntent().getStringExtra("answer");
        answerBox.setText(StringEscapeUtils.unescapeJava(a));

        String oldAnswer = answerBox.getText().toString();
        answerBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(answerBox.getText().toString().trim().length() > 0 && !answerBox.getText().toString().trim().equals(oldAnswer)) submitButton.setVisibility(View.VISIBLE);
                else submitButton.setVisibility(View.GONE);
            }
        });


        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Comfortaa_Regular.ttf");
        question.setTypeface(font);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitButton.startAnimation();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        submit();
                    }
                }, 1200);
            }
        });
    }

    String myquestionnaire = "";

    public void submit(){
        if(answerBox.getText().toString().trim().length() > 0){
            for(int i=0; i< Commons.questionnaires.size(); i++){
                if(Commons.questionnaires.get(i).get_text().equals(q)){
                    Commons.questionnaires.get(i).set_answer(StringEscapeUtils.escapeJava(answerBox.getText().toString().trim()));
                    try {
                        uploadQuestionnaireInfo(myquestionnaire=createAnswerJsonString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }else {
            showToast("Please enter your answer.");
            submitButton.revertAnimation();
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

    public String createAnswerJsonString()throws JSONException {

        answers = "";
        JSONObject jsonObj = null;
        JSONArray jsonArr = new JSONArray();
        if (Commons.questionnaires.size()>0){
            for(int i=0; i<Commons.questionnaires.size(); i++){

                String question = Commons.questionnaires.get(i).get_text();
                String answer = Commons.questionnaires.get(i).get_answer();
                String isactive = Commons.questionnaires.get(i).get_isactive();

                jsonObj=new JSONObject();

                try {
                    jsonObj.put("question",question);
                    jsonObj.put("answer",answer);
                    jsonObj.put("isactive",isactive);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                jsonArr.put(jsonObj);
            }
            JSONObject questionnaireObj = new JSONObject();
            questionnaireObj.put("questionnaires", jsonArr);
            answers = questionnaireObj.toString();
            return answers;
        }
        return answers;
    }

    public void uploadQuestionnaireInfo(String questionnaireInfo) {

        String url = ReqConst.SERVER_URL + "uploadQuestionnaireInfo";

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
                submitButton.revertAnimation();
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("email", Commons.thisUser.get_email());
                params.put("questionnaireinfo", questionnaireInfo);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseRestUrlsResponse(String json) {

        submitButton.revertAnimation();

        try {

            JSONObject response = new JSONObject(json);

            String success = response.getString("result_code");

            Log.d("Result_Code===> :",success);

            if (success.equals("0")) {
                showToast("Updated!");
                Commons.textView.setText(answerBox.getText().toString().trim());
                Commons.thisUser.set_answers(myquestionnaire);
                Commons.qa_sublayout.setBackground(null);
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
