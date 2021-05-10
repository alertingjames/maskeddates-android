package com.date.maskeddates.main;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
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
import android.widget.EditText;
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
import com.ayz4sci.androidfactory.permissionhelper.PermissionHelper;
import com.cunoraz.gifview.library.GifView;
import com.date.maskeddates.MyApplication;
import com.date.maskeddates.R;
import com.date.maskeddates.commons.Commons;
import com.date.maskeddates.commons.Constants;
import com.date.maskeddates.commons.ReqConst;
import com.date.maskeddates.preference.PrefConst;
import com.date.maskeddates.preference.Preference;

import de.hdodenhof.circleimageview.CircleImageView;
import com.date.maskeddates.utils.MultiPartRequest;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.picasso.transformations.GrayscaleTransformation;
import pl.tajchert.nammu.PermissionCallback;

public class RegisterActivity extends AppCompatActivity {

    EditText firstname, lastname;
    TextView gender, age;
    ImageView gendericon, backgroundImage;
    CircleImageView pictureFrame;
    File destination = null;
    PermissionHelper permissionHelper;
    GifView progressBar=null;
    NumberPicker numberPicker;

    private static final String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.INSTALL_PACKAGES,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.VIBRATE,
            android.Manifest.permission.SET_TIME,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAPTURE_VIDEO_OUTPUT,
            android.Manifest.permission.WAKE_LOCK,
            android.Manifest.permission.LOCATION_HARDWARE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_register);

        checkAllPermission();
        permissionHelper = PermissionHelper.getInstance(this);

        progressBar = (GifView)findViewById(R.id.progressBar) ;

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        ((TextView)findViewById(R.id.title)).setTypeface(font);

        Typeface font2 = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Comfortaa_Regular.ttf");

        pictureFrame = (CircleImageView) findViewById(R.id.picture);
        backgroundImage = (ImageView) findViewById(R.id.backgroundImage);
        try{
            Picasso.with(getApplicationContext())
                    .load(Commons.thisUser.get_fbPhoto())
                    //       .transform(new BlurTransformation(getApplicationContext(), 1, 1))
                    .transform(new GrayscaleTransformation())
                    .error(R.drawable.selfiebackground)
                    .placeholder(R.drawable.selfiebackground)
                    .into(backgroundImage);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            backgroundImage.setImageResource(R.drawable.selfiebackground);
        }

        firstname = (EditText) findViewById(R.id.firstname);
        lastname = (EditText) findViewById(R.id.lastname);
        firstname.setTypeface(font2);
        lastname.setTypeface(font2);

        firstname.setText(Commons.thisUser.get_firstName());
        lastname.setText(Commons.thisUser.get_lastName());

        gender = (TextView) findViewById(R.id.gender);
        gendericon = (ImageView)findViewById(R.id.gendericon);
        age = (TextView) findViewById(R.id.age);
        gender.setTypeface(font2);
        age.setTypeface(font2);

        String gnd = Commons.thisUser.get_gender();
        if(gnd.equals("male"))gnd = "Male";
        else if(gnd.equals("female")) gnd = "Female";
        gender.setText(gnd);

        numberPicker = findViewById(R.id.age_picker);

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);

        String dateString = Commons.thisUser.get_birthday();
        if(dateString.length() > 0){
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date convertedDate = new Date();
            try {
                convertedDate = dateFormat.parse(dateString);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(convertedDate);
                int birthyear = calendar.get(Calendar.YEAR);

                int agee = year - birthyear;
                age.setText(String.valueOf(agee));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dateString.length() == 0) numberPicker.setVisibility(View.VISIBLE);
            }
        });

        String finalGnd = gnd;
        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(finalGnd.length() == 0) showGenderAlert();
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
        numberPicker.setMaxValue(70);
        numberPicker.setMinValue(18);
        numberPicker.setValue(20);

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

        pictureFrame.setImageBitmap(thumbnail);
