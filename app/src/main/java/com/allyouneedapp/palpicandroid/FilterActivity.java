package com.allyouneedapp.palpicandroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.allyouneedapp.palpicandroid.adapters.FilterAdapter;
import com.allyouneedapp.palpicandroid.listeners.RecyclerOnItemClickListener;
import com.allyouneedapp.palpicandroid.models.Filter;
import com.allyouneedapp.palpicandroid.utils.FilterUtil;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

import jp.co.cyberagent.android.gpuimage.GPUImage;

public class FilterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnNext;
    private Button btnBack;
    private Button btnResume;
    private RecyclerView recyclerView;
    private ImageView gpuImageView;
    private FilterAdapter adapter;
    private String filePath;
    public static Bitmap tempBitmap;
    public static Bitmap editBitmap;
    private boolean isFromFilter = false;
    private ArrayList<Filter> gpuImageFilters = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        initAllViews();
        getSupportActionBar().hide();
        getEditBitmapFromFinish();
        loadBanner();

    }

    private void initAllViews(){
        btnNext = (Button) findViewById(R.id.btn_next_filter);
        btnNext.setOnClickListener(this);
        btnBack = (Button) findViewById(R.id.btn_back_filter);
        btnBack.setOnClickListener(this);
        btnResume = (Button) findViewById(R.id.btn_resume_filter);
        btnResume.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.rc_filter);
        gpuImageView = (ImageView) findViewById(R.id.gpu_imageView_filter);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnItemTouchListener(new RecyclerOnItemClickListener(this, new RecyclerOnItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                GPUImage image = new GPUImage(FilterActivity.this);
                image.setFilter(FilterUtil.createFilterForType(FilterActivity.this,gpuImageFilters.get(position).getType()));
                editBitmap = image.getBitmapWithFilterApplied(tempBitmap);
                gpuImageView.setImageBitmap(editBitmap);
            }
        }));

        /**GPUImageFilters*/
        gpuImageFilters.add(new Filter("Original", FilterUtil.FilterType.ORIGINAL));
        gpuImageFilters.add(new Filter("Instant", FilterUtil.FilterType.BRIGHTNESS));
        gpuImageFilters.add(new Filter("Transfer" , FilterUtil.FilterType.NEWRGB));
        gpuImageFilters.add(new Filter("Fade", FilterUtil.FilterType.LEVELS_FILTER_MIN));
        gpuImageFilters.add(new Filter("Vivid   ", FilterUtil.FilterType.RGB));
        gpuImageFilters.add(new Filter("Process", FilterUtil.FilterType.PROCESS));
        gpuImageFilters.add(new Filter("Vignette", FilterUtil.FilterType.VIGNETTE));
        gpuImageFilters.add(new Filter("Bright", FilterUtil.FilterType.BRIGHT));
        gpuImageFilters.add(new Filter("Sepia", FilterUtil.FilterType.NEWSEPIA));
        gpuImageFilters.add(new Filter("Mono", FilterUtil.FilterType.GRAYSCALE));
        gpuImageFilters.add(new Filter("LightGray" , FilterUtil.FilterType.LIGHTGRAY));
        gpuImageFilters.add(new Filter("Linear", FilterUtil.FilterType.LOOKUP_AMATORKA));
        gpuImageFilters.add(new Filter("Toon", FilterUtil.FilterType.TOON));
        gpuImageFilters.add(new Filter("Emboss", FilterUtil.FilterType.EMBOSS));
        gpuImageFilters.add(new Filter("Haze", FilterUtil.FilterType.HAZE));
        gpuImageFilters.add(new Filter("Sketch", FilterUtil.FilterType.SKETCH));


    }

    private void getEditBitmapFromFinish(){
        if (getIntent().hasExtra("filePath")) {
            this.filePath = getIntent().getExtras().getString("filePath");
            isFromFilter = false;
            getIntent().removeExtra("filePath");
            btnResume.setVisibility(View.GONE);
            btnBack.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.VISIBLE);
            editBitmap = tempBitmap;
        } else if (getIntent().hasExtra("filter")){
            isFromFilter = true;
            getIntent().removeExtra("filter");
            btnResume.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.GONE);
            btnBack.setVisibility(View.GONE);
        }
        gpuImageView.setImageBitmap(editBitmap);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter = new FilterAdapter(this, tempBitmap, gpuImageFilters);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next_filter:
                Intent intent = new Intent(this, FinishUpActivity.class);
                FinishUpActivity.bitmap = editBitmap;
                intent.putExtra("filePath",filePath);
                startActivity(intent);
                break;
            case R.id.btn_back_filter:
                this.finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            case R.id.btn_resume_filter:
                FinishUpActivity.bitmap = editBitmap;
                finish();
                overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (btnResume.getVisibility() == View.VISIBLE) {
            finish();
            overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
        } else {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
    }

    /**Banner view method*/
    private void loadBanner() {
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}
