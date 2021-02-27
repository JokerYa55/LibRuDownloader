package app;

import app.service.LibRuService;
import app.util.ZipUtil;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class App implements CommandLineRunner {

    @Autowired
    LibRuService libService;

    public static void main(String[] args) {
        log.info("Starting app ...");
        SpringApplication.run(App.class, args);
        log.info("Started app");
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("test");
        JSONObject json = new JSONObject();
        json.put("Фантастика", new ArrayList<>());
        libService.getUrlList("http://lib.ru/RUFANT/")
                .parallelStream()
                .filter(t -> t.getName().matches("^[а-я]+\\s[а-я]+$") && !t.getName().equals("огл"))
                .limit(5)
                .collect(Collectors.toMap(value -> value.getName(), value -> libService.getUrlList(value.getUrl())))
                .forEach((t, u) -> {
                    System.out.println("Автор : " + t);
                    u.parallelStream()
                            .filter(t1 -> t1.getUrl().contains("shtml"))
                            .filter(t1 -> t1.getUrl().contains(t1.getParentUrl()))
                            .filter(t1 -> t1.getName().trim().length() > 0)
                            .filter(t1 -> t1.getName().matches("^[а-я\\.\\s]+$"))
                            .filter(t1 -> !t1.getName().matches("^(дате)$||(^названию$)||(^популярности$)||(^статистика раздела$)||(^список опубликованного$)"))
                            .forEach(t1 -> {
                                System.out.println("\tКнига : " + t1.getName() + " url = " + t1.getUrl());
                                try {
                                    String filename = "c:/temp/lib/" + UUID.randomUUID().toString();
                                    log.info("filename = {} : zip = {}", filename, ZipUtil.compress(filename));
                                    FileUtils.copyURLToFile(new URL(t1.getUrl()), new File(filename));
                                    String data = Files.readString(Paths.get(filename)) ;
                                    
                                } catch (IOException ex) {
                                    System.out.println("error = " + ex.getMessage());
                                }
                            });
                });
        System.out.println("json = " + json.toString());

    }
}
