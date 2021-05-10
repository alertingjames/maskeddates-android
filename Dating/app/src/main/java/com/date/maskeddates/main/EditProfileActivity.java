package com.date.maskeddates.main;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.ayz4sci.androidfactory.permissionhelper.PermissionHelper;
import com.date.maskeddates.MyApplication;
import com.date.maskeddates.R;
import com.date.maskeddates.commons.Commons;
import com.date.maskeddates.commons.Constants;
import com.date.maskeddates.commons.ReqConst;
import com.date.maskeddates.preference.PrefConst;
import com.date.maskeddates.preference.Preference;
import com.date.maskeddates.utils.MultiPartRequest;
import com.eyalbira.loadingdots.LoadingDots;
import com.marozzi.roundbutton.RoundButton;
import com.shawnlin.numberpicker.NumberPicker;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.BlurTransformation;
import pl.tajchert.nammu.PermissionCallback;

public class EditProfileActivity extends AppCompatActivity {

    EditText name;
    TextView gender, age, location;
    ImageView backgroundImage;
    CircleImageView photo;
    File destination = null;
    PermissionHelper permissionHelper;
    NumberPicker numberPicker;
    LoadingDots progressBar;
    Button saveButton;
    Typeface font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_edit_profile);

        permissionHelper = PermissionHelper.getInstance(this);
        progressBar = (LoadingDots) findViewById(R.id.progressBar) ;
        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Comfortaa_Bold.ttf");
