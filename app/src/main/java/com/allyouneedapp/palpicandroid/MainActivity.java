package com.allyouneedapp.palpicandroid;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.allyouneedapp.palpicandroid.adapters.AlbumAdapter;
import com.allyouneedapp.palpicandroid.adapters.GalleryAdapter;
import com.allyouneedapp.palpicandroid.models.Album;
import com.allyouneedapp.palpicandroid.models.AlbumData;
import com.allyouneedapp.palpicandroid.utils.AlbumLoader;
import com.allyouneedapp.palpicandroid.utils.AppUtils;
import com.allyouneedapp.palpicandroid.utils.FirebaseDbUtils;
import com.baoyz.actionsheet.ActionSheet;
import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.client.Firebase;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cn.jarlen.photoedit.utils.FileUtils;

import static com.allyouneedapp.palpicandroid.utils.Constants.CAMERA_WITH_DATA;
import static com.allyouneedapp.palpicandroid.utils.Constants.FACEBOOK_URL;
import static com.allyouneedapp.palpicandroid.utils.Constants.KEY_EMAIL;
import static com.allyouneedapp.palpicandroid.utils.Constants.KEY_NAME;
import static com.allyouneedapp.palpicandroid.utils.Constants.KEY_UID;
import static com.allyouneedapp.palpicandroid.utils.Constants.MY_PERMISSIONS_REQUEST_CAMERA;
import static com.allyouneedapp.palpicandroid.utils.Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
import static com.allyouneedapp.palpicandroid.utils.Constants.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ListView.OnItemClickListener, ActionSheet.ActionSheetListener , AppUtils.AppUtilsListener,AlbumLoader.AlbumLoadListener{

    public static final String CAMERA_IMAGE_BUCKET_NAME = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera";
    public static final String CAMERA_IMAGE_BUCKET_ID = getBucketId(CAMERA_IMAGE_BUCKET_NAME);

    //ImageButton
    private ImageButton btnAllbums;
    private ImageButton btnCamera;
    private ImageButton btnMore;
    private ImageButton btnContactUs;

    //ContactUs View
    private ImageButton btnSuggestSticker;
    private ImageButton btnFanpage;
    private ImageButton btnEmail;
    private ImageButton btnFollowUs;
    private ImageButton btnAboutus;
    //More view
    private ImageButton btnTellFriend;
    private ImageButton btnSignOut;
    private ImageButton btnRate;

    //Album List
    private ListView listviewAlbums;

    //GridView
    private GridView gridView;

    //dim_image
    private ImageView dimBackground;

    //Layouts
    private LinearLayout layoutMore;
    private LinearLayout layoutContact;

    //register layout
    private LinearLayout layoutRegister;
    private EditText tVEmail;
    private EditText tVPassword;
    private Button btnRegister;
    private Button btnLogin;
    private Button btnCancel;
    private LoginButton btnFaceBook;
    private SignInButton btnGoogle;

    //suggest sticker layout
    private RelativeLayout layoutSuggest;
    private EditText textSuggestTitle;
    private Button btnSuggest;
    private Button btnCancelSuggest;

    //camera pic
    private String photoPath = null, tempPhotoPath;
    private File mCurrentPhotoFile;

    ShareDialog shareDialog;

    private int currentViewId = 0;

    //Albums Name and count of content
    ArrayList<Album> mAlbumsList;
    AlbumAdapter albumAdapter;
    GalleryAdapter galleryAdapter;
    ArrayList<AlbumData> galleryData = new ArrayList<>();
    AlbumLoader albumLoader;

    ACProgressFlower dialog;

    private FirebaseAuth.AuthStateListener mAuthListener;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    private AppUtils appUtils;

    public static String getBucketId(String path) {
        return String.valueOf(path.toLowerCase().hashCode());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        MultiDex.install(this);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
        FacebookSdk.sdkInitialize(this);
        getSupportActionBar().hide();

        initAllValiables();
        initViews();
        resetAllViews();

        loadBanner();

        /**Getting permission in 23+ SDK version*/
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
            } else {
                getAlbumNames();
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                }
            } else {

            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)) {

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }
            } else {

            }
        } else {
            getAlbumNames();
        }

        //Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(appUtils.mCallbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(MainActivity.this,"success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this,"failed:"+error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    private void initViews() {
        btnAllbums = (ImageButton) findViewById(R.id.main_bar_btn_album);
        btnAllbums.setOnClickListener(this);
        btnCamera = (ImageButton) findViewById(R.id.main_bar_btn_camera);
        btnCamera.setOnClickListener(this);
        btnMore = (ImageButton) findViewById(R.id.main_bar_btn_more);
        btnMore.setOnClickListener(this);
        btnContactUs = (ImageButton) findViewById(R.id.main_bar_btn_contact);
        btnContactUs.setOnClickListener(this);
        //contact us views
        btnSuggestSticker = (ImageButton) findViewById(R.id.btn_suggestSticker_main);
        btnSuggestSticker.setOnClickListener(this);
        btnFanpage = (ImageButton) findViewById(R.id.btn_fanpage);
        btnFanpage.setOnClickListener(this);
        btnEmail = (ImageButton) findViewById(R.id.btn_email);
        btnEmail.setOnClickListener(this);
        btnFollowUs = (ImageButton) findViewById(R.id.btn_follow_us);
        btnFollowUs.setOnClickListener(this);
        btnAboutus = (ImageButton) findViewById(R.id.btn_about_us);
        btnAboutus.setOnClickListener(this);

        //More views
        btnTellFriend = (ImageButton) findViewById(R.id.btn_tell_friend);
        btnTellFriend.setOnClickListener(this);
        btnSignOut = (ImageButton) findViewById(R.id.btn_sign_out);
        btnSignOut.setOnClickListener(this);
        btnRate = (ImageButton) findViewById(R.id.btn_rate);
        btnRate.setOnClickListener(this);
        //Album List
        listviewAlbums = (ListView) findViewById(R.id.listView_albumName);
        listviewAlbums.setOnItemClickListener(this);
        //
        gridView = (GridView) findViewById(R.id.gridView_album);
        gridView.setOnItemClickListener(this);

        dimBackground = (ImageView) findViewById(R.id.dim_back);
        dimBackground.setOnClickListener(this);

        layoutMore = (LinearLayout) findViewById(R.id.layout_more);
        layoutMore.setOnClickListener(this);
        layoutContact = (LinearLayout) findViewById(R.id.layout_contact_us);
        layoutContact.setOnClickListener(this);

        //register layout
        layoutRegister = (LinearLayout) findViewById(R.id.layout_register_login_main);
        tVEmail = (EditText) findViewById(R.id.text_email_main);
        tVPassword = (EditText) findViewById(R.id.text_password_main);
        btnRegister = (Button) findViewById(R.id.btn_register_main);
        btnRegister.setOnClickListener(this);
        btnLogin = (Button) findViewById(R.id.btn_login_main);
        btnLogin.setOnClickListener(this);
        btnCancel = (Button) findViewById(R.id.btn_cancel_main);
        btnCancel.setOnClickListener(this);

        btnFaceBook = (LoginButton) findViewById(R.id.btn_fb_login_main);

        btnFaceBook.registerCallback(appUtils.mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken token = loginResult.getAccessToken();
                appUtils.handleFacebookAccessToken(token);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this,error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        btnGoogle = (SignInButton) findViewById(R.id.btn_google_main);
        btnGoogle.setOnClickListener(this);

        //suggest sticker layout
        layoutSuggest = (RelativeLayout) findViewById(R.id.layout_suggest_sticker_logged_main);
        textSuggestTitle = (EditText) findViewById(R.id.edit_text_suggest_sticker_main);

        btnSuggest = (Button) findViewById(R.id.btn_suggest_sticker_main);
        btnSuggest.setOnClickListener(this);
        btnCancelSuggest = (Button) findViewById(R.id.btn_suggest_sticker_cancel_main);
        btnCancelSuggest.setOnClickListener(this);

        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.DKGRAY).build();
    }

    @Override
    protected void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    } else {
                        albumLoader.loadAlbums(CAMERA_IMAGE_BUCKET_ID);
                    }
                } else {
                    albumLoader.loadAlbums(CAMERA_IMAGE_BUCKET_ID);
                }
            }
        });

        ViewTreeObserver vto = gridView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                gridView.getViewTreeObserver().removeOnPreDrawListener(this);
                int width = gridView.getMeasuredWidth();
                ViewGroup.LayoutParams layoutParams = gridView.getLayoutParams();
                layoutParams.width = width;
