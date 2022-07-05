package br.com.moraesit.movies.info.service.repository;

import br.com.moraesit.movies.info.service.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataMongoTest
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.5.5")
@ActiveProfiles("test")
class MovieInfoRepositoryIntegrationTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

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
    void findAll() {
        var moviesInfoFlux = movieInfoRepository.findAll().log();

        StepVerifier.create(moviesInfoFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void findById() {
        var movieInfoMono = movieInfoRepository.findById("abc").log();

        StepVerifier.create(movieInfoMono)
                .assertNext(movieInfo -> {
                    assertEquals(2012, movieInfo.getYear());
                    assertEquals(LocalDate.of(2012, 7, 20), movieInfo.getReleaseDate());
                    assertEquals("Dark Knight Rises", movieInfo.getName());
                    assertEquals(2, movieInfo.getCast().size());
                })
                .verifyComplete();
    }

    @Test
    void saveMovieInfo() {
        var movieInfo = new MovieInfo(null, "Batman Begins X", 2005,
                List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        var movieInfoMono = movieInfoRepository.save(movieInfo).log();

        StepVerifier.create(movieInfoMono)
                .assertNext(movieInfo1 -> {
                    assertNotNull(movieInfo1.getMovieInfoId());
                    assertEquals(2005, movieInfo1.getYear());
                    assertEquals(LocalDate.of(2005, 6, 15), movieInfo1.getReleaseDate());
                    assertEquals("Batman Begins X", movieInfo1.getName());
                    assertEquals(2, movieInfo1.getCast().size());
                })
                .verifyComplete();
    }
}