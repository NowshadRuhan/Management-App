package com.capsulestudio.schoolmanagement.Api;

import com.capsulestudio.schoolmanagement.Model.Result;
import com.capsulestudio.schoolmanagement.Model.UserLogin;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Shuvo on 2/27/2018.
 */

public interface ApiService {

    //The register call
    @FormUrlEncoded
    @POST("registeruser")
    Call<Result> createUser(
            @Field("first_name") String first_name,
            @Field("last_name") String last_name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("mobile") String mobile,
            @Field("school_name") String school_name,
            @Field("status") String stattus,
            @Field("reg_date") String reg_date);


    //the signin call
    @FormUrlEncoded
    @POST("loginUser")
    Call<Result> userLogin(
            @Field("email") String email,
            @Field("password") String password);


    //updating user
    @FormUrlEncoded
    @POST("updateuser/{id_user}")
    Call<Result> updateUser(
            @Path("id_user") int id_user,
            @Field("first_name") String first_name,
            @Field("last_name") String last_name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("mobile") String mobile,
            @Field("school_name") String  school_name
    );

    //deleting user
    @DELETE("deleteuser/{id_user}")
    Call<Result> deleteUser(
            @Path("id_user") int id_user);


    //get all admins
    @GET("allusers")
    Call<Result> getUsers();

}
