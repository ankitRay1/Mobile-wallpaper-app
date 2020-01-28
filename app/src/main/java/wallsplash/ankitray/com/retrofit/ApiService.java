package wallsplash.ankitray.com.retrofit;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    String BASEURL = "https://api.unsplash.com/";

    @GET("collections/{id}/photos")
    Call<JsonElement> getTrendingPhotosbyId(@Path("id") String id, @Query("page") Integer page, @Query("per_page") Integer perPage,@Query("client_id") String client_id/*, @Query("order_by") String orderBy*/);

    @GET("collections")
    Call<JsonElement> getPhotos(@Query("page") Integer page, @Query("per_page") Integer perPage, @Query("client_id") String client_id/*, @Query("order_by") String orderBy*/);

    @GET("photos/{id}")
    Call<JsonElement> getPhotosById(@Path("id") String id, @Query("client_id") String client_id/*, @Query("order_by") String orderBy*/);

    @GET("collections/{id}/photos")
    Call<JsonElement> getNewPhotos(@Path("id") String id, @Query("client_id") String client_id/*, @Query("order_by") String orderBy*/);

    @GET("collections")
    Call<JsonElement> getExplore(@Query("page") Integer page, @Query("per_page") Integer perPage, @Query("client_id") String client_id/*, @Query("order_by") String orderBy*/);

     @GET("collections")
    Call<JsonElement> getTrending(@Query("client_id") String client_id);

     @GET("search/photos")
    Call<JsonElement> getSearch(@Query("query") String query,@Query("page") Integer page, @Query("per_page") Integer perPage,@Query("client_id") String client_id);

   @GET("collections/{id}")
    Call<JsonElement> getExploreCat(@Path("id") String id,@Query("client_id") String client_id);

     @GET("photos/random")
    Call<JsonElement> getRandom(@Query("count") String count,@Query("client_id") String client_id);
   // https://api.unsplash.com/photos/random?count=5&client_id=b723d80217a350112f8754ec0e380fe9f22437db368c41573f695ace10ce031a

    @GET("photos/{id}/download")
    Call<JsonElement> getDownloadPhoto(@Path("id") String id,@Query("client_id") String client_id);

    @GET("users/{username}")
    Call<JsonElement> getPortfolio(@Path("username") String users,@Query("client_id") String client_id);
}

