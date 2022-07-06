package br.com.moraesit.movies.info.service.controller;

import br.com.moraesit.movies.info.service.domain.MovieInfo;
import br.com.moraesit.movies.info.service.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@AutoConfigureWebTestClient
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.5.5")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MoviesInfoControllerIntegrationTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @Autowired
    WebTestClient webTestClient;

    static String MOVIE_INFOS_URL = "/v1/movieinfos";

    @BeforeEach
    void setUp() {
        var moviesInfo = List.of(
                new MovieInfo(null, "Batman Begins", 2005,
                        List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),

                new MovieInfo(null, "The Dark Knight", 2008,
                        List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),

                new MovieInfo("abc", "Dark Knight Rises", 2012,
                        List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2012-07-20"))
        );

        movieInfoRepository.saveAll(moviesInfo)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void addMovieInfo() {
        var movieInfo = new MovieInfo(null, "Batman Begins1", 2005,
                List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        webTestClient
                .post()
                .uri(MOVIE_INFOS_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(savedMovieInfo);
                    assertNotNull(savedMovieInfo.getMovieInfoId());
                    assertEquals("Batman Begins1", savedMovieInfo.getName());
                });

    }

    @Test
    void getAllMovieInfos() {
        webTestClient
                .get()
                .uri(MOVIE_INFOS_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void getMovieInfoByYear() {
        var uri = UriComponentsBuilder.fromUriString(MOVIE_INFOS_URL)
                .queryParam("year", 2005)
                .buildAndExpand().toUri();

        webTestClient
                .get()
                .uri(uri)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);
    }

    @Test
    void getMovieInfoById() {
        String movieInfoId = "abc";

        webTestClient
                .get()
                .uri(MOVIE_INFOS_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(movieInfo);
                    assertEquals("Dark Knight Rises", movieInfo.getName());
                    assertEquals(2012, movieInfo.getYear());
                });

    }

    @Test
    void getMovieInfoById_notFound() {
        String movieInfoId = "def";

        webTestClient
                .get()
                .uri(MOVIE_INFOS_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void updateMovieInfo() {
        var movieInfoId = "abc";

        var updateMovieInfo = new MovieInfo("abc", "Batman Begins Updated", 2022,
                List.of("Christian Bale"), LocalDate.parse("2022-06-15"));

        webTestClient
                .put()
                .uri(MOVIE_INFOS_URL + "/{id}", movieInfoId)
                .bodyValue(updateMovieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var updatedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(updatedMovieInfo);
                    assertNotNull(updatedMovieInfo.getMovieInfoId());
                    assertEquals("Batman Begins Updated", updatedMovieInfo.getName());
                    assertEquals(2022, updatedMovieInfo.getYear());
                    assertEquals(LocalDate.of(2022, 6, 15), updatedMovieInfo.getReleaseDate());
                    assertEquals(1, updatedMovieInfo.getCast().size());
                });

    }

    @Test
    void deleteMovieInfo() {
        var movieInfoId = "abc";

        webTestClient
                .delete()
                .uri(MOVIE_INFOS_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void updateMovieInfo_notFound() {
        var movieInfoId = "def";

        var updateMovieInfo = new MovieInfo(null, "Batman Begins Updated", 2022,
                List.of("Christian Bale"), LocalDate.parse("2022-06-15"));

        webTestClient
                .put()
                .uri(MOVIE_INFOS_URL + "/{id}", movieInfoId)
                .bodyValue(updateMovieInfo)
                .exchange()
                .expectStatus()
                .isNotFound();
    }
}