package com.allyouneedapp.palpicandroid.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.allyouneedapp.palpicandroid.R;
import com.allyouneedapp.palpicandroid.database.PalPicDBHandler;
import com.allyouneedapp.palpicandroid.models.Sticker;
import com.allyouneedapp.palpicandroid.models.SuggestSticker;
import com.allyouneedapp.palpicandroid.models.UseSticker;
import com.fasterxml.jackson.core.json.UTF8StreamJsonParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.jarlen.photoedit.utils.FileUtils;

import static com.allyouneedapp.palpicandroid.utils.Constants.KEY_UID;
import static jp.co.cyberagent.android.gpuimage.GPUImageFilter.convertStreamToString;

/**
 * Created by Mostofa on 11/9/2016.
 */

public class FirebaseDbUtils {

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseStorage storage;
    StorageReference storageRef;
    Context context;
    SharedPreferences prefs;
    String uid;
    PalPicDBHandler db;
    ArrayList<SuggestSticker> suggestStickers;
    public FirebaseDbUtils(Context context) {
        this.database = FirebaseDatabase.getInstance();
        this.myRef = database.getReference();
        this.storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        this.context = context;
        prefs = context.getSharedPreferences("palpic", Context.MODE_PRIVATE);
        uid = prefs.getString(KEY_UID, null);
        db = new PalPicDBHandler(context);
    }

    public void suggestStickerToFirebase(String title, String uid) {
        DatabaseReference stickerRef = myRef.child("stickers");
        // Read from the database
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        HashMap<String, Object> result = new HashMap<>();
        result.put(SuggestSticker.KEY_SUGGESTEDWORD, title);
        result.put(SuggestSticker.KEY_TIMESTAMP, ts);
        result.put(SuggestSticker.KEY_STATUS, "Suggested");
        String storePath = uid+"/"+stickerRef.push().getKey();
        stickerRef.child(storePath).setValue(result, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast.makeText(context,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
        Toast.makeText(context,"Successfully suggested",Toast.LENGTH_SHORT).show();
    }

    public void getSuggestedStickers() {
        DatabaseReference stickerRef = myRef.child("stickers");
        suggestStickers = new ArrayList<>();
        stickerRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getKey();
                suggestStickers.clear();
                Iterable<DataSnapshot> values = dataSnapshot.getChildren();
                for (DataSnapshot item : values)
                {
                    String key = item.getKey();
                    Map<String , String> value = (Map<String, String>) item.getValue();

                    if(value.get(SuggestSticker.KEY_STATUS).equals("Completed")&&db.getStickersWithTile(value.get(SuggestSticker.KEY_SUGGESTEDWORD)).isEmpty())
                    suggestStickers.add(new SuggestSticker(key, value.get(SuggestSticker.KEY_STATUS),
                                                                value.get(SuggestSticker.KEY_TIMESTAMP),
                                                                value.get(SuggestSticker.KEY_SUGGESTEDWORD)));
                }

                if (!suggestStickers.isEmpty()) {
                    alertWithTitle();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void alertWithTitle() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setTitle("Palpic");
        builder.setMessage("Stickers ready to download");
        builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                for (SuggestSticker item: suggestStickers) {
                    getDownloadListFromJson(item);
                }
            }
        }).setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    public void getDownloadListFromJson(final SuggestSticker item) {
        StorageReference ref = storageRef.child("jsons").child(item.getId()+".json");

        ref.getBytes(1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                String ids = new String(bytes);
                String [] titleArray;
                try {
                    JSONArray array = new JSONArray(ids);
                    titleArray = new String[array.length()];
                    for (int i = 0; array.length() > i; i++) {
                        titleArray[i] = array.get(i).toString();
                        downloadImageFileToAsset(array.get(i).toString(),item.getSuggestedWord());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void downloadImageFileToAsset(final String id, final String title) {
        StorageReference ref = storageRef.child("stickers").child(id+".png");
        String packagePath = Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getApplicationContext().getPackageName()
                + "/Files";

        final File localFile = FileUtils.createNewFile(packagePath+"/"+id+".png");

        ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                db.insert(new Sticker(id, title, localFile.getPath()));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    public void saveUseStickersToServer(ArrayList<String> words, ArrayList<String> stickers, String userId) {
        DatabaseReference ref = myRef.child("useStickers");

        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        for (String item : stickers) {
            HashMap<String, String> values = new HashMap<>();
            String key = ref.child(item).push().getKey();
            values.put(UseSticker.USE_KEY_TIMESTAMP,ts);
            values.put(UseSticker.USE_KEY_USERID,userId);
            ref.child(item).child(key).setValue(values);
        }

//        DatabaseReference wordRef = myRef.child("stickers");
        for (final String item : words) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    saveUseWordsToServer(item,uid);
                }
            });
//            HashMap<String, String> values = new HashMap<>();
//            String key = wordRef.child(item).push().getKey();
//            values.put(UseSticker.USE_KEY_USERID,userId);
//            values.put(UseSticker.USE_KEY_TIMESTAMP, ts);
//            wordRef.child(item).child(key).setValue(values);
        }

    }

    public void saveUseWordsToServer(String word, String uid) {
        DatabaseReference stickerRef = myRef.child("stickers");
        // Read from the database
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        HashMap<String, Object> result = new HashMap<>();
        result.put(SuggestSticker.KEY_SUGGESTEDWORD, word);
        result.put(SuggestSticker.KEY_TIMESTAMP, ts);
        result.put(SuggestSticker.KEY_STATUS, "UsedWord");
        String storePath = uid+"/"+stickerRef.push().getKey();
        stickerRef.child(storePath).setValue(result, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                }
            }
        });
    }

}