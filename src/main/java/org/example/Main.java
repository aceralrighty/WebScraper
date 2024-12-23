package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;


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
                doc = Jsoup.connect("http://www.ufcstats.com/statistics/events/completed")
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36").get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Elements rows = doc.selectXpath("/html/body/section/div/div/div/div[2]/div/table/tbody/tr[1]");
            for (Element fight : rows) {
                Events event = new Events();


                event.setWin_loss(fight.selectXpath("/html/body/section/div/div/table/tbody/tr[1]/td[1]").attr("td"));
                event.setFighter(fight.selectXpath("/html/body/section/div/div/table/tbody/tr[1]/td[2]").attr("td"));
                event.setKd(fight.selectXpath("/html/body/section/div/div/table/tbody/tr[1]/td[3]").attr("td"));
                event.setStr(fight.selectXpath("/html/body/section/div/div/table/tbody/tr[1]/td[4]").attr("td"));
                event.setTd(fight.selectXpath("/html/body/section/div/div/table/tbody/tr[1]/td[5]").attr("td"));
                event.setSub(fight.selectXpath("/html/body/section/div/div/table/tbody/tr[1]/td[6]").attr("td"));
                event.setWeight_class(fight.selectXpath("/html/body/section/div/div/table/tbody/tr[1]/td[7]").attr("td"));
                event.setMethod(fight.selectXpath("/html/body/section/div/div/table/tbody/tr[1]/td[8]").attr("td"));
                event.setRound(fight.selectXpath("/html/body/section/div/div/table/tbody/tr[1]/td[9]").attr("td"));
                event.setTime(fight.selectXpath("/html/body/section/div/div/table/tbody/tr[1]/td[10]").attr("td"));
                events.add(event);
            }
            Elements paginations = doc.selectXpath("/html/body/section/div/div/div/div[2]/div/table/tbody/tr[2]/td[1]/i/a");

            for (Element pagination : paginations) {
                String pageUrl = pagination.attr("href");
                if (!pagesDiscovered.contains(pageUrl) && !pagesToScrape.contains(pageUrl)) {
                    pagesToScrape.add(pageUrl);
                }
                pagesDiscovered.add(pageUrl);
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

//        Document doc = Jsoup.connect("http://www.ufcstats.com/statistics/events/completed")
//                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36").get();
//        List<Events> events = new ArrayList<>();
//        Elements fighterElements = doc.select("body > section > div > div > table > tbody");
//
//        Elements rows = doc.select("tr.b-statistics__table-row_type_first");
//
//        for (Element row : rows) {
//            Element link = row.selectFirst("a.b-link_style_white");
//            if (link != null) {
//                String url = link.attr("href");
//                Document eventDoc = Jsoup.connect(url).get();
//            }
//            Elements fights = doc.select("tbody > tr > td");
//            for (Element fight : fights) {
//                Events event = new Events();
//
//                event.setWin_loss(fight.selectFirst("a.b-flag.b-flag_style_green .b-flag__text").text());
//                event.setFighter(fight.selectFirst("a.b-link.b-link_style_black").text());
//                event.setKd(fight.selectFirst("td.b-fight-details__table-col p.b-fight-details__table-text").text());
//                event.setStr(fight.selectFirst("body > section > div > div > table > tbody > tr:nth-child(1) > td:nth-child(4)").text());
//                event.setTd(fight.selectFirst("body > section > div > div > table > tbody > tr:nth-child(1) > td:nth-child(5)").text());
//                event.setSub(fight.selectFirst("body > section > div > div > table > tbody > tr:nth-child(1) > td:nth-child(6)").text());
//                event.setWeight_class(fight.selectFirst("body > section > div > div > table > tbody > tr:nth-child(1) > td:nth-child(7)").text());
//                event.setMethod(fight.selectFirst("body > section > div > div > table > tbody > tr:nth-child(1) > td:nth-child(8)").text());
//                event.setRound(fight.selectFirst("body > section > div > div > table > tbody > tr:nth-child(1) > td:nth-child(9)").text());
//                event.setTime(fight.selectFirst("body > section > div > div > table > tbody > tr:nth-child(1) > td:nth-child(10)").text());
//                events.add(event);
//            }
//        }
//        System.out.println(doc + " -> pages scraped " + events.size() + " events");


        File csvFile = new File("Events.csv");
        try (PrintWriter pw = new PrintWriter(csvFile)) {
            for (Events event : events) {
                List<String> row = getStrings(event);
                pw.println(String.join(",", row));
            }

        }
    }

    private static List<String> getStrings(Events event) {
        List<String> row = new ArrayList<>();
        row.add("\"" + event.getWin_loss());
        row.add("\"" + event.getFighter());
        row.add("\"" + event.getKd());
        row.add("\"" + event.getStr());
        row.add("\"" + event.getTd());
        row.add("\"" + event.getSub());
        row.add("\"" + event.getWeight_class());
        row.add("\"" + event.getMethod());
        row.add("\"" + event.getRound());
        row.add("\"" + event.getTime());
        return row;
    }

}
