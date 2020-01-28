package wallsplash.ankitray.com.home;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wallsplash.ankitray.com.MainActivity;
import wallsplash.ankitray.com.search.SearchFragment;
import wallsplash.ankitray.com.bean.PhotosBean;
import wallsplash.ankitray.com.bean.TrendingBean;
import wallsplash.ankitray.com.details.DetailFragment;
import wallsplash.ankitray.com.retrofit.Config;
import wallsplash.ankitray.com.retrofit.RestClient;
import wallsplash.ankitray.com.utils.SharedObjects;
import wallsplash.ankitray.com.wallsplash.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements NewPhotosAdapter.OnPhotoSelectedListner, TrendingAdapter.OnCategorySelectedListner, TrendingPhotoByIdAdapter.OnCategorybyidSelectedListner {


    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 1;
    private static final int REQUEST_PERMISSION_SETTING = 0;
    @BindView(R.id.edtSearch)
    EditText edtSearch;
    @BindView(R.id.cv_Share)
    ImageView cvShare;
    @BindView(R.id.cv_like)
    CheckBox cvLike;
    @BindView(R.id.cv_download)
    ImageView cvDownload;
    @BindView(R.id.ivUserProfile)
    CircleImageView ivUserProfile;
    Unbinder unbinder;
    @BindView(R.id.rvNewPhotos)
    RecyclerView rvNewPhotos;
    @BindView(R.id.rvTrendingphotosbyId)
    RecyclerView rvTrendingphotosbyId;
    @BindView(R.id.rvTrending)
    RecyclerView rvTrending;
    @BindView(R.id.ivRandom)
    ImageView ivRandom;
    @BindView(R.id.ivDownlod)
    ImageView ivDownlod;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.tvDesc)
    TextView tvDesc;
    String trendId;
    String sharlink;
    InterstitialAd mInterstitialAd;
    private NewPhotosAdapter newPhotosAdapter;
    private ArrayList<PhotosBean> newPhotoslist = new ArrayList<>();

    private TrendingAdapter trendingAdapter;
    private ArrayList<TrendingBean> trendingList = new ArrayList<>();

    private TrendingPhotoByIdAdapter trendingPhotoByIdAdapter;
    private ArrayList<TrendingBean> trendingPhotosByIdList = new ArrayList<>();

    private ArrayList<PhotosBean> randomList = new ArrayList<>();
    private ProgressDialog progressDialog;

    String url;
    Bitmap anImage;
    String randomPhotoId;
    String  alt_description;
    String  exploretitle;
    private AsyncTask mMyTask;
    private ProgressDialog mProgressDialog;
    private static final int PERMISSION_REQUEST_CODE = 1;
    String wantPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private AdView mAdView;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        // Initialize the progress dialog
        mAdView = view.findViewById(R.id.adView);
      /*  MobileAds.initialize(getActivity(),
                getString(R.string.admob_app_id));*/
        Banner();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                InterstitialAd();
            }
        }, 5000);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(true);
        // Progress dialog horizontal style
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // Progress dialog title
        mProgressDialog.setTitle("AsyncTask");
        // Progress dialog message
        mProgressDialog.setMessage("Please wait, we are downloading your image file...");
        if (checkPermission(wantPermission)) {
          //  Toast.makeText(getActivity(), "Permission already granted.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "Please request permission.", Toast.LENGTH_LONG).show();
        }
        if (!checkPermission(wantPermission)) {
            requestPermission(wantPermission);
        } else {
        //    Toast.makeText(getActivity(),"Permission already granted.", Toast.LENGTH_LONG).show();
        }
        //requestAppPermissions();
        ProgressDialogSetup();
        getRandom();
        getNewPhotos();
        getTrending();
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    exploretitle=edtSearch.getText().toString();

                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                    SearchFragment newsDetailsFragment = SearchFragment.newInstance(exploretitle);
                    loadFragment(newsDetailsFragment);
                    edtSearch.setText("");
                    return true;
                }
                return false;
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
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public  void Banner(){
        //mAdView = findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
            }

            @Override
            public void onAdOpened() {
            }

            @Override
            public void onAdLeftApplication() {
            }

            @Override
            public void onAdClosed() {
            }
        });


    }
    public void InterstitialAd(){
        mInterstitialAd = new InterstitialAd(getActivity());

        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.intertatial_ad_id));
        // mInterstitialAd.setAdUnitId("ca-app-pub-4906098412516661/9907689540");

        AdRequest adRequest = new AdRequest.Builder()
                .build();

        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        showInterstitial();
                    }
                }, 5000);

            }
        });
       /* mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });*/
    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();

        }


    }

    private void loadFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped) { //fragment not in back stack, create it.
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(R.id.fl_container, fragment, null);
            ft.hide(HomeFragment.this);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }
    @OnClick({R.id.edtSearch, R.id.cv_Share, R.id.cv_like, R.id.cv_download,R.id.ivRandom})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.edtSearch:
                break;
            case R.id.cv_Share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, sharlink);
                startActivity(Intent.createChooser(shareIntent,alt_description));
                break;
            case R.id.cv_like:
                break;
                case R.id.ivRandom:
                  //  ((MainActivity) getActivity()).loadFragment(new DetailFragment());

                    DetailFragment newsDetailsFragment = DetailFragment.newInstance(randomPhotoId);
                    loadFragment(newsDetailsFragment);
                   /* Bundle bundle=new Bundle();
                    bundle.putString(Config.photoid,randomPhotoId);
                    DetailFragment newsDetailsFragment  = new DetailFragment();
                    newsDetailsFragment.setArguments(bundle);
                    loadFragment(newsDetailsFragment);*/
                break;
            case R.id.cv_download:


                 anImage      = ((BitmapDrawable) ivRandom.getDrawable()).getBitmap();
                saveImageToExternalStorage(anImage);
                Toast.makeText(getActivity(), "Download successfuly", Toast.LENGTH_SHORT).show();

                break;
        }
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
            MediaScannerConnection.scanFile(getActivity(), new String[]{file.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });


    }


    private boolean checkPermission(String permission){
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(getActivity(), permission);
            if (result == PackageManager.PERMISSION_GRANTED){
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private void requestPermission(String permission){
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)){
            Toast.makeText(getActivity(), "Write external storage permission allows us to write data. \n" +
                    "                    Please allow in App Settings for additional functionality",Toast.LENGTH_LONG).show();
        }
        ActivityCompat.requestPermissions(getActivity(), new String[]{permission},PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                 /*   Toast.makeText(getActivity(), "Permission Granted. Now you can write data.",
                            Toast.LENGTH_LONG).show();*/
                } else {
                    Toast.makeText(getActivity(),"Permission Denied. You cannot write data.",
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    private void getRandom() {
        progressDialog.show();
        Call<JsonElement> call1 = RestClient.post().getRandom("30", Config.unsplash_access_key);
        call1.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                progressDialog.dismiss();
                //   randomList.clear();
              //  if (response.errorBody().toString())
//                    Log.e("errorbody",response.errorBody().toString());
                Log.e("random", response.body().toString());
                if (response.isSuccessful()) {

                    JSONArray jsonArr = null;
                    try {
                        jsonArr = new JSONArray(response.body().toString());
                        if (jsonArr.length() > 0) {
                            for (int i = 0; i < jsonArr.length(); i++) {
                                JSONObject json2 = jsonArr.getJSONObject(i);
                                randomPhotoId=json2.getString("id");
                                alt_description=json2.getString("alt_description");

                                JSONObject object=json2.getJSONObject("urls");
                                url=object.getString("regular");

                                JSONObject objectUser=json2.getJSONObject("user");
                                JSONObject objectUserProfile=objectUser.getJSONObject("profile_image");
                                String userprofile=objectUserProfile.getString("large");

                                String name=objectUser.getString("name");
                                JSONObject jsonObject=json2.getJSONObject("links");
                                sharlink=jsonObject.getString("html");
                                randomList.add(new PhotosBean(randomPhotoId,url));
                                Glide.with(getActivity()).load(url)
                                        .thumbnail(0.5f)
                                        .placeholder(R.drawable.ic_place_holder)
                                        .error(R.drawable.ic_place_holder)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(ivRandom);

                                Glide.with(getActivity()).load(userprofile)
                                        .thumbnail(0.5f)
                                        .placeholder(R.drawable.ic_user)
                                        .error(R.drawable.ic_user)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(ivUserProfile);
                                /*Picasso.get()
                                        .load(userprofile)
                                        .into(ivUserProfile);*/
                                tvUserName.setText(name);
                                tvDesc.setText(alt_description);
                                if (FirebaseAuth.getInstance().getCurrentUser()==null){

                                }else {
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                    ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("favourites").child(randomPhotoId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (isVisible()) {
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
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }

                                cvLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                        if (FirebaseAuth.getInstance().getCurrentUser()==null){
                                            Toast.makeText(getActivity(), "login require", Toast.LENGTH_SHORT).show();
                                            compoundButton.setChecked(false);
                                            return;
                                        }
                                        String uid;
                                        SharedObjects sharedObjects;
                                        sharedObjects = new SharedObjects(getActivity());
                                        uid=sharedObjects.getUserID();
                                        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference()
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child("favourites")
                                                .child(randomPhotoId);

                                        if (b){

                                            databaseReference.child("Id").setValue(randomPhotoId);
                                            databaseReference.child("url").setValue(url);
                                        }else {
                                            databaseReference.child("Id").setValue(null);
                                            databaseReference.child("url").setValue(null);
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
                progressDialog.dismiss();

            }
        });


    }
    private void getNewPhotos() {
        Call<JsonElement> call1 = RestClient.post().getNewPhotos(Config.NEW_ID, Config.unsplash_access_key);
        call1.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                //  progressDialog.dismiss();
                newPhotoslist.clear();
                Log.e("FeatureNews", response.body().toString());
                if (response.isSuccessful()) {

                    JSONArray jsonArr = null;
                    try {
                        jsonArr = new JSONArray(response.body().toString());
                        if (jsonArr.length() > 0) {
                            for (int i = 0; i < jsonArr.length(); i++) {
                                JSONObject json2 = jsonArr.getJSONObject(i);
                                String id=json2.getString("id");

                                JSONObject object=json2.getJSONObject("urls");
                                String url=object.getString("regular");

                                JSONObject objectUser=json2.getJSONObject("user");
                                JSONObject objectUserProfile=objectUser.getJSONObject("profile_image");
                                String userprofile=objectUserProfile.getString("large");


                                newPhotoslist.add(new PhotosBean(id,url,userprofile));

                            }

                            bindTrendData();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                // progressDialog.dismiss();

            }
        });

    }
    private void getTrending() {

        progressDialog.show();
        Call<JsonElement> call1 = RestClient.post().getTrending(Config.unsplash_access_key);
        call1.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                  progressDialog.dismiss();

                Log.e("FeatureNews", response.body().toString());
                if (response.isSuccessful()) {

                    trendingList.clear();
                    JSONArray jsonArr = null;
                    try {
                        jsonArr = new JSONArray(response.body().toString());


                        if (jsonArr.length() > 0) {
                            for (int i = 0; i < jsonArr.length(); i++) {
                                JSONObject json2 = jsonArr.getJSONObject(i);
                                String id = json2.getString("id");
                                String title = json2.getString("title");
                                trendingList.add(new TrendingBean(id, title,false));

                            }
                            bindCategoryAdapter();
                          //  rvTrending.setAdapter(trendingAdapter);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                 progressDialog.dismiss();

            }
        });

    }
    private void getTrendPhotosById() {

        Call<JsonElement> call1 = RestClient.post().getTrendingPhotosbyId(trendId,1,12,Config.unsplash_access_key);
        call1.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                //  progressDialog.dismiss();

                Log.e("FeatureNews", response.body().toString());
                if (response.isSuccessful()) {

                    trendingPhotosByIdList.clear();
                    JSONArray jsonArr = null;
                    try {
                        jsonArr = new JSONArray(response.body().toString());


                        if (jsonArr.length() > 0) {
                            for (int i = 0; i < jsonArr.length(); i++) {
                                JSONObject json2 = jsonArr.getJSONObject(i);
                                String id=json2.getString("id");

                                JSONObject object=json2.getJSONObject("urls");
                                String url=object.getString("regular");
                                trendingPhotosByIdList.add(new TrendingBean(id, url));

                            }
                            bindTrendPhotosByIdAdapternews();
                          //  rvTrending.setAdapter(trendingAdapter);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                // progressDialog.dismiss();

            }
        });

    }

    private void bindCategoryAdapter() {
        if (trendingList.size() > 0){
            trendingAdapter = new TrendingAdapter(getActivity(), trendingList);
            rvTrending.setAdapter(trendingAdapter);
            trendId=trendingList.get(0).getId();
            trendingList.get(0).setSelected(true);
        }else {

        }
        trendingAdapter.setOnCategorySelectedListner(this);
        trendingAdapter.notifyDataSetChanged();

        getTrendPhotosById();
    }

    private void bindTrendPhotosByIdAdapternews() {

        if (trendingPhotosByIdList.size() > 0){
            trendingPhotoByIdAdapter = new TrendingPhotoByIdAdapter(getActivity(), trendingPhotosByIdList);
            trendingPhotoByIdAdapter.setOnCategorybyidSelectedListner(this);
            rvTrendingphotosbyId.setAdapter(trendingPhotoByIdAdapter);
        }

    }
    private void bindTrendData() {

        if (newPhotoslist.size() > 0){

            newPhotosAdapter = new NewPhotosAdapter(getActivity(), newPhotoslist);
            newPhotosAdapter.setOnCategorySelectedListner(this);
            rvNewPhotos.setAdapter(newPhotosAdapter);
            //rvNewPhotos.setAdapter(newPhotosAdapter);
        }

    }
    @Override
    public void setOnPhotoSelatedListner(int position, PhotosBean dataBean) {
        DetailFragment newsDetailsFragment = DetailFragment.newInstance(dataBean.getId());
        loadFragment(newsDetailsFragment);
        /*Bundle bundle=new Bundle();
        bundle.putString(Config.photoid,dataBean.getId());
        DetailFragment newsDetailsFragment  = new DetailFragment();
        newsDetailsFragment.setArguments(bundle);
        loadFragment(newsDetailsFragment);*/
    }

    @Override
    public void setOnCategorySelatedListner(int position, TrendingBean trendingBean) {
        for (int i = 0; i < trendingList.size(); i++) {
            trendingList.get(i).setSelected(false);
        }
        if (trendingList.size()>0){
            trendingAdapter.notifyDataSetChanged();
            trendId= trendingBean.getId();
           // setitem.setText(dataBean.getName());
          getTrendPhotosById();
            trendingBean.setSelected(true);
        }
    }

    @Override
    public void setOnCategorybyidSelatedListner(int position, TrendingBean trendingBean) {
        DetailFragment newsDetailsFragment = DetailFragment.newInstance(trendingBean.getId());
       // loadFragment(newsDetailsFragment);
        ((MainActivity) getActivity()).loadFragment(newsDetailsFragment);
       /* Bundle bundle=new Bundle();
        bundle.putString(Config.photoid,trendingBean.getId());
        DetailFragment newsDetailsFragment  = new DetailFragment();
        newsDetailsFragment.setArguments(bundle);
        loadFragment(newsDetailsFragment);*/
    }
}
