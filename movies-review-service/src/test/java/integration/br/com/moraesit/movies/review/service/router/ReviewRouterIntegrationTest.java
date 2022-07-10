package br.com.moraesit.movies.review.service.router;

import br.com.moraesit.movies.review.service.domain.Review;
import br.com.moraesit.movies.review.service.repository.ReviewReactiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@AutoConfigureWebTestClient
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.5.5")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReviewRouterIntegrationTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReviewReactiveRepository reviewReactiveRepository;

    static String REVIEWS_URL = "/v1/reviews";

    @BeforeEach
    void setUp() {
        var reviews = List.of(
                new Review(null, 1L, "Awesome Movie", 9.0),
                new Review(null, 1L, "Fantastic Movie", 8.5),
                new Review(null, 2L, "Bad Movie", 3.0)
        );

        reviewReactiveRepository.saveAll(reviews).blockLast();
    }

    @AfterEach
    void tearDown() {
        reviewReactiveRepository.deleteAll().block();
    }

    @Test
    void addReview() {
        var review = new Review(null, 1L, "Awesome Movie", 9.0);

        webTestClient
                .post()
                .uri(REVIEWS_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var savedReview = reviewEntityExchangeResult.getResponseBody();
                    assertNotNull(savedReview);
                    assertNotNull(savedReview.getReviewId());
                    assertNotNull(savedReview.getMovieInfoId());
                    assertEquals("Awesome Movie", savedReview.getComment());
                    assertEquals(9.0, savedReview.getRating());
                });
    }

    @Test
    void getReviews() {
        webTestClient
                .get()
                .uri(REVIEWS_URL)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Review.class)
                .hasSize(3)
                .value(reviews -> {
                    assertEquals(3, reviews.size());
                });
    }

    @Test
    void updateReview() {
        var review = new Review(null, 1L, "Awesome Movie", 9.0);

        var savedReview = reviewReactiveRepository.save(review).block();

        var reviewUpdate = new Review(null, 1L, "Not an Awesome Movie", 8.0);

        assertNotNull(savedReview);
        assertNotNull(savedReview.getReviewId());

        webTestClient
                .put()
                .uri(REVIEWS_URL + "/{id}", savedReview.getReviewId())
                .bodyValue(reviewUpdate)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var updatedReview = reviewEntityExchangeResult.getResponseBody();
                    assertNotNull(updatedReview);
                    assertNotNull(updatedReview.getReviewId());
                    assertEquals(8.0, updatedReview.getRating());
                    assertEquals("Not an Awesome Movie", updatedReview.getComment());
                });
    }

    @Test
    void deleteReview() {
        var review = new Review(null, 1L, "Awesome Movie", 9.0);
        var savedReview = reviewReactiveRepository.save(review).block();

        assertNotNull(savedReview);
        assertNotNull(savedReview.getReviewId());

        webTestClient
                .delete()
                .uri(REVIEWS_URL + "/{id}", savedReview.getReviewId())
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void getReviewsByMovieInfoId() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(REVIEWS_URL)
                        .queryParam("movieInfoId", "1")
                        .build())
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .value(reviews -> {
                    System.out.println("reviews : " + reviews);
                    assertEquals(2, reviews.size());
                });
    }
}