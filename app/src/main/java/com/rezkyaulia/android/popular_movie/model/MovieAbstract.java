package com.rezkyaulia.android.popular_movie.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rezky Aulia Pratama on 8/6/2017.
 */

public class MovieAbstract implements Parcelable {
    int type;
    String category;
    Movie movie;

    public MovieAbstract() {
    }

    public MovieAbstract(int type,String category) {
        this.type = type;
        this.category = category;
    }

    public MovieAbstract(int type, Movie movie) {
        this.type = type;
        this.movie = movie;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
        dest.writeString(this.category);
        dest.writeParcelable(this.movie, flags);
    }

    protected MovieAbstract(Parcel in) {
        this.type = in.readInt();
        this.category = in.readString();
        this.movie = in.readParcelable(Movie.class.getClassLoader());
    }

    public static final Creator<MovieAbstract> CREATOR = new Creator<MovieAbstract>() {
        @Override
        public MovieAbstract createFromParcel(Parcel source) {
            return new MovieAbstract(source);
        }

        @Override
        public MovieAbstract[] newArray(int size) {
            return new MovieAbstract[size];
        }
    };
}
