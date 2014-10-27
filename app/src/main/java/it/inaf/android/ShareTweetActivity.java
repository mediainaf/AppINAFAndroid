/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ShareTweetActivity extends NavigationDrawerActivity {
    private String mTwHashTag;
    private String mTwEventUrl;
    private String mTwBeginDate;
    private String mTwEndDate;
    String mCurrentPhotoPath;
    private boolean mActionBarDisabled = false;

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_LOAD_PHOTO = 2;
    static final int REQUEST_SHARE_TWITTER = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            mTwHashTag = savedInstanceState.getString("twhashtag");
            mTwEventUrl = savedInstanceState.getString("tweventurl");
            mTwBeginDate = savedInstanceState.getString("twbegindate");
            mTwEndDate = savedInstanceState.getString("twenddate");
            mActionBarDisabled = savedInstanceState.getBoolean("actionbar");
        }
        else {
            JSONArray jsonAbout = INAF.loadJson(this, "json_about");
            try {
                JSONObject obj = jsonAbout.getJSONObject(0);
                mTwHashTag = obj.getString("twhashtag");
                mTwEventUrl = obj.getString("tweventurl");
                mTwBeginDate = obj.getString("twbegindate");
                mTwEndDate = obj.getString("twenddate");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            Date beginDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(mTwBeginDate);
            Date endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(mTwEndDate);
            Calendar cal = Calendar.getInstance();
            Date currentDate = cal.getTime();
            if(currentDate.before(beginDate) || currentDate.after(endDate)) {
                Toast.makeText(this, "Nessun evento con tweet in programma.", Toast.LENGTH_LONG).show();
                mActionBarDisabled = true;
                return;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        ShareTweetFragment fragment = new ShareTweetFragment();
        Bundle args = new Bundle();
        args.putString("tweventurl", mTwEventUrl);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment, "fragment_container")
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tweet_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mActionBarDisabled)
            return super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.action_open_camera:
                dispatchTakePictureIntent();
                return true;
            case R.id.action_open_gallery:
                openPictureFromGallery();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            File picture = new File(mCurrentPhotoPath);
            sendTweet(Uri.fromFile(picture));
        } else if (requestCode == REQUEST_LOAD_PHOTO && resultCode == RESULT_OK) {
            sendTweet(data.getData());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("twhashtag", mTwHashTag);
        outState.putString("tweventurl", mTwEventUrl);
        outState.putString("twbegindate", mTwBeginDate);
        outState.putString("twenddate", mTwEndDate);
        outState.putBoolean("actionbar", mActionBarDisabled);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        mCurrentPhotoPath = image.getCanonicalPath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void openPictureFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_LOAD_PHOTO);
    }

    private void sendTweet(Uri photoUri) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        List<ResolveInfo> resolvedInfoList = getPackageManager().queryIntentActivities(sharingIntent, PackageManager.MATCH_DEFAULT_ONLY);

        boolean found = false;
        for(ResolveInfo resolveInfo: resolvedInfoList){
            if(resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")) {
                sharingIntent.setClassName(
                        resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name);
                found = true;
                break;
            }
        }
        if(found){
            sharingIntent.putExtra(Intent.EXTRA_TEXT, "#"+mTwHashTag);
            sharingIntent.putExtra(Intent.EXTRA_STREAM, photoUri);
            startActivityForResult(sharingIntent, REQUEST_SHARE_TWITTER);
            startActivity(sharingIntent);
        }else{
            Toast.makeText(this, "Errore. L'applicazione Twitter non e` stata trovata.", Toast.LENGTH_LONG).show();
        }
    }
}