//        pictureFrame.setBackground(null);

    }

    @Override
    public void onDestroy() {
        permissionHelper.finish();
        super.onDestroy();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void checkAllPermission() {

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (hasPermissions(this, PERMISSIONS)){

        }else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 101);
        }
    }
    public static boolean hasPermissions(Context context, String... permissions) {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {

            for (String permission : permissions) {

                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
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

    public void next(View view){
        if(firstname.getText().toString().trim().length() > 0 && lastname.getText().toString().trim().length() > 0 && gender.getText().length() > 0 && age.getText().length() > 0 && destination != null) {
            ((RoundButton)findViewById(R.id.proceedButton)).startAnimation();
            registerUser();
        }
        else {
            if(destination == null)showToast("Please upload your selfie.");
            else if(gender.getText().length() == 0)showToast("Please enter your gender.");
            else if(age.getText().length() == 0)showToast("Please enter your age.");
            else if(firstname.getText().toString().trim().length() == 0)showToast("Please enter your first name.");
            else if(lastname.getText().toString().trim().length() == 0)showToast("Please enter your last name.");
            else showToast("Please complete your information.");
        }
    }

    int _idx = 0;

    public void registerUser() {

        String url = ReqConst.SERVER_URL + "registerMember";

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
                ((RoundButton)findViewById(R.id.proceedButton)).revertAnimation();
                Toast.makeText(getApplicationContext(), "We can not detect any internet connectivity. Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("name", firstname.getText().toString().trim() + " " + lastname.getText().toString().trim());
                params.put("email", Commons.thisUser.get_email());
                params.put("password", "");
                params.put("fb_photo", Commons.thisUser.get_fbPhoto());
                params.put("gender", gender.getText().toString());
                params.put("age", age.getText().toString());
                params.put("phone", "");
                params.put("address", "");
                params.put("lat", "");
                params.put("lng", "");
                params.put("answers", "");
                params.put("answers2", "");
                params.put("premium", "");
                params.put("photo_unlock", "");
                params.put("photos", "");
                params.put("selfie_approved", "");
                params.put("last_login", "");
                params.put("actives", "");
                params.put("phone_imei", getDeviceIMEI());

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseRestUrlsResponse(String json) {

        try {

            JSONObject response = new JSONObject(json);   Log.d("response=====> :",response.toString());

            String success = response.getString("result_code");

            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {

                _idx = response.getInt("member_id");   Log.d("member_id===>",String.valueOf(_idx));
                uploadImage(_idx);

            }
            else if(success.equals("100")){
                ((RoundButton)findViewById(R.id.proceedButton)).revertAnimation();
                showToast("You have already registered with us using " + response.getString("email") + ". Please login with that account.");
            }
            else if(success.equals("101")){
                ((RoundButton)findViewById(R.id.proceedButton)).revertAnimation();
                showToast("You have already registered with the email. Please login...");
            }
            else {
                ((RoundButton)findViewById(R.id.proceedButton)).revertAnimation();
                Toast.makeText(getApplicationContext(), "We can not detect any internet connectivity. Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            ((RoundButton)findViewById(R.id.proceedButton)).revertAnimation();
            Toast.makeText(getApplicationContext(), "We can not detect any internet connectivity. Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    String photoUrl = "";

    public void uploadImage(int id) {
        try {

            final Map<String, String> params = new HashMap<>();
            params.put("member_id", String.valueOf(id));

            String url = ReqConst.SERVER_URL + "uploadMemberPicture";

            MultiPartRequest reqMultiPart = new MultiPartRequest(url, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    ((RoundButton)findViewById(R.id.proceedButton)).revertAnimation();
                    Toast.makeText(getApplicationContext(), "We can not detect any internet connectivity. Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
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

        ((RoundButton)findViewById(R.id.proceedButton)).revertAnimation();

        try {
            JSONObject response = new JSONObject(json);
            int result_code = response.getInt("result_code");
            Log.d("result_code===",String.valueOf(result_code));

            if (result_code == 0) {
                photoUrl = response.getString("photo_url");
                processResult();
            }
            else {
                Toast.makeText(getApplicationContext(), "We can not detect any internet connectivity. Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "We can not detect any internet connectivity. Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void processResult(){
        Preference.getInstance().put(getApplicationContext(),
                PrefConst.PREFKEY_USEREMAIL, Commons.thisUser.get_email());
        Commons.thisUser.set_idx(_idx);
        Commons.thisUser.set_name(firstname.getText().toString().trim() + " " + lastname.getText().toString().trim());
        Commons.thisUser.set_gender(gender.getText().toString());
        Commons.thisUser.set_age(age.getText().toString());
        Commons.thisUser.set_photoUrl(photoUrl);
        Intent intent=new Intent(getApplicationContext(), UploadPicturesActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.right_in,R.anim.left_out);
    }

    AlertDialog alertDialog;
    CharSequence[] editItems = {"Male","Female"};

    public void showGenderAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(editItems, 0, new DialogInterface.OnClickListener() {

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

    @Override
    public void onBackPressed() {
        finish();
    }
}































