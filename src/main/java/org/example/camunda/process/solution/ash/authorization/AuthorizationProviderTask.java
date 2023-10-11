package org.example.camunda.process.solution.ash.authorization;

import io.camunda.tasklist.CamundaTaskListClient;
import io.camunda.tasklist.auth.SelfManagedAuthentication;
import io.camunda.tasklist.exception.TaskListException;

/**
 * Example application that connects to a locally deployed cluster. When connecting to a cluster,
 * this application uses the following default information to connect to a cluster with Camunda's
 * Identity as an identity provider option:
 *
 * <ul>
 *   <li>ZEEBE_ADDRESS
 *   <li>ZEEBE_CLIENT_ID
 *   <li>ZEEBE_CLIENT_SECRET
 *   <li>ZEEBE_AUTHORIZATION_SERVER_URL
 * </ul>
 */
public class AuthorizationProviderTask {
  public static void main(final String[] args) {

    CamundaTaskListClient client = null;

    final String ZEEBE_ADDRESS = "localhost:8084";
    final String ZEEBE_CLIENT_ID = "tasklist";
    final String ZEEBE_CLIENT_SECRET = "XALaRPl5qwTEItdwCMiPS62nVpKs7dL7";
    final String ZEEBE_CLIENT_AUDIENCE = "tasklist-api";
    final String ZEEBE_AUTHORIZATION_SERVER_URL =
        "http://localhost:18080/auth/realms/camunda-platform/protocol/openid-connect/token";

    // Self Managed
    SelfManagedAuthentication sma =
        new SelfManagedAuthentication()
            .clientId(ZEEBE_CLIENT_ID)
            .clientSecret(ZEEBE_CLIENT_SECRET)
            .keycloakUrl("http://localhost:18080");
    try {
      client =
          new CamundaTaskListClient.Builder()
              .shouldReturnVariables()
              .taskListUrl("http://localhost:8082")
              .authentication(sma)
              .build();
    } catch (TaskListException e) {
      e.printStackTrace();
    }

    // System.out.println("Zeebe topology: " +
    // client.newTopologyRequest().send().join().toString());

  }
}
