package com.date.maskeddates.models;

/**
 * Created by sonback123456 on 6/18/2018.
 */

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


public class Album implements Parcelable {
    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    private String albumName;
    private String albumId;
    private ArrayList<String> photosUrls;

    public Album(String name, String id) {
        this.albumName = name;
        this.albumId = id;
        this.photosUrls = new ArrayList<>();
    }

    protected Album(Parcel in) {
        albumName = in.readString();
        albumId = in.readString();
        photosUrls = in.createStringArrayList();
    }

    public String getAlbumName() {
        return albumName;
    }

    public ArrayList<String> getPhotosUrls() {
        return photosUrls;
    }

    public String getAlbumId() {
        return albumId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(albumName);
        dest.writeString(albumId);
        dest.writeStringList(photosUrls);
    }
}
