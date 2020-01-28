package wallsplash.ankitray.com.details;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import wallsplash.ankitray.com.ApplyActivity;
import wallsplash.ankitray.com.bean.FavouriteBean;
import wallsplash.ankitray.com.bean.RelatedBean;
import wallsplash.ankitray.com.portfolio.PortfolioFragment;
import wallsplash.ankitray.com.retrofit.Config;
import wallsplash.ankitray.com.retrofit.RestClient;
import wallsplash.ankitray.com.utils.SharedObjects;
import wallsplash.ankitray.com.wallsplash.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements RelatedPhotosAdapter.OnPhotoSelectedListner {


    @BindView(R.id.ivUserProfile)
    CircleImageView ivUserProfile;
    @BindView(R.id.tvDesc)
    TextView tvDesc;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.cv_Share)
    ImageView cvShare;
    @BindView(R.id.cv_like)
    CheckBox cvLike;
    @BindView(R.id.ivPhoto)
    ImageView ivPhoto;
    @BindView(R.id.cv_download)
    ImageView cvDownload;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.rvRelated)
    RecyclerView rvRelated;
    @BindView(R.id.ll_portfolio)
    LinearLayout ll_portfolio;
    Unbinder unbinder;
    String photoId;
    String username;
    String sharlink;
    String alt_description;
    String uid;
    SharedObjects sharedObjects;
    Bitmap anImage;

    private ArrayList<FavouriteBean> favouriteList = new ArrayList<>();
    private DatabaseReference databaseReference;

    private RelatedPhotosAdapter relatedPhotosAdapter;
    private ArrayList<RelatedBean> relatedPhotoslist = new ArrayList<>();

    private ProgressDialog progressDialog;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoId = getArguments().getString(Config.photoid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        sharedObjects=new SharedObjects(getActivity());
        uid=sharedObjects.getUserID();
        ProgressDialogSetup();
        photoId = getArguments().getString(Config.photoid,"");
        /*Bundle bundle=  this.getArguments();
        photoId =  bundle.getString(Config.photoid);*/
     //   photoId = getArguments().getString(Config.favphotoid);
        getPhotosById();

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

   /* @Override
    public void onResume() {
        super.onResume();
        photoId = getArguments().getString(Config.photoid);
        getPhotosById();
    }
*/
    /*@Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        photoId = getArguments().getString(Config.photoid);
        getPhotosById();
    }*/

    public static DetailFragment newInstance(String ID) {
        DetailFragment exploreDetailFragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(Config.photoid, ID);
        exploreDetailFragment.setArguments(args);
        return exploreDetailFragment;
    }

    private void getPhotosById() {
        progressDialog.show();
        Call<JsonElement> call1 = RestClient.post().getPhotosById(photoId, Config.unsplash_access_key);
        call1.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                progressDialog.dismiss();
                relatedPhotoslist.clear();
                Log.e("photobyid", response.body().toString());
                if (response.isSuccessful()) {

                    JSONObject json2 = null;
                    try {
                        json2 = new JSONObject(response.body().toString());
                        if (json2.length() > 0) {


                            final String id = json2.getString("id");
                            alt_description = json2.getString("alt_description");

                            JSONObject object = json2.getJSONObject("urls");
                            final String url = object.getString("regular");
                            JSONObject jsonObjectlink = json2.getJSONObject("links");
                            sharlink = jsonObjectlink.getString("html");
                            JSONObject objectUser = json2.getJSONObject("user");
                            JSONObject objectUserProfile = objectUser.getJSONObject("profile_image");
                            String userprofile = objectUserProfile.getString("large");
                            username = objectUser.getString("username");

                            String name = objectUser.getString("name");


                            Glide.with(getActivity()).load(url)
                                    .thumbnail(0.5f)
                                    .placeholder(R.drawable.ic_placeholder_photos)
                                    .error(R.drawable.ic_placeholder_photos)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(ivPhoto);
                            Glide.with(getActivity()).load(userprofile)
                                    .thumbnail(0.5f)
                                    .placeholder(R.drawable.ic_user)
                                    .error(R.drawable.ic_user)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(ivUserProfile);
                            tvUserName.setText(name);
                            tvDesc.setText(alt_description);

                            if (FirebaseAuth.getInstance().getCurrentUser()==null){

                            }else {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("favourites").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
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

                                    if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                                        Toast.makeText(getActivity(), "login require", Toast.LENGTH_SHORT).show();
                                        compoundButton.setChecked(false);
                                        return;
                                    }
                                    String uid;
                                    SharedObjects sharedObjects;
                                    sharedObjects = new SharedObjects(getActivity());
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


                            JSONObject objectRelated = json2.getJSONObject("related_collections");
                            JSONArray array = objectRelated.getJSONArray("results");
                            if (array.length() > 0) {
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject jsonObject = array.getJSONObject(i);

                                    JSONObject jsonObject1 = jsonObject.getJSONObject("cover_photo");
                                    JSONObject objectCoverPhoto = jsonObject1.getJSONObject("urls");
                                    String coverUrl = objectCoverPhoto.getString("regular");
                                    String idRelated = jsonObject1.getString("id");
                                    relatedPhotoslist.add(new RelatedBean(idRelated, coverUrl));

                                }
                                bindRelatedData();
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


    private void bindRelatedData() {

        if (relatedPhotoslist.size() > 0) {

            relatedPhotosAdapter = new RelatedPhotosAdapter(getActivity(), relatedPhotoslist);
            relatedPhotosAdapter.setOnCategorySelectedListner(this);
            rvRelated.setAdapter(relatedPhotosAdapter);
            //rvNewPhotos.setAdapter(newPhotosAdapter);
        }

    }

    private void loadFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped) { //fragment not in back stack, create it.
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(R.id.fl_container, fragment, null);
            ft.hide(DetailFragment.this);
            ft.addToBackStack(backStateName);
            ft.commit();
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
    @OnClick({R.id.ivUserProfile, R.id.cv_Share, R.id.cv_download, R.id.ll_portfolio, R.id.ivBack,R.id.ivPhoto})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivUserProfile:
                break;
            case R.id.cv_Share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, sharlink);
                startActivity(Intent.createChooser(shareIntent, alt_description));
                break;

            case R.id.cv_download:
                anImage      = ((BitmapDrawable) ivPhoto.getDrawable()).getBitmap();
                saveImageToExternalStorage(anImage);
                Toast.makeText(getActivity(), "Download successfuly", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ivBack:
                getActivity().onBackPressed();
                break;
                case R.id.ivPhoto:
                    Intent intent=new Intent(getActivity(),ApplyActivity.class);
                    intent.putExtra("photoid",photoId);
                    startActivity(intent);
                break;
            case R.id.ll_portfolio:
                PortfolioFragment portfolioFragment = PortfolioFragment.newInstance(username);
                loadFragment(portfolioFragment);
                break;
        }
    }

    @Override
    public void setOnPhotoSelatedListner(int position, RelatedBean relatedBean) {
        photoId = relatedBean.getId();
        getPhotosById();
    }

    public void refreshData(Bundle arguments) {
        photoId = arguments.getString(Config.photoid,"");
        getPhotosById();
    }
}
