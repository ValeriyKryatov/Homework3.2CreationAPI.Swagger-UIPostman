package ru.hogwarts.school.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.IntStream;
import java.util.stream.Stream;

@RestController
@RequestMapping("/info")
public class InfoController {

    private final static Logger logger = LoggerFactory.getLogger(InfoController.class);

    private final int port;

    public InfoController(@Value("${server.port}") int port) {
        this.port = port;
    }

    @GetMapping("/getPort")
    public int port() {
        return port;
    }

    @GetMapping("/getIntegerValue")
    public void getIntegerValue() {
        long startTime1 = System.currentTimeMillis();
        logger.info("Метод запустился!");
        int sum = Stream
                .iterate(1, a -> a + 1)
                .limit(1_000_000)
                .reduce(0, (a, b) -> a + b);
        long finishTime1 = System.currentTimeMillis() - startTime1;
        logger.info("Метод завершил выполнение за = " + finishTime1 + " мс");
    }

    @GetMapping("/getIntegerValueModified")
    public void getIntegerValueModified() {
        long startTime2 = System.currentTimeMillis();
        logger.info("Модифицированный метод запустился!");
        int sum = Stream
                .iterate(1, a -> a + 1)
                .parallel()
                .limit(1_000_000)
                .reduce(0, Integer::sum);
        long finishTime2 = System.currentTimeMillis() - startTime2;
        logger.info("Модифицированный метод завершил выполнение за = " + finishTime2 + " мс");
    }

    @GetMapping("/getIntegerValueModified_2")
    public void getIntegerValueModified2() {
        long startTime3 = System.currentTimeMillis();
        logger.info("Модифицированный метод 2 запустился!");
        int sum = IntStream
                .iterate(1, a -> a + 1)
                .parallel()
                .limit(1_000_000)
                .reduce(0, Integer::sum);
        long finishTime3 = System.currentTimeMillis() - startTime3;
        logger.info("Модифицированный метод 2 завершил выполнение за = " + finishTime3 + " мс");
    }
}