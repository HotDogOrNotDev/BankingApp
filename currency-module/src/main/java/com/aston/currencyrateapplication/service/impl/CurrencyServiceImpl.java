package com.aston.currencyrateapplication.service.impl;

import com.aston.currencyrateapplication.controller.dto.xml.ValCurs;
import com.aston.currencyrateapplication.controller.dto.xml.Valute;
import com.aston.currencyrateapplication.domain.entity.CurrencyRate;
import com.aston.currencyrateapplication.exception.ParseValutesException;
import com.aston.currencyrateapplication.repository.CurrencyRepository;
import com.aston.currencyrateapplication.service.CurrencyService;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;

    @Value("${cbr.url}")
    private String cbrUrl;

    private WebClient webClient;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @PostConstruct
    private void initWebClient() {
        this.webClient = WebClient.create(cbrUrl);
    }

    @Override
    public Flux<CurrencyRate> fetchAndSaveRates(LocalDate date) {
        String dateStr = date.format(FORMATTER);

        return webClient.get()
                .uri("/scripts/XML_daily.asp?date_req={date}", dateStr)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(xml -> Mono.fromCallable(() -> parseValutes(xml))
                        .onErrorMap(e -> new ParseValutesException("Ошибка при парсинге XML", e))
                )
                .flatMapMany(valutes -> Flux.fromIterable(valutes)
                        .flatMap(valute -> saveOrUpdateRate(date, valute))
                );
    }

    private List<Valute> parseValutes(String xml) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        ValCurs valCurs = xmlMapper.readValue(xml, ValCurs.class);
        return valCurs.getValute();
    }

    private Mono<CurrencyRate> saveOrUpdateRate(LocalDate date, Valute valute) {
        String charCode = valute.getCharCode();
        return currencyRepository.findByDateAndCharCode(date, charCode)
                .flatMap(existing -> {
                    existing.setValue(valute.getValue());
                    existing.setVunitRate(valute.getVunitRate());
                    return currencyRepository.save(existing);
                })
                .switchIfEmpty(
                        Mono.defer(() -> {
                            CurrencyRate newRate = CurrencyRate.builder()
                                    .date(date)
                                    .cbrId(valute.getId())
                                    .numCode(valute.getNumCode())
                                    .charCode(charCode)
                                    .nominal(valute.getNominal())
                                    .name(valute.getName())
                                    .value(valute.getValue())
                                    .vunitRate(valute.getVunitRate())
                                    .build();
                            return currencyRepository.save(newRate);
                        })
                );
    }

    @Override
    public Flux<CurrencyRate> getRatesByDate(LocalDate date) {
        return currencyRepository.findAllByDate(date);
    }

}