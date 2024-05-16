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
}