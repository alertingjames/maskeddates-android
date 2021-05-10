package com.date.maskeddates.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.date.maskeddates.adapters.MyPhotosAdapter;
import com.date.maskeddates.commons.Commons;
import com.date.maskeddates.commons.Constants;
import com.date.maskeddates.commons.ReqConst;
import com.date.maskeddates.models.Picture;
import com.date.maskeddates.utils.MultiPartRequest;
import com.eyalbira.loadingdots.LoadingDots;
import com.marozzi.roundbutton.RoundButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UploadPicturesActivity extends AppCompatActivity {

    LoadingDots progressBar;
    ArrayList<Picture> photos = new ArrayList<>();
    ListView list;
    MyPhotosAdapter adapter = new MyPhotosAdapter(this);
    int pictureCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pictures);

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Comfortaa_Bold.ttf");
        title.setTypeface(font);

        progressBar = (LoadingDots)findViewById(R.id.progressBar);
        list = (ListView) findViewById(R.id.photoList);

        getPictures(Commons.thisUser.get_idx());
    }

    public void pickPhoto(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), 100);
    }

    int pictureID = 0;

    public void editPicture(int picture_id){
        pictureID = picture_id;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case 100:
                if (resultCode == RESULT_OK) {
                    onSelectFromGalleryResult(data, 0);
                }
                break;
            case 200:
                if (resultCode == RESULT_OK) {
                    onSelectFromGalleryResult(data, 1);
                }
                break;
        }
    }

    File imageFile = null;

    private void onSelectFromGalleryResult(Intent data, int cat) {

        if (data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getApplicationContext().getContentResolver(), data.getData());
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                imageFile = new File(Environment.getExternalStorageDirectory()+"/Pictures",
                        System.currentTimeMillis() + ".jpg");

                FileOutputStream fo;
                try {
                    imageFile.createNewFile();
                    fo = new FileOutputStream(imageFile);
                    fo.write(byteArrayOutputStream.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(cat == 0)
                    uploadPicture(Commons.thisUser.get_idx(), imageFile);
                else if(cat == 1)
                    updatePicture(pictureID, imageFile);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadPicture(int user_id, File file) {

        try {

            final Map<String, String> params = new HashMap<>();
            params.put("member_id", String.valueOf(user_id));

            progressBar.setVisibility(View.VISIBLE);

            String url = ReqConst.SERVER_URL + "uploadPicture";

            MultiPartRequest reqMultiPart = new MultiPartRequest(url, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
                }
            }, new Response.Listener<String>() {

                @Override
                public void onResponse(String json) {

                    parseUploadPictureResponse(json);
                }
            }, file, "file", params);

            reqMultiPart.setRetryPolicy(new DefaultRetryPolicy(
                    Constants.VOLLEY_TIME_OUT, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            MyApplication.getInstance().addToRequestQueue(reqMultiPart, url);

        } catch (Exception e) {
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
            showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
        }
    }

    public void parseUploadPictureResponse(String json) {

        progressBar.setVisibility(View.GONE);

        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");

            if (result_code == 0) {
                imageFile=null;
                showToast("Successfully uploaded");
                showPictureJsonData(response);
            }
            else if (result_code == 1) {
                imageFile=null;
                showToast("You can feature only up to 4 photos.");
            }
            else {
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
            }

        } catch (JSONException e) {
            showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
            e.printStackTrace();
        }
    }

    public void updatePicture(int picture_id, File file) {

        try {

            final Map<String, String> params = new HashMap<>();
            params.put("picture_id", String.valueOf(picture_id));

            progressBar.setVisibility(View.VISIBLE);

            String url = ReqConst.SERVER_URL + "updatePicture";

            MultiPartRequest reqMultiPart = new MultiPartRequest(url, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
                }
            }, new Response.Listener<String>() {

                @Override
                public void onResponse(String json) {

                    parseEditPictureResponse(json);
                }
            }, file, "file", params);

            reqMultiPart.setRetryPolicy(new DefaultRetryPolicy(
                    Constants.VOLLEY_TIME_OUT, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            MyApplication.getInstance().addToRequestQueue(reqMultiPart, url);

        } catch (Exception e) {
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
            showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
        }
    }

    public void parseEditPictureResponse(String json) {

        progressBar.setVisibility(View.GONE);

        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");

            if (result_code == 0) {
                imageFile=null;
                showToast("Updated");
                showPictureJsonData(response);
            }
            else {
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
            }

        } catch (JSONException e) {
            showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");
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

    public void next(View view){
        if(pictureCount<2){
            showToast("Please upload at least 2 pictures.");
            return;
        }
        ((RoundButton)findViewById(R.id.proceedButton)).startAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ((RoundButton)findViewById(R.id.proceedButton)).revertAnimation();
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        }, 800);
    }

    public void getPictures(int user_id) {

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

    public void showDelPictureAlert(int picture_id){
        pictureID = picture_id;
        ((LinearLayout)findViewById(R.id.alert_picture)).setVisibility(View.VISIBLE);
        ((FrameLayout)findViewById(R.id.picturedeletebackground)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.yes_picture_delete)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LinearLayout)findViewById(R.id.alert_picture)).setVisibility(View.GONE);
                ((FrameLayout)findViewById(R.id.picturedeletebackground)).setVisibility(View.GONE);
                deletePicture(pictureID);
            }
        });
    }

    public void closePictureDeleteAlert(View view){
        ((LinearLayout)findViewById(R.id.alert_picture)).setVisibility(View.GONE);
        ((FrameLayout)findViewById(R.id.picturedeletebackground)).setVisibility(View.GONE);
    }

    public void deletePicture(int picture_id) {

        String url = ReqConst.SERVER_URL + "deletePicture";
        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());
                parseDelPictureResponse(response);
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

                params.put("picture_id", String.valueOf(picture_id));
                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(post, url);
    }

    public void parseDelPictureResponse(String json) {
        try{

            JSONObject response = new JSONObject(json);

            int result_code = response.getInt("result_code");
            if(result_code == 0) {
                showToast("Deleted");
                showPictureJsonData(response);
            }else
                showToast("We can not detect any internet connectivity. Please check your internet connection and try again.");

        }catch (JSONException e){
            e.printStackTrace();
        }
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
            pictureCount = photos.size();
            if(photos.isEmpty()){
                list.setVisibility(View.GONE);
                ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.VISIBLE);
            }else {
                list.setVisibility(View.VISIBLE);
                ((FrameLayout)findViewById(R.id.no_result)).setVisibility(View.GONE);
            }
            adapter.setDatas(photos);
            adapter.notifyDataSetChanged();
            list.setAdapter(adapter);
        }catch (JSONException e){}
    }

    @Override
    public void finish() {
        super.finish();
        if(progressBar != null) {
            if(progressBar.getVisibility() == View.VISIBLE)
                progressBar.setVisibility(View.GONE);
            progressBar = null;
        }
    }
}



























