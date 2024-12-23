package org.example;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Main {
    public static void scrapeUFCFights(List<Events> events, Set<String> pagesDiscovered, List<String> pagesToScrape) {

        if (!pagesToScrape.isEmpty()) {
            String url = pagesToScrape.getFirst();
            pagesDiscovered.add(url);

            Document doc;
            try {
                doc = Jsoup.connect("http://www.ufcstats.com/event-details/72c9c2eadfc3277e")
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36").get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Element table = doc.select("table").getFirst();
            Element tbody = table.select("tbody").first();
            Elements headerRows = table.select("tr").get(0).select("th, td");
            List<String> headers = new ArrayList<String>();
            for (Element header : headerRows) {
                headers.add(header.text());
            }

            Elements rows = tbody.select("tr");
            for (Element row : rows) {
                Events event = new Events();

                event.setWin_loss(row.select("td:nth-child(1)").text());
                Elements fighterElements = row.select("td:nth-child(2) p a");
                StringBuilder fighters = new StringBuilder();

                for (Element fighterElement : fighterElements) {
                    String fighterName = fighterElement.text().trim();
                    if (!fighters.isEmpty()) {
                        fighters.append("\n"); // Add a newline between fighters
                    }
                    fighters.append(fighterName);
                }
                event.setFighter(fighters.toString());
                event.setKd(row.select("td:nth-child(3)").text());
                event.setStr(row.select("td:nth-child(4)").text());
                event.setTd(row.select("td:nth-child(5)").text());
                event.setSub(row.select("td:nth-child(6)").text());
                event.setWeight_class(row.select("td:nth-child(7)").text());
                event.setMethod(row.select("td:nth-child(8)").text());
                event.setRound(row.select("td:nth-child(9)").text());
                event.setTime(row.select("td:nth-child(10)").text());
                events.add(event);
            }

            Elements paginations = doc.select("a[href]");
            for (Element pagination : paginations) {
                String pageUrl = pagination.attr("href");
                if (!pagesDiscovered.contains(pageUrl) && !pagesToScrape.contains(pageUrl)) {
                    pagesToScrape.add(pageUrl);
                }
            }

            System.out.println(url + " -> page scraped");
        }
    }

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {


        List<Events> events = Collections.synchronizedList(new ArrayList<>());

        Set<String> pagesDiscovered = Collections.synchronizedSet(new HashSet<>());

        List<String> pagesToScrape = Collections.synchronizedList(new ArrayList<>());

        pagesToScrape.add("https://www.ufcstats.com/statistics/events/completed");

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        scrapeUFCFights(events, pagesDiscovered, pagesToScrape);

        int i = 1;

        int limit = 12;

        while (!pagesToScrape.isEmpty() && i < limit) {
            executorService.execute(() -> scrapeUFCFights(events, pagesDiscovered, pagesToScrape));

            TimeUnit.MILLISECONDS.sleep(200);
            i++;
        }
        executorService.shutdown();
        executorService.awaitTermination(300, TimeUnit.SECONDS);
        System.out.println(events.size() + " events scraped");


        File csvFile = new File("Events.csv");
        try (PrintWriter pw = new PrintWriter(csvFile)) {
            for (Events event : events) {
                Map<String, String> row = getStrings(event);
                List<String> values = new ArrayList<>(row.values());
                pw.println(String.join(",", values));
            }
        }
    }

    private static Map<String, String> getStrings(Events event) {
        Map<String, String> row = new LinkedHashMap<>();
        row.put("win_loss", event.getWin_loss());
        row.put("fighter1", event.getFighter1());
        row.put("fighter2", event.getFighter2());
        row.put("kd", event.getKd());
        row.put("str", event.getStr());
        row.put("td", event.getTd());
        row.put("sub", event.getSub());
        row.put("weight_class", event.getWeight_class());
        row.put("method", event.getMethod());
        row.put("round", event.getRound());
        row.put("time", event.getTime());
        return row;
    }

}
