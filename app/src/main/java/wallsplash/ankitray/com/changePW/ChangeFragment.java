package wallsplash.ankitray.com.changePW;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import wallsplash.ankitray.com.bean.User;
import wallsplash.ankitray.com.login.LoginActivity;
import wallsplash.ankitray.com.utils.SharedObjects;
import wallsplash.ankitray.com.wallsplash.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangeFragment extends Fragment {


    @BindView(R.id.edtPassword)
    EditText edtPassword;
    @BindView(R.id.edtNewPassword)
    EditText edtNewPassword;
    @BindView(R.id.edtConPassword)
    EditText edtConPassword;
    @BindView(R.id.tvUpdate)
    TextView tvUpdate;
    Unbinder unbinder;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    SharedObjects sharedObjects;
    String uid;
    String uidprof;
    String oldPw;
    String currentpassword;
    private ProgressDialog progressDialog;
    public ChangeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change, container, false);
        unbinder = ButterKnife.bind(this, view);
        sharedObjects = new SharedObjects(getActivity());
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
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase."

                User user = snapshot.getValue(User.class);

                currentpassword=user.getPassword();
                // edtPassword.setText(user.getPassword());
                oldPw=(user.getPassword());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("QError", databaseError.getMessage());
            }
        });



        return view;
    }
    public void ProgressDialogSetup() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
    }
    private void changePW() {

        if (!validatePassword()){
            return;
        }if (!validateNewPassword()){
            return;
        }
        if (!validateConfirmPassword()){
            return;
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(mAuth.getCurrentUser().getEmail(),currentpassword); // Current Login Credentials \\
        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //         Log.d(TAG, "User re-authenticated.");
                        //Now change your email address \\
                        //----------------Code for Changing Email Address----------\\
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        //----------------------------------------------------------\\


                        user.updatePassword(edtNewPassword.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getActivity(), "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                            getActivity().onBackPressed();

                                        } else {
                                            Toast.makeText(getActivity(), "Failed to update password!", Toast.LENGTH_SHORT).show();
                                            //  progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });

                    }
                });

       // User user1 = new User(password);
        Map<String, Object> postValues = new HashMap<>();

        postValues.put("password",edtNewPassword.getText().toString());

        mDatabase.child(uid).updateChildren(postValues);

    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private boolean validatePassword() {
        if (edtPassword.getText().toString().trim().isEmpty()) {
            //  edtPassword.setEnabled(true);
            edtPassword.setError(getResources().getString(R.string.errPasswordRequired));
            edtPassword.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getActivity().getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            requestFocus(edtPassword);
            return false;
        }else if(edtPassword.getText().toString().length()<6 || edtPassword.getText().toString().length()>30){
            //edtPassword.setEnabled(true);
            edtPassword.setError(getResources().getString(R.string.errPasswordRange));
            edtPassword.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getActivity().getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            requestFocus(edtPassword);
            return false;
        } else {
            edtPassword.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getActivity().getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            //  edtPassword.setEnabled(false);
        }
        return true;
    }
    private boolean validateNewPassword() {
        if (edtNewPassword.getText().toString().trim().isEmpty()) {
            //  edtPassword.setEnabled(true);
            edtNewPassword.setError(getResources().getString(R.string.errPasswordRequired));
            edtNewPassword.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getActivity().getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            requestFocus(edtNewPassword);
            return false;
        }else if(edtNewPassword.getText().toString().length()<6 || edtNewPassword.getText().toString().length()>30){
            //edtPassword.setEnabled(true);
            edtNewPassword.setError(getResources().getString(R.string.errPasswordRange));
            edtNewPassword.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getActivity().getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            requestFocus(edtNewPassword);
            return false;
        } else {
            edtNewPassword.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getActivity().getApplicationContext(), android.R.color.transparent),
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
                    ContextCompat.getColor(getActivity().getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            requestFocus(edtConPassword);
            return false;
        }else if(edtConPassword.getText().toString().length()<6 || edtConPassword.getText().toString().length()>30){
            // edtPasswordConfirm.setEnabled(true);
            edtConPassword.setError(getResources().getString(R.string.errConPasswordRange));
            edtConPassword.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getActivity().getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            requestFocus(edtConPassword);
            return false;
        }
        else if (!edtNewPassword.getText().toString().trim().equals(confirmPassword)) {
            edtConPassword.setError(getResources().getString(R.string.errConfirmPasswordMismatch));
            edtConPassword.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getActivity().getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            requestFocus(edtConPassword);
            return false;
        } else {
            edtConPassword.getBackground().mutate().setColorFilter(
                    ContextCompat.getColor(getActivity().getApplicationContext(), android.R.color.transparent),
                    PorterDuff.Mode.SRC_ATOP);
            //  edtPasswordConfirm.setEnabled(false);
        }
        return true;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.tvUpdate)
    public void onViewClicked() {
        changePW();
    }
}
