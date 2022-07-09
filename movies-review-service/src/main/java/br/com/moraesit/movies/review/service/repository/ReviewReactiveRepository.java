package br.com.moraesit.movies.review.service.repository;

import br.com.moraesit.movies.review.service.domain.Review;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ReviewReactiveRepository extends ReactiveMongoRepository<Review, String> {
}
