package wallsplash.ankitray.com.registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import wallsplash.ankitray.com.MainActivity;
import wallsplash.ankitray.com.bean.User;
import wallsplash.ankitray.com.login.LoginActivity;
import wallsplash.ankitray.com.utils.SharedObjects;
import wallsplash.ankitray.com.wallsplash.R;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.edtFname)
    EditText edtFname;
    @BindView(R.id.edtUsername)
    EditText edtUsername;
    @BindView(R.id.edtEmail)
    EditText edtEmail;
    @BindView(R.id.edtPassword)
    EditText edtPassword;
    @BindView(R.id.edtConPassword)
    EditText edtConPassword;
    @BindView(R.id.tvRegister)
    TextView tvRegister;
    @BindView(R.id.tvLogin)
    TextView tvLogin;
    @BindView(R.id.tvSkip)
    TextView tvSkip;
    String email;
    String fname;
    String username;
    String password;

    ProgressBar progressBar;
    @BindView(R.id.iv_profile)
    CircleImageView ivProfile;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;
    private DatabaseReference mDatabase;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;
    FirebaseStorage storage;
    StorageReference storageReference;
    SharedObjects sharedObjects;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        sharedObjects = new SharedObjects(RegisterActivity.this);
        //firebaseManager = FirebaseManager.getInstance();
        ProgressDialogSetup();
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                if (filePath==null){
                    Toast.makeText(RegisterActivity.this, "Please upload profile image", Toast.LENGTH_SHORT).show();
                }else {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    ivProfile.setImageBitmap(bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void ProgressDialogSetup() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
    }

    private void registerUser(final String Email, final String Password, final String FName, final String Username) {

        email = edtEmail.getText().toString().trim();
        fname = edtFname.getText().toString().trim();
        username = edtUsername.getText().toString().trim();
        password = edtPassword.getText().toString().trim();
        ///String Conpassword = edtConPassword.getText().toString().trim();

        if (!validateFirstName()){
            return;
        }
        if (!validateUsername()){
            return;
        }
        if (!validateEmail()){
            return;
        }
        if (!validatePassword()){
            return;
        }
        if (!validateConfirmPassword()){
            return;
        }


        if (filePath==null){
            Toast.makeText(RegisterActivity.this, "Please upload profile image", Toast.LENGTH_SHORT).show();
        }else {
            progressDialog.show();

            //create user
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                           // Toast.makeText(RegisterActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.

                            if (!task.isSuccessful()) {

                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                   // Toast.makeText(RegisterActivity.this, "User with this email already exist.", Toast.LENGTH_SHORT).show();
                                }

                               /* Toast.makeText(RegisterActivity.this, task.getException().getMessage() + task.getException(),
                                        Toast.LENGTH_SHORT).show();*/
                            } else {
                                uploadImage(task.getResult().getUser().getUid());
                                User user = new User(fname, username, email, password,"");
                                mDatabase.child(task.getResult().getUser().getUid()).setValue(user);

                            }
                        }
                    });
        }
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private boolean validateFirstName() {
        String username = edtFname.getText().toString().trim();
        if (username.isEmpty()) {
            //  edtUsername.setEnabled(true);
            edtFname.setError(getResources().getString(R.string.errfnameRequired));
            edtFname.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            requestFocus(edtFname);
            return false;
        } else {
            edtFname.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getApplicationContext(), android.R.color.transparent),
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
                    ContextCompat.getColor(getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            requestFocus(edtUsername);
            return false;
        } else {
            edtUsername.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getApplicationContext(), android.R.color.transparent),
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
                    ContextCompat.getColor(getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            requestFocus(edtEmail);
            return false;
        } else if (!SharedObjects.isValidEmail(email)) {
            edtEmail.setError(getResources().getString(R.string.errEmailInvalid));
            edtEmail.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            requestFocus(edtEmail);
            return false;
        } else {
            edtEmail.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            //  edtEmail.setEnabled(false);
        }
        return true;
    }
    private boolean validatePassword() {
        if (edtPassword.getText().toString().trim().isEmpty()) {
            //  edtPassword.setEnabled(true);
            edtPassword.setError(getResources().getString(R.string.errPasswordRequired));
            edtPassword.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            requestFocus(edtPassword);
            return false;
        }else if(edtPassword.getText().toString().length()<6 || edtPassword.getText().toString().length()>30){
            //edtPassword.setEnabled(true);
            edtPassword.setError(getResources().getString(R.string.errPasswordRange));
            edtPassword.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            requestFocus(edtPassword);
            return false;
        } else {
            edtPassword.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            //  edtPassword.setEnabled(false);
        }
        return true;
    }

    private boolean validateConfirmPassword() {
        String confirmPassword = edtConPassword.getText().toString().trim();
        if (edtConPassword.getText().toString().trim().isEmpty()) {
            edtConPassword.setError(getResources().getString(R.string.errConfirmPasswordRequired));
            edtConPassword.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            requestFocus(edtConPassword);
            return false;
        }else if(edtConPassword.getText().toString().length()<6 || edtConPassword.getText().toString().length()>30){
            // edtPasswordConfirm.setEnabled(true);
            edtConPassword.setError(getResources().getString(R.string.errConPasswordRange));
            edtConPassword.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            requestFocus(edtConPassword);
            return false;
        }
        else if (!edtPassword.getText().toString().trim().equals(confirmPassword)) {
            edtConPassword.setError(getResources().getString(R.string.errConfirmPasswordMismatch));
            edtConPassword.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            requestFocus(edtConPassword);
            return false;
        } else {
            edtConPassword.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            //  edtPasswordConfirm.setEnabled(false);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressDialog.dismiss();

    }

    @OnClick({R.id.iv_profile, R.id.edtFname, R.id.edtUsername, R.id.edtEmail, R.id.edtPassword, R.id.edtConPassword, R.id.tvRegister, R.id.tvLogin,R.id.cvCancel,R.id.tvSkip})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.edtFname:
                break;
            case R.id.edtUsername:
                break;
            case R.id.edtEmail:
                break;
            case R.id.edtPassword:
                break;
            case R.id.edtConPassword:
                break;
                case R.id.tvSkip:
                    Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(intent);
                break;
            case R.id.tvRegister:
                registerUser(email, password, fname, username);
                break;
            case R.id.iv_profile:
                chooseImage();
                break;
                case R.id.cvCancel:
                finish();
                break;
            case R.id.tvLogin:
                Intent intent1 = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent1);
                finish();
                break;
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
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + uid);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            sharedObjects.setUserID(uid);
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            intent.putExtra("uid",uid);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
}
