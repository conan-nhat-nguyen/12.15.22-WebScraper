package org.example;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Webscraper {

    record Job (String title, String company, String location, double salary){}
    public static void main(String[] args) {
        // The Web Browser
        WebClient webClient = new WebClient(BrowserVersion.CHROME);

        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setPrintContentOnFailingStatusCode(false);

        try {
            // Go this page in the browser
           HtmlPage page = webClient.getPage("https://www.indeed.com/jobs");
           HtmlForm form = (HtmlForm) page.getByXPath("//form").get(0);
           HtmlInput jobNameField = form.getInputByName("q");
           HtmlInput jobLocationField = form.getInputByName("l");
           HtmlInput findJobButton = form.getInputByName("yosegi-InlineWhatWhere-primaryButton");
           jobNameField.type("Software Developer");
           HtmlPage resultsPage = findJobButton.click();
           List<Job> jobs = parseResults(resultsPage);
           for (Job job : jobs) {
               System.out.println(job);
           }

        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    private static List<Job> parseResults(HtmlPage resultsPage) {
        HtmlTable table = (HtmlTable) resultsPage.getByXPath("//table").get(0);
        List<Job> jobs = table.getBodies().get(0).getRows().stream()
                .map(r -> {
                    String salary = r.getCell(3).getTextContent();
                    return new Job(
                            r.getCell(0).getTextContent(),
                            r.getCell(1).getTextContent(),
                            r.getCell(2).getTextContent(),
                            Integer.parseInt(salary)
                    );
                }).collect(Collectors.toList());
        return jobs;
    }
}