package br.com.moraesit.movies.info.service.repository;

import br.com.moraesit.movies.info.service.domain.MovieInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MovieInfoRepository extends ReactiveMongoRepository<MovieInfo, String> {
}
