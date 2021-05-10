package com.date.maskeddates.fragments;

/**
 * Created by sonback123456 on 6/6/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.date.maskeddates.MyApplication;
import com.date.maskeddates.R;
import com.date.maskeddates.commons.Commons;
import com.date.maskeddates.commons.Constants;
import com.date.maskeddates.commons.ReqConst;
import com.date.maskeddates.main.MyWallActivity;
import com.date.maskeddates.preference.PrefConst;
import com.date.maskeddates.preference.Preference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by a on 2016.11.02.
 */
public class FragmentL extends Fragment {
    FrameLayout first, second, third, fourth;
    ImageView firstbutton, secondbutton, thirdbutton, fourthbutton;
    String[] titles = {"LinkedIn", "FaceTime", "Instagram", "Facebook"};
    String checkedText = "";
    String answers = "";
    private YoYo.YoYoString rope;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_l, container, false);

        first = (FrameLayout) v.findViewById(R.id.first);
        second = (FrameLayout)v.findViewById(R.id.second);
        third = (FrameLayout)v.findViewById(R.id.third);
        fourth = (FrameLayout)v.findViewById(R.id.fourth);

        firstbutton = (ImageView)v.findViewById(R.id.firstbutton);
        secondbutton = (ImageView)v.findViewById(R.id.secondbutton);
        thirdbutton = (ImageView)v.findViewById(R.id.thirdbutton);
        fourthbutton = (ImageView)v.findViewById(R.id.fourthbutton);

        FrameLayout[] frameLayouts = {first, second, third, fourth};
        ImageView[] imageViews = {firstbutton, secondbutton, thirdbutton, fourthbutton};

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Comfortaa_Bold.ttf");
        ((TextView)v.findViewById(R.id.question)).setTypeface(font);

        if(Commons.questionnaires2.size() > 0){
            for(int i=0; i<Commons.questionnaires2.size(); i++){
                if(Commons.questionnaires2.get(i).get_text().equals("How do you let the world know what you're feeling?")) {
                    String answer = Commons.questionnaires2.get(i).get_answer();
                    clearAllCheckeds();
                    for(int j=0; j<titles.length; j++){
                        if(answer.equals("I Spend my time on " + titles[j])){
                            imageViews[j].setImageResource(R.drawable.radioicon2);
                            frameLayouts[j].setBackgroundResource(R.drawable.red_fill_round2);
                            if (rope != null) {
                                rope.stop(true);
                            }
                            rope = YoYo.with(Techniques.Pulse)
                                    .duration(400)
                                    .repeat(2)
                                    .playOn(frameLayouts[j]);
                        }
                    }
                }
            }
        }

        for(int k=0; k<frameLayouts.length; k++){
            int finalK = k;
            frameLayouts[k].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clearAllCheckeds();
                    imageViews[finalK].setImageResource(R.drawable.radioicon2);
                    frameLayouts[finalK].setBackgroundResource(R.drawable.red_fill_round2);
                    checkedText = "I Spend my time on " + titles[finalK];
                    if (rope != null) {
                        rope.stop(true);
                    }
                    rope = YoYo.with(Techniques.Pulse)
                            .duration(400)
                            .repeat(2)
                            .playOn(frameLayouts[finalK]);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(Commons.questionnaires2.size() > 0){
                                for(int i=0; i<Commons.questionnaires2.size(); i++){
                                    if(Commons.questionnaires2.get(i).get_text().equals("How do you let the world know what you're feeling?")) {
                                        Commons.questionnaires2.get(i).set_answer(checkedText);
                                        Commons.questionnaires2.get(i).set_isactive("active");
                                    }
                                }
                            }
                            if(checkActives()) {
                                try {
                                    submit();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else Commons.viewPager.setCurrentItem(Commons.viewPager.getCurrentItem() + 1);
                        }
                    }, 1500);
                }
            });
        }

        return v;
    }

    public static FragmentL newInstance(String text) {

        FragmentL f = new FragmentL();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void clearAllCheckeds(){
        firstbutton.setImageResource(R.drawable.radioicon);
        secondbutton.setImageResource(R.drawable.radioicon);
        thirdbutton.setImageResource(R.drawable.radioicon);
        fourthbutton.setImageResource(R.drawable.radioicon);

        first.setBackgroundResource(R.drawable.red_stroke_round);
        second.setBackgroundResource(R.drawable.red_stroke_round);
        third.setBackgroundResource(R.drawable.red_stroke_round);
        fourth.setBackgroundResource(R.drawable.red_stroke_round);
    }

    private static boolean checkActives(){
        boolean done = true;
        for (int i=0; i < Commons.questionnaires2.size(); i++){
            if(Commons.questionnaires2.get(i).get_answer().length()==0) {
                done = false;
                break;
            }
        }
        return done;
    }

    public void submit() throws JSONException {
        uploadQuestionnaireInfo(createAnswerJsonString());
    }

    public String createAnswerJsonString()throws JSONException {

        answers = "";
        JSONObject jsonObj = null;
        JSONArray jsonArr = new JSONArray();
        if (Commons.questionnaires2.size()>0){
            for(int i=0; i<Commons.questionnaires2.size(); i++){

                String question = Commons.questionnaires2.get(i).get_text();
                String answer = Commons.questionnaires2.get(i).get_answer();
                String isactive = Commons.questionnaires2.get(i).get_isactive();

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

        ((LinearLayout)getActivity().findViewById(R.id.loadingProgressBar)).setVisibility(View.VISIBLE);
        ((View)getActivity().findViewById(R.id.loadingbg)).setVisibility(View.VISIBLE);
        String url = ReqConst.SERVER_URL + "load2QuestionnaireInfo";

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
                ((LinearLayout)getActivity().findViewById(R.id.loadingProgressBar)).setVisibility(View.GONE);
                ((View)getActivity().findViewById(R.id.loadingbg)).setVisibility(View.GONE);
                Log.d("debug", error.toString());
                Toast.makeText(getActivity(), "Loading failed", Toast.LENGTH_SHORT).show();

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

        try {

            JSONObject response = new JSONObject(json);   Log.d("RESPONSE=====> :",response.toString());

            String success = response.getString("result_code");

            Log.d("Result_Code===> :",success);

            if (success.equals("0")) {
                login();
            }
            else {
                ((LinearLayout)getActivity().findViewById(R.id.loadingProgressBar)).setVisibility(View.GONE);
                ((View)getActivity().findViewById(R.id.loadingbg)).setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Loading failed. Try again", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            ((LinearLayout)getActivity().findViewById(R.id.loadingProgressBar)).setVisibility(View.GONE);
            ((View)getActivity().findViewById(R.id.loadingbg)).setVisibility(View.GONE);
            Toast.makeText(getActivity(), "Loading failed. Try again", Toast.LENGTH_SHORT).show();
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
                ((LinearLayout)getActivity().findViewById(R.id.loadingProgressBar)).setVisibility(View.GONE);
                ((View)getActivity().findViewById(R.id.loadingbg)).setVisibility(View.GONE);
                parseRestUrlsResponse1(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ((LinearLayout)getActivity().findViewById(R.id.loadingProgressBar)).setVisibility(View.GONE);
                ((View)getActivity().findViewById(R.id.loadingbg)).setVisibility(View.GONE);
                Log.d("debug", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("email", Commons.thisUser.get_email());
                params.put("phone_imei", getDeviceIMEI());

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);

    }

    /**
     * Returns the unique identifier for the device
     *
     * @return unique identifier for the device
     */
    public String getDeviceIMEI() {
        String deviceUniqueIdentifier = null;
        TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            deviceUniqueIdentifier = tm.getDeviceId();
        }
        if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
            deviceUniqueIdentifier = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceUniqueIdentifier;
    }

    public void parseRestUrlsResponse1(String json) {

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

                Commons.thisUser.set_phone_imei(jsonUser.getString("phone_imei"));
                Commons.thisUser.set_phone(jsonUser.getString("phone"));             /////////////////////////////////////////// Updated membership //////////////////////////////////////////

                if(jsonUser.getString("password").equals("deactivated")){
                    return;
                }

                if(Commons.thisUser.get_premium().length() > 0)
                    parsePremium(Commons.thisUser.get_premium());
                else Commons.max_dates = Commons.plan.getDates();    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                Preference.getInstance().put(getActivity(),
                        PrefConst.PREFKEY_USEREMAIL, Commons.thisUser.get_email());

                Intent intent=new Intent(getActivity(), MyWallActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.right_in,R.anim.left_out);

            }
            else if(result_code.equals("100")){
                Toast.makeText(getActivity(), "You have already registered with us using " + response.getString("email") + ". Please login with that account.", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getActivity(), "Loading failed. Try again", Toast.LENGTH_SHORT).show();
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
}

