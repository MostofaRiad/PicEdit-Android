package com.allyouneedapp.palpicandroid.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.allyouneedapp.palpicandroid.R;
import com.cybozu.labs.langdetect.LangDetectException;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import static com.allyouneedapp.palpicandroid.utils.Constants.KEY_EMAIL;
import static com.allyouneedapp.palpicandroid.utils.Constants.KEY_NAME;
import static com.allyouneedapp.palpicandroid.utils.Constants.KEY_UID;

/**
 * Created by Mostofa on 11/2/2016.
 */

public class AppUtils {
    public static final String KEY_LOGIN = "login";
    public static final String KEY_REGISTER = "register";
    private Activity context;

    public FirebaseAuth mAuth;
    public CallbackManager mCallbackManager;
    public GoogleApiClient mGoogleApiClient;

    public AppUtils(final Activity context) {
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        //google login function
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(this.context.getString(R.string.server_client_id))
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this.context)
                .enableAutoManage((FragmentActivity) context, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(context, "Google Play Services error.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
    }
    /**Sing-Up and Sign-In with email*/
    public void loginToFirebase(String email ,String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                appUtilsListener.onSuccess(authResult, KEY_LOGIN);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                appUtilsListener.onFailed(e, KEY_LOGIN);
            }
        });
    }

    public void registeWithEmail(String email ,String password ) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(final AuthResult authResult) {
                appUtilsListener.onSuccess(authResult, KEY_REGISTER);
                authResult.getUser().getUid();
                if (authResult.getUser() != null) {
                    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(authResult.getUser().getUid());
                    HashMap<String, Object> values = new HashMap<>();

                    values.put("FBName" ,authResult.getUser().getDisplayName());
                    values.put("FCMToken", FirebaseInstanceId.getInstance().getToken());
                    values.put("email",authResult.getUser().getEmail());
                    values.put("lang", Locale.getDefault().getDisplayLanguage());
                    values.put("region", Locale.getDefault().getDisplayCountry());

                    ref.setValue(values);
                    appUtilsListener.onSuccess(authResult, KEY_LOGIN);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                appUtilsListener.onFailed(e, KEY_REGISTER);
            }
        });
    }

    public boolean isSignIn(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return false;
        } else {
            return true;
        }
    }

    public void handleFacebookAccessToken(final AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Sign-In Success with Facebook.",
                                    Toast.LENGTH_SHORT).show();
                            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(task.getResult().getUser().getUid());
                            HashMap<String, Object> values = new HashMap<>();

                            values.put("FBName" ,task.getResult().getUser().getDisplayName());
                            values.put("FCMToken", FirebaseInstanceId.getInstance().getToken());
                            values.put("email","Nil");
                            values.put("lang", Locale.getDefault().getDisplayLanguage());
                            values.put("region", Locale.getDefault().getDisplayCountry());

                            ref.setValue(values);

                            SharedPreferences.Editor pref = context.getSharedPreferences("palpic", Context.MODE_PRIVATE).edit();
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Name, email address, and profile photo Url
                                String name = user.getDisplayName();
                                String email = user.getEmail();
                                String uid = user.getUid();

                                pref.putString(KEY_NAME,name);
                                pref.putString(KEY_EMAIL,email);
                                pref.putString(KEY_UID,uid);
                                pref.commit();
                                /**Download Stickers*/
                                FirebaseDbUtils dbUtils = new FirebaseDbUtils(context);
                                dbUtils.getSuggestedStickers();
                            }

                            appUtilsListener.onSuccess(task.getResult(),KEY_LOGIN);
                        }
                    }
                });
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            Toast.makeText(context,"Log-in failed", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Sign-In success with" + task.getResult().getUser().getEmail(),Toast.LENGTH_LONG).show();
                            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(task.getResult().getUser().getUid());
                            HashMap<String, Object> values = new HashMap<>();

                            values.put("FBName" ,"Nil");
                            values.put("FCMToken", FirebaseInstanceId.getInstance().getToken());
                            values.put("email",task.getResult().getUser().getEmail());
                            values.put("lang", Locale.getDefault().getDisplayLanguage());
                            values.put("region", Locale.getDefault().getDisplayCountry());

                            ref.setValue(values);


                            SharedPreferences.Editor pref = context.getSharedPreferences("palpic", Context.MODE_PRIVATE).edit();
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Name, email address, and profile photo Url
                                String name = user.getDisplayName();
                                String email = user.getEmail();
                                String uid = user.getUid();

                                pref.putString(KEY_NAME,name);
                                pref.putString(KEY_EMAIL,email);
                                pref.putString(KEY_UID,uid);
                                pref.commit();
                                /**Download Stickers*/
                                FirebaseDbUtils dbUtils = new FirebaseDbUtils(context);
                                dbUtils.getSuggestedStickers();
                            }

                            appUtilsListener.onSuccess(task.getResult(),KEY_LOGIN);
                        }
                    }
                });
    }

    /**Sign CallBack Listener*/
    public AppUtilsListener appUtilsListener;

    public void setAppUtilsListener(AppUtilsListener listener){ this.appUtilsListener = listener;}

    public interface AppUtilsListener {
        public void onSuccess(AuthResult authResult,String status);
        public void onFailed(Exception e, String status);
    }

    /**
     * Instagram photo share methods
     */
    public void sharePhotoWithInstagram(){
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("image/*");

        final ContentResolver cr = context.getContentResolver();
        final String[] p1 = new String[] {
                MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.TITLE, MediaStore.Images.ImageColumns.DATE_TAKEN
        };
        Cursor c1 = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, p1, null, null, p1[1] + " DESC");

        if (c1.moveToFirst() ) {
            Log.i("Teste", "last picture (" + c1.getString(1) + ") taken on: " + new Date(c1.getLong(2)));
        }

        Log.i("Caminho download imagem", "file://"+Environment.getExternalStorageDirectory()+ "/Tubagram/"  + c1.getString(1) + ".png");

        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+ Environment.getExternalStorageDirectory()+ "/Tubagram/" + c1.getString(1)+".png"));
        shareIntent.setPackage("com.instagram.android");

        c1.close();

        context.startActivity(shareIntent);
    }

    public boolean isInstalledInstagramApp(){
        boolean installed = false;

        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo("com.instagram.android", 0);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    public void createInstagramIntent(String type, String mediaPath){

        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType(type);

        // Create the URI from the media
        File media = new File(mediaPath);
        Uri uri = Uri.fromFile(media);

        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);


        // Broadcast the Intent.
        try {
            if (context.getPackageManager().getPackageInfo("com.instagram.android", 0) != null) {
                share.setPackage("com.instagram.android");
            }
            context.startActivity(Intent.createChooser(share, "Share to with instagram"));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }

    }

    public void alertWithTitle(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    public void shareImageWithEmail(String filePath){
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("application/image");
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"My wonderful moment");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "I wanna share this image with you...");
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+filePath));
        context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    public String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    public static int getExifRotation(String imgPath){
        try
        {
            ExifInterface exif = new ExifInterface(imgPath);
            String rotationAmount = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            if (!TextUtils.isEmpty(rotationAmount))
            {
                int rotationParam = Integer.parseInt(rotationAmount);
                switch (rotationParam)
                {
                    case ExifInterface.ORIENTATION_NORMAL:
                        return 0;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        return 90;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        return 180;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        return 270;
                    default:
                        return 0;
                }
            }
            else
            {
                return 0;
            }
        }
        catch (Exception ex)
        {
            return 0;
        }
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        Bitmap rotatedImage = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                matrix, true);
        return rotatedImage;
    }

    public String detectLanguage(String inputString) {

        String detectedLanguage = "";
        try {
            detectedLanguage = LanguageDetector.detect(inputString);
        } catch (LangDetectException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return detectedLanguage;

    }

    public void initLanguageDetector(){
        try {
            LanguageDetector.init(loadProfilesFromAsset());
        } catch (LangDetectException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ArrayList<String> loadProfilesFromAsset(){
        ArrayList<String> profiles = new ArrayList<>();

        try {
            String [] files = context.getAssets().list("profiles");
            for (String item : files) {
                profiles.add(loadJSONFromAsset(item));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return profiles;
    }

    public String loadJSONFromAsset(String file) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("profiles/"+file);
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


}
