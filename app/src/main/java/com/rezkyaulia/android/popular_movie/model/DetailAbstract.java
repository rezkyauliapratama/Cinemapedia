package com.rezkyaulia.android.popular_movie.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rezky Aulia Pratama on 8/6/2017.
 */

public class DetailAbstract implements Parcelable {
    int type;
    boolean isLandscape;
    Movie movie;

    public DetailAbstract(int type, Movie movie,boolean isLandscape) {
        this.type = type;
        this.movie = movie;
        this.isLandscape = isLandscape;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isLandscape() {
        return isLandscape;
    }

    public void setLandscape(boolean landscape) {
        isLandscape = landscape;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeByte(this.isLandscape ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.movie, flags);
    }

    protected DetailAbstract(Parcel in) {
        this.type = in.readInt();
        this.isLandscape = in.readByte() != 0;
        this.movie = in.readParcelable(Movie.class.getClassLoader());
    }

    public static final Creator<DetailAbstract> CREATOR = new Creator<DetailAbstract>() {
        @Override
        public DetailAbstract createFromParcel(Parcel source) {
            return new DetailAbstract(source);
        }

        @Override
        public DetailAbstract[] newArray(int size) {
            return new DetailAbstract[size];
        }
    };
}
