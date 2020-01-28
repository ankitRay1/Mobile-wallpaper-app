package wallsplash.ankitray.com;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wallsplash.ankitray.com.bean.User;
import wallsplash.ankitray.com.login.LoginActivity;
import wallsplash.ankitray.com.registration.RegisterActivity;
import wallsplash.ankitray.com.utils.SharedObjects;
import wallsplash.ankitray.com.wallsplash.R;

public class StratupActivity extends AppCompatActivity {

    @BindView(R.id.tvSkip)
    TextView tvSkip;
    @BindView(R.id.tvLogin)
    TextView tvLogin;
    @BindView(R.id.tvFbLogin)
    TextView tvFbLogin;
    @BindView(R.id.tvJoin)
    TextView tvJoin;
    //------------Facebook integration start--------------------------------
    private CallbackManager callbackManager;
    String name;
    String location;
    String uid;
    SharedObjects sharedObjects;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    Uri photoUrl;
    StorageReference storageReference;
    StorageReference storageRef;
    FirebaseStorage storage;
    //------------Facebook integration end---------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stratup);
        ButterKnife.bind(this);
        sharedObjects = new SharedObjects(StratupActivity.this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        storageRef = storage.getReference();

        //------------Facebook integration start--------------------------------
        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
//------------Facebook integration end---------------------------------

        getFbKeyHash("wallsplash.ankitray.com");


        //--------------Facebook integration start----------------------------------------------------
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                /*SharedPreferences.Editor editor = prefs.edit();
                editor.putString("LoginWith","Facebook");*/
                /*System.out.println("Facebook Login Successful!");
                System.out.println("Logged in user Details : ");
                System.out.println("--------------------------");
                System.out.println("User ID  : " + loginResult.getAccessToken().getUserId());
                System.out.println("Authentication Token : " + loginResult.getAccessToken().getToken());*/

                //     Log.i(TAG,"Hello"+loginResult.getAccessToken().getToken());
                //  Toast.makeText(MainActivity.this, "Token:"+loginResult.getAccessToken(), Toast.LENGTH_SHORT).show();

                //Toast.makeText(StratupActivity.this, loginResult.getAccessToken().getUserId(), Toast.LENGTH_SHORT).show();
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Toast.makeText(StratupActivity.this, "Login cancelled by user!", Toast.LENGTH_LONG).show();
                System.out.println("Facebook Login failed!!");

            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(StratupActivity.this, "Login unsuccessful!", Toast.LENGTH_LONG).show();
                System.out.println("Facebook Login failed!!");
            }
        });
        //--------------Facebook integration end----------------------------------------------------

        mAuthListener = new FirebaseAuth.AuthStateListener() {


            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    name = user.getDisplayName();

                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {

                            System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase."

                            User user = snapshot.getValue(User.class);

                            location = (user.getLocation());


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("QError", databaseError.getMessage());
                        }
                    });
                    Toast.makeText(StratupActivity.this, "" + user.getDisplayName(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(StratupActivity.this, "something went wrong", Toast.LENGTH_LONG).show();
                }


            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
      //  currentUser.getUid();
        //  updateUI(currentUser);
    }


    private void handleFacebookAccessToken(final AccessToken token) {
        Log.d("AccessToken", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            final boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();

                            // Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(StratupActivity.this, "Success",
                                    Toast.LENGTH_SHORT).show();
                          //  uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            uid=user.getUid();
                            final String name = user.getDisplayName();
                            final String email = user.getEmail();
                           /* photoUrl = Profile.getCurrentProfile().getProfilePictureUri(325, 325);
                            Picasso.get()
                                    .load(photoUrl)
                                    .into(getTarget(task.getResult().getUser().getUid()));*/

                            DatabaseReference userIdRef = mDatabase.child(uid).getRef();
                          //  DatabaseReference userIdRef1 = mDatabase.getDatabase().getReference();
                           // userIdRef.keepSynced(true);

                            ValueEventListener valueEventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {


                                        if (isNew){
                                            photoUrl = Profile.getCurrentProfile().getProfilePictureUri(325, 325);
                                            Picasso.get()
                                                    .load(photoUrl)
                                                    .into(getTarget(task.getResult().getUser().getUid()));
                                            User fuser = new User(name, name, email, "123456", "");
                                            mDatabase.child(task.getResult().getUser().getUid()).setValue(fuser);

                                        }else {

                                          //  photoUrl = Profile.getCurrentProfile().getProfilePictureUri(325, 325);

                                            User user =  dataSnapshot.getValue(User.class);
//                                        User user = ds.getValue(User.class);
                                            String id=dataSnapshot.getKey();
                                            String location=user.getLocation();
                                            String username=user.getUserName();
                                            String fullname=user.getFullName();
                                            User fuser = new User(fullname,username,email,"123456",location);
                                            mDatabase.child(task.getResult().getUser().getUid()).setValue(fuser);
                                            sharedObjects.setUserID(uid);
                                            Intent intent = new Intent(StratupActivity.this, MainActivity.class);
                                            intent.putExtra("uid", uid);
                                            startActivity(intent);
                                            finish();
                                        }

//                                    }


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.w("LogFragment", "loadLog:onCancelled", databaseError.toException());
                                }
                            };
                            userIdRef.addValueEventListener(valueEventListener);
                        } else {
                        Toast.makeText(StratupActivity.this, "Authentication error",
                                Toast.LENGTH_SHORT).show();

                    }


                    }
                });
    }

    //target to save
    private Target getTarget(final String uid) {
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                uploadImage(uid, bitmapToBase64(bitmap));

                                // Stuff that updates the UI
                            }
                        });
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };
        return target;
    }

    public static byte[] bitmapToBase64(Bitmap bitmap) {
        byte[] byteArray = new byte[0];
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byteArray = byteArrayOutputStream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    private void uploadImage(final String uid, byte[] bytes) {

        if (photoUrl != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + uid);

            ref.putBytes(bytes)
                    /* ref.putFile(photoUrl)*/
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(StratupActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            sharedObjects.setUserID(uid);
                            Intent intent = new Intent(StratupActivity.this, MainActivity.class);
                            intent.putExtra("uid", uid);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(StratupActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    public void getFbKeyHash(String packageName) {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("YourKeyHash :", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                System.out.println("YourKeyHash: " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            photoUrl = data.getData();
            try {
                if (photoUrl == null) {
                    Toast.makeText(StratupActivity.this, "Please upload profile image", Toast.LENGTH_SHORT).show();
                } else {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUrl);
                    //  ivProfile.setImageBitmap(bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick({R.id.tvSkip, R.id.tvLogin, R.id.tvFbLogin, R.id.tvJoin})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {

            case R.id.tvSkip:
                intent = new Intent(StratupActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);

                break;
            case R.id.tvLogin:

                intent = new Intent(StratupActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(R.anim.stay, R.anim.slide_up_anim);

                //   finish();
                break;
            case R.id.tvFbLogin:
                LoginManager.getInstance().logInWithReadPermissions(StratupActivity.this, Arrays.asList("public_profile", "email"));
                break;
            case R.id.tvJoin:
                intent = new Intent(StratupActivity.this, RegisterActivity.class);
                startActivity(intent);
                // finish();
                break;
        }
    }
}
