package com.date.maskeddates.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ayz4sci.androidfactory.permissionhelper.PermissionHelper;
import com.date.maskeddates.MyApplication;
import com.date.maskeddates.R;
import com.date.maskeddates.commons.Commons;
import com.date.maskeddates.commons.Constants;
import com.date.maskeddates.commons.ReqConst;
import com.date.maskeddates.preference.PrefConst;
import com.date.maskeddates.preference.Preference;
import com.date.maskeddates.utils.MultiPartRequest;
import com.marozzi.roundbutton.RoundButton;
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

public class UpdateSelfieActivity extends AppCompatActivity {

    ImageView backgroundImage;
    CircleImageView photo;
    File destination = null;
    PermissionHelper permissionHelper;
    RoundButton saveButton;
    Typeface font;
    LinearLayout alertBox;
    View alertBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_update_selfie);

        permissionHelper = PermissionHelper.getInstance(this);
        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Comfortaa_Bold.ttf");

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_top);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });

        alertBox = (LinearLayout)findViewById(R.id.alertbox);
        alertBackground = (View)findViewById(R.id.layout);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                alertBox.setAnimation(animation);
                alertBox.setVisibility(View.VISIBLE);
                alertBackground.setVisibility(View.VISIBLE);
            }
        }, 1000);


        ((TextView)findViewById(R.id.title)).setTypeface(font);
        saveButton = (RoundButton) findViewById(R.id.saveButton);
        saveButton.setTypeface(font);

        backgroundImage = (ImageView)findViewById(R.id.backgroundImage);
        photo = (CircleImageView)findViewById(R.id.photo);

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

    }

    public void closeAlert(View view){
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_off);
        alertBackground.setAnimation(animation);
        alertBox.setAnimation(animation);
        alertBox.setVisibility(View.GONE);
        alertBackground.setVisibility(View.GONE);
    }

    public void takePhoto(View view){
        if(alertBox.getVisibility() == View.VISIBLE){
            alertBox.setVisibility(View.GONE);
            alertBackground.setVisibility(View.GONE);
        }
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

    public void updateSelfie(View view){
        if(destination != null){
            uploadSelfie();
        }else {
            showToast("Please take your selfie.");
        }
    }

    public void uploadSelfie() {
        saveButton.startAnimation();
        try {

            final Map<String, String> params = new HashMap<>();
            params.put("member_id", String.valueOf(Commons.thisUser.get_idx()));

            String url = ReqConst.SERVER_URL + "uploadMemberPicture";

            MultiPartRequest reqMultiPart = new MultiPartRequest(url, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    saveButton.revertAnimation();
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
            showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
        }
    }


    public void ParseUploadImgResponse(String json) {

        saveButton.revertAnimation();

        try {
            JSONObject response = new JSONObject(json);
            int result_code = response.getInt("result_code");

            if (result_code == 0) {
                String photo = response.getString("photo_url");
                showToast("Updated!");
                Commons.thisUser.set_photoUrl(photo);
                Preference.getInstance().put(getApplicationContext(), PrefConst.SELFIE_UPDATED, "updated");
                Intent intent = new Intent(getApplicationContext(), MyWallActivity.class);
                startActivity(intent);
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




























