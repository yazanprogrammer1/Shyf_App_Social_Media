package com.example.shyf_.apis

import com.example.shyf_.model.Comments
import com.example.shyf_.model.Followers
import com.example.shyf_.model.Notes
import com.example.shyf_.model.Posts
import com.example.shyf_.model.Request
import com.example.shyf_.model.Result
import com.example.shyf_.model.ResultComments
import com.example.shyf_.model.ResultFollow
import com.example.shyf_.model.ResultFollowers
import com.example.shyf_.model.ResultLikes
import com.example.shyf_.model.Result_AddNotes
import com.example.shyf_.model.Result_AddPost
import com.example.shyf_.model.Result_AddRequest
import com.example.shyf_.model.Result_AddStory
import com.example.shyf_.model.Storys
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("send")
    fun sendMessage(
        @HeaderMap header: HashMap<String, String>,
        @Body message: String
    ): Call<String>


    @GET("getPosts.php")
    fun getPosts(): Call<List<Posts>>

    @GET("getPostsByContry.php")
    fun getPostsByContry(
        @Query("nameContry") nameContry: String
    ): Call<List<Posts>>

    @FormUrlEncoded
    @POST("SignUp.php")
    fun SignUp(
        @Field("id") id: Int?,
        @Field("email") email: String?,
        @Field("password") password: String?,
        @Field("whatsappLink") whatsappLink: String?,
        @Field("facebookLink") facebookLink: String?,
        @Field("name") name: String?,
        @Field("information") information: String?,
        @Field("userImage") userImage: String?,
        @Field("userToken") userToken: String?,
        @Field("UserIdFb") userId: String?
    ): Call<Result>

    @FormUrlEncoded
    @POST("UpdateToken.php")
    fun UpdateToken(
        @Field("email") email: String?,
        @Field("password") password: String?,
        @Field("userToken") userToken: String?
    ): Call<Result>

    @FormUrlEncoded
    @POST("SignIn.php")
    fun SignIn(
        @Field("email") email: String?, @Field("password") password: String?
    ): Call<Result>

    @FormUrlEncoded
    @POST("UpdateUser.php")
    fun updateUser(
        @Field("id") id: Int,
        @Field("whatsappLink") whatsappLink: String?,
        @Field("facebookLink") facebookLink: String?,
        @Field("name") name: String?,
        @Field("information") description: String?
    ): Call<Result>

    @FormUrlEncoded
    @POST("UpdateUserImage.php")
    fun UpdateUserImage(
        @Field("id") id: Int, @Field("image") image: String?
    ): Call<Result>


    @FormUrlEncoded
    @POST("UpdateUserImageBackground.php")
    fun UpdateUserImageBackground(
        @Field("id") id: Int, @Field("backgroundImage") backgroundImage: String?
    ): Call<Result>


    @FormUrlEncoded
    @POST("InsertPosts.php")
    fun InsertPosts(
        @Field("country") country: String?,
        @Field("city") city: String?,
        @Field("street") street: String?,
        @Field("description") description: String?,
        @Field("price") price: Double?,
        @Field("TimeAdd") TimeAdd: String?,
        @Field("image") image: String?,
        @Field("userId") userId: String?
    ): Call<Result_AddPost>

    @FormUrlEncoded
    @POST("deletePostById.php")
    fun deletePostById(
        @Field("id") id: Int
    ): Call<Posts>

    @GET("getPostsOfUser.php")
    fun getPostsOfUser(
        @Query("user_id") user_id: Int
    ): Call<List<Posts>>

    @GET("getPostsUserIfFollow.php")
    fun getPostsUserIfFollow(
        @Query("user_id") user_id: Int
    ): Call<List<Posts>>

    @FormUrlEncoded
    @POST("updatePostSecand.php")
    fun updatePost(
        @Field("id") id: Int,
        @Field("country") country: String?,
        @Field("city") city: String?,
        @Field("street") street: String?,
        @Field("description") description: String?,
        @Field("image") image: String?
    ): Call<Posts>


    @FormUrlEncoded
    @POST("Insertfollowers.php")
    fun Insertfollowers(
        @Field("idUser") idUser: Int?, @Field("idUserFollow") idUserFollow: Int?
    ): Call<ResultFollowers>

    @FormUrlEncoded
    @POST("Deletefollowers.php")
    fun Deletefollowers(
        @Field("idUser") idUser: Int?, @Field("idUserFollow") idUserFollow: Int?
    ): Call<ResultFollowers>


    @GET("getFollowers.php")
    fun getFollowers(
        @Query("user_id") user_id: Int
    ): Call<List<Followers>>


    @GET("getFollowing.php")
    fun getFollowing(
        @Query("user_id") user_id: Int
    ): Call<List<Followers>>


    @FormUrlEncoded
    @POST("getnumberFollow.php")
    fun getnumberFollow(
        @Field("id") id: Int?
    ): Call<Result>

    @FormUrlEncoded
    @POST("getIfFollow.php")
    fun getIfFollow(
        @Field("idUser") idUser: Int?, @Field("idUserFollow") idUserFollow: Int?
    ): Call<ResultFollow>


    @FormUrlEncoded
    @POST("InsertSavedPost.php")
    fun InsertSavedPost(
        @Field("idUser") idUser: Int?,
        @Field("idPost") idPost: Int?,
        @Field("iduserPost") iduserPost: Int?
    ): Call<ResultFollowers>

    @FormUrlEncoded
    @POST("getIfSaved.php")
    fun getIfSaved(
        @Field("idUser") idUser: Int?,
        @Field("idPost") idPost: Int?,
        @Field("iduserPost") iduserPost: Int?
    ): Call<ResultFollowers>

    @FormUrlEncoded
    @POST("DeleteSavedPost.php")
    fun DeleteSavedPost(
        @Field("idUser") idUser: Int?,
        @Field("idPost") idPost: Int?,
        @Field("iduserPost") iduserPost: Int?
    ): Call<ResultFollowers>

    @GET("getSavedPosts.php")
    fun getSavedPosts(
        @Query("idUser") idUser: Int
    ): Call<List<Posts>>


    // like
    @FormUrlEncoded
    @POST("InsertLikes.php")
    fun InsertLikes(
        @Field("idUser") idUser: Int?,
        @Field("idPost") idPost: Int?,
        @Field("iduserPost") iduserPost: Int?
    ): Call<ResultLikes>

    @FormUrlEncoded
    @POST("DeleteLikedPost.php")
    fun DeleteLikedPost(
        @Field("idUser") idUser: Int?,
        @Field("idPost") idPost: Int?,
        @Field("iduserPost") iduserPost: Int?
    ): Call<ResultLikes>


    @FormUrlEncoded
    @POST("getIfLike.php")
    fun getIfLike(
        @Field("idUser") idUser: Int?,
        @Field("idPost") idPost: Int?,
        @Field("iduserPost") iduserPost: Int?
    ): Call<ResultLikes>


    @FormUrlEncoded
    @POST("InsertNewRequest.php")
    fun InsertNewRequest(
        @Field("userId") userId: Int?,
        @Field("postId") postId: Int?,
        @Field("count") count: Int?,
        @Field("totalPrice") totalPrice: Double?
    ): Call<Result_AddRequest>

    @GET("getRequestOfUser.php")
    fun getRequestOfUser(
        @Query("user_id") user_id: Int
    ): Call<List<Request>>

    @FormUrlEncoded
    @POST("deleteRequestById.php")
    fun deleteRequestById(
        @Field("id") id: Int
    ): Call<Request>


    @GET("getNoteForUser.php")
    fun getNoteForUser(
        @Query("user_id") user_id: Int
    ): Call<List<Notes>>

    @FormUrlEncoded
    @POST("InsertNote.php")
    fun InsertNote(
        @Field("userId") userId: Int?,
        @Field("note") note: String?
    ): Call<Result_AddNotes>

    @GET("getNotesUserIfFollow.php")
    fun getNotesUserIfFollow(
        @Query("user_id") user_id: Int
    ): Call<List<Notes>>


    //  add story user
    @FormUrlEncoded
    @POST("InsertStoryUser.php")
    fun insertStory(
        @Field("userId") userId: Int?,
        @Field("Subtitle") subtitle: String?,
        @Field("description") description: String?,
        @Field("image") image: String?,
        @Field("TimeAdd") TimeAdd: String?
    ): Call<Result_AddStory>

    @GET("getStoryForUser.php")
    fun getStoryForUser(
        @Query("user_id") user_id: Int
    ): Call<List<Storys>>

    @FormUrlEncoded
    @POST("InsertComments.php")
    fun insertComments(
        @Field("comment") comment: String?,
        @Field("timeAdd") timeAdd: String?,
        @Field("userId") userId: Int?,
        @Field("postId") postId: Int?
    ): Call<ResultComments>


    @GET("getCommentsPost.php")
    fun getCommentsPost(
        @Query("postId") postId: Int
    ): Call<ArrayList<Comments>>

    @FormUrlEncoded
    @POST("deleteCommentsById.php")
    fun deleteCommentsById(
        @Field("id") id: Int?,
        @Field("postId") postId: Int?
    ): Call<ResultComments>
}