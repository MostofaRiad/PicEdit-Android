package com.allyouneedapp.palpicandroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
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

import com.allyouneedapp.palpicandroid.adapters.ColorBarAdapter;
import com.allyouneedapp.palpicandroid.adapters.FontAdapter;
import com.allyouneedapp.palpicandroid.adapters.RecentStickerAdapter;
import com.allyouneedapp.palpicandroid.adapters.StickerGridViewAdapter;
import com.allyouneedapp.palpicandroid.adapters.StickerTitleAdapter;
import com.allyouneedapp.palpicandroid.database.PalPicDBHandler;
import com.allyouneedapp.palpicandroid.listeners.RecyclerOnItemClickListener;
import com.allyouneedapp.palpicandroid.models.Sticker;
import com.allyouneedapp.palpicandroid.utils.AppUtils;
import com.allyouneedapp.palpicandroid.utils.Constants;
import com.allyouneedapp.palpicandroid.utils.FirebaseDbUtils;
import com.allyouneedapp.palpicandroid.utils.StickerUtil;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cn.jarlen.photoedit.crop.ImageViewUtil;
import cn.jarlen.photoedit.operate.ImageObject;
import cn.jarlen.photoedit.operate.OperateUtils;
import cn.jarlen.photoedit.operate.OperateView;
import cn.jarlen.photoedit.operate.TextObject;
import cn.jarlen.photoedit.utils.FileUtils;

import static com.allyouneedapp.palpicandroid.utils.Constants.KEY_GOOGLE_SIGN_IN;
import static com.allyouneedapp.palpicandroid.utils.Constants.KEY_UID;
import static com.allyouneedapp.palpicandroid.utils.Constants.REQUEST_FINISH_FILTER;

public class FinishUpActivity extends AppCompatActivity implements View.OnClickListener , ListView.OnItemClickListener,AppUtils.AppUtilsListener ,RecentStickerAdapter.OnDropListener, View.OnDragListener{

    public static Bitmap bitmap;

    //views
    private Button btnBack;
    private Button btnSaveShare;
    private Button btnDismiss;

    private LinearLayout mLayout;
    private ImageView imageViewForSize;

    //bottom bar
    private ImageButton btnFilter;
    private ImageButton btnAddSticker;
    private ImageButton btnAddWord;
    private ImageButton btnFont;

    private OperateView operateView;
    private OperateUtils operateUtils;

    //search bar
    private SearchView searchView;
    private ListView listView;
    private GridView gridView;

    private LinearLayout layoutList;
    private LinearLayout layoutFontColorBar;
    private LinearLayout layoutRecentSticker;

    private RecyclerView recyclerViewColor;
    private RecyclerView recyclerViewRecent;

//    private ArrayList<String> stickerTitles;
    private ArrayList<String> searchedStickers;
    private ArrayList<Sticker> recentStickers;
    private RecentStickerAdapter recentStickerAdapter;
    private StickerTitleAdapter stickerTitleAdapter;
    private ArrayList<Sticker> stickersforGrid;
    private StickerGridViewAdapter stickerGridViewAdapter;

    //input text layout
    private RelativeLayout layoutInputText;
    private Button btnInputTextDone;
    private EditText inputText;

    //register and login layout
    private LinearLayout layoutRegisterLogin;
    private EditText textEmail;
    private EditText textPassword;
    private Button btnRegister;
    private Button btnLogin;
    private Button btnCancel;
    private LoginButton btnFBLogin;
    private SignInButton btnGoogleLogin;

    //suggest sticker layout
    private  LinearLayout layoutSuggest;
    private EditText textSuggestTitle;
    private Button btnSuggest;
    private Button btnCancelSuggest;

    //save and share layout
    private LinearLayout layoutSaveAndShare;
    private ImageButton btnFB;
    private ImageButton btnInstar;
    private ImageButton btnEmail;
    private ImageButton btnSuggestSticker;
    private ImageButton btnStartOver;
    private boolean isSaveAndShare = false;

    //Inerstitial banner view
    InterstitialAd mInterstitialAd;

    private int[] colorItems = {
            R.color.colorItem1,R.color.colorItem2,R.color.colorItem3,R.color.colorItem4,
            R.color.colorItem5,R.color.colorItem6,R.color.colorItem7,R.color.colorItem8,
            R.color.colorItem9,R.color.colorItem10,R.color.colorItem11,R.color.colorItem12,
            R.color.colorItem13,R.color.colorItem14,R.color.colorItem15,R.color.colorItem16,
            R.color.colorItem17,R.color.colorItem18,R.color.colorItem19,R.color.colorItem20,
            R.color.colorItem21,R.color.colorItem22,R.color.colorItem23,R.color.colorItem24,
            R.color.colorItem25,R.color.colorItem26,R.color.colorItem27,R.color.colorItem28,
            R.color.colorItem29,R.color.colorItem30,R.color.colorItem31,R.color.colorItem32,
            R.color.colorItem33,R.color.colorItem34,R.color.colorItem35,R.color.colorItem36,
            R.color.colorItem37,R.color.colorItem38,R.color.colorItem39,R.color.colorItem40,
            R.color.colorItem41,R.color.colorItem42,R.color.colorItem43,R.color.colorItem44,
            R.color.colorItem45,R.color.colorItem46,R.color.colorItem47,R.color.colorItem48,
            R.color.colorItem49,R.color.colorItem50,R.color.colorItem51,R.color.colorItem52,
            R.color.colorItem53,R.color.colorItem54,R.color.colorItem55,R.color.colorItem56,
            R.color.colorItem57,R.color.colorItem58,R.color.colorItem59,R.color.colorItem60,
            R.color.colorItem61};
    private ArrayList<String> fontNames = new ArrayList<>();
    private ArrayList<Typeface> typefaceOther = new ArrayList<>();
    private ArrayList<Typeface> typefaceCN = new ArrayList<>();
    private ArrayList<Typeface> typefaceJP = new ArrayList<>();
    private ArrayList<Typeface> typefaceKR = new ArrayList<>();
    private ArrayList<Typeface> typefaceRU = new ArrayList<>();
    private ArrayList<Typeface> typefaceTH = new ArrayList<>();
    private ArrayList<Typeface> typefaceSelected = new ArrayList<>();


