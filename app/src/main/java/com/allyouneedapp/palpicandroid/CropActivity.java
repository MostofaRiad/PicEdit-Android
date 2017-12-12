package com.allyouneedapp.palpicandroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.allyouneedapp.palpicandroid.utils.AppUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cn.jarlen.photoedit.crop.CropImageType;
import cn.jarlen.photoedit.crop.CropImageView;
import cn.jarlen.photoedit.crop.CropOverlayView;

public class CropActivity extends AppCompatActivity implements View.OnClickListener, CropOverlayView.TouchOutSideListener {

    public static String path;
    private ImageButton btnRotate;
    private ImageButton btnResize;
    private Bitmap tempBitmap;

    private Button btnBack;
    private Button btnNext;
    private Button btnCrop;

    private CropImageView cropImageView;

    private LinearLayout layoutResize;
    private Button btnCustom;
    private Button btnSquare;
    private Button btn23;
    private Button btn34;
    private Button btn35;
    private Button btn916;
    private Button btn32;
    private Button btn43;
    private Button btn53;
    private Button btn169;
    private Button btn1911;

    int currentSelectedView = 0;

    ACProgressFlower dialog;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);

        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.DKGRAY).build();

        initAllViews();
        gridViewSettings();


        loadBanner();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        if (this.tempBitmap == null) {
            this.dialog.show();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    private void gridViewSettings() {
        cropImageView.setGuidelines(CropImageType.CROPIMAGE_GRID_ON);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.crop_button);
        cropImageView.setCropOverlayCornerBitmap(bm);
        cropImageView.setFixedAspectRatio(false);
        path = getIntent().getStringExtra("filePath");

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, options);
                int imageHeight = options.outHeight;
                int imageWidth = options.outWidth;

                DisplayMetrics displaymetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                int height = displaymetrics.heightPixels;
                int width = displaymetrics.widthPixels;
                Bitmap bit = null;
                if (imageHeight < imageWidth) {
                    if (imageWidth > width * 4) {
                        int newHeight = imageHeight * width / imageWidth;
                        bit = decodeSampledBitmapFromFile(path, width * 4, newHeight * 4);
                    } else {
                        bit = BitmapFactory.decodeFile(path);
                    }
                } else {
                    if (imageHeight > height * 4) {
                        int newWidth = imageWidth * height / imageHeight;
                        bit = decodeSampledBitmapFromFile(path, height * 4, newWidth * 4);
                    } else {
                        bit = BitmapFactory.decodeFile(path);
                    }
                }

                if (AppUtils.getExifRotation(path) == 90) {
                    tempBitmap = AppUtils.rotateBitmap(bit, 90);
                } else {
                    tempBitmap = bit;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CropActivity.this.cropImageView.setImageBitmap(tempBitmap);
                        if (CropActivity.this.dialog.isShowing())
                            CropActivity.this.dialog.dismiss();
                    }
                });
            }
        });

        getSupportActionBar().hide();

        setCropImageEditStatus(false);
    }

    private void initAllViews() {
        btnRotate = (ImageButton) findViewById(R.id.btn_rotate);
        btnRotate.setOnClickListener(this);
        btnResize = (ImageButton) findViewById(R.id.btn_resize);
        btnResize.setOnClickListener(this);

        btnBack = (Button) findViewById(R.id.btn_back_crop);
        btnBack.setOnClickListener(this);
        btnNext = (Button) findViewById(R.id.btn_next_crop);
        btnNext.setOnClickListener(this);
        btnCrop = (Button) findViewById(R.id.btn_crop_crop);
        btnCrop.setOnClickListener(this);

        cropImageView = (CropImageView) findViewById(R.id.cropmageView);
        cropImageView.setOnClickListener(this);
        cropImageView.getmCropOverlayView().setmTouchOutSideListener(this);

        layoutResize = (LinearLayout) findViewById(R.id.layout_ratio_crop);

        btnCustom = (Button) findViewById(R.id.btn_ratio_custom);
        btnCustom.setOnClickListener(this);
        btnSquare = (Button) findViewById(R.id.btn_ratio_square);
        btnSquare.setOnClickListener(this);
        btn23 = (Button) findViewById(R.id.btn_ratio_23);
        btn23.setOnClickListener(this);
        btn34 = (Button) findViewById(R.id.btn_ratio_34);
        btn34.setOnClickListener(this);
        btn35 = (Button) findViewById(R.id.btn_ratio_35);
        btn35.setOnClickListener(this);
        btn916 = (Button) findViewById(R.id.btn_ratio_916);
        btn916.setOnClickListener(this);
        btn32 = (Button) findViewById(R.id.btn_ratio_32);
        btn32.setOnClickListener(this);
        btn43 = (Button) findViewById(R.id.btn_ratio_43);
        btn43.setOnClickListener(this);
        btn53 = (Button) findViewById(R.id.btn_ratio_53);
        btn53.setOnClickListener(this);
        btn169 = (Button) findViewById(R.id.btn_ratio_169);
        btn169.setOnClickListener(this);
        btn1911 = (Button) findViewById(R.id.btn_ratio_1911);
        btn1911.setOnClickListener(this);

        //init views
        layoutResize.setVisibility(View.GONE);
        btnResize.setImageResource(R.drawable.resizepress);

    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // BEGIN_INCLUDE (calculate_sample_size)
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            long totalPixels = width * height / inSampleSize;

            // Anything more than 2x the requested pixels we'll sample down further
            final long totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels > totalReqPixelsCap) {
                inSampleSize *= 2;
                totalPixels /= 2;
            }
        }
        return inSampleSize;
        // END_INCLUDE (calculate_sample_size)
    }

    public static Bitmap decodeSampledBitmapFromFile(String filename, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filename, options);
    }



    @Override
    public void onClick(View v) {
        layoutResize.setVisibility(View.GONE);
        btnResize.setImageResource(R.drawable.resizepress);
        switch (v.getId()) {
            //Bottom Bar button click
            case R.id.btn_rotate:
                cropImageView.rotateImage(-90);
                tempBitmap = rotateBitmap(tempBitmap, -90);
                break;
            case R.id.btn_resize:
                if (currentSelectedView == v.getId()) {
                    layoutResize.setVisibility(View.GONE);
                    currentSelectedView = 0;
                    return;
                } else {
                    layoutResize.setVisibility(View.VISIBLE);
                    btnResize.setImageResource(R.drawable.resize);
                }
                break;
            //Navigation bar item click
            case R.id.btn_back_crop:
                this.finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            case R.id.btn_next_crop:
                Intent okData = new Intent(this, FilterActivity.class);
                FilterActivity.tempBitmap = tempBitmap;
                okData.putExtra("filePath", path);
                startActivity(okData);
                overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
                break;
            case R.id.btn_crop_crop:
                setCropImageEditStatus(false);
                tempBitmap = cropImageView.getCroppedImage();
                cropImageView.setImageBitmap(tempBitmap);
                break;
            //crop size button click
            case R.id.btn_ratio_custom:
                setCropImageEditStatus(true);
                cropImageView.setFixedAspectRatio(false);
                break;
            case R.id.btn_ratio_square:
                setCropImageEditStatus(true);
                cropImageView.setFixedAspectRatio(true);
                cropImageView.setAspectRatio(10, 10);
                break;
            case R.id.btn_ratio_23:
                setCropImageEditStatus(true);
                cropImageView.setFixedAspectRatio(true);
                cropImageView.setAspectRatio(20, 30);
                break;
            case R.id.btn_ratio_34:
                setCropImageEditStatus(true);
                cropImageView.setFixedAspectRatio(true);
                cropImageView.setAspectRatio(30, 40);
                break;
            case R.id.btn_ratio_35:
                setCropImageEditStatus(true);
                cropImageView.setFixedAspectRatio(true);
                cropImageView.setAspectRatio(30, 50);
                break;
            case R.id.btn_ratio_916:
                setCropImageEditStatus(true);
                cropImageView.setFixedAspectRatio(true);
                cropImageView.setAspectRatio(90, 160);
                break;
            case R.id.btn_ratio_32:
                setCropImageEditStatus(true);
                cropImageView.setFixedAspectRatio(true);
                cropImageView.setAspectRatio(30, 20);
                break;
            case R.id.btn_ratio_43:
                setCropImageEditStatus(true);
                cropImageView.setFixedAspectRatio(true);
                cropImageView.setAspectRatio(40, 30);
                break;
            case R.id.btn_ratio_53:
                setCropImageEditStatus(true);
                cropImageView.setFixedAspectRatio(true);
                cropImageView.setAspectRatio(50, 30);
                break;
            case R.id.btn_ratio_169:
                setCropImageEditStatus(true);
                cropImageView.setFixedAspectRatio(true);
                cropImageView.setAspectRatio(160, 90);
                break;
            case R.id.btn_ratio_1911:
                setCropImageEditStatus(true);
                cropImageView.setFixedAspectRatio(true);
                cropImageView.setAspectRatio(191, 100);
                break;
            default:
                setCropImageEditStatus(false);
                break;
        }
        currentSelectedView = v.getId();
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        Bitmap rotatedImage = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                matrix, true);
        return rotatedImage;
    }

    private void setCropImageEditStatus(boolean status) {
        if (status) {
            cropImageView.getmCropOverlayView().setVisibility(View.VISIBLE);
            btnCrop.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.INVISIBLE);
        } else {
            cropImageView.getmCropOverlayView().setVisibility(View.INVISIBLE);
            btnCrop.setVisibility(View.INVISIBLE);
            btnNext.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTouchOutSide(View view) {
        btnResize.setImageResource(R.drawable.resizepress);
        currentSelectedView = 0;
        setCropImageEditStatus(false);
        layoutResize.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        layoutResize.setVisibility(View.GONE);
        btnResize.setImageResource(R.drawable.resizepress);
        cropImageView.getmCropOverlayView().setVisibility(View.INVISIBLE);
        btnCrop.setVisibility(View.INVISIBLE);
        btnNext.setVisibility(View.VISIBLE);
    }

    /**
     * Banner view method
     */
    private void loadBanner() {
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Crop Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
