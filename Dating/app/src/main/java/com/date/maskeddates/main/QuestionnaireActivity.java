package com.date.maskeddates.main;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;

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
import com.date.maskeddates.classes.ViewPagerCustomDuration;
import com.date.maskeddates.commons.Commons;
import com.date.maskeddates.commons.Constants;
import com.date.maskeddates.commons.ReqConst;
import com.date.maskeddates.models.Questionnaire;
import com.marozzi.roundbutton.RoundButton;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class QuestionnaireActivity extends Activity {

    public static ViewPagerCustomDuration mPager;
    private PageAdapter mAdapter;
    ArrayList<Questionnaire> questionnaires = new ArrayList<>();
    public static Toolbar toolbar;
    Typeface font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_questionnaire);

        toolbar = (Toolbar)findViewById(R.id.toolbar_top);

        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Comfortaa_Bold.ttf");
        ((TextView)findViewById(R.id.title)).setTypeface(font);

        Commons.questionnaires.clear();
        getDynamicals();
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String EXTRA_POSITION = "EXTRA_POSITION";
        String answers = "";
        GifView progressBar;
        RoundButton submitButton;
        Handler handler = new Handler();

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            final int position = getArguments().getInt(EXTRA_POSITION);
            final FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_questionnaire, container, false);
            final TextView question = (TextView) frameLayout.findViewById(R.id.question) ;
            final EditText answerInputBox = (EditText) frameLayout.findViewById(R.id.answerInputBox) ;
            submitButton = (RoundButton) frameLayout.findViewById(R.id.proceedButton) ;
            final ImageView checkMark = (ImageView) frameLayout.findViewById(R.id.checkMark) ;
            final ImageView leftarrow = (ImageView) frameLayout.findViewById(R.id.leftarrow) ;
            final ImageView rightarrow = (ImageView) frameLayout.findViewById(R.id.rightarrow) ;
            progressBar=(GifView) frameLayout.findViewById(R.id.progressBar);

