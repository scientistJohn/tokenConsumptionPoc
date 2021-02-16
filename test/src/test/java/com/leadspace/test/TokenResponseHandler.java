package com.leadspace.test;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TokenResponseHandler implements ResponseHandler<String> {
    @Override
    public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new IOException();
        }
        String token = null;
        try (Scanner scanner = new Scanner(response.getEntity().getContent(), StandardCharsets.UTF_8.name())) {
            token = scanner.useDelimiter("\\A").next();
        }
        return token;
    }
}
