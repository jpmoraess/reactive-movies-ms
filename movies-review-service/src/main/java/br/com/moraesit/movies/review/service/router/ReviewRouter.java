package br.com.moraesit.movies.review.service.router;

import br.com.moraesit.movies.review.service.handler.ReviewHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ReviewRouter {

    @Bean
    public RouterFunction<ServerResponse> reviewsRoute(ReviewHandler reviewHandler) {
        return route()
                .GET("/v1/helloworld", (request -> ServerResponse.ok().bodyValue("helloWorld")))
                .POST("/v1/reviews", reviewHandler::addReview)
                .GET("/v1/reviews", reviewHandler::getReviews)
                .build();
    }
}
