package com.date.maskeddates.adapters;

/**
 * Created by sonback123456 on 6/6/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.date.maskeddates.R;
import com.date.maskeddates.main.UploadPicturesActivity;
import com.date.maskeddates.main.ViewPhotoActivity;
import com.date.maskeddates.models.Picture;
import com.eyalbira.loadingdots.LoadingDots;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by a on 3/28/2017.
 */

public class MyPhotosAdapter extends BaseAdapter {

    private UploadPicturesActivity _context;
    private ArrayList<Picture> _datas = new ArrayList<>();
    private ArrayList<Picture> _alldatas = new ArrayList<>();

    public MyPhotosAdapter(UploadPicturesActivity context){

        super();
        this._context = context;
    }

    public void setDatas(ArrayList<Picture> datas) {

        _alldatas = datas;
        _datas.clear();
        _datas.addAll(_alldatas);
    }

    @Override
    public int getCount(){
        return _datas.size();
    }

    @Override
    public Object getItem(int position){
        return _datas.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        CustomHolder holder;

        if (convertView == null) {
            holder = new CustomHolder();

            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            convertView = inflater.inflate(R.layout.my_photos_item, parent, false);

            holder.picture = (ImageView) convertView.findViewById(R.id.photo);
            holder.progressBar = (LoadingDots) convertView.findViewById(R.id.progressBar);

            holder.editButton = (ImageView) convertView.findViewById(R.id.editButton);
            holder.deleteButton = (ImageView) convertView.findViewById(R.id.deleteButton);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final Picture picture = (Picture) _datas.get(position);

        Picasso.with(_context)
                .load(picture.getPicture_url())
                .error(R.drawable.q9)
                .placeholder(R.drawable.q9)
                .into(holder.picture, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.INVISIBLE);
                    }
                    @Override
                    public void onError() {
                        holder.progressBar.setVisibility(View.INVISIBLE);
                    }
                });

        holder.picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(_context, ViewPhotoActivity.class);
                intent.putExtra("photoUrl", picture.getPicture_url());
                _context.startActivity(intent);
            }
        });

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _context.editPicture(picture.getIdx());
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _context.showDelPictureAlert(picture.getIdx());
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return convertView;
    }

    class CustomHolder {
        ImageView picture;
        LoadingDots progressBar;
        ImageView editButton, deleteButton;
    }
}



