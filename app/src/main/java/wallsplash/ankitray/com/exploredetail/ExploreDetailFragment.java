package wallsplash.ankitray.com.exploredetail;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wallsplash.ankitray.com.bean.ExploreBean;
import wallsplash.ankitray.com.bean.ExploreCatBean;
import wallsplash.ankitray.com.details.DetailFragment;
import wallsplash.ankitray.com.retrofit.Config;
import wallsplash.ankitray.com.retrofit.RestClient;
import wallsplash.ankitray.com.wallsplash.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreDetailFragment extends Fragment implements ExplorePhotoByIdAdapter.OnCategorybyidSelectedListner, ExplorePhotoByIdAdapter.OnLoadMoreListener, ExploreCatAdapter.OnCategorySelectedListner {


    @BindView(R.id.llNorecord)
    LinearLayout llNorecord;
    @BindView(R.id.rvExplorePhotoById)
    RecyclerView rvExplorePhotoById;
    @BindView(R.id.rvExploreCat)
    RecyclerView rvExploreCat;
    @BindView(R.id.tvCat)
    TextView tvCat;
    @BindView(R.id.edtSearch)
    EditText edtSearch;
    Unbinder unbinder;
    ProgressDialog progressDialog;
    private ExplorePhotoByIdAdapter explorePhotoByIdAdapter;
    private ArrayList<ExploreBean> explorePhotosByIdList = new ArrayList<>();

    private ExploreCatAdapter exploreCatAdapter;
    private ArrayList<ExploreCatBean> exploreCatlist = new ArrayList<>();

    String collectionId;
    String exploretitle;
    private int per_page = 1;
    public ExploreDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_explore_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        collectionId = getArguments().getString(Config.collectionid);

        ProgressDialogSetup();
        getExploreCat();
        edtSearch.addTextChangedListener(new TextWatcher() {
                                             @Override
                                             public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                             }

                                             @Override
                                             public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                                 for (int j = 0; j < exploreCatlist.size(); j++) {
                                                     if (charSequence.toString().equals(exploreCatlist.get(j).getTitle())) {
                                                         // rvExploreCat.setVisibility(View.VISIBLE);
                                                         exploreCatlist.get(j).setSelected(true);
                                                     } else {
                                                         // rvExploreCat.setVisibility(View.GONE);
                                                         exploreCatlist.get(j).setSelected(false);
                                                     }
                                                 }
                                                 exploreCatAdapter.notifyDataSetChanged();
                                             }

                                             @Override
                                             public void afterTextChanged(Editable editable) {

                                             }
                                         }
        );

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    per_page=1;
                    exploretitle=edtSearch.getText().toString();
                    tvCat.setText(exploretitle);

                  /*  for (int i = 0; i < exploreCatlist.size(); i++) {
                        if (edtSearch.equals(exploreCatlist.get(i).getTitle())) {
                            // rvExploreCat.setVisibility(View.VISIBLE);
                            exploreCatlist.get(i).setSelected(true);
                        } else {
                            // rvExploreCat.setVisibility(View.GONE);
                            exploreCatlist.get(i).setSelected(false);
                        }
                    }*/
                    getExplorePhotosById(per_page);
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
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
    public static ExploreDetailFragment newInstance(String ID) {
        ExploreDetailFragment exploreDetailFragment = new ExploreDetailFragment();
        Bundle args = new Bundle();
        args.putString(Config.collectionid, ID);
        exploreDetailFragment.setArguments(args);
        return exploreDetailFragment;
    }
    private void getExplorePhotosById(final int per_page) {
        if (per_page == 1) {
            progressDialog.show();
        }
        Call<JsonElement> call1 = RestClient.post().getSearch(exploretitle,per_page,30,Config.unsplash_access_key);
        call1.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                //  progressDialog.dismiss()
                if (per_page == 1) {
                    progressDialog.dismiss();
                }
                if (response.body() == null) {
                    explorePhotoByIdAdapter.setLoaded();
                    explorePhotoByIdAdapter.notifyDataSetChanged();
                }

                // explorePhotosByIdList.clear();
                Log.e("FeatureNews", response.body().toString());
                if (response.isSuccessful()) {

                    // explorePhotosByIdList.clear();
                    JSONObject jsonArr = null;
                    try {
                        jsonArr = new JSONObject(response.body().toString());

                        JSONArray json = jsonArr.getJSONArray("results");
                        if (per_page == 1) {
                            explorePhotosByIdList.clear();
                            if (json.length() > 0) {

                                for (int i = 0; i < json.length(); i++) {
                                    JSONObject json2 = json.getJSONObject(i);
                                    String id = json2.getString("id");
                                    String description=json2.getString("description");

                                    JSONObject object = json2.getJSONObject("urls");
                                    String url = object.getString("regular");
                                    explorePhotosByIdList.add(new ExploreBean(id, description, url));

                                }
                                bindExplorePhotosByIdAdapternews();
                                //  rvTrending.setAdapter(trendingAdapter);

                            }else {
                                if (response.body() == null) {
                                    explorePhotoByIdAdapter.setLoaded();
                                    explorePhotoByIdAdapter.notifyDataSetChanged();
                                }else {
                                    rvExplorePhotoById.setVisibility(View.GONE);
                                    llNorecord.setVisibility(View.VISIBLE);
                                }


                            }
                        }else {
                            if (json.length() > 0) {
                                explorePhotosByIdList.remove(explorePhotosByIdList.size()-1);
                                explorePhotoByIdAdapter.notifyItemRemoved(explorePhotosByIdList.size());
                                for (int i = 0; i < json.length(); i++) {
                                    JSONObject json2 = json.getJSONObject(i);
                                    String id = json2.getString("id");
                                    String description=json2.getString("description");

                                    JSONObject object = json2.getJSONObject("urls");
                                    String url = object.getString("regular");
                                    explorePhotosByIdList.add(new ExploreBean(id, description, url));
                                    explorePhotoByIdAdapter.setLoaded();
                                    explorePhotoByIdAdapter.notifyDataSetChanged();
                                }
                            }else {
                                explorePhotoByIdAdapter.setLoaded();
                                explorePhotoByIdAdapter.notifyDataSetChanged();
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }else {
                    if (per_page == 1) {
                        progressDialog.dismiss();
                    }

                    explorePhotosByIdList.remove(explorePhotosByIdList.size() - 1);
                    explorePhotoByIdAdapter.notifyItemRemoved(explorePhotosByIdList.size());

                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                // progressDialog.dismiss();

            }
        });

    }
    private void bindExplorePhotosByIdAdapternews() {

        if (explorePhotosByIdList.size() > 0){
            llNorecord.setVisibility(View.GONE);
            rvExplorePhotoById.setVisibility(View.VISIBLE);
            explorePhotoByIdAdapter = new ExplorePhotoByIdAdapter(getActivity(), explorePhotosByIdList,rvExplorePhotoById);
            explorePhotoByIdAdapter.setOnCategorybyidSelectedListner(this);
            explorePhotoByIdAdapter.setOnLoadMoreListener(this);
            rvExplorePhotoById.setHasFixedSize(true);
            rvExplorePhotoById.setItemAnimator(null);
          //  StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
            //rvExplorePhotoById.setLayoutManager(layoutManager);

            rvExplorePhotoById.setAdapter(explorePhotoByIdAdapter);
        }else {
            llNorecord.setVisibility(View.VISIBLE);
            rvExplorePhotoById.setVisibility(View.GONE);
        }

    }
    private void getExploreCat() {

        progressDialog.show();
        Call<JsonElement> call1 = RestClient.post().getExploreCat(collectionId,Config.unsplash_access_key);
        call1.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                progressDialog.dismiss();

                Log.e("Explorcat", response.body().toString());

                if (response.isSuccessful()) {

                    exploreCatlist.clear();
                    JSONObject jsonArr = null;
                    try {
                        jsonArr = new JSONObject(response.body().toString());

                        JSONArray jsonArray=jsonArr.getJSONArray("tags");

                        if (jsonArr.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject json2 = jsonArray.getJSONObject(i);
                                String title = json2.getString("title");
                                String capsWordTitle = title.substring(0, 1).toUpperCase() + title.substring(1);


                                exploreCatlist.add(new ExploreCatBean(capsWordTitle,false));

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
    private void bindCategoryAdapter() {
        if (exploreCatlist.size() > 0){
            exploreCatAdapter = new ExploreCatAdapter(getActivity(), exploreCatlist);
            rvExploreCat.setAdapter(exploreCatAdapter);
            exploretitle=exploreCatlist.get(0).getTitle();
            tvCat.setText(exploretitle);

            edtSearch.setText(exploretitle);

            exploreCatlist.get(0).setSelected(true);
        }else {

        }

        exploreCatAdapter.setOnCategorySelectedListner(this);

        exploreCatAdapter.notifyDataSetChanged();

        getExplorePhotosById(per_page);
    }
    @OnClick(R.id.rvExplorePhotoById)
    public void onViewClicked() {
    }
    private void loadFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped) { //fragment not in back stack, create it.
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.fl_container, fragment, null);
            ft.hide(ExploreDetailFragment.this);
            ft.addToBackStack(backStateName);
            ft.commit();
        }else {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(R.id.fl_container, fragment, null);
            ft.hide(ExploreDetailFragment.this);
            if (fragment instanceof DetailFragment){

            }else {
                ft.addToBackStack(backStateName);
            }
            //ft.addToBackStack(backStateName);
            ft.commit();

        }
    }
    @Override
    public void setOnCategorybyidSelatedListner(int position, ExploreBean exploreBean) {
        DetailFragment newsDetailsFragment = DetailFragment.newInstance(exploreBean.getId());
        loadFragment(newsDetailsFragment);
       /* Bundle bundle=new Bundle();
        bundle.putString(Config.photoid,exploreBean.getId());
        DetailFragment newsDetailsFragment  = new DetailFragment();
        newsDetailsFragment.setArguments(bundle);
        loadFragment(newsDetailsFragment);*/
    }

    @Override
    public void onLoadMore() {
        Log.e("haint", "Load More");
        if (!explorePhotoByIdAdapter.isLoading) {
            explorePhotosByIdList.add(null);
            explorePhotoByIdAdapter.notifyDataSetChanged();
            per_page++;
            getExplorePhotosById(per_page);
        }
    }

    @Override
    public void setOnCategorySelatedListner(int position, ExploreCatBean trendingBean) {
        per_page = 1;
        for (int i = 0; i < exploreCatlist.size(); i++) {
            exploreCatlist.get(i).setSelected(false);
        }
        if (exploreCatlist.size()>0){

            exploreCatAdapter.notifyDataSetChanged();
            exploretitle= trendingBean.getTitle();
             tvCat.setText(trendingBean.getTitle());
             edtSearch.setText(trendingBean.getTitle());

            getExplorePhotosById(per_page);
            trendingBean.setSelected(true);
        }
    }
}
