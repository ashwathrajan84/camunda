package org.example.camunda.process.solution.ash.authorization;

import io.camunda.zeebe.client.CredentialsProvider;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

public class MyCredentialsProvider implements CredentialsProvider {

  /** Adds a token to the Authorization header of a gRPC call. */
  @Override
  public void applyCredentials(final Metadata headers) {
    final Metadata.Key<String> authHeaderkey =
        Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
    headers.put(authHeaderkey, "Bearer token");
  }

  /** Retries request if it failed with a timeout. */
  @Override
  public boolean shouldRetryRequest(final Throwable throwable) {
    return ((StatusRuntimeException) throwable).getStatus() == Status.DEADLINE_EXCEEDED;
  }
}
