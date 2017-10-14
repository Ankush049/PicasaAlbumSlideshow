package com.pranks.picasa.album.slideshow.picasaalbumslideshow.imagemanager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.photos.AlbumEntry;
import com.google.gdata.data.photos.AlbumFeed;
import com.google.gdata.data.photos.GphotoEntry;
import com.google.gdata.data.photos.GphotoFeed;
import com.google.gdata.data.photos.PhotoEntry;
import com.google.gdata.data.photos.UserFeed;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.ServiceForbiddenException;
import com.pranks.picasa.album.slideshow.picasaalbumslideshow.LoginActivity;
import com.pranks.picasa.album.slideshow.picasaalbumslideshow.PictureDisplayActivity;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ankushc on 10/12/17.
 */

public class ImageManager implements AccountManagerCallback<Bundle> {

    public static final String PHOTO_SLIDESHOW_URLS = "PHOTO_SLIDESHOW_URLS";
    private static final String TAG = ImageManager.class.getCanonicalName();
    private static final String API_PREFIX = "https://picasaweb.google.com/data/feed/api/user/";

    private PicasawebService mPicasaService;
    private Context mContext;
    private AccountManager mAccountManager;
    private Account mUserAccount;
    private List<String> mPhotoUrls = new ArrayList<String>();

    public ImageManager(Context context, AccountManager accountManager,
                        Account selectedAccount) {
        this.mContext = context;
        this.mAccountManager = accountManager;
        this.mUserAccount = selectedAccount;
    }

    @Override
    public void run(AccountManagerFuture<Bundle> result) {
        try {
            Bundle b = result.getResult();

            if (b.containsKey(AccountManager.KEY_INTENT)) {
                Intent intent = b.getParcelable(AccountManager.KEY_INTENT);
                int flags = intent.getFlags();
                intent.setFlags(flags);
                flags &= ~Intent.FLAG_ACTIVITY_NEW_TASK;
                ((LoginActivity) mContext).startActivityForResult(intent, LoginActivity
                        .REQUEST_AUTHENTICATE);
                return;
            }

            if (b.containsKey(AccountManager.KEY_AUTHTOKEN)) {
                final String authToken = b.getString(AccountManager.KEY_AUTHTOKEN);
                Log.d("Auth token {}", authToken);
                mPicasaService = new PicasawebService("pictureframe");
                mPicasaService.setUserToken(authToken);

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        List<AlbumEntry> albums = null;
                        try {

                            albums = getAlbums(mUserAccount.name);
                            Log.d("Got {} albums", "album size is : " + albums.size());
                            for (AlbumEntry myAlbum : albums) {
                                Log.d("Album {} ", myAlbum.getTitle().getPlainText());
                            }

                            if (albums == null || albums.isEmpty()) {
                                return null;
                            }

                            AlbumEntry album = albums.get(2);
                            List<PhotoEntry> photos = getPhotos(mUserAccount.name, album);

                            for (PhotoEntry photo : photos) {
                                mPhotoUrls.add(photo.getMediaContents().get(0).getUrl());
                            }

                            Intent startPhotoDisplayApp = new Intent(mContext,
                                    PictureDisplayActivity.class);
                            startPhotoDisplayApp.putStringArrayListExtra(PHOTO_SLIDESHOW_URLS,
                                    (ArrayList<String>) mPhotoUrls);
                            mContext.startActivity(startPhotoDisplayApp);
                            return null;
                        } catch (ServiceForbiddenException e) {
                            Log.e(TAG, "Token expired, invalidating");
                            mAccountManager.invalidateAuthToken("com.google", authToken);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ServiceException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            Log.e(TAG, "Some exception happened, most likely due to no pics in " +
                                    "album ", e);
                        }
                        return null;
                    }

                    protected void onPostExecute(Void result) {
                        Toast.makeText(mContext, "Fetched the pictures for the album successfully", Toast.LENGTH_SHORT).show();
                    }
                }.execute(null, null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private List<AlbumEntry> getAlbums(String userId) throws IOException, ServiceException {

        String albumUrl = API_PREFIX + userId;
        UserFeed userFeed = getFeed(albumUrl, UserFeed.class);

        List<GphotoEntry> entries = userFeed.getEntries();
        List<AlbumEntry> albums = new ArrayList<AlbumEntry>();
        for (GphotoEntry entry : entries) {
            AlbumEntry ae = new AlbumEntry(entry);
            Log.d(TAG, "Album name {}" + ae.getName());
            albums.add(ae);
        }

        return albums;
    }

    private List<PhotoEntry> getPhotos(String userId, AlbumEntry album) throws IOException,
            ServiceException {
        AlbumFeed feed = album.getFeed();
        List<PhotoEntry> photos = new ArrayList<PhotoEntry>();
        for (GphotoEntry entry : feed.getEntries()) {
            PhotoEntry pe = new PhotoEntry(entry);
            photos.add(pe);
        }
        Log.d("Album {} has {} photos", album.getName() + photos.size());
        return photos;
    }

    private <T extends GphotoFeed> T getFeed(String feedHref, Class<T> feedClass) throws
            IOException, ServiceException {
        Log.d(TAG, "Get Feed URL: " + feedHref);
        return mPicasaService.getFeed(new URL(feedHref), feedClass);
    }
}

