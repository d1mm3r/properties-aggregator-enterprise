package com.sarsx.srealityparser;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    public Parser() {
    }

    private WebClient initializeClient() {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.waitForBackgroundJavaScript(3000);
        return webClient;
    }

    public int getPropertyCount() {
        WebClient webClient = initializeClient();
        int propertyCount = 0;
        try {
            HtmlPage filterPage = webClient.getPage("https://www.sreality.cz/en/search/to-rent/apartments/praha?disposition=1%2Bkt,1%2B1,2%2Bkt,2%2B1&published=today&min-sq-meters=35&max-sq-meters=10000000000&min-price=0&max-price=15000#filter");
            Optional<Object> estatesCountRawString = filterPage.getByXPath("//button[@class='btn-full btn-XL ng-binding ng-scope']/text()").stream().findFirst();
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(estatesCountRawString.get().toString());
            while (matcher.find()) {
                propertyCount = Integer.parseInt(matcher.group());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        webClient.close();
        return propertyCount;
    }

    public List<String> getPropertiesUrl() {
        WebClient webClient = initializeClient();
        List<String> urls = new ArrayList<>();
        try {
            HtmlPage properties = webClient.getPage("https://www.sreality.cz/en/search/to-rent/apartments/praha?disposition=1%2Bkt,1%2B1,2%2Bkt,2%2B1&published=today&min-sq-meters=35&max-sq-meters=10000000000&min-price=0&max-price=15000");
            HtmlAnchor buttonNext;
            do {
                buttonNext = (HtmlAnchor) properties.getByXPath("(//ul[@class='paging-full']/li[@class='paging-item'])[last()]/a").get(0);
                for (Object element : properties.getByXPath("//div[@class='property ng-scope']/a[@class='images count3 clear']")) {
                    DomNode root = (DomNode) element;
                    if (!root.getByXPath("span[@class='tip-region ng-scope']").stream().findFirst().isPresent()) {
                        DomAttr href = (DomAttr) root.getByXPath("@href").stream().findFirst().get();
                        urls.add("<url>https://sreality.cz" + href.getValue() + "</url>\n");
                    }
                }
                properties = buttonNext.click();
            } while (!buttonNext.getByXPath("@class").stream().findFirst().get().toString().contains("disabled"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        webClient.close();
        return urls;
    }
}
