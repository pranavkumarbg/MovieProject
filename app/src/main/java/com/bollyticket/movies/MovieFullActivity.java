/*
 * Copyright (C) 2016 Bolly Ticket
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bollyticket.movies;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bollyticket.movies.services.MovieDownloadService;
import com.bollyticket.movies.utils.BlurTransformation;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.startapp.android.publish.StartAppAd;

import java.util.concurrent.TimeUnit;

public class MovieFullActivity extends AppCompatActivity implements Target {

    ImageView imageView;
    private StartAppAd startAppAd = new StartAppAd(this);

    private static final int BACKGROUND_IMAGES_WIDTH = 360;
    private static final int BACKGROUND_IMAGES_HEIGHT = 360;
    private static final float BLUR_RADIUS = 25F;
    private final Handler handler = new Handler();
    AlertDialog levelDialog;
    final CharSequence[] items = {" Internal Player ", " External Player "};
    private BlurTransformation blurTransformation;
    private Point backgroundImageTargetSize;
    String image;
    String flag;
    String name;
    TextView tv;
    Button watch, send;
    RatingBar ratingBar;
    ProgressBar progressBar;
    String sendbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moviefullview);

        progressBar = (ProgressBar) findViewById(R.id.progressbarfull);
        blurTransformation = new BlurTransformation(this, BLUR_RADIUS);
        backgroundImageTargetSize = calculateBackgroundImageSizeCroppedToScreenAspectRatio(getWindowManager().getDefaultDisplay());

        updateWindowBackground();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarfull);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setTitle("Movie");
        ab.setDisplayHomeAsUpEnabled(true);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);

        scrollView.setSmoothScrollingEnabled(true);
        imageView = (ImageView) findViewById(R.id.imageViewfull);
        tv=(TextView)findViewById(R.id.textviewmy);
        Intent i = getIntent();

        flag = i.getStringExtra("flagurl");
        image = i.getStringExtra("flagimage");
        name = i.getStringExtra("flagname");

        tv.setText(name);

        watch = (Button) findViewById(R.id.Button04);
        ratingBar = (RatingBar) findViewById(R.id.rating);
        send = (Button) findViewById(R.id.buttonsend);

        Glide.with(this)
                .load(image)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new BitmapImageViewTarget(imageView) {

                    @Override
                    public void onResourceReady(final Bitmap resource, GlideAnimation glideAnimation) {
                        super.onResourceReady(resource, glideAnimation);


                    }
                });


        watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MovieFullActivity.this, R.style.MyDialogTheme);
                builder.setTitle("Select The Player");
                builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {


                        switch (item) {
                            case 0:

                                Samples.Sample sample = new Samples.Sample("Android screens (Matroska)", flag, PlayerActivity.TYPE_OTHER);

                                Intent mpdIntent = new Intent(MovieFullActivity.this, PlayerActivity.class)

                                        .setData(Uri.parse(sample.uri))
                                        .putExtra(PlayerActivity.CONTENT_ID_EXTRA, sample.contentId)
                                        .putExtra(PlayerActivity.CONTENT_TYPE_EXTRA, sample.type);
                                startActivity(mpdIntent);
                                break;
                            case 1:

                                Uri intentUri = Uri.parse(flag);

                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setDataAndType(intentUri, "video/*");
                                startActivity(intent);


                                break;


                        }
                        levelDialog.dismiss();
                    }
                });
                levelDialog = builder.create();
                levelDialog.show();
            }
        });



        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                sendbar = String.valueOf(v);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "bollyticket@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, sendbar);

                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        startAppAd.onPause();

        handler.removeCallbacksAndMessages(null);


    }

    @Override
    public void onResume() {
        super.onResume();
        startAppAd.onResume();
    }


    @Override
    public void onBackPressed() {
        startAppAd.onBackPressed();
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onBitmapFailed(Drawable drawable) {
        getWindow().setBackgroundDrawable(drawable);
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        changeBackground(new BitmapDrawable(getResources(), bitmap));
        progressBar.setVisibility(View.GONE);
        startAppAd.showAd();
        startAppAd.loadAd();

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }

    private void updateWindowBackground() {

        progressBar.setVisibility(View.VISIBLE);


        Picasso.with(this).load(image)
                .resize(backgroundImageTargetSize.x, backgroundImageTargetSize.y).centerCrop()
                .transform(blurTransformation).error(R.color.white).into((Target) this);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateWindowBackground();
            }
        }, TimeUnit.SECONDS.toMillis(1));
    }


    private void changeBackground(Drawable drawable) {
        View decorView = getWindow().getDecorView();
        Drawable oldBackgroundDrawable = decorView.getBackground();
        TransitionDrawable transitionDrawable = new TransitionDrawable(
                new Drawable[]{oldBackgroundDrawable, drawable});
        setBackgroundCompat(decorView, transitionDrawable);
        transitionDrawable.startTransition(1000);
    }

    private static void setBackgroundCompat(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    private static Point calculateBackgroundImageSizeCroppedToScreenAspectRatio(Display display) {
        final Point screenSize = new Point();
        getSizeCompat(display, screenSize);
        int scaledWidth = (int) (((double) BACKGROUND_IMAGES_HEIGHT * screenSize.x) / screenSize.y);
        int croppedWidth = Math.min(scaledWidth, BACKGROUND_IMAGES_WIDTH);
        int scaledHeight = (int) (((double) BACKGROUND_IMAGES_WIDTH * screenSize.y) / screenSize.x);
        int croppedHeight = Math.min(scaledHeight, BACKGROUND_IMAGES_HEIGHT);
        return new Point(croppedWidth, croppedHeight);
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private static void getSizeCompat(Display display, Point screenSize) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(screenSize);
        } else {
            screenSize.x = display.getWidth();
            screenSize.y = display.getHeight();
        }
    }


}
