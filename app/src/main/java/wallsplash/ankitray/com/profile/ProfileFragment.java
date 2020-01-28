package wallsplash.ankitray.com.profile;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import wallsplash.ankitray.com.bean.User;

import wallsplash.ankitray.com.changePW.ChangeFragment;
import wallsplash.ankitray.com.login.LoginActivity;
import wallsplash.ankitray.com.retrofit.Config;
import wallsplash.ankitray.com.utils.SharedObjects;
import wallsplash.ankitray.com.wallsplash.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    Unbinder unbinder;

    String email;
    String fname;
    String username;
    String password;
    String location;
    //  PrefUtils prefUtils;
    @BindView(R.id.iv_profile)
    CircleImageView ivProfile;
    @BindView(R.id.iv_edit)
    ImageView iv_edit;
    @BindView(R.id.edtFname)
    EditText edtFname;
    @BindView(R.id.edtUsername)
    EditText edtUsername;
    @BindView(R.id.edtEmail)
    EditText edtEmail;
    @BindView(R.id.edtLocation)
    EditText edtLocation;
    @BindView(R.id.edtPassword)
    EditText edtPassword;
    @BindView(R.id.edtNewPassword)
    EditText edtNewPassword;
    @BindView(R.id.edtConPassword)
    EditText edtConPassword;
    @BindView(R.id.tvUpdate)
    TextView tvUpdate;
    @BindView(R.id.tvChangePW)
    TextView tvChangePW;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    SharedObjects sharedObjects;
    FirebaseStorage storage ;
    private Uri filePath;
    private FirebaseAuth.AuthStateListener authListener;
    private final int PICK_IMAGE_REQUEST = 71;
    private StorageReference storageRef;

    String uid;
    String uidprof;
    String oldPw;
    String currentpassword;
    private ProgressDialog progressDialog;
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        sharedObjects = new SharedObjects(getActivity());
        ProgressDialogSetup();
        uid=sharedObjects.getUserID();
        //storageprofile = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();
                }
            }
        };




        progressDialog.show();
        storage = FirebaseStorage.getInstance();

        uidprof="images/"+sharedObjects.getUserID();
        storageRef = storage.getReferenceFromUrl(Config.DatabasePATH).child((uidprof));
        try {
            final File localFile = File.createTempFile("images", "jpg");
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                   Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    ivProfile.setImageBitmap(bitmap);
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    exception.printStackTrace();
                    progressDialog.dismiss();
                }
            });
        } catch (IOException e) {
        }


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

            if (isVisible()){
                System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase."

                User user = snapshot.getValue(User.class);

                //assert user != null;
                edtFname.setText(user.getFullName());
                edtUsername.setText(user.getUserName());
                edtEmail.setText(user.getEmail());
                edtLocation.setText(user.getLocation());
                currentpassword=user.getPassword();
                // edtPassword.setText(user.getPassword());
                oldPw=(user.getPassword());
            }else {

            }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("QError", databaseError.getMessage());
            }
        });


        for (UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals("facebook.com")) {
                tvChangePW.setVisibility(View.GONE);
                edtEmail.setFocusableInTouchMode(false);

                email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                currentpassword="123456";
            }else {
                email = edtEmail.getText().toString().trim();
            }
        }


        return view;
    }
    public void ProgressDialogSetup() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

 private void registerUser() {

        email = edtEmail.getText().toString().trim();
        fname = edtFname.getText().toString().trim();
        username = edtUsername.getText().toString().trim();
      //  password = edtPassword.getText().toString().trim();
        location = edtLocation.getText().toString().trim();
        ///String Conpassword = edtConPassword.getText().toString().trim();

     if (!validateFirstName()){
         return;
     }
     if (!validateUsername()){
         return;
     }
     if (!validateEmail()){
         return;
     }if (!validateLocation()){

         return;
     }


             FirebaseAuth mAuth = FirebaseAuth.getInstance();
             FirebaseUser usercred = FirebaseAuth.getInstance().getCurrentUser();
             AuthCredential credential = EmailAuthProvider
                     .getCredential(mAuth.getCurrentUser().getEmail(),"123456"); // Current Login Credentials \\
             // Prompt the user to re-provide their sign-in credentials
             usercred.reauthenticate(credential)
                     .addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             //         Log.d(TAG, "User re-authenticated.");
                             //Now change your email address \\
                             //----------------Code for Changing Email Address----------\\
                             if (isVisible()) {
                                 FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                 user.updateEmail(edtEmail.getText().toString())
                                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                                             @Override
                                             public void onComplete(@NonNull Task<Void> task) {
                                                 if (task.isSuccessful()) {
                                                     Toast.makeText(getActivity(), "Profile update successfuly ", Toast.LENGTH_LONG).show();


                                                     //   Log.d(TAG, "User email address updated.");
                                                 }
                                             }
                                         });
                                 //----------------------------------------------------------\\
                             }
                         }
                     });

             User user1 = new User(fname, username, email,password,edtLocation.getText().toString());
             Map<String, Object> postValues = new HashMap<>();
             postValues.put("fullName",fname);
             postValues.put("userName",username);
             postValues.put("email",email);
             //   postValues.put("password",edtNewPassword.getText().toString());
             postValues.put("location",edtLocation.getText().toString());

             mDatabase.child(uid).updateChildren(postValues);






        }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                if (filePath==null){
                    Toast.makeText(getActivity(), "Please upload profile image", Toast.LENGTH_SHORT).show();
                }else {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                    ivProfile.setImageBitmap(bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void uploadImage(final String uid) {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

           // StorageReference ref = storageRef.child("images/" + uid);
            storageRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                            sharedObjects.setUserID(uid);

                           /* Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.putExtra("uid",uid);
                            startActivity(intent);
                            getActivity().finish();*/
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateFirstName() {
        String username = edtFname.getText().toString().trim();
        if (username.isEmpty()) {
            //  edtUsername.setEnabled(true);
            edtFname.setError(getResources().getString(R.string.errfnameRequired));
            edtFname.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getActivity().getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            requestFocus(edtFname);
            return false;
        } else {
            edtFname.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getActivity().getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            //  edtUsername.setEnabled(false);
        }
        return true;
    }private boolean validateUsername() {
        String username = edtUsername.getText().toString().trim();
        if (username.isEmpty()) {
            //  edtUsername.setEnabled(true);
            edtUsername.setError(getResources().getString(R.string.errUsernameRequired));
            edtUsername.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getActivity().getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            requestFocus(edtUsername);
            return false;
        } else {
            edtUsername.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getActivity().getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            //  edtUsername.setEnabled(false);
        }
        return true;
    }private boolean validateLocation() {
        String username = edtLocation.getText().toString().trim();
        if (username.isEmpty()) {
            //  edtUsername.setEnabled(true);
            edtLocation.setError(getResources().getString(R.string.errLocationRequired));
            edtLocation.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getActivity().getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            requestFocus(edtLocation);
            return false;
        } else {
            edtLocation.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getActivity().getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            //  edtUsername.setEnabled(false);
        }
        return true;
    }
    private boolean validateEmail() {
        String email = edtEmail.getText().toString().trim();
        if (email.isEmpty()) {
            // edtEmail.setEnabled(true);
            edtEmail.setError(getResources().getString(R.string.errEmailRequired));
            edtEmail.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getActivity().getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            requestFocus(edtEmail);
            return false;
        } else if (!SharedObjects.isValidEmail(email)) {
            edtEmail.setError(getResources().getString(R.string.errEmailInvalid));
            edtEmail.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getActivity().getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            requestFocus(edtEmail);
            return false;
        } else {
            edtEmail.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getActivity().getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            //  edtEmail.setEnabled(false);
        }
        return true;
    }

    private void loadFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped) { //fragment not in back stack, create it.
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(R.id.fl_container, fragment, null);
            ft.hide(ProfileFragment.this);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }
    public void signOut() {
        auth.signOut();
    }
    @OnClick({R.id.tvUpdate,R.id.iv_profile,R.id.iv_edit,R.id.tvChangePW})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvUpdate:

                uploadImage(uid);
                registerUser();
               // getActivity().onBackPressed();
                break;
            case R.id.iv_profile:
                //chooseImage();
                break;
                case R.id.iv_edit:
                chooseImage();
                break;
                case R.id.tvChangePW:
                    ChangeFragment changeFragment=new ChangeFragment();
                    loadFragment(changeFragment);
                break;


        }


    }
}
