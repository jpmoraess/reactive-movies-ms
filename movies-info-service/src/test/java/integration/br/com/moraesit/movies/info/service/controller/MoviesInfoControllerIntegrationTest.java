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

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
                    assert savedMovieInfo != null;
                    assert savedMovieInfo.getMovieInfoId() != null;
                    assertEquals("Batman Begins1", savedMovieInfo.getName());
                });

    }
}