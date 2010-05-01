package com.talios;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.concordion.api.Resource;
import org.concordion.api.ResultSummary;
import org.concordion.internal.ConcordionBuilder;
import org.testng.Reporter;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class AppTest {

    public static final java.lang.String NAMESPACE_TALIOS = "http://www.talios.com/2010/concordion";
    private HttpClient client;

    @BeforeTest
    public void setup() {
         client = new HttpClient();

    }

    @Test
    public void specification() throws IOException {

        System.setProperty("concordion.runner.concordion", BasicConcordionRunner.class.getName());

        ResultSummary resultSummary = new ConcordionBuilder()
                .withCommand(NAMESPACE_TALIOS, "eval", new EvalCommand())
                .build()
                .process(new Resource("/devcamp.html"), this);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resultSummary.print(new PrintStream(baos));
        Reporter.log(baos.toString());

        resultSummary.assertIsSatisfied();
    }


    // ********************

    public String listQuestions(String url) {

        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod(url);
        String body;

        try {
            client.executeMethod(method);
            body = method.getResponseBodyAsString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            method.releaseConnection();

        }
        return body;
    }


    public TestResponse voteResponse(String url, String id, String vote, String confirm) {

        HttpClient client = new HttpClient();
        PostMethod putMethod = new PostMethod();
        putMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        putMethod.setRequestBody("id=" + id + "&vote=" + vote + "&confirm=" + confirm);
        putMethod.setPath(url);
        try {
            client.executeMethod(putMethod);

            return new TestResponse("" + putMethod.getStatusLine().getStatusCode(), putMethod.getResponseBodyAsString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public static class TestResponse {
        public String code;
        public String content;

        public TestResponse(String code, String content) {
            this.code = code;
            this.content = content;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

}
