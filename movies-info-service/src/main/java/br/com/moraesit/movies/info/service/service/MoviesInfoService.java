package br.com.moraesit.movies.info.service.service;

import br.com.moraesit.movies.info.service.domain.MovieInfo;
import br.com.moraesit.movies.info.service.repository.MovieInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
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

    public Flux<MovieInfo> searchMovieInfos(MovieInfo movieInfo) {
        return movieInfoRepository.findAll(Example.of(movieInfo, ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)));
    }

    public Mono<MovieInfo> getMovieInfoById(String movieInfoId) {
        return movieInfoRepository.findById(movieInfoId);
    }

    public Mono<MovieInfo> updateMovieInfo(String movieInfoId, MovieInfo updateMovieInfo) {
        return movieInfoRepository.findById(movieInfoId)
                .flatMap(movieInfo -> {
                    movieInfo.setName(updateMovieInfo.getName());
                    movieInfo.setYear(updateMovieInfo.getYear());
                    movieInfo.setCast(updateMovieInfo.getCast());
                    movieInfo.setReleaseDate(updateMovieInfo.getReleaseDate());
                    return movieInfoRepository.save(movieInfo);
                });
    }

    public Mono<Void> deleteMovieInfo(String movieInfoId) {
        return movieInfoRepository.deleteById(movieInfoId);
    }

    public Flux<MovieInfo> getMovieInfoByYear(Integer year) {
        return movieInfoRepository.findByYear(year);
    }
}