//                layoutParams.height = width - 5;//paid version
                layoutParams.height = width*3/4 - 5;//unpaid version
                gridView.setLayoutParams(layoutParams);
                return true;
            }
        });

        resetAllViews();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
        appUtils.mAuth.addAuthStateListener(mAuthListener);
        appUtils.mGoogleApiClient.connect();

    }

    private void resetAllViews() {
        listviewAlbums.setVisibility(View.INVISIBLE);

        dimBackground.setVisibility(View.INVISIBLE);

        layoutContact.setVisibility(View.INVISIBLE);
        layoutMore.setVisibility(View.INVISIBLE);
        layoutRegister.setVisibility(View.INVISIBLE);
        layoutSuggest.setVisibility(View.GONE);

        btnAllbums.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        btnMore.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        btnContactUs.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        currentViewId = 0;
    }

    private void initAllValiables() {
        mAlbumsList = new ArrayList<>();
        appUtils = new AppUtils(this);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        appUtils.initLanguageDetector();
                    }
                }).start();
            }
        });

        albumLoader = new AlbumLoader(this);
        albumLoader.setListener(this);

        galleryAdapter = new GalleryAdapter(this,galleryData);

        //set auth of firebase listener
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                SharedPreferences.Editor pref = getSharedPreferences("palpic", Context.MODE_PRIVATE).edit();
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // Name, email address, and profile photo Url
                    String name = user.getDisplayName();
                    String email = user.getEmail();
                    String uid = user.getUid();

                    pref.putString(KEY_NAME,name);
                    pref.putString(KEY_EMAIL,email);
                    pref.putString(KEY_UID,uid);
                    pref.commit();
                    FirebaseInstanceId.getInstance().getToken();
                    /**Download Stickers*/
                    FirebaseDbUtils dbUtils = new FirebaseDbUtils(MainActivity.this);
                    dbUtils.getSuggestedStickers();

                } else {
                    SharedPreferences pre = getSharedPreferences("palpic", Context.MODE_PRIVATE);
                    if (pre.getString("non_register", null) == null) {
                        pref.putString(KEY_NAME, null);
                        pref.putString(KEY_EMAIL, null);
                        pref.putString(KEY_UID, null);
                        pref.putString("non_register", appUtils.getSaltString());
                        pref.commit();
                    }
                }
            }
        };

    }

    @Override
    public void onClick(View v) {
        String email = tVEmail.getText().toString();
        String password = tVPassword.getText().toString();
        appUtils.setAppUtilsListener(this);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        switch (v.getId()) {
            //tab bar
            case R.id.main_bar_btn_album:
                albumAdapter = new AlbumAdapter(this, this.mAlbumsList);
                this.listviewAlbums.setAdapter(albumAdapter);
                albumAdapter.notifyDataSetChanged();
                break;
            case R.id.main_bar_btn_camera:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        getPictureFormCamera();
                    }
                } else {
                    getPictureFormCamera();
                }
                break;
            //contact us views
            case R.id.btn_suggestSticker_main:
                if ( appUtils.isSignIn()) {
                // User is signed in
                    layoutSuggest.setVisibility(View.VISIBLE);
                } else {
                    // No user is signed in
                    layoutRegister.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btn_fanpage:
                Uri facebookuri;
                try {
                    getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    facebookuri = Uri.parse("fb://facewebmodal/f?href=" + FACEBOOK_URL);
                } catch (PackageManager.NameNotFoundException e) {
                    facebookuri = Uri.parse(FACEBOOK_URL);
                }

                Intent facebookIntent = new Intent(Intent.ACTION_VIEW, facebookuri);
                startActivity(facebookIntent);
                break;
            case R.id.btn_email:
                /* Create the Intent */
                final Intent emailIntent = new Intent(Intent.ACTION_SEND);

                /* Fill it with Data */
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@allyouneedapp.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Check this app: Android "+ Build.VERSION.SDK_INT);
                emailIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, "http://allyouneedapp.com");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear, \n \n \n Machine Model: "+Build.MODEL +" \n OS: "+Build.VERSION.RELEASE);

                /* Send it off to the Activity-Chooser */
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                break;
            case R.id.btn_follow_us:
                String uri = "http://instagram.com/_u/palpicapp";
                Intent likeIng = newInstagramProfileIntent(uri);
                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/palpicapp")));
                }
                break;
            case R.id.btn_about_us:
                String url = "http://allyouneedapp.com";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
            //more views
            case R.id.btn_tell_friend:
                //Action sheet -> FaceBook, Twitter, Email, cancel
                ActionSheet.createBuilder(this, getSupportFragmentManager())
                        .setCancelButtonTitle("Cancel")
                        .setOtherButtonTitles("FaceBook", "Twitter","Instagram", "E-mail")
                        .setCancelableOnTouchOutside(true)
                        .setListener(this).show();
                break;
            case R.id.btn_sign_out:
                //Alert will be displayed.
                //check firebase sign in and show alert : success sign out or not sign-in yet.
                String _title = "PalPic";
                String _message = "You are not Sign-in";
                String _signOut = "Sign-Out success";
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // User is signed in
                    appUtils.mAuth.signOut();
                    LoginManager.getInstance().logOut();
                    alertWithTitle(_title,_signOut);
                } else {
                    // No user is signed in
                    alertWithTitle(_title, _message);
                }
                break;
            case R.id.btn_rate:
                //Alert will be displayed. -> SharePic
