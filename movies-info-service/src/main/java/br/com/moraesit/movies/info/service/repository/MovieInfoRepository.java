package br.com.moraesit.movies.info.service.repository;

import br.com.moraesit.movies.info.service.domain.MovieInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface MovieInfoRepository extends ReactiveMongoRepository<MovieInfo, String> {
    Flux<MovieInfo> findByYear(Integer year);

    Flux<MovieInfo> findByName(String name);
}
