package com.example.dramebaz.shg.client;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;

public class SplitwiseApi extends DefaultApi10a {

    private static final String BASE_URL = "https://secure.splitwise.com";
    private static final String AUTHORIZE_URL = BASE_URL + "/authorize?oauth_token=%s";// https://api.twitter.com/oauth/authorize?oauth_token=%s";
    private static final String REQUEST_TOKEN_RESOURCE = BASE_URL + "/api/v3.0/get_request_token"; //"api.twitter.com/oauth/request_token";
    private static final String ACCESS_TOKEN_RESOURCE = BASE_URL + "/api/v3.0/get_access_token"; //"api.twitter.com/oauth/access_token";

    @Override
    public String getAccessTokenEndpoint() {
        return ACCESS_TOKEN_RESOURCE;
    }

    @Override
    public String getRequestTokenEndpoint() {
        return REQUEST_TOKEN_RESOURCE;
    }

    @Override
    public String getAuthorizationUrl(Token requestToken) {
        return String.format(AUTHORIZE_URL, requestToken.getToken());
    }
}