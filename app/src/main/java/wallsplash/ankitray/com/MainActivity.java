package wallsplash.ankitray.com;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import wallsplash.ankitray.com.explore.ExploreFragment;
import wallsplash.ankitray.com.favourite.FavouriteFragment;
import wallsplash.ankitray.com.home.HomeFragment;
import wallsplash.ankitray.com.login.LoginActivity;
import wallsplash.ankitray.com.profile.ProfileFragment;
import wallsplash.ankitray.com.retrofit.Config;
import wallsplash.ankitray.com.utils.AppUtils;
import wallsplash.ankitray.com.utils.SharedObjects;
import wallsplash.ankitray.com.wallsplash.R;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.ivDrawer)
    ImageView ivDrawer;
    @BindView(R.id.txtX)
    TextView txtX;
    @BindView(R.id.ivProfile)
    CircleImageView ivProfile;
    @BindView(R.id.ivlogout)
    ImageView ivlogout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.AppBar)
    LinearLayout AppBar;
    @BindView(R.id.fl_container)
    FrameLayout flContainer;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;

    boolean clickAgainToExit = false;
    FirebaseStorage storage;
    private StorageReference storageRef;
    private String userId;
    private String username;
    FirebaseUser user;
    SharedObjects sharedObjects;
    String uid;
    Bitmap bitmap;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private boolean activityStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        // OneSignal.setSubscription(true);
        if (activityStarted
                && getIntent() != null
                && (getIntent().getFlags() & Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) != 0) {
            finish();
            return;
        }

        activityStarted = true;


        sharedObjects = new SharedObjects(MainActivity.this);
        loadFragment(new HomeFragment());
        ivProfile.setVisibility(View.VISIBLE);
        ivlogout.setVisibility(View.GONE);
        bottomNavigation.setOnNavigationItemSelectedListener(this);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity

                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();


                }
            }
        };


        if (user == null) {
            ivProfile.setVisibility(View.GONE);
            ivlogout.setVisibility(View.GONE);
          /*  bottomNavigation.getMenu().findItem(R.id.navigation_profile).setVisible(false);
            bottomNavigation.getMenu().findItem(R.id.navigation_favourite).setVisible(false);*/
        } else {
            //  ivlogout.setVisibility(View.VISIBLE);
            ivProfile.setVisibility(View.VISIBLE);
           /* bottomNavigation.getMenu().findItem(R.id.navigation_profile).setVisible(true);
            bottomNavigation.getMenu().findItem(R.id.navigation_favourite).setVisible(true);*/
        }
        Intent intent = getIntent();


        getprofileImage();
    }

    //sign out method
    private void getprofileImage() {

        storage = FirebaseStorage.getInstance();

        uid = "images/" + sharedObjects.getUserID();
        // if (intent.hasExtra("uid")) {
        //   String uid = intent.getStringExtra("uid");
        storageRef = storage.getReferenceFromUrl(Config.DatabasePATH).child((uid));
        // }


        try {
            final File localFile = File.createTempFile("images", "jpg");
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    ivProfile.setImageBitmap(bitmap);


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    exception.printStackTrace();
                }
            });
        } catch (IOException e) {
        }
    }

    public void signOut() {
        auth.signOut();
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
    }

    @OnClick({R.id.ivDrawer, R.id.ivlogout, R.id.bottom_navigation})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivDrawer:

                break;
            case R.id.ivlogout:
                sharedObjects.setCode(0);
                signOut();
                Intent intent = new Intent(MainActivity.this, StratupActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
                break;
            case R.id.bottom_navigation:
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            // Home
            case R.id.navigation_home:
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    ivlogout.setVisibility(View.GONE);
                    ivProfile.setVisibility(View.GONE);
                } else {
                    getprofileImage();
                    ivlogout.setVisibility(View.GONE);
                    ivProfile.setVisibility(View.VISIBLE);
                }
                loadFragment(new HomeFragment());
                break;
            case R.id.navigation_explore:
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    ivlogout.setVisibility(View.GONE);
                    ivProfile.setVisibility(View.GONE);
                } else {
                    getprofileImage();
                    ivlogout.setVisibility(View.GONE);
                    ivProfile.setVisibility(View.VISIBLE);
                }
                loadFragment(new ExploreFragment());
                break;
            case R.id.navigation_favourite:
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    ivlogout.setVisibility(View.GONE);
                    ivProfile.setVisibility(View.GONE);
                    sharedObjects.setCode(2);
                    startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 2);
                } else {
                    getprofileImage();
                    ivlogout.setVisibility(View.GONE);
                    ivProfile.setVisibility(View.VISIBLE);
                    loadFragment(new FavouriteFragment());
                }

                break;
            case R.id.navigation_profile:
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    ivlogout.setVisibility(View.GONE);
                    ivProfile.setVisibility(View.GONE);
                    sharedObjects.setCode(1);
                    startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 1);
                } else {
                    getprofileImage();
                    ivlogout.setVisibility(View.VISIBLE);
                    ivProfile.setVisibility(View.GONE);
                    loadFragment(new ProfileFragment());
                }
                break;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            getprofileImage();
            ivlogout.setVisibility(View.VISIBLE);
            ivProfile.setVisibility(View.GONE);
            loadFragment(new ProfileFragment());
            // loginStatus = sharedObjects.preferencesEditor.getPreference(AppConstants.STATUS);
           /* if (user == null) {
                //   startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 1);
                sharedObjects.setCode(1);
                startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 1);
            } else {
                loadFragment(new ProfileFragment());
            }*/
        }else if (resultCode==2){
            getprofileImage();
            ivlogout.setVisibility(View.GONE);
            ivProfile.setVisibility(View.VISIBLE);
            loadFragment(new FavouriteFragment());
           /* if (user == null) {
                //   startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 1);
                sharedObjects.setCode(2);
                startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 2);
            } else {
                loadFragment(new FavouriteFragment());
            }*/
        }else if (resultCode==0) {
            getprofileImage();
            ivProfile.setVisibility(View.VISIBLE);
            ivlogout.setVisibility(View.GONE);
            loadFragment(new HomeFragment());
        }
    }

   /* public void loadFragment(Fragment fragment) {
// Insert the fragment by replacing any existing fragment
        String backStateName = fragment.getClass().getName();

        FragmentManager fragmentManager = getSupportFragmentManager();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped) { //fragment not in back stack, create it.
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.fl_container, fragment, backStateName);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }*/
    public void loadFragment(Fragment fragment) {

        String backStateName = fragment.getClass().getName();
        String fragmentTag = backStateName;

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.fl_container, fragment, fragmentTag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
      //  drawer.closeDrawers();
    }
    public void onBackPressed() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            ivlogout.setVisibility(View.GONE);
            ivProfile.setVisibility(View.GONE);
        } else {
            ivProfile.setVisibility(View.VISIBLE);
            ivlogout.setVisibility(View.GONE);
            getprofileImage();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        // fragmentManager.popBackStackImmediate();
        if (fragmentManager.getBackStackEntryCount() == 1) {
            getprofileImage();
            if (clickAgainToExit) {

                super.onBackPressed();
                finish();
                return;
            }
            clickAgainToExit = true;
            AppUtils.ShortToast(MainActivity.this, getResources().getString(R.string.app_backpress));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    clickAgainToExit = false;
                }
            }, 2000);
        } else {
            getprofileImage();
            super.onBackPressed();
        }
    }

}
