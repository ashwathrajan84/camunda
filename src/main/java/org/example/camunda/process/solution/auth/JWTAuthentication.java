package org.example.camunda.process.solution.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.example.camunda.process.solution.TaskListRestClient;
import org.example.camunda.process.solution.dto.AccessTokenRequest;
import org.example.camunda.process.solution.dto.AccessTokenResponse;
import org.example.camunda.process.solution.exception.TaskListException;
import org.example.camunda.process.solution.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JWTAuthentication implements Authentication {

  public static final String CLIENT_CREDENTIALS = "client_credentials";

  String authorizationServerUrl;
  String audience;
  String clientId;
  String clientSecret;

  // For SaaS, content type should be `application/x-www-form-urlencoded`
  // For SM, content type should be `application/json`
  String contentType;

  HttpClient client;

  public JWTAuthentication(
      @Value("${tasklist.client.authorizationUrl:'tasklist.camunda.io'}")
          String authorizationServerUrl,
      @Value("${tasklist.client.clientId:'tasklist'}") String clientId,
      @Value("${tasklist.client.clientSecret}") String clientSecret,
      @Value("${tasklist.client.contentType:'application/json'}") String contentType,
      @Value("${tasklist.client.audience:'tasklist.camunda.io'}") String audience) {

    this.authorizationServerUrl = authorizationServerUrl;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.contentType = contentType;
    this.audience = audience;

    client =
        HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
  }

  @Override
  public Boolean authenticate(TaskListRestClient taskListClient) throws TaskListException {

    try {

      System.out.println("Inside Authenticate: " + contentType);
      String body;
      if (contentType.equalsIgnoreCase("application/json")) {
        System.out.println("Inside contentType application/");
        AccessTokenRequest accessTokenRequest =
            new AccessTokenRequest(clientId, clientSecret, audience, CLIENT_CREDENTIALS);
        JsonUtils<AccessTokenRequest> jsonUtils = new JsonUtils<>(AccessTokenRequest.class);
        body = jsonUtils.toJson(accessTokenRequest);
        System.out.println("Inside body" + body);

      } else if (contentType.equals("application/x-www-form-urlencoded")) {

        Map<String, String> parameters = new HashMap<>();
        parameters.put("client_id", clientId);
        parameters.put("client_secret", clientSecret);
        parameters.put("audience", audience);
        parameters.put("grant_type", CLIENT_CREDENTIALS);

        body =
            parameters.entrySet().stream()
                .map(
                    e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
        System.out.println("Inside body 2: " + body);

      } else {
        System.out.println("Inside Exception: " + contentType);
        throw new TaskListException(
            "Content type must either be `json` or `x-www-form-urlencoded`");
      }

      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(new URI(authorizationServerUrl))
              .header("Content-Type", "application/x-www-form-urlencoded")
              .timeout(Duration.ofSeconds(10))
              .POST(HttpRequest.BodyPublishers.ofString(body))
              .build();
      System.out.println("Build Requst" + request.toString());
      System.out.println("Build header:" + request.headers());
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      JsonUtils<AccessTokenResponse> jsonUtils = new JsonUtils<>(AccessTokenResponse.class);
      System.out.println("response status Code : " + response.statusCode());
      System.out.println("response String : " + response.toString());
      System.out.println("response body : " + response.body());
      AccessTokenResponse accessTokenResponse = jsonUtils.fromJson(response.body());
      System.out.println("108");
      taskListClient.setAccessTokenResponse(accessTokenResponse);
      return true;

    } catch (URISyntaxException e) {
      throw new TaskListException("Authorization Server URL must be a valid URI", e);
    } catch (JsonProcessingException e) {
      throw new TaskListException("Unable to serialize AccessTokenRequest to json", e);
    } catch (IOException | InterruptedException e) {
      throw new TaskListException("Unable to send Access Token Request", e);
    }
  }
}
