package com.pranks.picasa.album.slideshow.picasaalbumslideshow.bootloader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pranks.picasa.album.slideshow.picasaalbumslideshow.PictureDisplayActivity;

/**
 * Created by ankushc on 10/11/17.
 */

public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent activityIntent = new Intent(context, PictureDisplayActivity.class);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);
        }
    }
}
