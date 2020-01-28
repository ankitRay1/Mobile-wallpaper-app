package wallsplash.ankitray.com;

import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wallsplash.ankitray.com.retrofit.Config;
import wallsplash.ankitray.com.retrofit.RestClient;
import wallsplash.ankitray.com.utils.SharedObjects;
import wallsplash.ankitray.com.wallsplash.R;

public class ApplyActivity extends AppCompatActivity {

    @BindView(R.id.ivWallpaper)
    ImageView ivWallpaper;
    @BindView(R.id.btnAplay)
    Button btnAplay;
    @BindView(R.id.cv_like)
    CheckBox cvLike;
    String photoid;
    WallpaperManager wallpaperManager ;
    Bitmap bitmap1, bitmap2 ;
    Bitmap anImage;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply);
        ButterKnife.bind(this);
        ProgressDialogSetup();
        Intent intent=getIntent();
        photoid=intent.getStringExtra("photoid");
        PhotoDetails();

    }
    public void ProgressDialogSetup() {
        progressDialog = new ProgressDialog(ApplyActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
    }
    private void PhotoDetails() {
        progressDialog.show();
        Call<JsonElement> call1 = RestClient.post().getPhotosById(photoid, Config.unsplash_access_key);
        call1.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                //  progressDialog.dismiss();
                progressDialog.dismiss();
                Log.e("FeatureNews", response.body().toString());
                if (response.isSuccessful()) {

                    JSONObject jsonArr = null;
                    try {
                        jsonArr = new JSONObject(response.body().toString());
                        if (jsonArr.length() > 0) {
                            for (int i = 0; i < jsonArr.length(); i++) {

                                final String id=jsonArr.getString("id");

                                JSONObject object=jsonArr.getJSONObject("urls");
                                final String url=object.getString("regular");
                                Picasso.get()
                                        .load(url)
                                        .into(ivWallpaper);
                                if (FirebaseAuth.getInstance().getCurrentUser()==null){

                                }else {
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                    ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("favourites").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                // use "username" already exists
                                                // Let the user know he needs to pick another username.
                                                cvLike.setChecked(true);
                                            } else {
                                                // User does not exist. NOW call createUserWithEmailAndPassword
                                                // Your previous code here.
                                                cvLike.setChecked(false);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                                cvLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                                        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                                            Toast.makeText(ApplyActivity.this, "login require", Toast.LENGTH_SHORT).show();
                                            compoundButton.setChecked(false);
                                            return;
                                        }
                                        String uid;
                                        SharedObjects sharedObjects;
                                        sharedObjects = new SharedObjects(ApplyActivity.this);
                                        uid = sharedObjects.getUserID();
                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child("favourites")
                                                .child(id);

                                        if (b) {

                                            databaseReference.child("Id").setValue(id);
                                            databaseReference.child("url").setValue(url);
                                        } else {

                                            databaseReference.child("Id").removeValue();
                                            databaseReference.child("url").removeValue();
                                        }


                                    }
                                });


                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                // progressDialog.dismiss();
                progressDialog.dismiss();
            }
        });

    }
    private void saveImageToExternalStorage(Bitmap finalBitmap) {
        // String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();

        //   String filename = "MyApp/MediaTag/MediaTag-"+"objectId"+".png";
        // File file = new File(Environment.getExternalStorageDirectory(), filename);
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        // File myDir = new File(root + "/saved_images_1");

        File myDir = new File(root + "/Wallsplash");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        //MediaStore.Images.Media.insertImage(getContentResolver(), yourBitmap, yourTitle , yourDescription)
        MediaScannerConnection.scanFile(ApplyActivity.this, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });


    }
    @OnClick({R.id.ivWallpaper, R.id.btnAplay,R.id.cv_download})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivWallpaper:
                break;
                case R.id.cv_download:
                    anImage      = ((BitmapDrawable) ivWallpaper.getDrawable()).getBitmap();
                    saveImageToExternalStorage(anImage);
                    Toast.makeText(ApplyActivity.this, "save success", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnAplay:
                wallpaperManager = WallpaperManager.getInstance(ApplyActivity.this);

                try {
                    //wallpaperManager  = WallpaperManager.getInstance(getApplicationContext());

                /*    bitmapDrawable = (BitmapDrawable) ivPhoto.getDrawable();

                    bitmap1 = bitmapDrawable.getBitmap();*/

                    bitmap1 = ((BitmapDrawable) ivWallpaper.getDrawable()).getBitmap();

                    wallpaperManager.setBitmap(bitmap1);

                    // wallpaperManager.suggestDesiredDimensions(width, height);
                    finish();
                   /* Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);*/



                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