//            int color = ColorHelper.getRandomMaterialColor(getActivity());
//            submitButton.setBackgroundColor(color);

            Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Comfortaa_Bold.ttf");
            question.setTypeface(font);
            answerInputBox.setTypeface(font);

            answerInputBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(answerInputBox.getText().toString().trim().length() > 0) submitButton.setVisibility(View.VISIBLE);
                    else submitButton.setVisibility(View.GONE);
                }
            });

            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    submitButton.startAnimation();
                    submitButton.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(answerInputBox.getText().length() > 0){
                                Commons.questionnaires.get(position - 1).set_isactive("active");
                                Commons.questionnaires.get(position - 1).set_answer(StringEscapeUtils.escapeJava(answerInputBox.getText().toString()));
                                checkMark.setVisibility(View.VISIBLE);

                                if(mPager.getCurrentItem() < Commons.questionnaires.size()){
                                    mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                                }

                                if(position >= Commons.questionnaires.size() && !checkActives()) {
                                    submitButton.revertAnimation();
                                    showToast("Please answer all the questions.");
                                }

                                if(checkActives())
                                {
//                            ((LinearLayout)frameLayout.findViewById(R.id.layout)).setVisibility(View.VISIBLE);
//                            ((LinearLayout)frameLayout.findViewById(R.id.alert)).setVisibility(View.VISIBLE);
                                    try {
                                        uploadQuestionnaireInfo(createAnswerJsonString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }else submitButton.revertAnimation();
                            }else {
                                submitButton.revertAnimation();
                                showToast("Please answer the question");
                            }
                        }
                    }, 2000);
                }
            });

            ((TextView)frameLayout.findViewById(R.id.yes_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((LinearLayout)frameLayout.findViewById(R.id.layout)).setVisibility(View.GONE);
                    ((LinearLayout)frameLayout.findViewById(R.id.alert)).setVisibility(View.GONE);
                    yes();
                }
            });

            ((TextView)frameLayout.findViewById(R.id.no_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((LinearLayout)frameLayout.findViewById(R.id.layout)).setVisibility(View.GONE);
                    ((LinearLayout)frameLayout.findViewById(R.id.alert)).setVisibility(View.GONE);
                }
            });

            if(position == 1){
                leftarrow.setVisibility(View.GONE);
            }else if(position >= Commons.questionnaires.size()){
                rightarrow.setVisibility(View.GONE);
            }else {
                leftarrow.setVisibility(View.VISIBLE);
                rightarrow.setVisibility(View.VISIBLE);
            }

            Log.d("LEFT==RIGHT===>", String.valueOf(mPager.getCurrentItem()) + "/" + String.valueOf(Commons.questionnaires.size()));

            leftarrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPager.setCurrentItem(mPager.getCurrentItem() - 1);
                }
            });

            rightarrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                }
            });

            question.setText(Commons.questionnaires.get(position - 1).get_text());
            if(Commons.questionnaires.get(position - 1).get_answer().length() > 0){
                answerInputBox.setText(StringEscapeUtils.unescapeJava(Commons.questionnaires.get(position - 1).get_answer()));
                String oldAnswer = answerInputBox.getText().toString();
                submitButton.setVisibility(View.GONE);
                checkMark.setVisibility(View.VISIBLE);
                answerInputBox.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(answerInputBox.getText().toString().trim().length() > 0 && !answerInputBox.getText().toString().trim().equals(oldAnswer)) submitButton.setVisibility(View.VISIBLE);
                        else submitButton.setVisibility(View.GONE);
                    }
                });
            }

            int[] backgroundPictures = {R.drawable.qb, R.drawable.qb2, R.drawable.qb3, R.drawable.qb4, R.drawable.qb5, R.drawable.qb6, R.drawable.qb7, R.drawable.qb8, R.drawable.q9};

            Random r = new Random();
            int random = r.nextInt(8 - 0) + 0;

   //         frameLayout.setBackgroundResource(backgroundPictures[random]);

            return frameLayout;
        }

        public void yes(){
            try {
                uploadQuestionnaireInfo(createAnswerJsonString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void showToast(String content){
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.custom_toast, null);
            TextView textView=(TextView)dialogView.findViewById(R.id.text);
            textView.setText(content);
            Toast toast=new Toast(getActivity());
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
                    Toast.makeText(getActivity(), "We can not detect any internet connectivity. Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();

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

                JSONObject response = new JSONObject(json);   Log.d("RESPONSE=====> :",response.toString());

                String success = response.getString("result_code");

                Log.d("Result_Code===> :",success);

                if (success.equals("0")) {
                    Intent intent=new Intent(getActivity(), Questionnaire2Activity.class);
                    startActivity(intent);
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.right_in,R.anim.left_out);
                }
                else {
                    Toast.makeText(getActivity(), "We can not detect any internet connectivity. Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                Toast.makeText(getActivity(), "We can not detect any internet connectivity. Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

        public void login() {

            String url = ReqConst.SERVER_URL + "loginMember";

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

                    Log.d("debug", error.toString());
                    progressBar.setVisibility(View.GONE);

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

        public void parseRestUrlsResponse1(String json) {

            progressBar.setVisibility(View.GONE);

            Log.d("JsonLogin====>", json);

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

                    Intent intent=new Intent(getActivity(), Questionnaire2Activity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
//                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.right_in,R.anim.left_out);

                }
                else {

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private static boolean checkActives(){
        boolean done = true;
        for (int i=0; i < Commons.questionnaires.size(); i++){
            if(Commons.questionnaires.get(i).get_answer().length()==0) {
                done = false;
                break;
            }
        }
        return done;
    }

    private static final class PageAdapter extends FragmentStatePagerAdapter {

        public PageAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            final Bundle bundle = new Bundle();
            bundle.putInt(PlaceholderFragment.EXTRA_POSITION, position + 1);

            final PlaceholderFragment fragment = new PlaceholderFragment();
            fragment.setArguments(bundle);

            return fragment;
        }

        @Override
        public int getCount() {
            return Commons.questionnaires.size();
        }

    }

    public void getDynamicals() {

        String url = ReqConst.SERVER_URL + "getDynamicals";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("Dynamicals====>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseGetDynamicalsResponse1(response);

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

    public void parseGetDynamicalsResponse1(String json) {

        try {
            JSONObject response = new JSONObject(json);

            String result_code = response.getString("result_code");

            if (result_code.equals("0")) {

                //==========================================================

                JSONArray dynamicalsInfo = response.getJSONArray("data");

                if(dynamicalsInfo.length() > 0){
                    for(int i=0; i<dynamicalsInfo.length(); i++) {
                        JSONObject jsonDynamical = (JSONObject) dynamicalsInfo.get(i);
                        Questionnaire questionnaire = new Questionnaire();
                        questionnaire.set_text(jsonDynamical.getString("dynamical_question"));
                        questionnaire.set_isactive("inactive");
                        Commons.questionnaires.add(questionnaire);
                    }
                }else {
                    String[] texts = {"Other than appearance, what is the first thing that people notice about you?",
                            "What’s the most important thing you’re looking for in another person?",
                            "If you could travel anywhere, where would you go?",
                            "What are your favourite books/movies/music/quotes?",
                            "If you won the lottery tomorrow, what’s the first thing you’d buy?"};

                    for(int i=0; i<texts.length; i++){
                        Questionnaire questionnaire = new Questionnaire();
                        questionnaire.set_text(texts[i]);
                        questionnaire.set_isactive("inactive");
                        Commons.questionnaires.add(questionnaire);
                    }
                }

                mAdapter = new PageAdapter(getFragmentManager());

                mPager = (ViewPagerCustomDuration) findViewById(R.id.viewpager);
                mPager.setScrollDuration(800);
                mPager.setAdapter(mAdapter);
                mPager.setPageTransformer(true, new CubeOutTransformer());

            }
            else {

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void back(View view){
        finish();
    }

}

