//        ((TextView)findViewById(R.id.deactivateCaption)).setTypeface(font);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_top);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });

        ((TextView)findViewById(R.id.title)).setTypeface(font);
        ((TextView)findViewById(R.id.nameCaption)).setTypeface(font);
        ((TextView)findViewById(R.id.ageCaption)).setTypeface(font);
        ((TextView)findViewById(R.id.genderCaption)).setTypeface(font);
        ((TextView)findViewById(R.id.locationCaption)).setTypeface(font);

        saveButton = (Button)findViewById(R.id.saveButton);
        saveButton.setTypeface(font);

        backgroundImage = (ImageView)findViewById(R.id.backgroundImage);
        photo = (CircleImageView)findViewById(R.id.photo);

        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Comfortaa_Regular.ttf");
        ((TextView)findViewById(R.id.deactivateButton)).setTypeface(font);

        name = (EditText)findViewById(R.id.name);
        age = (TextView)findViewById(R.id.age);
        gender = (TextView)findViewById(R.id.gender);
        location = (TextView)findViewById(R.id.location);
        numberPicker = findViewById(R.id.age_picker);

        name.setTypeface(font);
        age.setTypeface(font);
        gender.setTypeface(font);
        location.setTypeface(font);
        numberPicker.setTypeface(font);

        Picasso.with(getApplicationContext())
                .load(Commons.thisUser.get_photoUrl())
                .transform(new BlurTransformation(getApplicationContext(), 1, 1))
                .error(R.drawable.q9)
                .placeholder(R.drawable.q9)
                .into(backgroundImage);
        Picasso.with(getApplicationContext())
                .load(Commons.thisUser.get_photoUrl())
        //        .transform(new BlurTransformation(getApplicationContext(), 1, 1))
                .error(R.drawable.q9)
                .placeholder(R.drawable.q9)
                .into(photo);

        name.setText(Commons.thisUser.get_name().trim());
        age.setText(Commons.thisUser.get_age());
        gender.setText(Commons.thisUser.get_gender());
        location.setText(Commons.thisUser.get_address());
        Commons.lat = Commons.thisUser.get_lat();
        Commons.lng = Commons.thisUser.get_lng();

        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
       //         showGenderAlert();
            }
        });

        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberPicker.setVisibility(View.VISIBLE);
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Commons.edit_profile_flag = true;
                Commons.textView = location;
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

        // Set divider color
        numberPicker.setDividerColor(ContextCompat.getColor(this, R.color.colorAccent));
        numberPicker.setDividerColorResource(R.color.colorAccent);

        // Set formatter
        numberPicker.setFormatter(getString(R.string.number_picker_formatter));
        numberPicker.setFormatter(R.string.number_picker_formatter);

        // Set selected text color
        numberPicker.setSelectedTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        numberPicker.setSelectedTextColorResource(R.color.colorAccent);

        // Set selected text size
        numberPicker.setSelectedTextSize(getResources().getDimension(R.dimen.selected_text_size));
        numberPicker.setSelectedTextSize(R.dimen.selected_text_size);

        // Set text color
        numberPicker.setTextColor(ContextCompat.getColor(this, R.color.color_primary));
        numberPicker.setTextColorResource(R.color.color_primary);

        // Set text size
        numberPicker.setTextSize(getResources().getDimension(R.dimen.text_size));
        numberPicker.setTextSize(R.dimen.text_size);

        // Set typeface
        numberPicker.setTypeface(Typeface.create(getString(R.string.roboto_light), Typeface.NORMAL));
        numberPicker.setTypeface(getString(R.string.roboto_light), Typeface.NORMAL);
        numberPicker.setTypeface(getString(R.string.roboto_light));
        numberPicker.setTypeface(R.string.roboto_light, Typeface.NORMAL);
        numberPicker.setTypeface(R.string.roboto_light);

        // Set value
        numberPicker.setMaxValue(100);
        numberPicker.setMinValue(18);
        numberPicker.setValue(Integer.parseInt(Commons.thisUser.get_age()));

        // Set fading edge enabled
        numberPicker.setFadingEdgeEnabled(true);

        // Set scroller enabled
        numberPicker.setScrollerEnabled(true);

        // Set wrap selector wheel
        numberPicker.setWrapSelectorWheel(true);

        // OnClickListener
        numberPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                age.setText(String.valueOf(numberPicker.getValue()));
                numberPicker.setVisibility(View.GONE);
            }
        });

        // OnValueChangeListener
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                age.setText(String.valueOf(newVal));
            }
        });
    }

    AlertDialog alertDialog;
    CharSequence[] editItems = {"Male","Female"};

    public void showGenderAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        int checked = 0;
        if(Commons.thisUser.get_gender().equals("Male")) checked = 0;
        else checked = 1;
        builder.setSingleChoiceItems(editItems, checked, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch(item)
                {
                    case 0:
                        gender.setText("Male");
                        break;
                    case 1:
                        gender.setText("Female");
                        break;
                }
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = 600;
        lp.height = 400;
        alertDialog.getWindow().setAttributes(lp);
    }

    public void takePhoto(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, Constants.TAKE_FROM_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        permissionHelper.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.TAKE_FROM_CAMERA)
            {
                permissionHelper.verifyPermission(
                        new String[]{"take picture"},
                        new String[]{Manifest.permission.CAMERA},
                        new PermissionCallback() {
                            @Override
                            public void permissionGranted() {
                                //action to perform when permission granteed
                            }

                            @Override
                            public void permissionRefused() {
                                //action to perform when permission refused
                            }
                        }
                );
                onCaptureImageResult(data);
            }

        }
    }

    private void onCaptureImageResult(Intent data) {

        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Pictures");
        if (!dir.exists())
            dir.mkdirs();

        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        destination = new File(Environment.getExternalStorageDirectory()+"/Pictures",
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(byteArrayOutputStream.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        photo.setImageBitmap(thumbnail);
        backgroundImage.setImageBitmap(thumbnail);

    }

    int _idx = 0;

    public void updateUser(View view) {

        if(name.getText().toString().trim().length()==0){
            name.setError("Input your name");
            return;
        }

        String url = ReqConst.SERVER_URL + "updateMember";
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
                Toast.makeText(getApplicationContext(), "We can not detect any internet connectivity. Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("name", name.getText().toString().trim());
                params.put("email", Commons.thisUser.get_email());
                params.put("gender", gender.getText().toString());
                params.put("age", age.getText().toString());
                params.put("location", location.getText().toString());
                params.put("lat", Commons.lat);
                params.put("lng", Commons.lng);
                params.put("phone_imei", getDeviceIMEI());

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
        Toast toast=new Toast(this);
        toast.setView(dialogView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    public void parseRestUrlsResponse(String json) {

        try {

            JSONObject response = new JSONObject(json);   Log.d("response=====> :",response.toString());

            String success = response.getString("result_code");

            if (success.equals("0")) {
                _idx = response.getInt("member_id");
                Commons.profileViews[0].setText(name.getText().toString());
                Commons.profileViews[1].setText(name.getText().toString());
                Commons.profileViews[2].setText(age.getText().toString());
                Commons.profileViews[3].setText(gender.getText().toString());
                Commons.profileViews[4].setText(location.getText().toString());

                Commons.thisUser.set_name(name.getText().toString());
                Commons.thisUser.set_age(age.getText().toString());
                Commons.thisUser.set_gender(gender.getText().toString());
                Commons.thisUser.set_address(location.getText().toString());
                Commons.thisUser.set_lat(Commons.lat);
                Commons.thisUser.set_lng(Commons.lng);

                if(destination != null)
                    uploadImage(_idx);
                else {
                    progressBar.setVisibility(View.GONE);
                    showToast("Updated!");
                    finish();
                    overridePendingTransition(0,0);
                }
            }
            else {
                progressBar.setVisibility(View.GONE);
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again..");
            }

        } catch (JSONException e) {
            progressBar.setVisibility(View.GONE);
            showToast("We can not detect any internet connectivity. Please check your internet connection and try again..");
            e.printStackTrace();
        }
    }

    public void uploadImage(int id) {
        try {

            final Map<String, String> params = new HashMap<>();
            params.put("member_id", String.valueOf(id));

            String url = ReqConst.SERVER_URL + "uploadMemberPicture";

            MultiPartRequest reqMultiPart = new MultiPartRequest(url, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
                }
            }, new Response.Listener<String>() {

                @Override
                public void onResponse(String json) {

                    ParseUploadImgResponse(json);
                    Log.d("imageJson===",json.toString());
                }
            }, destination, "file", params);

            reqMultiPart.setRetryPolicy(new DefaultRetryPolicy(
                    Constants.VOLLEY_TIME_OUT, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            MyApplication.getInstance().addToRequestQueue(reqMultiPart, url);

        } catch (Exception e) {

            e.printStackTrace();
            ((RoundButton)findViewById(R.id.proceedButton)).revertAnimation();
            Toast.makeText(getApplicationContext(), "We can not detect any internet connectivity. Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
        }
    }


    public void ParseUploadImgResponse(String json) {

        progressBar.setVisibility(View.GONE);

        try {
            JSONObject response = new JSONObject(json);
            int result_code = response.getInt("result_code");

            if (result_code == 0) {
                String photo = response.getString("photo_url");
                showToast("Updated!");
                Picasso.with(getApplicationContext())
                        .load(photo)
                        //      .transform(new BlurTransformation(getApplicationContext(), 25, 1))
                        .error(R.drawable.q9)
                        .placeholder(R.drawable.q9)
                        .into(Commons.profileImage);
                Picasso.with(getApplicationContext())
                        .load(photo)
                        //      .transform(new BlurTransformation(getApplicationContext(), 25, 1))
                        .error(R.drawable.q9)
                        .placeholder(R.drawable.q9)
                        .into(Commons.backgroundImage);
                Commons.thisUser.set_photoUrl(photo);
                finish();
                overridePendingTransition(0,0);
            }
            else {
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
        }
    }

    /**
     * Returns the unique identifier for the device
     *
     * @return unique identifier for the device
     */
    public String getDeviceIMEI() {
        String deviceUniqueIdentifier = null;
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            deviceUniqueIdentifier = tm.getDeviceId();
        }
        if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
            deviceUniqueIdentifier = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceUniqueIdentifier;
    }

    public void showAlert(View view){
        ((FrameLayout)findViewById(R.id.deactivatebackground)).setVisibility(View.VISIBLE);
        ((LinearLayout)findViewById(R.id.alert_deactivate)).setVisibility(View.VISIBLE);
    }

    public void closeAlert(View view){
        ((FrameLayout)findViewById(R.id.deactivatebackground)).setVisibility(View.GONE);
        ((LinearLayout)findViewById(R.id.alert_deactivate)).setVisibility(View.GONE);
    }

    public void deactivate(View view){
        deactivateUser();
    }

    public void deactivateUser() {

        String url = ReqConst.SERVER_URL + "memberDeactivate";
        progressBar.setVisibility(View.VISIBLE);

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseDeactivateMemberResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "We can not detect any internet connectivity. Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("member_id", String.valueOf(Commons.thisUser.get_idx()));

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseDeactivateMemberResponse(String json) {

        progressBar.setVisibility(View.GONE);

        try {

            JSONObject response = new JSONObject(json);   Log.d("response=====> :",response.toString());

            String success = response.getString("result_code");

            if (success.equals("0")) {
                Preference.getInstance().put(getApplicationContext(), PrefConst.PREFKEY_USEREMAIL, "");
        //        Preference.getInstance().put(getApplicationContext(), PrefConst.SETTING_MAX_AGE, "");
        //        Preference.getInstance().put(getApplicationContext(), PrefConst.SETTING_MIN_AGE, "");
        //        Preference.getInstance().put(getApplicationContext(), PrefConst.SETTING_GENDER, "");
        //        Preference.getInstance().put(getApplicationContext(), PrefConst.SETTING_PROFILE, "");
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
            else {
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again..");
            }

        } catch (JSONException e) {
            showToast("We can not detect any internet connectivity. Please check your internet connection and try again..");
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0,0);
    }

    @Override
    public void onDestroy() {
        permissionHelper.finish();
        super.onDestroy();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}



























