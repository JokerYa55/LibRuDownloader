package app.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

/**
 *
 * @author vasil
 */
@Service
@Slf4j
public class LibRuService {

    public List<UrlData> getUrlList(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.select("a");
            return elements.stream()
                    .map(t -> new UrlData(t.html().replaceAll("<b>", "").replaceAll("</b>", "").toLowerCase(), t.absUrl("href"), url))                  
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return new ArrayList<>();
    }
}
