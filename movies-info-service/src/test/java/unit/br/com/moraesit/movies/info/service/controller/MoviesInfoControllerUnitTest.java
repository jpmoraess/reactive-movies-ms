package br.com.moraesit.movies.info.service.controller;

import br.com.moraesit.movies.info.service.domain.MovieInfo;
import br.com.moraesit.movies.info.service.service.MoviesInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebTestClient
public class MoviesInfoControllerUnitTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MoviesInfoService moviesInfoServiceMock;

    static String MOVIE_INFOS_URL = "/v1/movieinfos";

    @Test
    void getAllMoviesInfo() {
        var moviesInfo = List.of(
                new MovieInfo(null, "Batman Begins", 2005,
                        List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),

                new MovieInfo(null, "The Dark Knight", 2008,
                        List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),

                new MovieInfo("abc", "Dark Knight Rises", 2012,
                        List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2012-07-20"))
        );

        when(moviesInfoServiceMock.getAllMovieInfos()).thenReturn(Flux.fromIterable(moviesInfo));

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
    void searchMovieInfos() {
        var moviesInfo = List.of(
                new MovieInfo(null, "The Dark Knight", 2008,
                        List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18"))
        );

        var movieInfo = new MovieInfo();
        movieInfo.setName("ht");

        when(moviesInfoServiceMock.searchMovieInfos(movieInfo))
                .thenReturn(Flux.fromIterable(moviesInfo));

        var uri = UriComponentsBuilder.fromUriString(MOVIE_INFOS_URL + "/search")
                .queryParam("name", "ht")
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
    void getMovieById() {
        var movieInfoId = "abc";
        var movieInfo = new MovieInfo("abc", "Dark Knight Rises", 2012,
                List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2012-07-20"));

        when(moviesInfoServiceMock.getMovieInfoById(movieInfoId)).thenReturn(Mono.just(movieInfo));

        webTestClient
                .get()
                .uri(MOVIE_INFOS_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfo1 = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(movieInfo1);
                    assertEquals("Dark Knight Rises", movieInfo1.getName());
                });
    }

    @Test
    void addMovieInfo() {
        var movieInfo = new MovieInfo(null, "Batman Begins1", 2005,
                List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        when(moviesInfoServiceMock.addMovieInfo(isA(MovieInfo.class)))
                .thenReturn(Mono.just(
                        new MovieInfo("mockId", "Batman Begins1", 2005,
                                List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"))
                ));

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
                    assertEquals("mockId", savedMovieInfo.getMovieInfoId());
                    assertEquals("Batman Begins1", savedMovieInfo.getName());
                    assertEquals(2005, savedMovieInfo.getYear());
                    assertEquals(2, savedMovieInfo.getCast().size());
                    assertEquals(LocalDate.of(2005, 6, 15), savedMovieInfo.getReleaseDate());
                });
    }

    @Test
    void addMovieInfo_validation() {
        var movieInfo = new MovieInfo(null, "", -2010,
                List.of(""), LocalDate.parse("2005-06-15"));

        webTestClient
                .post()
                .uri(MOVIE_INFOS_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> {
                    var responseBody = stringEntityExchangeResult.getResponseBody();
                    assertNotNull(responseBody);
                    var expectedErrorMessage = "movieInfo.cast must be present,movieInfo.name must be present,movieInfo.year must be a positive";
                    assertEquals(expectedErrorMessage, responseBody);
                });
    }

    @Test
    void updateMovieInfo() {
        var movieInfoId = "abc";

        var movieInfo = new MovieInfo(null, "Batman Begins", 2022,
                List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2022-06-15"));

        when(moviesInfoServiceMock.updateMovieInfo(isA(String.class), isA(MovieInfo.class)))
                .thenReturn(Mono.just(
                        new MovieInfo(movieInfoId, "Batman Begins Updated", 2023,
                                List.of("Christian Bale"), LocalDate.parse("2023-06-15"))
                ));

        webTestClient
                .put()
                .uri(MOVIE_INFOS_URL + "/{id}", movieInfoId)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var updatedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(updatedMovieInfo);
                    assertNotNull(updatedMovieInfo.getMovieInfoId());
                    assertEquals("Batman Begins Updated", updatedMovieInfo.getName());
                    assertEquals(2023, updatedMovieInfo.getYear());
                    assertEquals(LocalDate.of(2023, 6, 15), updatedMovieInfo.getReleaseDate());
                    assertEquals(1, updatedMovieInfo.getCast().size());
                });

    }

    @Test
    void deleteMovieInfo() {
        var movieInfoId = "abc";

        when(moviesInfoServiceMock.deleteMovieInfo(isA(String.class))).thenReturn(Mono.empty());

        webTestClient
                .delete()
                .uri(MOVIE_INFOS_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .isNoContent();
    }
}
