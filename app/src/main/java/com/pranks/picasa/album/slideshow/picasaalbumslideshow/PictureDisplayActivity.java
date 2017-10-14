package com.pranks.picasa.album.slideshow.picasaalbumslideshow;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterViewFlipper;

import com.pranks.picasa.album.slideshow.picasaalbumslideshow.imagemanager.ImageManager;

import java.util.List;

public class PictureDisplayActivity extends Activity {

    private static final int DISPLAY_TIME_INTERVAL = 10000;

    private List<String> mPhotoUrls;
    private AdapterViewFlipper mImageAdapterViewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_display);

        mPhotoUrls = getIntent().getExtras().getStringArrayList(ImageManager.PHOTO_SLIDESHOW_URLS);

        mImageAdapterViewFlipper =
                (AdapterViewFlipper) findViewById(R.id.simpleAdapterViewFlipper);

        setupImageViewFlippingAdapter();
    }

    /**
     * To hide the bottom navigation buttons.
     *
     * https://stackoverflow.com/questions/19049764/how-to-remove-button-bar-at-the-bottom-screen
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void setupImageViewFlippingAdapter() {
        if (mPhotoUrls == null) {
            //TODO Check the mPhotoUrls for null and show user a message
        }
        // Custom Adapter for setting the data in Views
        PictureDisplayAdapter pictureDisplayAdapter = new PictureDisplayAdapter(this, mPhotoUrls);
        mImageAdapterViewFlipper.setAdapter(pictureDisplayAdapter); // set adapter for
        // AdapterViewFlipper
        setupAnimForImageRotation();
        // set interval time for flipping between views
        mImageAdapterViewFlipper.setFlipInterval(DISPLAY_TIME_INTERVAL);
        // set auto start for flipping between views
        mImageAdapterViewFlipper.setAutoStart(true);
    }

    /**
     * Animation for the image transitions.
     */
    private void setupAnimForImageRotation() {
        // set the animation type to ViewFlipper
        mImageAdapterViewFlipper.setInAnimation(this, R.animator.slide_in);
        mImageAdapterViewFlipper.setOutAnimation(this, R.animator.slide_out);
    }
}
