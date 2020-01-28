package wallsplash.ankitray.com.explore;


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

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wallsplash.ankitray.com.bean.ExploreBean;
import wallsplash.ankitray.com.exploredetail.ExploreDetailFragment;
import wallsplash.ankitray.com.retrofit.Config;
import wallsplash.ankitray.com.retrofit.RestClient;
import wallsplash.ankitray.com.wallsplash.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends Fragment implements ExploreAdapter.OnExploreSelectedListner, ExploreAdapter.OnLoadMoreListener {


    @BindView(R.id.rvExplore)
    RecyclerView rvExplore;
    Unbinder unbinder;
    ProgressDialog progressDialog;
    private ExploreAdapter exploreAdapter;
    private ArrayList<ExploreBean> explorelist = new ArrayList<>();
    private int per_page = 1;

    public ExploreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        unbinder = ButterKnife.bind(this, view);

        ProgressDialogSetup();
        exploreAdapter = new ExploreAdapter(getActivity(), explorelist,rvExplore);
        exploreAdapter.setOnExploreSelectedListner(this);
        exploreAdapter.setOnLoadMoreListener(this);
        rvExplore.setAdapter(exploreAdapter);

        getExplore(per_page);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    public void ProgressDialogSetup() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
    }
    private void getExplore(final int per_page) {
        if (per_page == 1) {
            progressDialog.show();
        }
        Call<JsonElement> call1 = RestClient.post().getExplore(per_page,20, Config.unsplash_access_key);
        call1.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                //  progressDialog.dismiss();
                if (per_page == 1) {
                    progressDialog.dismiss();
                }
                if (response.body() == null) {
                    exploreAdapter.setLoaded();
                    exploreAdapter.notifyDataSetChanged();
                }

                Log.e("Explore", response.body().toString());
                if (response.isSuccessful()) {

                   /* List<CollectionBean> photos = response.body();
                    Log.d("Photos", "Photos Fetched " + photos.size());
                    //add to adapter
                    *//* page++;*//*
                    collectionAdapter.addPhotos(photos);
                    rvCollection.setAdapter(collectionAdapter);*/
                    //  bindLatestAdapternews();


                    JSONArray jsonArr = null;
                    try {
                        jsonArr = new JSONArray(response.body().toString());
                        if (per_page == 1) {

                            explorelist.clear();
                            if (jsonArr.length() > 0) {
                                for (int i = 0; i < jsonArr.length(); i++) {
                                    JSONObject json2 = jsonArr.getJSONObject(i);
                                    String id = json2.getString("id");
                                    String title = json2.getString("title");
                                    explorelist.add(new ExploreBean(id,title));

                                }
                                rvExplore.setAdapter(exploreAdapter);
                            }else {
                                exploreAdapter.setLoaded();
                                exploreAdapter.notifyDataSetChanged();
                            }

                        }else {
                            if (jsonArr.length() > 0) {
                                explorelist.remove(explorelist.size() - 1);
                                exploreAdapter.notifyItemRemoved(explorelist.size());
                                for (int i = 0; i < jsonArr.length(); i++) {
                                    JSONObject json2 = jsonArr.getJSONObject(i);
                                    String id = json2.getString("id");
                                    String title = json2.getString("title");
                                    explorelist.add(new ExploreBean(id,title));
                                    exploreAdapter.notifyDataSetChanged();
                                    exploreAdapter.setLoaded();
                                }

                            }else {
                                exploreAdapter.setLoaded();
                                exploreAdapter.notifyDataSetChanged();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }else {
                    if (per_page == 1) {
                        progressDialog.dismiss();
                    }

                    explorelist.remove(explorelist.size() - 1);
                    exploreAdapter.notifyItemRemoved(explorelist.size());
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                // progressDialog.dismiss();

            }
        });

    }

    private void loadFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped) { //fragment not in back stack, create it.
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(R.id.fl_container, fragment, null);
            ft.hide(ExploreFragment.this);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    @Override
    public void setOnExploreSelatedListner(int position, ExploreBean dataBean) {

        //((MainActivity) getActivity()).loadFragment(new ExploreDetailFragment());

        ExploreDetailFragment newsDetailsFragment = ExploreDetailFragment.newInstance(dataBean.getId());
        loadFragment(newsDetailsFragment);
      //  ((MainActivity) getActivity()).loadFragment(newsDetailsFragment);
    }

    @Override
    public void onLoadMore() {
        Log.e("haint", "Load More");
        if (!exploreAdapter.isLoading) {
            explorelist.add(null);
            exploreAdapter.notifyDataSetChanged();
            per_page++;
            getExplore(per_page);
        }
    }
}
