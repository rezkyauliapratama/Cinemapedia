package com.rezkyaulia.android.popular_movie.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rezky Aulia Pratama on 8/5/2017.
 */

public class DetailModel implements Parcelable {
    int type;
    int id;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeInt(this.id);
    }

    public DetailModel() {
    }

    public DetailModel(int type, int id) {
        this.type = type;
        this.id = id;
    }

    protected DetailModel(Parcel in) {
        this.type = in.readInt();
        this.id = in.readInt();
    }

    public static final Creator<DetailModel> CREATOR = new Creator<DetailModel>() {
        @Override
        public DetailModel createFromParcel(Parcel source) {
            return new DetailModel(source);
        }

        @Override
        public DetailModel[] newArray(int size) {
            return new DetailModel[size];
        }
    };
}