    private ColorBarAdapter colorBarAdapter;
    private FontAdapter fontAdapter;
    private int selectedColor = R.color.colorItem31;

    // to edit sticker and textobject to operateview.
    private TextObject selectedTextObject;
    private ImageObject selectedImageObject;
    private boolean isTextEdit = false;
    private boolean isImageSticker = false;

    //to control the bottom bar status
    private int currentSelectedBtnId = 0;

    public String filePath;

    //database
    private PalPicDBHandler db;

    ShareDialog shareDialog;

    private AppUtils appUtils;

    private String storedFilePath;
    private Bitmap editedBitmap;


    //firebase
    private FirebaseAuth.AuthStateListener mAuthListener;

    int finalHeight, finalWidth;

    //drag and drop dragPosition
    private int dragPosition = -1;
    private Bitmap dragBitmap;

    InputMethodManager imm;

    //to save on firebase database used stickers and words
    private ArrayList<String> usedStickers = new ArrayList<>();
    private ArrayList<String> usedWords = new ArrayList<>();

    ACProgressFlower dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_up);
        overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
        getSupportActionBar().hide();
        filePath = getIntent().getExtras().getString("filePath");
        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.DKGRAY).build();

        fontNames.add("Bandal");
        fontNames.add("Bangwool");
        fontNames.add("drfont_daraehand_Basic");
        fontNames.add("Eunjin");
        fontNames.add("EunjinNakseo");
        fontNames.add("Guseul");
        fontNames.add("TH Baijam Bold");
        fontNames.add("TH Chakra Petch Bold");
        fontNames.add("TH Charm of AU");
        fontNames.add("TH Charmonman Bold");
        fontNames.add("TH Fahkwang Bold");
        fontNames.add("TH K2D July8 Bold");
        fontNames.add("TH Kodchasal Bold");
        fontNames.add("TH Krub Bold");
        fontNames.add("TH Mali Grade6 Bold");
        fontNames.add("TH Niramit AS Bold");
        fontNames.add("TH Srisakdi Bold");
        fontNames.add("THSarabun Bold");
        fontNames.add("YOzFonNL");
        initInformation();
        initAllViews();

        //Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());

        //init arrays
        db = new PalPicDBHandler(this);
        searchedStickers = new ArrayList<>();
        recentStickers = new ArrayList<>();
        stickersforGrid = new ArrayList<>();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                loadAllStickers();
            }
        });
        dialog.show();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        /***
         * *Interstital banner view init
         * */

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-9318863419427951/8621523426");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });
        requestNewInterstitial();
    }


    @Override
    protected void onStart() {

        super.onStart();

        setViewsStatus(0); //init views status

        colorBarAdapter = new ColorBarAdapter(this, colorItems);
        recyclerViewColor.setAdapter(colorBarAdapter);

        //Color bar settings
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        recyclerViewColor.setLayoutManager(gridLayoutManager);

        recyclerViewColor.addOnItemTouchListener(new RecyclerOnItemClickListener(this, new RecyclerOnItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                selectedColor = colorItems[position];
                int color = ContextCompat.getColor(FinishUpActivity.this , selectedColor);

                if (isImageSticker) {
                    Bitmap src = selectedImageObject.getSrcBm();
                    Bitmap tintBitmap = tintImage(src, color);
                    selectedImageObject.setSrcBm(tintBitmap);
                } else {
                    selectedTextObject.setColor(color);
                    selectedTextObject.commit();
                    selectedTextObject.regenerateBitmap();
                }

                operateView.invalidate();
            }
        }));

        // Recent Bar settings

        StaggeredGridLayoutManager recentgridLayoutManager =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        recyclerViewRecent.setLayoutManager(recentgridLayoutManager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isSaveAndShare){
            setViewsStatus(8);
        }

    }

    /**get all views from xml*/
    private void initAllViews(){
        btnBack = (Button) findViewById(R.id.btn_back_finish);
        btnBack.setOnClickListener(this);
        btnSaveShare = (Button) findViewById(R.id.btn_save_share_finish);
        btnSaveShare.setOnClickListener(this);
        btnDismiss = (Button) findViewById(R.id.btn_dismiss_finish);
        btnDismiss.setOnClickListener(this);

        mLayout = (LinearLayout) findViewById(R.id.layout_edit_finish);
        mLayout.setOnDragListener(this);
        imageViewForSize = (ImageView) this.findViewById(R.id.image_view_for_size);
        ViewTreeObserver vto = imageViewForSize.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                imageViewForSize.getViewTreeObserver().removeOnPreDrawListener(this);
                int height = imageViewForSize.getMeasuredHeight();
                int width = imageViewForSize.getMeasuredWidth();
                Rect rect;
                rect = ImageViewUtil.calculateFitCenterObjectRect(bitmap.getWidth(),bitmap.getHeight(),width,height);
                finalWidth = rect.width(); finalHeight = rect.height();
                fillContent();
                return true;
            }
        });

        btnFilter = (ImageButton) findViewById(R.id.btn_filter_finish);
        btnFilter.setOnClickListener(this);
        btnAddSticker = (ImageButton) findViewById(R.id.btn_addsticker_finish);
        btnAddSticker.setOnClickListener(this);
        btnAddWord = (ImageButton) findViewById(R.id.btn_add_word_finish);
        btnAddWord.setOnClickListener(this);
        btnFont = (ImageButton) findViewById(R.id.btn_bar_font_finish);
        btnFont.setOnClickListener(this);

        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Search Here");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                gridView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                searchedStickers.clear();
                searchedStickers = db.searchStickersWithTile(newText);
                stickerTitleAdapter = new StickerTitleAdapter(FinishUpActivity.this,searchedStickers);
                listView.setAdapter(stickerTitleAdapter);
                return false;
            }
        });




        listView = (ListView) findViewById(R.id.listView_font);
        listView.setOnItemClickListener(this);
        gridView = (GridView) findViewById(R.id.gridView_sticker);
        gridView.setOnItemClickListener(this);

        layoutList = (LinearLayout) findViewById(R.id.layout_list);
        layoutFontColorBar = (LinearLayout) findViewById(R.id.layout_font_color_finish);
        layoutRecentSticker = (LinearLayout) findViewById(R.id.layout_recent_finish);

        recyclerViewColor = (RecyclerView) findViewById(R.id.bar_color_finish);
        recyclerViewRecent = (RecyclerView) findViewById(R.id.bar_recent_finish);
        operateUtils = new OperateUtils(this);

        //input text layout
        layoutInputText = (RelativeLayout) findViewById(R.id.layout_input_text);
        inputText = (EditText) findViewById(R.id.text_input_text);
        inputText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    layoutInputText.setVisibility(View.GONE);
                    currentSelectedBtnId = 0;
                }
            }
        });

        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isTextEdit){
                    editText(selectedTextObject);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (inputText.isFocused()) {
                        Rect outRect = new Rect();
                        inputText.getGlobalVisibleRect(outRect);
                        if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                            inputText.clearFocus();

                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                            layoutInputText.setVisibility(View.GONE);
                            currentSelectedBtnId = 0;
                        }
                    }
                }
                return false;
            }
        });
        btnInputTextDone = (Button)findViewById(R.id.btn_input_text_done);
        btnInputTextDone.setOnClickListener(this);

        //register and login layout
        layoutRegisterLogin = (LinearLayout) findViewById(R.id.layout_register_login_finish);
        textEmail = (EditText) findViewById(R.id.text_email_finish);
        textPassword = (EditText)findViewById(R.id.text_password_finish);

        btnRegister = (Button) findViewById(R.id.btn_register_finish);
        btnRegister.setOnClickListener(this);
        btnLogin = (Button) findViewById(R.id.btn_login_finish);
        btnLogin.setOnClickListener(this);
        btnCancel = (Button) findViewById(R.id.btn_cancel_finish);
        btnCancel.setOnClickListener(this);
        btnFBLogin = (LoginButton) findViewById(R.id.btn_fb_login_finish);

        btnFBLogin.registerCallback(appUtils.mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                appUtils.handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(FinishUpActivity.this,error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        btnGoogleLogin = (SignInButton) findViewById(R.id.btn_google_finish);
        btnGoogleLogin.setOnClickListener(this);

        //suggest sticker layout
        layoutSuggest = (LinearLayout) findViewById(R.id.layout_suggest_sticker_logged_finish);
        textSuggestTitle = (EditText) findViewById(R.id.edit_text_suggest_sticker_finish);

        btnSuggest = (Button) findViewById(R.id.btn_suggest_sticker_finish);
        btnSuggest.setOnClickListener(this);
        btnCancelSuggest = (Button) findViewById(R.id.btn_suggest_sticker_cancel_finish);
        btnCancelSuggest.setOnClickListener(this);

        //save and share layout
        layoutSaveAndShare = (LinearLayout) findViewById(R.id.layout_save_share_finish);

        btnFB = (ImageButton) findViewById(R.id.btn_fb_finish);
        btnFB.setOnClickListener(this);
        btnInstar = (ImageButton) findViewById(R.id.btn_insta_finish);
        btnInstar.setOnClickListener(this);
        btnEmail = (ImageButton) findViewById(R.id.btn_email_finish);
        btnEmail.setOnClickListener(this);
        btnSuggestSticker = (ImageButton) findViewById(R.id.btn_suggestSticker_finish);
        btnSuggestSticker.setOnClickListener(this);
        btnStartOver = (ImageButton) findViewById(R.id.btn_start_over_finish);
        btnStartOver.setOnClickListener(this);

        loadBanner();
    }

    /**init acount information*/
    private void initInformation(){
        appUtils = new AppUtils(this);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getFontTypesFromAsset();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        //set auth of firebase listener
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                } else {

                }
            }
        };

        shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(appUtils.mCallbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(FinishUpActivity.this,"success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(FinishUpActivity.this,"failed:"+error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**Load all stickers from json file and db*/
    private void loadAllStickers(){

        SharedPreferences prefs = getSharedPreferences("palpic", Context.MODE_PRIVATE);
        boolean check = prefs.getBoolean("isAdded", false);
        ArrayList<Sticker> stickers;

        if (!check) {
            // get stickers from the asset
            String json = loadJSONFromAsset();
            stickers = jsonParser(json);
            db.inSertAll(stickers);
            SharedPreferences.Editor pref = getSharedPreferences("palpic", Context.MODE_PRIVATE).edit();
            pref.putBoolean("isAdded",true);
            pref.apply();
        }
        searchedStickers = db.searchStickersWithTile("");
        recentStickers.addAll(db.getAllRecent());
        recentStickerAdapter = new RecentStickerAdapter(FinishUpActivity.this,recentStickers);
        recentStickerAdapter.setOnDropListener(this);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
               if (recentStickerAdapter != null) {
                   recyclerViewRecent.setAdapter(recentStickerAdapter);
               }
                if (dialog.isShowing()) dialog.dismiss();

            }
        });
    }

    /**Buttons click event*/
    @Override
    public void onClick(View v) {

        String email = textEmail.getText().toString();
        String password = textPassword.getText().toString();

        appUtils.setAppUtilsListener(this);

        switch (v.getId()) {
            case R.id.btn_back_finish:
                if (layoutSaveAndShare.getVisibility() == View.VISIBLE) {
                    setViewsStatus(0);
                    btnAddSticker.setEnabled(true);
                    btnAddWord.setEnabled(true);
                    recyclerViewRecent.setEnabled(true);
                    btnFilter.setEnabled(true);
                    View view = this.getCurrentFocus();
                    if (view != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    isSaveAndShare = false;
                    mLayout.setEnabled(false);
                    imageViewForSize.setEnabled(false);
                } else {
                    showBackConfirmAlert("Confirm!", "Are you sure you want to back? You will lose your existing edit.");
                }
                break;
            case R.id.btn_save_share_finish:
                setViewsStatus(0);
                layoutSaveAndShare.setVisibility(View.VISIBLE);
                btnSaveShare.setVisibility(View.GONE);
                btnDismiss.setVisibility(View.GONE);
                btnAddSticker.setEnabled(false);
                btnAddWord.setEnabled(false);
                recyclerViewRecent.setEnabled(false);
                btnFilter.setEnabled(false);

                isSaveAndShare = true;
                editedBitmap = getBitmapByView(operateView);
                if (storedFilePath == null) {
                    storedFilePath = getFilePathWithTimeStump();
                }

                dialog.show();

                mInterstitialAd.show();

                FileUtils.writeImage(editedBitmap,storedFilePath,100);
                //save all word and stickers to server
                saveAllStickersToServer();

                break;
            case R.id.btn_dismiss_finish:
                setViewsStatus(0);
                View view = this.getCurrentFocus();
                if (view != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                break;
            case R.id.btn_filter_finish:

                Intent intent = new Intent(this, FilterActivity.class);
                FilterActivity.editBitmap = bitmap;
                intent.putExtra("filter", true);
                startActivityForResult(intent, REQUEST_FINISH_FILTER);
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                break;
            case R.id.btn_addsticker_finish:        // add sticker button
                if(currentSelectedBtnId == R.id.btn_addsticker_finish){
                    setViewsStatus(0);
                    currentSelectedBtnId = 0;
                    return;
                }
                setViewsStatus(7);
                stickerTitleAdapter = new StickerTitleAdapter(this,searchedStickers);
                listView.setAdapter(stickerTitleAdapter);
                searchView.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_add_word_finish:  //add word button
                if(currentSelectedBtnId == R.id.btn_add_word_finish){
                    setViewsStatus(0);
                    currentSelectedBtnId = 0;
                    return;
                }
                showSoftKeyboard(inputText);
                isTextEdit = false;
                setViewsStatus(0);
                inputText.setText("");
                layoutInputText.setVisibility(View.VISIBLE);
                addword();

                break;
            case R.id.btn_bar_font_finish:
                if(currentSelectedBtnId == R.id.btn_bar_font_finish){
                    setViewsStatus(0);
                    currentSelectedBtnId = 0;
                    return;
                }
                setViewsStatus(7);

                String lang = appUtils.detectLanguage(selectedTextObject.getText());
                typefaceSelected.clear();
                if (lang.equals(Locale.ENGLISH.getLanguage())) {
                    fontAdapter = new FontAdapter(this,selectedTextObject.getText(), typefaceOther);
                    typefaceSelected.addAll(typefaceOther);
                } else if (lang.equals("zh-cn")||lang.equals("zh-tw")) {
                    fontAdapter = new FontAdapter(this,selectedTextObject.getText(),typefaceCN);
                    typefaceSelected.addAll(typefaceCN);
                } else if (lang.equals(Locale.KOREAN.getLanguage())) {
                    fontAdapter = new FontAdapter(this,selectedTextObject.getText(),typefaceKR);
                    typefaceSelected.addAll(typefaceKR);
                } else if (lang.equals(Locale.JAPANESE.getLanguage())) {
                    fontAdapter = new FontAdapter(this,selectedTextObject.getText(),typefaceJP);
                    typefaceSelected.addAll(typefaceJP);
                } else if (lang.equals("th")) {
                    fontAdapter = new FontAdapter(this,selectedTextObject.getText(),typefaceTH);
                    typefaceSelected.addAll(typefaceTH);
                } else if (lang.equals("ru")) {
                    fontAdapter = new FontAdapter(this,selectedTextObject.getText(),typefaceRU);
                    typefaceSelected.addAll(typefaceRU);
                } else {
                    fontAdapter = new FontAdapter(this,selectedTextObject.getText(), typefaceOther);
                    typefaceSelected.addAll(typefaceOther);
                }

                listView.setAdapter(fontAdapter);
                searchView.setVisibility(View.GONE);
                break;
            case R.id.btn_input_text_done:
                if (isTextEdit){
                    if(inputText.getText().toString().equals("")){
                        operateView.imgLists.remove(selectedTextObject);
                        operateView.invalidate();
                    } else {
                        editText(selectedTextObject);
                    }
                } else {
                    addword();
                }
                setViewsStatus(0);
                layoutFontColorBar.setVisibility(View.VISIBLE);
                btnFont.setVisibility(View.VISIBLE);
                layoutRecentSticker.setVisibility(View.GONE);
                View view1 = getCurrentFocus();
                if (view1 != null) {
                    imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
                }
                break;
            //register and login layout
            case R.id.btn_register_finish:
                if (email.isEmpty()||password.isEmpty()){
                    Toast.makeText(this,"Insert E-mail and Password", Toast.LENGTH_LONG).show();
                } else {
                    appUtils.registeWithEmail(email, password);

                }
                break;
            case R.id.btn_login_finish:
                if (email.isEmpty()||password.isEmpty()){
                    Toast.makeText(this,"Insert E-mail and Password", Toast.LENGTH_LONG).show();
                } else {
                    appUtils.loginToFirebase(email, password);
                    dialog.show();

                }
                break;
            case R.id.btn_cancel_finish:
                setViewsStatus(-1);
                break;
            case R.id.btn_google_finish:
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(appUtils.mGoogleApiClient);
                startActivityForResult(signInIntent, KEY_GOOGLE_SIGN_IN);
                break;
            //suggest sticker layout
            case R.id.btn_suggest_sticker_finish:
                SharedPreferences prefs = getSharedPreferences("palpic", Context.MODE_PRIVATE);
                String uid = prefs.getString(KEY_UID, null);
                FirebaseDbUtils dbUtils1 = new FirebaseDbUtils(this);
                String suggestedWord = textSuggestTitle.getText().toString();
                if ("".equals(suggestedWord)) {
                    Toast.makeText(this,"Please insert a word to suggest",Toast.LENGTH_SHORT).show();
                } else {
                    dbUtils1.suggestStickerToFirebase(suggestedWord, uid);
                }
                break;
            case R.id.btn_suggest_sticker_cancel_finish:

                layoutSuggest.setVisibility(View.GONE);
                break;
            //Save and share layout
            case R.id.btn_fb_finish:// facebook share

                if (ShareDialog.canShow(ShareLinkContent.class)) {

                    Bitmap image = getBitmapByView(operateView);

                    SharePhoto photo = new SharePhoto.Builder().setBitmap(image).build();

                    SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();

                    shareDialog.show(content);
                }

                setViewsStatus(8);

                break;
            case R.id.btn_insta_finish: //instagram share
                String type = "image/*";
                if (appUtils.isInstalledInstagramApp()) {
                    appUtils.createInstagramIntent( type,storedFilePath);
                } else {
                    appUtils.alertWithTitle("Palpic","Couldn't find Instagram App");
                }
                setViewsStatus(8);
                break;
            case R.id.btn_email_finish:
                appUtils.shareImageWithEmail(storedFilePath);
                setViewsStatus(8);
                break;
            case R.id.btn_suggestSticker_finish:
                setViewsStatus(8);
                if (appUtils.isSignIn()){
                    layoutSuggest.setVisibility(View.VISIBLE);
                } else {
                    layoutRegisterLogin.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.btn_start_over_finish:
                Intent startOver = new Intent(this, MainActivity.class);
                startOver.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(startOver);
                break;
            default:
                setViewsStatus(0);
                imm.hideSoftInputFromWindow(inputText.getWindowToken(), 0);
                break;
        }
        currentSelectedBtnId = v.getId();
    }

    @Override
    public void onBackPressed() {
        if((btnBack.getVisibility() == View.VISIBLE)&&(layoutSaveAndShare.getVisibility()== View.GONE))
            showBackConfirmAlert("Confirm!", "Are you sure you want to back? You will lose your existing edit.");
        else {
            setViewsStatus(0);
        }
    }

    /** Set init views status*/
    private void setViewsStatus(int status){

        listView.setVisibility(View.GONE);
        gridView.setVisibility(View.GONE);
        btnDismiss.setVisibility(View.GONE);
        layoutList.setVisibility(View.GONE);
        layoutFontColorBar.setVisibility(View.GONE);
        layoutInputText.setVisibility(View.GONE);
        layoutSaveAndShare.setVisibility(View.GONE);
        layoutSuggest.setVisibility(View.GONE);
        layoutRegisterLogin.setVisibility(View.GONE);
        btnSaveShare.setVisibility(View.VISIBLE);
        layoutRecentSticker.setVisibility(View.GONE);
        btnAddSticker.setEnabled(true);
        btnAddWord.setEnabled(true);
        recyclerViewRecent.setEnabled(true);
        btnFilter.setEnabled(true);

        switch (status) {
            case 0: // init status
                btnSaveShare.setVisibility(View.VISIBLE);
                btnBack.setVisibility(View.VISIBLE);
                layoutRecentSticker.setVisibility(View.VISIBLE);
                break;
            case 1: //image editing
                layoutFontColorBar.setVisibility(View.VISIBLE);
                btnFont.setVisibility(View.GONE);
                btnBack.setVisibility(View.VISIBLE);
                break;
            case 2: //text object editing
                layoutFontColorBar.setVisibility(View.VISIBLE);
                btnFont.setVisibility(View.VISIBLE);
                layoutInputText.setVisibility(View.VISIBLE);
                break;
            case 3: // register login status
                layoutRegisterLogin.setVisibility(View.VISIBLE);
                break;
            case 4: // suggest status
                layoutSuggest.setVisibility(View.VISIBLE);
                break;
            case 5: //save and share status
                layoutSaveAndShare.setVisibility(View.VISIBLE);
                break;
            case 6: // sticker grid View shown
                btnBack.setVisibility(View.GONE);
                btnSaveShare.setVisibility(View.GONE);
                btnDismiss.setVisibility(View.VISIBLE);
                gridView.setVisibility(View.VISIBLE);
                layoutList.setVisibility(View.VISIBLE);
                break;
            case 7: // font and sticker title list view shown
                btnBack.setVisibility(View.GONE);
                btnSaveShare.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                btnDismiss.setVisibility(View.VISIBLE);
                layoutList.setVisibility(View.VISIBLE);
                break;
            case 8: //save and share status
                btnBack.setVisibility(View.VISIBLE);
                layoutRecentSticker.setVisibility(View.VISIBLE);
                layoutSaveAndShare.setVisibility(View.VISIBLE);
                layoutSaveAndShare.setClickable(true);
                btnSaveShare.setVisibility(View.GONE);
                btnDismiss.setVisibility(View.GONE);
                btnAddSticker.setEnabled(false);
                btnAddWord.setEnabled(false);
                recyclerViewRecent.setEnabled(false);
                btnFilter.setEnabled(false);
                mLayout.setEnabled(false);
                imageViewForSize.setEnabled(false);


            default:
                break;
        }
    }

    /**Init operate view to start edit photo*/
    private void fillContent()
    {
        operateView = new OperateView(FinishUpActivity.this, bitmap, finalWidth,finalHeight);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                finalWidth, finalHeight);
        operateView.setLayoutParams(layoutParams);
        mLayout.addView(operateView);
        operateView.setMultiAdd(true);
        operateView.setOnListener(new OperateView.MyListener()
        {

            @Override
            public void onClick(TextObject tObject, boolean isMove) {
                if (selectedTextObject != null) {
                    if (tObject.getText().equals("Text Here")) {
                        return;
                    }
                    if (selectedTextObject.equals(tObject) && !isMove) {
                        inputText.setText(tObject.getText());
                        setViewsStatus(2);
                        inputText.setFocusable(true);
                        showSoftKeyboard(inputText);
                    } else {
                        setViewsStatus(2);
                        layoutInputText.setVisibility(View.GONE);
                        View view1 = getCurrentFocus();
                        if (view1 != null) {
                            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
                        }
                    }

                    isTextEdit = true;
                    isImageSticker = false;
                    selectedTextObject = tObject;
                }
            }

            @Override
            public void onOutSideClick(TextObject textObject) {
                if (selectedTextObject != null) {
                    if (textObject.equals(selectedTextObject)) {
                        isTextEdit = false;
                        textObject.setSelected(false);
//                        selectedTextObject = new TextObject();
                        if (imm.isActive(inputText)) {
                            View view1 = getCurrentFocus();
                            if (view1 != null) {
                                imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
                            }
                            setViewsStatus(0);
                            layoutRecentSticker.setVisibility(View.GONE);
                            layoutFontColorBar.setVisibility(View.VISIBLE);
                        } else {
                            setViewsStatus(0);
                        }

                        if (textObject.getText().equals("Text Here")) {
                            operateView.imgLists.remove(textObject);
                            setViewsStatus(0);
                        }
                    }
                }
            }

            @Override
            public void onImageObjectClick(ImageObject imageObject) {
                isImageSticker = true;
                isTextEdit = false;
                selectedImageObject = imageObject;
                setViewsStatus(1);
                View view1 = getCurrentFocus();
                if (view1 != null) {
                    imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
                }

            }

            @Override
            public void onImageObjectOutSideClick(ImageObject imageObject) {
                if (selectedImageObject != null) {
                    if (selectedImageObject.equals(imageObject)) {
                        setViewsStatus(0);
                        selectedImageObject = null;
                    }
                    View view1 = getCurrentFocus();
                    if (view1 != null) {
                        imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
                    }
                }
            }

            @Override
            public void onDelete(ImageObject imageObject) {
                setViewsStatus(0);
                View view1 = getCurrentFocus();
                if (view1 != null) {
                    imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
                }
            }
        });
    }

    /**To add text sticker*/
    private void addword()
    {
        final TextObject textObj = operateUtils.getTextObject("Text Here",
                operateView, OperateUtils.CENTER, 50, 50);
        int color = ContextCompat.getColor(FinishUpActivity.this , R.color.colorWhite);
        textObj.setColor(color);
        textObj.commit();
        operateView.addItem(textObj);
        selectedTextObject = textObj;
        isImageSticker = false;
        isTextEdit = true;
    }

    /**To edit text sticker*/
    private void editText(final TextObject tObject)
    {
        String string = inputText.getText().toString();
        if ((string.equals("")&&(layoutInputText.getVisibility() == View.VISIBLE))) {
            tObject.setText("Text Here");
            tObject.commit();
        } else if (string.equals("")) {
            operateView.imgLists.remove(tObject);
        } else {
            tObject.setText(string);
            tObject.commit();
            if(string.equals("Text Here")){
                operateView.imgLists.remove(tObject);
            }
        }
        operateView.invalidate();
    }

    /**To Add image sticker*/
    private void addpic(Bitmap bitmap, String id, String title)
    {

        ImageObject imgObject = operateUtils.getImageObject(bitmap, operateView,
                5, 150, 100);
        imgObject.id = id;
        imgObject.title = title;
        operateView.addItem(imgObject);
        selectedImageObject = imgObject;
        isImageSticker = true;

    }

    /** to Change the image sticker color**/
    public static Bitmap tintImage(Bitmap bitmap, int color) {
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        Bitmap bitmapResult = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapResult);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bitmapResult;
    }

    /** get stickers from Asset*/
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("stickers/stickers.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static ArrayList<Sticker> jsonParser(String jsonBuffer){
        ArrayList<Sticker> stickers = new ArrayList<>();
        try {
            JSONObject json = (JSONObject) new JSONTokener(jsonBuffer).nextValue();
            Log.d("","json : "+json);
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject stickerObj = json.getJSONObject(String.valueOf(key));
                String title = stickerObj.getString("title");
                stickers.add(new Sticker(key,title,"stickers/"+key+".png"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stickers;
    }

    /**Grid view and list view click listener*/
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.listView_font:
                if (listView.getAdapter().equals(stickerTitleAdapter)) {
                    stickersforGrid.clear();
                    stickersforGrid = db.getStickersWithTile(searchedStickers.get(position));
                    stickerGridViewAdapter = new StickerGridViewAdapter(this,stickersforGrid);
                    gridView.setAdapter(stickerGridViewAdapter);
                    stickerGridViewAdapter.notifyDataSetChanged();
                    setViewsStatus(6);
                    gridView.setVisibility(View.VISIBLE);

                } else if (listView.getAdapter().equals(fontAdapter)) {
                    selectedTextObject.setTypeface(typefaceSelected.get(position));
                    selectedTextObject.commit();
                    operateView.invalidate();
                    currentSelectedBtnId = 0;
                   setViewsStatus(2);
                    layoutInputText.setVisibility(View.GONE);
                }

                break;
            case R.id.gridView_sticker:
                gridView.setVisibility(View.GONE);
                Bitmap bitmap = StickerUtil.getBitmapFromAsset(this,stickersforGrid.get(position).filePath);
                if (bitmap == null) {
                    bitmap = BitmapFactory.decodeFile(stickersforGrid.get(position).filePath); /// is downloaded stickers
                }
                addpic(bitmap, stickersforGrid.get(position).id,stickersforGrid.get(position).title);
                // save to recent data
                db.insertToRecent(stickersforGrid.get(position));
                //reload recent stickers list
                recentStickers.clear();
                recentStickers.addAll(db.getAllRecent());
                if (recentStickerAdapter == null){
                    recentStickerAdapter = new RecentStickerAdapter(FinishUpActivity.this, recentStickers);
                    recyclerViewRecent.setAdapter(recentStickerAdapter);
                } else {
                    recentStickerAdapter.notifyDataSetChanged();
                }
                recyclerViewRecent.invalidate();
                setViewsStatus(1);
                currentSelectedBtnId = 0;
                break;
        }
    }

    /**get Bitmap image by operate view*/
    public Bitmap getBitmapByView(View v)
    {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }
    /** get file path to save edited new image file**/
    private String getFilePathWithTimeStump(){
        String cameraPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
//        String cameraPath = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath()+"Palpic";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String currentDateTimeString = dateFormat.format(new Date());
        return  cameraPath+"/" + "palpic_" +currentDateTimeString+".jpg";
    }

    /** get font type from asset*/
    private void getFontTypesFromAsset() throws IOException {
        for (String name: fontNames) {
            Typeface tf = Typeface.createFromAsset(this.getAssets(),
                    "fonts/"+name+".ttf");
            typefaceOther.add(tf);

        }

        for (String name : getAssets().list("fonts/CN")) {
            Typeface tf = Typeface.createFromAsset(this.getAssets(),
                    "fonts/CN/"+name);
            typefaceCN.add(tf);
        }

        for (String name : getAssets().list("fonts/JP")) {
            Typeface tf = Typeface.createFromAsset(this.getAssets(),
                    "fonts/JP/"+name);
            typefaceJP.add(tf);
        }

        for (String name : getAssets().list("fonts/KR")) {
            Typeface tf = Typeface.createFromAsset(this.getAssets(),
                    "fonts/KR/"+name);
            typefaceKR.add(tf);
        }

        for (String name : getAssets().list("fonts/RU")) {
            Typeface tf = Typeface.createFromAsset(this.getAssets(),
                    "fonts/RU/"+name);
            typefaceRU.add(tf);
        }

        for (String name : getAssets().list("fonts/TH")) {
            Typeface tf = Typeface.createFromAsset(this.getAssets(),
                    "fonts/TH/"+name);
            typefaceTH.add(tf);
        }
    }

    /** to receive filter result on operate view*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FINISH_FILTER) {
            operateView.bgBmp = bitmap;
            operateView.invalidate();
        } else if (requestCode == KEY_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                appUtils.firebaseAuthWithGoogle(account);
                layoutSuggest.setVisibility(View.VISIBLE);
                layoutRegisterLogin.setVisibility(View.GONE);
            } else {
                Toast.makeText(this, "Sign-in failed", Toast.LENGTH_SHORT).show();
            }
        }
        appUtils.mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**Sign in and Signup status methods*/
    @Override
    public void onSuccess(AuthResult authResult,String status) {
        if (AppUtils.KEY_LOGIN.equals(status)){
            layoutSuggest.setVisibility(View.VISIBLE);
            layoutRegisterLogin.setVisibility(View.GONE);

        } else if (AppUtils.KEY_REGISTER.equals(status)){
            Toast.makeText(this,"Sign-Up Success",Toast.LENGTH_LONG).show();
            textEmail.setText(""); textPassword.setText("");
        }

    }

    @Override
    public void onFailed(Exception e,String status) {
        if (AppUtils.KEY_LOGIN.equals(status)){
            Toast.makeText(this, "Sign-in failed"+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
        } else if (AppUtils.KEY_REGISTER.equals(status)){
            Toast.makeText(this,"Sign-Up failed" + e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }

    }

    /**Log in function to firebase*/
    private void showBackConfirmAlert(String title, String message){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
    }

    public void showSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

    /**Drag and Drop sticker from recent bar view*/
    @Override
    public void onDropSticker(int position, Bitmap bitmap) {
        this.dragPosition = position;
        this.dragBitmap = bitmap;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        int action = event.getAction();

        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:

                break;
            case DragEvent.ACTION_DRAG_ENTERED:

                break;
            case DragEvent.ACTION_DRAG_EXITED:

                break;
            case DragEvent.ACTION_DROP:
                int id = v.getId();
                if (id == R.id.layout_edit_finish){
                    addpic(dragBitmap, recentStickers.get(dragPosition).id, recentStickers.get(dragPosition).title);
                    if (dragPosition < 0) {
                        recentStickerAdapter.notifyDataSetChanged();
                        return true;
                    }
                    // save to recent data
                    db.insertToRecent(recentStickers.get(dragPosition));
                    recentStickers.clear();
                    recentStickers.addAll(db.getAllRecent());
                    recentStickerAdapter.notifyDataSetChanged();
                    recyclerViewRecent.invalidate();
                    layoutFontColorBar.setVisibility(View.VISIBLE);
                    btnFont.setVisibility(View.GONE);
                    layoutRecentSticker.setVisibility(View.GONE);
                }
                break;
            case DragEvent.ACTION_DRAG_ENDED:

                break;
            default:
                break;
        }
        return true;
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    private void saveAllStickersToServer() {

        usedStickers.clear();
        usedWords.clear();
        for(ImageObject obj : operateView.imgLists) {
            if (obj.isTextObject()) {
                TextObject textObject = (TextObject) obj;
                usedWords.add(textObject.getText());
            } else {
                usedStickers.add(obj.id);
            }
        }
        FirebaseDbUtils dbUtils = new FirebaseDbUtils(this);
        SharedPreferences pref = getSharedPreferences("palpic", Context.MODE_PRIVATE);
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            dbUtils.saveUseStickersToServer(usedWords,usedStickers,pref.getString("non_register",null));
        } else {
            dbUtils.saveUseStickersToServer(usedWords,usedStickers,pref.getString(Constants.KEY_UID,null));
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) dialog.dismiss();
            }
        }, 1000);
    }

    /**Banner view method*/
    private void loadBanner() {
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

}
