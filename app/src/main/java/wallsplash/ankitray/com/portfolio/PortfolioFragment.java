package wallsplash.ankitray.com.portfolio;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wallsplash.ankitray.com.bean.PortfolioBean;
import wallsplash.ankitray.com.details.DetailFragment;
import wallsplash.ankitray.com.retrofit.Config;
import wallsplash.ankitray.com.retrofit.RestClient;
import wallsplash.ankitray.com.wallsplash.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PortfolioFragment extends Fragment implements PortfolioPhotosAdapter.OnPhotoSelectedListner {

    @BindView(R.id.tvUsername)
    TextView tvUsername;
    @BindView(R.id.tvLocation)
    TextView tvLocation;
    @BindView(R.id.tvFollowingCount)
    TextView tvFollowingCount;
    @BindView(R.id.tvFollowersCount)
    TextView tvFollowersCount;
    @BindView(R.id.tvDownloadCount)
    TextView tvDownloadCount;
    @BindView(R.id.tvPortfolio)
    TextView tvPortfolio;
    @BindView(R.id.cvProfile)
    CircleImageView cvProfile;
    @BindView(R.id.rvProtfolio)
    RecyclerView rvProtfolio;
    Unbinder unbinder;
    private ProgressDialog progressDialog;
    String username;
    private PortfolioPhotosAdapter portfolioPhotosAdapter;
    private ArrayList<PortfolioBean> portfolioPhotoslist = new ArrayList<>();
    public PortfolioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_portfolio, container, false);
        unbinder = ButterKnife.bind(this, view);
        username = getArguments().getString(Config.username);
        ProgressDialogSetup();
        getPhotosById();
        return view;
    }

    public void ProgressDialogSetup() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
    }

    public static PortfolioFragment newInstance(String ID) {
        PortfolioFragment exploreDetailFragment = new PortfolioFragment();
        Bundle args = new Bundle();
        args.putString(Config.username, ID);
        exploreDetailFragment.setArguments(args);
        return exploreDetailFragment;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    private void getPhotosById() {
        progressDialog.show();
        Call<JsonElement> call1 = RestClient.post().getPortfolio(username, Config.unsplash_access_key);
        call1.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                progressDialog.dismiss();
                portfolioPhotoslist.clear();
                Log.e("photobyid", response.body().toString());
                if (response.isSuccessful()) {

                    JSONObject json2 = null;
                    try {
                        json2 = new JSONObject(response.body().toString());

                        if (json2.length() > 0) {


                            String id=json2.getString("id");
                            String location=json2.getString("location");
                            tvLocation.setText(location);
                            username=json2.getString("username");
                            tvUsername.setText(username);
                            String downloads=json2.getString("downloads");
                            tvDownloadCount.setText(downloads);
                            String followers_count=json2.getString("followers_count");
                            tvFollowersCount.setText(followers_count);
                            String following_count=json2.getString("following_count");
                            tvFollowingCount.setText(following_count);
                            JSONObject objectUserProfile=json2.getJSONObject("profile_image");
                            String userprofile=objectUserProfile.getString("large");

                            Glide.with(getActivity()).load(userprofile)
                                    .thumbnail(0.5f)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(cvProfile);

                           // tvDesc.setText(alt_description);
                        //    JSONObject objectRelated=json2.getJSONObject("photos");
                            JSONArray array=json2.getJSONArray("photos");
                            if (array.length() > 0) {
                                tvPortfolio.setVisibility(View.VISIBLE);
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject jsonObject = array.getJSONObject(i);

                                    String id1=jsonObject.getString("id");

                                    JSONObject objectCoverPhoto=jsonObject.getJSONObject("urls");
                                    String coverUrl=objectCoverPhoto.getString("regular");

                                    portfolioPhotoslist.add(new PortfolioBean(id1,coverUrl));
                                }
                                bindRelatedData();
                            }else {
                                tvPortfolio.setVisibility(View.GONE);
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

        if (portfolioPhotoslist.size() > 0){

            portfolioPhotosAdapter = new PortfolioPhotosAdapter(getActivity(), portfolioPhotoslist);
            portfolioPhotosAdapter.setOnCategorySelectedListner(this);
            rvProtfolio.setAdapter(portfolioPhotosAdapter);
            //rvNewPhotos.setAdapter(newPhotosAdapter);
        }

    }
    @OnClick({R.id.tvUsername, R.id.tvLocation})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvUsername:
                break;
            case R.id.tvLocation:
                break;
        }
    }

    private void loadFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped) { //fragment not in back stack, create it.
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(R.id.fl_container, fragment, null);
            ft.hide(PortfolioFragment.this);
           // ft.addToBackStack(backStateName);
            ft.commit();
        }
           else {
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.add(R.id.fl_container, fragment, null);
                ft.hide(PortfolioFragment.this);
                if (fragment instanceof DetailFragment) {

                } else {
                    ft.addToBackStack(backStateName);
                }
                //ft.addToBackStack(backStateName);
                ft.commit();
            }
    }
    @Override
    public void setOnPhotoSelatedListner(int position, PortfolioBean portfolioBean) {
        DetailFragment newsDetailsFragment = DetailFragment.newInstance(portfolioBean.getId());
        loadFragment(newsDetailsFragment);
       /* Bundle bundle=new Bundle();
        bundle.putString(Config.photoid,portfolioBean.getId());
        DetailFragment newsDetailsFragment  = new DetailFragment();
        newsDetailsFragment.setArguments(bundle);
        loadFragment(newsDetailsFragment);*/
    }
}
