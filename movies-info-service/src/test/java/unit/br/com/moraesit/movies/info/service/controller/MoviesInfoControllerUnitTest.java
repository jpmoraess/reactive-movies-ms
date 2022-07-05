package br.com.moraesit.movies.info.service.controller;

import br.com.moraesit.movies.info.service.domain.MovieInfo;
import br.com.moraesit.movies.info.service.service.MoviesInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
}
