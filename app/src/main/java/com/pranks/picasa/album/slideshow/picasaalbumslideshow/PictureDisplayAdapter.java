package com.pranks.picasa.album.slideshow.picasaalbumslideshow;

import android.content.SharedPreferences;
import android.util.ArraySet;
import android.widget.BaseAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pranks.picasa.album.slideshow.picasaalbumslideshow.imagemanager.ImageManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ankushc on 10/11/17.
 */

public class PictureDisplayAdapter extends BaseAdapter {
    private static final String TAG = PictureDisplayAdapter.class.getCanonicalName();
    private final String SHARED_PREF_FILE_NAME = "PICTURE_DISPLAY_ADAPTER_SHARED_FILE";
    private final String IMAGE_NOT_FOUND_URL = "http://www.ehypermart.in/0/images/frontend/image-not-found.png";

    private Context mContext;
    private List<String> mPhotoUrls;
    private LayoutInflater mLayoutInflater;

    public PictureDisplayAdapter(Context applicationContext, List<String> photoUrls) {
        this.mContext = applicationContext;
        this.mPhotoUrls = photoUrls;
        this.mLayoutInflater = (LayoutInflater.from(applicationContext));

        // Handle one special use case
        if(mPhotoUrls == null || mPhotoUrls.isEmpty()) {
            getFromPreviousStore();
        } else {
            //storeUrlsForFuture();
        }
    }

    @Override
    public int getCount() {
        return mPhotoUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = mLayoutInflater.inflate(R.layout.picture_item, null);
        ImageView pictureDisplay = (ImageView) view.findViewById(R.id.picture_to_display);
        Picasso.with(mContext).load(mPhotoUrls.get(position)).into(pictureDisplay);
        return view;
    }

    private void storeUrlsForFuture() {
        Set<String> urls = null;
        for(String url: mPhotoUrls) {
            urls.add(url);
        }

        if(urls == null || urls.isEmpty()) {
            return;
        }

        SharedPreferences sharedPref =
                mContext.getSharedPreferences(SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(ImageManager.PHOTO_SLIDESHOW_URLS, urls);
    }

    private void getFromPreviousStore() {
        SharedPreferences sharedPref =
                mContext.getSharedPreferences(SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE);
        Set<String> noImageSet = new HashSet<>();
        noImageSet.add(IMAGE_NOT_FOUND_URL);
        Set<String> urls = sharedPref.getStringSet(ImageManager.PHOTO_SLIDESHOW_URLS, noImageSet);

        List<String> photoUrls = new ArrayList<>();
        for(String url: urls) {
            photoUrls.add(url);
        }

        if(photoUrls != null || !photoUrls.isEmpty()) {
            mPhotoUrls = photoUrls;
        }

    }
}
