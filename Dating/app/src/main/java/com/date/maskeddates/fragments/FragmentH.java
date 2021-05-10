package com.date.maskeddates.fragments;

/**
 * Created by sonback123456 on 6/6/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
import com.cunoraz.gifview.library.GifView;
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
public class FragmentH extends Fragment {
    FrameLayout first, second, third;
    ImageView firstbutton, secondbutton, thirdbutton;
    TextView firsttext, secondtext, thirdtext;
    ImageButton proceedButton;
    String checkedText = "";
    GifView progressBar = null;
    String answers = "";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_h, container, false);

        progressBar=(GifView) v.findViewById(R.id.progressBar);

        first = (FrameLayout) v.findViewById(R.id.first);
        second = (FrameLayout)v.findViewById(R.id.second);
        third = (FrameLayout)v.findViewById(R.id.third);

        proceedButton = (ImageButton) v.findViewById(R.id.proceedButton);

        firstbutton = (ImageView)v.findViewById(R.id.firstbutton);
        secondbutton = (ImageView)v.findViewById(R.id.secondbutton);
        thirdbutton = (ImageView)v.findViewById(R.id.thirdbutton);

        firsttext = (TextView) v.findViewById(R.id.firsttext);
        secondtext = (TextView)v.findViewById(R.id.secondtext);
        thirdtext = (TextView)v.findViewById(R.id.thirdtext);

        if(Commons.questionnaires2.size() > 0){
            for(int i=0; i<Commons.questionnaires2.size(); i++){
                if(Commons.questionnaires2.get(i).get_text().equals("Do You Like Attention?")) {
                    String answer = Commons.questionnaires2.get(i).get_answer();
                    if(answer.equals(firsttext.getText().toString()))firstbutton.setImageResource(R.drawable.radioicon2);
                    else if(answer.equals(secondtext.getText().toString()))secondbutton.setImageResource(R.drawable.radioicon2);
                    else if(answer.equals(thirdtext.getText().toString()))thirdbutton.setImageResource(R.drawable.radioicon2);
                }
            }
        }

        first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllCheckeds();
                firstbutton.setImageResource(R.drawable.radioicon2);
                checkedText = firsttext.getText().toString();
            }
        });

        second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllCheckeds();
                secondbutton.setImageResource(R.drawable.radioicon2);
                checkedText = secondtext.getText().toString();
            }
        });

        third.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllCheckeds();
                thirdbutton.setImageResource(R.drawable.radioicon2);
                checkedText = thirdtext.getText().toString();
            }
        });

        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Commons.questionnaires2.size() > 0){
                    for(int i=0; i<Commons.questionnaires2.size(); i++){
                        if(Commons.questionnaires2.get(i).get_text().equals("Do You Like Attention?")) {
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
        });

        return v;
    }

    public static FragmentH newInstance(String text) {

        FragmentH f = new FragmentH();
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

        String url = ReqConst.SERVER_URL + "load2QuestionnaireInfo";

        progressBar.setVisibility(View.VISIBLE);

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
                progressBar.setVisibility(View.GONE);
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
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Loading failed. Try again", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            progressBar.setVisibility(View.GONE);
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

