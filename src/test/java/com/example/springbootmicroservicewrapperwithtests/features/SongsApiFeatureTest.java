package com.example.springbootmicroservicewrapperwithtests.features;

import com.example.springbootmicroservicewrapperwithtests.models.Song;
import com.example.springbootmicroservicewrapperwithtests.repositories.SongRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SongsApiFeatureTest {

    @Autowired
    private SongRepository songRepository;

    Song secondSong;

    @Before
    public void setUp() {
        songRepository.deleteAll();

        Song firstSong = new Song(
                "song1",
                "artist1"
        );

        secondSong = new Song(
                "song2",
                "artist2"
        );

        Stream.of(firstSong, secondSong)
                .forEach(song -> {
                    songRepository.save(song);
                });
    }

    @After
    public void tearDown() {
        songRepository.deleteAll();
    }

    @Test
    public void shouldAllowFullCrudForASong() throws Exception {

        Song songNotYetInDb = new Song(
                "new_song",
                "new_artist"
        );

        given()
                .contentType(JSON)
                .and().body(songNotYetInDb)
                .when()
                .post("http://localhost:8080/songs")
                .then()
                .statusCode(is(200))
                .and().body(containsString("new_song"));

        when()
                .get("http://localhost:8080/songs/")
                .then()
                .statusCode(is(200))
                .and().body(containsString("new_song"))
                .and().body(containsString("new_artist"));

        when()
                .get("http://localhost:8080/songs/" + secondSong.getId())
                .then()
                .statusCode(is(200))
                .and().body(containsString("song2"))
                .and().body(containsString("artist2"));

        secondSong.setTitle("changed_title");

        given()
                .contentType(JSON)
                .and().body(secondSong)
                .when()
                .patch("http://localhost:8080/songs/" + secondSong.getId())
                .then()
                .statusCode(is(200))
                .and().body(containsString("changed_title"));

        when()
                .delete("http://localhost:8080/songs/" + secondSong.getId())
                .then()
                .statusCode(is(200));
    }
}