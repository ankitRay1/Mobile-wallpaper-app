package wallsplash.ankitray.com.search;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
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
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wallsplash.ankitray.com.bean.ExploreBean;
import wallsplash.ankitray.com.details.DetailFragment;
import wallsplash.ankitray.com.retrofit.Config;
import wallsplash.ankitray.com.retrofit.RestClient;
import wallsplash.ankitray.com.wallsplash.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements SearchAdapter.OnCategorybyidSelectedListner, SearchAdapter.OnLoadMoreListener {


    @BindView(R.id.rvSearch)
    RecyclerView rvSearch;
    @BindView(R.id.tvSearch)
    TextView tvSearch;
    @BindView(R.id.llNorecord)
    LinearLayout llNorecord;
    Unbinder unbinder;
    private SearchAdapter searchAdapter;
    private ArrayList<ExploreBean> searchList = new ArrayList<>();
    ProgressDialog progressDialog;
    private int per_page = 1;
    String exploretitle;
    @BindView(R.id.edtSearch)
    EditText edtSearch;
    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        unbinder = ButterKnife.bind(this, view);
        ProgressDialogSetup();
        exploretitle=getArguments().getString(Config.searchKey);
        edtSearch.setText(exploretitle);
        tvSearch.setText(exploretitle);
        search(per_page);
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    per_page=1;
                    exploretitle=edtSearch.getText().toString();
                    tvSearch.setText(exploretitle);
                    searchList.clear();
                    search(per_page);
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
    public static SearchFragment newInstance(String ID) {
        SearchFragment exploreDetailFragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(Config.searchKey, ID);
        exploreDetailFragment.setArguments(args);
        return exploreDetailFragment;
    }
    private void search(final int per_page) {
        if (per_page == 1) {
            progressDialog.show();
        }
        Call<JsonElement> call1 = RestClient.post().getSearch(exploretitle,per_page,30, Config.unsplash_access_key);
        call1.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                //  progressDialog.dismiss()
                if (per_page == 1) {
                    progressDialog.dismiss();
                }
                if (response.body() == null) {
                    searchAdapter.setLoaded();
                    searchAdapter.notifyDataSetChanged();
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
                            searchList.clear();
                            if (json.length() > 0) {

                                for (int i = 0; i < json.length(); i++) {
                                    JSONObject json2 = json.getJSONObject(i);
                                    String id = json2.getString("id");
                                    String description=json2.getString("description");

                                    JSONObject object = json2.getJSONObject("urls");
                                    String url = object.getString("regular");
                                    searchList.add(new ExploreBean(id, description, url));

                                }
                                bindExplorePhotosByIdAdapternews();
                                //  rvTrending.setAdapter(trendingAdapter);

                            }else {
                                if (response.body() == null) {
                                    rvSearch.setVisibility(View.GONE);
                                    llNorecord.setVisibility(View.VISIBLE);
                                    searchAdapter.setLoaded();
                                    searchAdapter.notifyDataSetChanged();
                                }else {
                                    rvSearch.setVisibility(View.GONE);
                                    llNorecord.setVisibility(View.VISIBLE);
                                }
                            }
                        }else {
                            if (json.length() > 0) {
                                searchList.remove(searchList.size()-1);
                                searchAdapter.notifyItemRemoved(searchList.size());
                                for (int i = 0; i < json.length(); i++) {
                                    JSONObject json2 = json.getJSONObject(i);
                                    String id = json2.getString("id");
                                    String description=json2.getString("description");

                                    JSONObject object = json2.getJSONObject("urls");
                                    String url = object.getString("regular");
                                    searchList.add(new ExploreBean(id, description, url));
                                    searchAdapter.setLoaded();
                                    searchAdapter.notifyDataSetChanged();
                                }
                            }else {
                                searchAdapter.setLoaded();
                                searchAdapter.notifyDataSetChanged();
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }else {
                    if (per_page == 1) {
                        progressDialog.dismiss();
                    }

                    searchList.remove(searchList.size() - 1);
                    searchAdapter.notifyItemRemoved(searchList.size());

                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                // progressDialog.dismiss();

            }
        });

    }
    private void bindExplorePhotosByIdAdapternews() {

        if (searchList.size() > 0){
            llNorecord.setVisibility(View.GONE);
            rvSearch.setVisibility(View.VISIBLE);
            searchAdapter = new SearchAdapter(getActivity(), searchList,rvSearch);
            searchAdapter.setOnCategorybyidSelectedListner(this);
            searchAdapter.setOnLoadMoreListener(this);
            //  StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
            //rvExplorePhotoById.setLayoutManager(layoutManager);

            rvSearch.setAdapter(searchAdapter);
        }else {
            llNorecord.setVisibility(View.VISIBLE);
            rvSearch.setVisibility(View.GONE);
        }

    }
    private void loadFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped) { //fragment not in back stack, create it.
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.fl_container, fragment, null);
            ft.hide(SearchFragment.this);
            ft.addToBackStack(backStateName);
            ft.commit();
        } else {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(R.id.fl_container, fragment, null);
            ft.hide(SearchFragment.this);
            if (fragment instanceof DetailFragment) {

            } else {
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
    }

    @Override
    public void onLoadMore() {
        Log.e("haint", "Load More");
        if (!searchAdapter.isLoading) {
            searchList.add(null);
            searchAdapter.notifyDataSetChanged();
            per_page++;
            search(per_page);
        }
    }
}
