package wallsplash.ankitray.com.forgotpassword;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wallsplash.ankitray.com.MainActivity;
import wallsplash.ankitray.com.utils.SharedObjects;
import wallsplash.ankitray.com.wallsplash.R;

public class ForgotPWActivity extends AppCompatActivity {

    @BindView(R.id.cvCancel)
    CardView cvCancel;
    @BindView(R.id.tvSkip)
    TextView tvSkip;
    @BindView(R.id.edtEmail)
    EditText edtEmail;
    @BindView(R.id.tvForgot)
    TextView tvForgot;
    private FirebaseAuth auth;
    // private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pw);
        ButterKnife.bind(this);
        auth = FirebaseAuth.getInstance();

        ProgressDialogSetup();
    }
    public void ProgressDialogSetup() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
    }
    @OnClick({R.id.cvCancel, R.id.tvSkip, R.id.tvForgot})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvCancel:
                finish();
                break;
            case R.id.tvSkip:
                Intent intent=new Intent(ForgotPWActivity.this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.tvForgot:
                if (!validateEmail()){
                    return;
                }
                progressDialog.show();
                if (!edtEmail.getText().toString().trim().equals("")) {
                    auth.sendPasswordResetEmail(edtEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ForgotPWActivity.this, "Reset password email is sent!", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        finish();

                                    } else {
                                        Toast.makeText(ForgotPWActivity.this, "Invalid email!", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();

                                    }
                                }
                            });
                } else {
                    edtEmail.setError("Enter email");
                    progressDialog.dismiss();

                }
                break;
        }
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
}
