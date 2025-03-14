package com.example;

import com.example.common.Util;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

@Slf4j
public class ReactiveTests {
    @Test
    void name() {
        Mono.just("Hello World")
                .as(StepVerifier::create)
          //      .consumeNextWith(x-> Assertions.assertTrue(x.contains("World")))
          //      .expectNextMatches(s -> s.startsWith("H"))
            //    .expectNextCount(1)
            //    .expectNext("Hello World")
                .verifyComplete();
    }

    @Test
    void error(){
        Mono.error(new RuntimeException("Error"))
                .as(StepVerifier::create)
                .verifyErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().equals("Error"));
             //   .verifyErrorMessage("Error");
           //     .verifyError(RuntimeException.class);
    }

    @Test
    void take(){
        Flux.interval(Duration.ofSeconds(1))
                .take(2)
                .as(StepVerifier::create)
                .expectNext(0L,1L)
                .verifyComplete();
    }

    @Test
    void onErrorReturn(){
        Flux.just(1,2)
                .concatWith(Flux.error(new IllegalArgumentException("Error")))
                .onErrorReturn(IllegalArgumentException.class,0)
                .as(StepVerifier::create)
                .expectNext(1,2,0)
                .verifyComplete();
    }

    @Test
    void flatMap(){
        Flux.range(1,3)
                .flatMap(i -> Mono.just(i*i))
                .as(StepVerifier::create)
                .expectNext(1,4,9)
                .verifyComplete();
    }

    @Test
    void map(){
        Flux.just("Orange","Banana","Lemon")
                .map(String::toUpperCase)
                .as(StepVerifier::create)
                .expectNext("ORANGE","BANANA","LEMON")
                .verifyComplete();
    }

    @Test
    void onErrorResume(){
        Mono.error(new RuntimeException("Error"))
                .onErrorResume(throwable -> Mono.just(1))
                .as(StepVerifier::create)
                .expectNext(1)
                .verifyComplete();
    }

    @Test
    void onErrorComplete(){
                Flux.error(new IllegalArgumentException("Error"))
                .onErrorComplete()
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void switchIfEmpty(){
        Flux.empty()
                .switchIfEmpty(Mono.just(1))
                .as(StepVerifier::create)
                .expectNext(1)
                .verifyComplete();
    }


    @Test
    void defaultIfEmpty(){
        Flux.<String>empty()
                .defaultIfEmpty("String")
                .as(StepVerifier::create)
                .expectNext("String")
                .verifyComplete();
    }

    @Test
    void onErrorMap(){
        Mono.error(new RuntimeException("Error"))
                .onErrorMap(ex->new IllegalArgumentException("Error"))
                .as(StepVerifier::create)
                .verifyError(IllegalArgumentException.class);
    }

    @Test
    void onErrorContinue(){
        Flux.range(1,4)
                .map(i->{
                    if(i==3) throw new RuntimeException("Error");
                    return i;
                })
                .onErrorContinue((error,obj)->log.info("Error on value: {}",obj))
                .as(StepVerifier::create)
                .expectNext(1,2,4)
                .verifyComplete();
    }

    @Test
    void fromIterable(){
        Flux.fromIterable(List.of(1,2,3))
                .as(StepVerifier::create)
                .expectNext(1,2,3)
                .verifyComplete();
    }

    @Test
    void concat(){
        Flux.concat(Flux.just(1,2,3),Flux.just(4,5,6))
                .as(StepVerifier::create)
                .expectNext(1,2,3,4,5,6)
                .verifyComplete();
    }

    @Test
    void merge(){
        Flux.merge(Flux.just(1,2,3),Flux.just(4,5,6))
                .as(StepVerifier::create)
                .expectNext(1,2,3,4,5,6)
                .verifyComplete();
    }

    @Test
    void filter(){
        Flux.range(1,30)
                .filter(i->i%2==0)
                .collectList()
                .as(StepVerifier::create)
                .expectNextMatches(x->x.stream().allMatch(y->y%2==0))
                .verifyComplete();
    }

    @Test
    void skip(){
        Flux.range(1,3)
                .skip(2)
                .as(StepVerifier::create)
                .expectNext(3)
                .verifyComplete();
    }

    @Test
    void buffer(){
        Flux.range(1,6)
                .buffer(2)
                .as(StepVerifier::create)
                .expectNext(List.of(1,2),List.of(3,4),List.of(5,6))
                .verifyComplete();
    }

    @Test
    void fromSupplier(){
        Mono.fromSupplier(() -> "Hello World")
                .as(StepVerifier::create)
                .expectNext("Hello World")
                .verifyComplete();
    }

    @Test
    void repeat(){
        Flux.just(2,4)
                .repeat(2)
                .as(StepVerifier::create)
           //     .expectNextCount(6)
                .expectNext(2,4,2,4,2,4)
                .verifyComplete();
    }

    @Test
    void distinct(){
        Flux.just(1,22,24,22)
                .distinct()
                .as(StepVerifier::create)
                .expectNext(1,22,24)
                .verifyComplete();
    }

    @Test
    void takeUntil(){
        Flux.range(1,10)
                .takeUntil(i->i%7==0)
                .as(StepVerifier::create)
                .expectNext(1,2,3,4,5,6,7)
                .verifyComplete();
    }

    @Test
    void takeWhile(){
        Flux.range(1,10)
                .takeWhile(i->i!=10)
                .as(StepVerifier::create)
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void firstWithValue(){
        Flux.firstWithValue(Flux.empty(),Flux.just(1,2,3))
                .as(StepVerifier::create)
                .expectNext(1,2,3)
                .verifyComplete();
    }

    @Test
    void delayElements(){
        Flux.range(1,4)
                .delayElements(Duration.ofSeconds(1))
                .as(StepVerifier::create)
                .thenAwait(Duration.ofSeconds(1))
                .expectNext(1,2,3,4)
                .verifyComplete();
    }

    @Test
    void reduce(){
        Flux.just(1,2,3)
                .reduce(Integer::sum)
                .as(StepVerifier::create)
                .expectNext(6)
                .verifyComplete();
    }

    @Test
    void collectMap(){
        Flux.interval(Duration.ofMillis(100))
                .map(i->Util.faker().name().firstName())
                .take(10)
                .collectMap(v->v,String::length)
                .as(StepVerifier::create)
                .expectNextMatches(x->{
                    var firstKey = x.keySet().stream().findFirst();
                    var firstValue = x.values().stream().findFirst();
                    return firstKey.get().length() ==(firstValue.get());
                }).verifyComplete();
    }

    @Test
    void flatMapMany(){
        Mono.just(1)
                .flatMapMany(i->Flux.range(i,3))
                .as(StepVerifier::create)
                .expectNext(1,2,3)
                .verifyComplete();
    }

    @Test
    void then(){
        Mono.just("78")
                .then(Mono.just("AB"))
                .as(StepVerifier::create)
                .expectNext("AB")
                .verifyComplete();
    }

    @Test
    void window(){
        Flux.range(1,6)
                .window(2)
                .flatMap(Flux::collectList)
                .as(StepVerifier::create)
                .expectNext(List.of(1,2),List.of(3,4),List.of(5,6))
                .verifyComplete();
    }
}
