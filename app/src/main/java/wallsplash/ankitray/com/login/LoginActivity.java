package wallsplash.ankitray.com.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wallsplash.ankitray.com.MainActivity;
import wallsplash.ankitray.com.forgotpassword.ForgotPWActivity;
import wallsplash.ankitray.com.registration.RegisterActivity;
import wallsplash.ankitray.com.utils.SharedObjects;
import wallsplash.ankitray.com.wallsplash.R;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.edtEmail)
    EditText edtEmail;
    @BindView(R.id.edtPassword)
    EditText edtPassword;
    @BindView(R.id.tvLogin)
    TextView tvLogin;
    @BindView(R.id.tvForgot)
    TextView tvForgot;
    @BindView(R.id.tvJoin)
    TextView tvJoin;
    private FirebaseAuth auth;
    // private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    SharedObjects sharedObjects;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get Firebase auth instance
        sharedObjects = new SharedObjects(LoginActivity.this);
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        ProgressDialogSetup();
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
    }
    public void ProgressDialogSetup() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
    }
    private  void login(){
        String email = edtEmail.getText().toString();
        final String password = edtPassword.getText().toString();

        if (!validateEmail()){
            return;
        }
        if (!validatePassword()){
            return;
        }
        progressDialog.show();

        //authenticate user
        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        progressDialog.dismiss();


                        if (!task.isSuccessful()) {
                            // there was an error
                            if (password.length() < 6) {
                                edtPassword.setError(getString(R.string.minimum_password));
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            }
                        } else {
                           String uid=task.getResult().getUser().getUid();
                            sharedObjects.setUserID(uid);
                            Intent intent1=new Intent(LoginActivity.this,MainActivity.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

                            int code= sharedObjects.getCode();
                            if (code==1){
                                setResult(1, intent1);
                            }else if (code==2){
                                setResult(2,intent1);
                            }else {
                                sharedObjects.setCode(0);
                                startActivity(intent1);
                            }
                            finish();
                        }
                    }
                });

    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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
    @OnClick({R.id.edtEmail, R.id.edtPassword, R.id.tvLogin, R.id.tvForgot, R.id.tvJoin,R.id.cvCancel,R.id.tvSkip})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.edtEmail:
                break;
            case R.id.edtPassword:
                break;
            case R.id.tvLogin:
                login();

                break;
            case R.id.tvSkip:
                Intent intent1=new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent1);
                break;
            case R.id.tvForgot:
                Intent intent2=new Intent(LoginActivity.this,ForgotPWActivity.class);
                startActivity(intent2);
                finish();
                break;
                case R.id.cvCancel:
                    finish();
                break;
            case R.id.tvJoin:
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
