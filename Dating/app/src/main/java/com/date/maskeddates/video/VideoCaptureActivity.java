package com.date.maskeddates.video;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.date.maskeddates.R;
import com.date.maskeddates.commons.Commons;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Random;

public class VideoCaptureActivity extends Activity implements View.OnClickListener{
    private static final String TAG ="File===>" ;
    VideoView videoView;
    TextView ui_txvpreview,ui_txvsave,ui_txvsubmit;
    RelativeLayout ui_lytvideo;
    private Uri fileUri;
    LinearLayout compressionMsg;
    Uri videoUri = null;
    Bitmap thumb = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_capture);

        compressionMsg = (LinearLayout) findViewById(R.id.compressionMsg);

        ui_txvpreview=(TextView)findViewById(R.id.txv_preview);
        ui_txvpreview.setOnClickListener(this);
        ui_txvsave=(TextView)findViewById(R.id.txv_save);
        ui_txvsave.setOnClickListener(this);
        ui_txvsubmit=(TextView)findViewById(R.id.txv_submit);
        ui_txvsubmit.setOnClickListener(this);

        ui_lytvideo=(RelativeLayout) findViewById(R.id.lytvideo);
        videoView=(VideoView) findViewById(R.id.videoView);

        String option=getIntent().getExtras().get("OPTION").toString();
        if(option.equals("capture"))dispatchTakeVideoIntent();
        else if(option.equals("pick")){selectVideoFromGallery();}
    }
    static final int REQUEST_VIDEO_CAPTURE = 1;
    static final int SELECT_VIDEO_REQUEST=2;

    private void dispatchTakeVideoIntent() {

        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {

            String takenVideoPath = getPath(data.getData());

            videoUri = data.getData();
            thumb = ThumbnailUtils.createVideoThumbnail(takenVideoPath, MediaStore.Images.Thumbnails.MINI_KIND);
            videoView.setBackground(new BitmapDrawable(getResources(), thumb));
            videoView.setVideoURI(videoUri);
            thumbToDonwloadUrl(saveImage(thumb, "videoThumb"));
        }

        else if(requestCode == SELECT_VIDEO_REQUEST && resultCode == RESULT_OK)
        {
            if(data.getData()!=null)
            {
                String selectedVideoPath = getPath(data.getData());
                try{
                    videoUri = data.getData();
                    thumb = ThumbnailUtils.createVideoThumbnail(selectedVideoPath, MediaStore.Images.Thumbnails.MINI_KIND);
                    videoView.setBackground(new BitmapDrawable(getResources(),thumb));//MediaStore.Video.Thumbnails.MINI_KIND
                    videoView.setVideoURI(videoUri);
                    thumbToDonwloadUrl(saveImage(thumb, "videoThumb"));

                }catch (NullPointerException e){

                }

            }
            else
            {
                Toast.makeText(getApplicationContext(), "Failed to select video" , Toast.LENGTH_LONG).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if(cursor!=null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }
    public void selectVideoFromGallery()
    {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_VIDEO_REQUEST);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txv_preview:
                Log.d("Press==>","");
                videoView.setBackground(null);
                videoView.setMediaController(new MediaController(this));
                videoView.setVideoURI(videoUri);
                videoView.requestFocus();
                videoView.start();
                initTabBackg();
                ui_txvpreview.setBackgroundColor(Color.parseColor("#fa7822"));
                ui_txvpreview.setTextColor(Color.WHITE);
                break;
            case R.id.txv_save:
                Save();
                initTabBackg();
                ui_txvsave.setBackgroundColor(Color.parseColor("#fa7822"));
                break;
            case R.id.txv_submit:
                initTabBackg();
                ui_txvsubmit.setBackgroundColor(Color.parseColor("#fa7822"));
                ui_txvsubmit.setTextColor(Color.WHITE);
                try {
                    Submit();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void Submit() throws IOException {

        File f = new File(this.getCacheDir(), "thumbnail");
        f.createNewFile();
//Convert bitmap to byte array
        Bitmap bitmap = thumb;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
        Commons.map=bitmap;
        Commons.imagePortion.setVisibility(View.VISIBLE);
        Commons.mapImage.setImageBitmap(bitmap);

        //create destination directory
        File f2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getPackageName() + "/media/videos");
        if (f2.mkdirs() || f2.isDirectory())
            //compress and output new video specs
            new VideoCompressAsyncTask(this).execute(videoUri.toString(), f2.getPath());
    }

    private void Save() {

        String root = Environment.getExternalStorageDirectory().getPath();
        File myDir = new File(root+ "/req_images");// + "/req_images"
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        Log.i(TAG, "" + file);

        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            Bitmap bm=thumb;
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Toast.makeText(this,"Urisav==>"+file,Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean hasCamera() {
        if (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FRONT)){
            return true;
        } else {
            return false;
        }
    }
    public void initTabBackg(){
        ui_txvpreview.setBackgroundColor(Color.parseColor("#e8e8e8"));
        ui_txvsave.setBackgroundColor(Color.parseColor("#e8e8e8"));
        ui_txvsubmit.setBackgroundColor(Color.parseColor("#e8e8e8"));
    }

    private void thumbToDonwloadUrl(String path){
        final String[] url = {""};
        final Uri[] uri = {Uri.fromFile(new File(path))};
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance("gs://datingapp-206116.appspot.com");
        StorageReference fileReference = firebaseStorage.getReference();

        UploadTask uploadTask = fileReference.child(uri[0].getLastPathSegment()+ ".jpg").putFile(uri[0]);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                Uri downloadUri = taskSnapshot.getDownloadUrl();
                Log.d("ImageUrl===>", downloadUri.toString());
                Commons.videoThumbStr = downloadUri.toString();
            }
        });
    }

    private String saveImage(Bitmap finalBitmap, String image_name) {
        String path = "";
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "Image-" + image_name+ ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            path = file.getPath();
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }


    class VideoCompressAsyncTask extends AsyncTask<String, String, String> {

        Context mContext;

        public VideoCompressAsyncTask(Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            compressionMsg.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... paths) {
            String filePath = null;
            try {

                filePath = SiliCompressor.with(mContext).compressVideo(Uri.parse(paths[0]), paths[1]);

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return  filePath;

        }


        @Override
        protected void onPostExecute(String compressedFilePath) {
            super.onPostExecute(compressedFilePath);
            File imageFile = new File(compressedFilePath);
            float length = imageFile.length() / 1024f; // Size in KB
            String value;
            if(length >= 1024)
                value = length/1024f+" MB";
            else
                value = length+" KB";
            String text = String.format(Locale.US, "%s\nName: %s\nSize: %s", "Video compression complete", imageFile.getName(), value);
            compressionMsg.setVisibility(View.GONE);
            Log.i("Silicompressor", "Path: "+compressedFilePath);
            Commons.compressedvideoUrl = compressedFilePath;
            finish();
        }
    }

}



























