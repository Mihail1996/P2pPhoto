package ist.meic.cmu.utils;


import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ServerService {

    @GET("/checkConnection")
    Call<Void> ping();

    @FormUrlEncoded
    @POST("/login")
    Call<Void> login(@Field("username") String username, @Field("password") String password);


    @FormUrlEncoded
    @POST("/cmu/createAlbum")
    Call<Void> createAlbum(@Field("name") String name, @Header("Cookie") String cookie);


    @FormUrlEncoded
    @POST("/cmu/createWifiAlbum")
    Call<Void> createWifiAlbum(@Field("name") String name, @Header("Cookie") String cookie);


    @FormUrlEncoded
    @POST("/addUser")
    Call<Void> register(@Field("username") String name, @Field("password") String password, @Field("token") String token);

    @POST("/logout")
    Call<Void> logout(@Header("Cookie") String cookie);

    @GET("/cmu/getUserAlbums")
    Call<ArrayList<String>> getUserAlbums(@Header("Cookie") String cookie);

    @GET("/cmu/getUserWifiAlbums")
    Call<ArrayList<String>> getUserWifiAlbums(@Header("Cookie") String cookie);

    @GET("/cmu/getAlbumCatalogs")
    Call<ArrayList<String>> getAlbumCatalogs(@Header("Cookie") String cookie, @Query("name") String name);

    @GET("/cmu/getMyAlbumCatalog")
    Call<String> getMyAlbumCatalog(@Header("Cookie") String cookie, @Query("name") String name);

    @GET("/cmu/hasWifiAlbumsInCommon")
    Call<Boolean> hasWifiAlbumsInCommon(@Header("Cookie") String cookie, @Query("username") String username, @Query("albumName") String albumName);

    @GET("/cmu/getDbToken")
    Call<String> getDbToken(@Header("Cookie") String cookie);

    @GET("{item}?dl=1")
    Call<String> getPhotos(@Path("item") String item);

    @FormUrlEncoded
    @POST("/cmu/addCatalogToAlbum")
    Call<Void> addCatalogToAlbum(@Header("Cookie") String cookie, @Field("url") String url, @Field("name") String name);

    @FormUrlEncoded
    @POST("/cmu/addUserToAlbum")
    Call<String> addUserToAlbum(@Header("Cookie") String cookie, @Field("username") String username, @Field("albumName") String albumName);

    @FormUrlEncoded
    @POST("/cmu/addUserToWifiAlbum")
    Call<String> addUserToWifiAlbum(@Header("Cookie") String cookie, @Field("username") String username, @Field("albumName") String albumName);


    @GET("/cmu/getLog")
    Call<ResponseBody> getLog(@Header("Cookie") String cookie);

    @GET("/cmu/users/{user}")
    Call<String> getUser(@Header("Cookie") String cookie, @Path("user") String user);

}
