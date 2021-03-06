package br.com.moraesit.movies.info.service.controller;

import br.com.moraesit.movies.info.service.domain.MovieInfo;
import br.com.moraesit.movies.info.service.service.MoviesInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
public class MoviesInfoController {

    private final MoviesInfoService moviesInfoService;

    public MoviesInfoController(MoviesInfoService moviesInfoService) {
        this.moviesInfoService = moviesInfoService;
    }

    @GetMapping("/movieinfos")
    public Flux<MovieInfo> getAllMovieInfos(@RequestParam(value = "year", required = false) Integer year) {
        if (year != null)
            return moviesInfoService.getMovieInfoByYear(year);
        return moviesInfoService.getAllMovieInfos();
    }

    @GetMapping("/movieinfos/search")
    public Flux<MovieInfo> searchMovieInfos(MovieInfo movieInfo) {
        return moviesInfoService.searchMovieInfos(movieInfo);
    }

    @GetMapping("/movieinfos/{movieInfoId}")
    public Mono<ResponseEntity<MovieInfo>> getMovieInfoById(@PathVariable String movieInfoId) {
        return moviesInfoService.getMovieInfoById(movieInfoId)
                .map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @PostMapping("/movieinfos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo) {
        return moviesInfoService.addMovieInfo(movieInfo);
    }

    @PutMapping("/movieinfos/{movieInfoId}")
    public Mono<ResponseEntity<MovieInfo>> updateMovieInfo(@PathVariable String movieInfoId,
                                                           @RequestBody MovieInfo updateMovieInfo) {
        return moviesInfoService.updateMovieInfo(movieInfoId, updateMovieInfo)
                .map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/movieinfos/{movieInfoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieInfo(@PathVariable String movieInfoId) {
        return moviesInfoService.deleteMovieInfo(movieInfoId);
    }
}
