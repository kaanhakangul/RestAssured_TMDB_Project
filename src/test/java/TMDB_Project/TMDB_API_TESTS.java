package TMDB_Project;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class TMDB_API_TESTS {

    RequestSpecification reqSpec;

    String apiKey = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIxMmMzY2M5YjkxMDY3N2FiMGMwYjM4YzY0YzQxMGU5MyIsInN1YiI6IjY2MzhkMmU4OGRlMGFlMDEyM2Y3MjQxMiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.FnJfuoncHDyCddGGuefP1UA8ZwCj3yvzu5RbyW_4MuQ";
    String accessToken = "Bearer " + apiKey;

    int accountID = 0;

    String movieID="5e959bc3db72c00014ad69d6";

    @BeforeClass
    public void Setup() {

        baseURI = "https://api.themoviedb.org/3";
        reqSpec = new RequestSpecBuilder()
                .addHeader("Authorization", accessToken)
                .setContentType(ContentType.JSON)
                .build();

    }

    @Test
    public void GetAccounts() {

        accountID =
                given()
                        .spec(reqSpec)

                        .when()
                        .get("/" + "account")

                        .then()
                        .statusCode(200)
                        .log().body()
                        .extract().path("id")

        ;

        System.out.println("accountID = " + accountID);
    }

    @Test (dependsOnMethods = "GetAccounts")
    public void addToFavorites() {

        Map<String, Object> addToFavorites = new HashMap<>();
        addToFavorites.put("media_type", "movie");
        addToFavorites.put("media_id", "5e959bc3db72c00014ad69d6");
        addToFavorites.put("favorite", true);

        String addToFavoritesMessage =
                given()
                        .spec(reqSpec)
                        .body(addToFavorites)
                        .when()
                        .post("/account/" + accountID + "/favorite")

                        .then()
                        .statusCode(201)
                        .extract().path("status_message");

        Assert.assertEquals(addToFavoritesMessage, "The item/record was updated successfully.");

    }

    @Test (dependsOnMethods = "addToFavorites")
    public void addToWatchlist() {

        Map<String, Object> addToWatchlist = new HashMap<>();
        addToWatchlist.put("media_type", "movie");
        addToWatchlist.put("media_id", "5e959bc3db72c00014ad69d6");
        addToWatchlist.put("watchlist", true);

        String watchlistMessage =
                given()
                        .spec(reqSpec)
                        .body(addToWatchlist)

                        .when()
                        .post("/account/" + accountID + "/watchlist")

                        .then()
                        .statusCode(201)
                        .extract().path("status_message");
        System.out.println("watchlistMessage = " + watchlistMessage);
        Assert.assertEquals(watchlistMessage, "The item/record was updated successfully.");

    }

    @Test (dependsOnMethods = "addToWatchlist")
    public void getFavoriteMovies() {

        Response response =

                given()
                        .spec(reqSpec)

                        .when()
                        .get("/account/" + accountID + "/favorite/movies")


                        .then()
                        .statusCode(200)
                        .extract().response();


        List<String> movies = response.jsonPath().getList("results.original_title");


        System.out.println("movies = " + movies);
        Assert.assertTrue(movies.contains("Four Rooms"), "The movie is not in the favorite list.");

    }

    

    @Test (dependsOnMethods = "getFavoriteMovies")
    public void getRatedMovies() {

        Response response =

                given()
                        .spec(reqSpec)

                        .when()
                        .get("/account/" + accountID + "/rated/movies")


                        .then()
                        .statusCode(200)
                        .extract().response();

        List<String> ratedMovies=response.jsonPath().getList("results.original_title");
        System.out.println("ratedMovies = " + ratedMovies);

        Assert.assertTrue(ratedMovies.contains("Abigail"), "The movie is not in the rated movie list.");
    }

    @Test (dependsOnMethods = "getRatedMovies")
    public void getWatchlistMovie(){

        List<String> wathlist=
        given()

                .spec(reqSpec)
                .when()
                .get("/account/" + accountID + "/watchlist/movies")


                .then()
                .statusCode(200)
                .extract().path("results.original_title")
                ;
        System.out.println("wathlist = " + wathlist);

        Assert.assertTrue(wathlist.contains("Dune: Part Two"),"The movie is not in the watchlist.");

    }

    @Test (dependsOnMethods = "getWatchlistMovie")
    public void getMovieGenres(){

        List<String> genres=
        given()
                .spec(reqSpec)

                .when()
                .get("/genre/movie/list")



                .then()
                .statusCode(200)
                .extract().path("genres.name")

        ;
        System.out.println("genres = " + genres);
        Assert.assertTrue(genres.contains("Thriller"),"Can't get genres.");
    }

    @Test (dependsOnMethods = "getMovieGenres")
    public void getNowPlayingMovies(){

        given()
                .spec(reqSpec)

                .when()
                .get("/movie/now_playing")



                .then()
                .statusCode(200)

        ;

    }

    @Test (dependsOnMethods = "getNowPlayingMovies")
    public void getPopularMovies(){

        given()
                .spec(reqSpec)

                .when()
                .get("/movie/popular")



                .then()
                .statusCode(200)

        ;

    }

    @Test (dependsOnMethods = "getPopularMovies")
    public void getTopRatedMovies(){

        given()
                .spec(reqSpec)

                .when()
                .get("/movie/top_rated")



                .then()
                .statusCode(200)

        ;

    }


    @Test (dependsOnMethods = "getTopRatedMovies")
    public void getUpcomingMovies(){

        given()
                .spec(reqSpec)

                .when()
                .get("/movie/upcoming")


                .then()
                .statusCode(200)

        ;

    }

    @Test (dependsOnMethods = "getUpcomingMovies")
    public void searchForMovies(){

        String movieName="the idea of you";

        String donenSonuc=
        given()
                .spec(reqSpec)
                .param("query",movieName)

                .when()
                .get("/search/movie")


                .then()
                .statusCode(200)
                .extract().path("results[0].original_title")

        ;

        System.out.println("donenSonuc = " + donenSonuc);
        Assert.assertEquals(donenSonuc.toLowerCase(),movieName,"There is no movie like this.");
    }

    @Test (dependsOnMethods = "searchForMovies")
    public void getMovieDetails(){

String movieDetailName="Four Rooms";

        String donenMovieName=
                given()
                        .spec(reqSpec)

                        .when()
                        .get("/movie/"+movieID)


                        .then()
                        .statusCode(200)
                        .extract().path("original_title")

                ;

        System.out.println("donenMovieName = " + donenMovieName);
        Assert.assertEquals(donenMovieName,movieDetailName,"There is no movie like this.");
    }

    @Test (dependsOnMethods = "getMovieDetails")
    public void searchForKeywords(){

        String query="war";


                given()
                        .spec(reqSpec)
                        .param("query",query)
                        .when()
                        .get("https://www.themoviedb.org/search")


                        .then()
                        .statusCode(200)

                ;

    }

    @Test (dependsOnMethods = "searchForKeywords")
    public void addMovieRating(){

        Map<String, Double> rating = new HashMap<>();
        rating.put("value", 8.5);


        given()
                .spec(reqSpec)
                .body(rating)
                .when()
                .post("movie/"+movieID+"/rating")


                .then()
                .statusCode(201)
                .body("success",equalTo(true))

        ;

    }
    @Test(dependsOnMethods = "addMovieRating")
    public void deleteMovieRating(){

        given()
                .spec(reqSpec)

                .when()
                .delete("/movie/"+movieID+"/rating")

                .then()
                .statusCode(200)
        ;
    }

    @Test(dependsOnMethods = "deleteMovieRating")
    public void unauthorizedAccess(){

        int mediaID=18;
        Map<String, Integer> unauth = new HashMap<>();
        unauth.put("media_id",mediaID);


        int status_code=
                given()
                        .spec(reqSpec)
                        .pathParam("listID", "[valid_list_id]")
                        .queryParam("session_id","[invalid_session_id]")
                        .body(unauth)


                        .when()
                        .post("/list/"+"{listID}"+"/add_item")


                        .then()
                        .statusCode(401)
                        .extract().path("status_code")
                ;
        Assert.assertEquals(status_code,3);
    }
}