package br.com.moraesit.movies.info.service.service;

import br.com.moraesit.movies.info.service.domain.MovieInfo;
import br.com.moraesit.movies.info.service.repository.MovieInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class MoviesInfoService {

    private final MovieInfoRepository movieInfoRepository;

    public MoviesInfoService(MovieInfoRepository movieInfoRepository) {
        this.movieInfoRepository = movieInfoRepository;
    }

    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo) {
        return movieInfoRepository.save(movieInfo);
    }

    public Flux<MovieInfo> getAllMovieInfos() {
        return movieInfoRepository.findAll();
    }

    public Mono<MovieInfo> getMovieInfoById(String movieInfoId) {
        return movieInfoRepository.findById(movieInfoId);
    }
}
