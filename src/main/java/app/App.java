package app;

import app.service.BookItem;
import app.service.LibRuService;
import app.util.ZipUtil;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
        final JSONObject json = new JSONObject();
        final List<JSONObject> authorList = new ArrayList<>();
        json.put("Фантастика", new ArrayList<>());
        libService.getUrlList("http://lib.ru/RUFANT/")
                .parallelStream()
                .filter(t -> t.getName().matches("^[а-я]+\\s[а-я]+$") && !t.getName().equals("огл"))
//                .limit(5)
                .collect(Collectors.toMap(value -> value.getName(), value -> libService.getUrlList(value.getUrl())))
                .forEach((t, u) -> {
                    JSONObject jsonAuthor = new JSONObject();
                    List<BookItem> bookList = new ArrayList<>();
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
                                    FileUtils.copyURLToFile(new URL(t1.getUrl()), new File(filename));
                                    String data = Files.readString(Paths.get(filename), Charset.defaultCharset());
                                    data = ZipUtil.compress(data);
                                    //System.out.println("data = " + data);
                                    bookList.add(new BookItem(t1.getName(), data));
                                } catch (IOException ex) {
                                    System.out.println("error = " + ex.getMessage());
                                }
                            });
                    jsonAuthor.put(t, bookList);
                    authorList.add(jsonAuthor);
                });
        json.put("Фантастика", authorList);
        try (final FileWriter writer = new FileWriter("C:/temp/lib/lib.txt", false)) {
            writer.write(json.toString());
            writer.flush();
        }
    }
}