//                launchMarket();
                String title = "PalPic";
                String message = "If app is not yet on the store or is not intended for AppStore release then don't worry about this.";
                alertWithTitle(title, message);
                break;
            //register layout
            case R.id.btn_register_main:
                if (email.isEmpty()||password.isEmpty()){
                    Toast.makeText(this,"Insert E-mail and Password", Toast.LENGTH_LONG).show();
                } else {
                    appUtils.registeWithEmail(email, password);
                    dialog.show();
                }
                break;
            case R.id.btn_login_main:
                if (email.isEmpty()||password.isEmpty()){
                    Toast.makeText(this,"Insert E-mail and Password", Toast.LENGTH_LONG).show();
                } else {
                    appUtils.loginToFirebase(email, password);
                    dialog.show();
                }
                break;
            case R.id.btn_cancel_main:
                layoutRegister.setVisibility(View.GONE);
                break;
            case R.id.btn_google_main:
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(appUtils.mGoogleApiClient);
                startActivityForResult(signInIntent, 1212);
                break;
            case R.id.btn_suggest_sticker_main:
                //send message to firebase db.
                SharedPreferences prefs = getSharedPreferences("palpic", Context.MODE_PRIVATE);
                String uid = prefs.getString(KEY_UID, null);
                FirebaseDbUtils dbUtils = new FirebaseDbUtils(this);
                String suggestedWord = textSuggestTitle.getText().toString();
                dbUtils.suggestStickerToFirebase(suggestedWord,uid);
                layoutSuggest.setVisibility(View.GONE);
                if (view != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                break;
            case R.id.btn_suggest_sticker_cancel_main:
                layoutSuggest.setVisibility(View.GONE);
                if (view != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                break;
            default:
                break;
        }
        setBottomBarStatus(v);
    }

    /**
     * get picture from camera
     */
    private void getPictureFormCamera() {

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

        tempPhotoPath = FileUtils.DCIMCamera_PATH + FileUtils.getNewFileName()
                + ".jpg";

        mCurrentPhotoFile = new File(tempPhotoPath);

        if (!mCurrentPhotoFile.exists()) {
            try {
                mCurrentPhotoFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (Build.VERSION.SDK_INT >= 24) {
            Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    mCurrentPhotoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    photoURI);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(mCurrentPhotoFile));
        }
        startActivityForResult(intent, CAMERA_WITH_DATA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        switch (requestCode) {
            case CAMERA_WITH_DATA:
                if (resultCode != RESULT_OK) {
                    return;
                }
                photoPath = tempPhotoPath;
                Intent intent = new Intent(this, CropActivity.class);
                intent.putExtra("filePath", photoPath);
                startActivity(intent);
                break;
            case 1212: //google login result
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = result.getSignInAccount();
                    appUtils.firebaseAuthWithGoogle(account);
                    layoutRegister.setVisibility(View.GONE);
                    layoutSuggest.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(this, "Sign-in failed: "+result.getStatus(), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }

        if(appUtils.mCallbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

    /**
     * Bottom bar status setting
     **/
    private void setBottomBarStatus(View v) {
        if (v.getId() == currentViewId) {
            listviewAlbums.setVisibility(View.GONE);
            layoutContact.setVisibility(View.GONE);
            layoutMore.setVisibility(View.GONE);
            dimBackground.setVisibility(View.GONE);
            layoutRegister.setVisibility(View.GONE);

            btnAllbums.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            btnMore.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            btnContactUs.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            currentViewId = 0;
            return;
        }

        btnAllbums.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        btnMore.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        btnContactUs.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        switch (v.getId()) {
            //tab bar
            case R.id.main_bar_btn_album:
                listviewAlbums.setVisibility(View.VISIBLE);
                layoutContact.setVisibility(View.GONE);
                layoutMore.setVisibility(View.GONE);
                dimBackground.setVisibility(View.VISIBLE);

                btnAllbums.setBackgroundColor(getResources().getColor(R.color.colorBackgroundMain));
                break;
            case R.id.main_bar_btn_camera:

                btnAllbums.setImageResource(R.drawable.albums);
                btnMore.setImageResource(R.drawable.more);
                btnContactUs.setImageResource(R.drawable.contactus);
                break;
            case R.id.main_bar_btn_more:
                listviewAlbums.setVisibility(View.GONE);
                layoutContact.setVisibility(View.GONE);
                layoutMore.setVisibility(View.VISIBLE);
                dimBackground.setVisibility(View.VISIBLE);

                btnMore.setBackgroundColor(getResources().getColor(R.color.colorBackgroundMain));
                break;
            case R.id.main_bar_btn_contact:
                listviewAlbums.setVisibility(View.GONE);
                layoutContact.setVisibility(View.VISIBLE);
                layoutMore.setVisibility(View.GONE);
                dimBackground.setVisibility(View.VISIBLE);

                btnContactUs.setBackgroundColor(getResources().getColor(R.color.colorBackgroundMain));
                break;
            default:
                listviewAlbums.setVisibility(View.GONE);
                layoutContact.setVisibility(View.GONE);
                layoutMore.setVisibility(View.GONE);
                dimBackground.setVisibility(View.GONE);
                break;
        }
        currentViewId = v.getId();
    }

    /**
     * Getting Album names from the device
     */
    private void getAlbumNames() {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Thumbnails.DATA, MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        ArrayList<String> ids = new ArrayList<String>();

        mAlbumsList.clear();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Album album = new Album();

                int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                album.id = cursor.getString(columnIndex);

                if (!ids.contains(album.id)) {
                    columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                    album.name = cursor.getString(columnIndex);

                    columnIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    album.thumbFilePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
                    album.coverID = cursor.getLong(columnIndex);

                    mAlbumsList.add(album);
                    ids.add(album.id);
                } else {
                    mAlbumsList.get(ids.indexOf(album.id)).count++;
                }
            }
            cursor.close();
        }

        Collections.sort(mAlbumsList, new Comparator<Album>() {
            @Override
            public int compare(Album o1, Album o2) {
                int i = 0;
                i = o2.count - o1.count;
                return i;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getAlbumNames();
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            albumLoader.loadAlbums(CAMERA_IMAGE_BUCKET_ID);
                        }
                    });
                    resetAllViews();
                } else {
                    String storageMessage = "App need to access to external storage. please allow external storage";
                    alertWithTitle("", storageMessage);
                    closeApp();
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    String storageMessage = "App need to access to external storage to write data. please allow write permission";
                    alertWithTitle("", storageMessage);
                    closeApp();
                }
                break;
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    String storageMessage = "App need to access to camera. please allow your camera permisson";
                    alertWithTitle("", storageMessage);
                    closeApp();
                }
                break;
        }
    }

    /**GridView, ListView Item click listener.*/
    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        switch (parent.getId()) {
            case R.id.gridView_album:
                Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                        getContentResolver(), galleryData.get(position).getId(),
                        MediaStore.Images.Thumbnails.MICRO_KIND, null);
                if (bitmap != null) {
                    Intent intent = new Intent(this, CropActivity.class);
                    intent.putExtra("filePath", galleryData.get(position).getPath());
                    startActivity(intent);
                } else {
                    appUtils.alertWithTitle("Error", "Unknown Image type!");
                }
                break;
            case R.id.listView_albumName:
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        albumLoader.loadAlbums(mAlbumsList.get(position).id);
                    }
                });
                resetAllViews();
                break;
            default:
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();

        //remove listener of firebase's auth
        if (mAuthListener != null) {
            appUtils.mAuth.removeAuthStateListener(mAuthListener);
        }
        appUtils.mGoogleApiClient.disconnect();
    }

    /**
     * Action sheet listener methods
     */
    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        switch (index) {
            case 0: // Facebook

                if (ShareDialog.canShow(ShareLinkContent.class)) {

                    ShareLinkContent  content = new ShareLinkContent.Builder()
                            .setContentTitle("Palpic")
                            .setContentDescription("this is the code iOS version. AppStoreUrl is string variable.\n let AppStoreUrl = \"")
                            .setContentUrl(Uri.parse("https://itunes.apple.com/app/apple-store/id1152245246?mt=8")).build();
                    shareDialog.show(content);
                }

//                Uri facebookuri;
//                try {
//                    getPackageManager().getPackageInfo("com.facebook.katana", 0);
////                    facebookuri = Uri.parse("fb://facewebmodal/f?href=" + FACEBOOK_URL);
//                    facebookuri = Uri.parse("fb://page/" + R.string.facebook_app_id);
//                } catch (PackageManager.NameNotFoundException e) {
//                    facebookuri = Uri.parse(FACEBOOK_URL);
//                }
//
//                Intent facebookIntent = new Intent(Intent.ACTION_VIEW, facebookuri);
//                facebookIntent.putExtra(Intent.EXTRA_TEXT, "http://allyouneedapp.com");
//                startActivity(facebookIntent);
                break;
            case 1: // Twitter
                Intent intent = null;
                try {
                    // get the Twitter app if possible
                    this.getPackageManager().getPackageInfo("com.twitter.android", 0);
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=USERID"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } catch (Exception e) {
                    // no Twitter app, revert to browser
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com"));
                }
                startActivity(intent);
                break;
            case 2: // instagram
                String uri = "http://instagram.com/_u/love_Mostofa820";
                Intent likeIng = newInstagramProfileIntent(uri);
                try {
                    startActivity(Intent.createChooser(likeIng,"tell friend via instagram"));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_SEND,
                            Uri.parse("http://instagram.com/palpicapp")));
                }

                break;
            case 3: // Email
                /* Create the Intent */
                final Intent emailIntent = new Intent(Intent.ACTION_SEND);

                /* Fill it with Data */
                emailIntent.setType("plain/text");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Check this app: PalPic");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi, \n \n I love PalPic app. It’s wonderful to share wonderful moment with friend and can put a meaningful sticker. Here is the link: http://allyouneedapp.com");

                /* Send it off to the Activity-Chooser */
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                break;
            default:
                break;
        }
    }

    public Intent newInstagramProfileIntent(String url) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            if (getPackageManager().getPackageInfo("com.instagram.android", 0) != null) {
                if (url.endsWith("/")) {
                    url = url.substring(0, url.length() - 1);
                }
                final String username = url.substring(url.lastIndexOf("/") + 1);
                intent.setData(Uri.parse("http://instagram.com/_u/" + username));
                intent.setPackage("com.instagram.android");
                return intent;
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        intent.setData(Uri.parse(url));
        return intent;
    }

    /**
     * To Rate this app on App store.
     **/
    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Alert Build
     */
    private void alertWithTitle(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    /**Login status methods*/
    @Override
    public void onSuccess(final AuthResult authResult, String status) {
        if (AppUtils.KEY_LOGIN.equals(status)){
            layoutSuggest.setVisibility(View.VISIBLE);
            layoutRegister.setVisibility(View.GONE);
        } else if (AppUtils.KEY_REGISTER.equals(status)){
            Toast.makeText(MainActivity.this,"Sign-Up Success",Toast.LENGTH_LONG).show();
            tVEmail.setText(""); tVPassword.setText("");
        }
        if (dialog.isShowing())
        dialog.dismiss();
    }

    @Override
    public void onFailed(Exception e,String status) {
        if (AppUtils.KEY_LOGIN.equals(status)){
            Toast.makeText(MainActivity.this, "Sign-in failed"+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
        } else if (AppUtils.KEY_REGISTER.equals(status)){
            Toast.makeText(MainActivity.this,"Sign-Up failed" + e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
        if (dialog.isShowing())
        dialog.dismiss();
    }

    public void closeApp(){
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
    /**AlbumLoad listener method*/
    @Override
    public void loadFinished(ArrayList<AlbumData> albumContents) {

        galleryData.clear();
        galleryData.addAll(albumContents);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (gridView.getAdapter() == null) {
                    gridView.setAdapter(galleryAdapter);
                } else {
                    galleryAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**Banner view method*/
    private void loadBanner() {
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

}
