import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HealthCheck
{
  public static void main(String[] args) throws java.lang.Throwable
  {

    HttpClient httpClient = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create(args[0]))
      .build();

    HttpResponse<Void> response = httpClient.send(request,
      HttpResponse.BodyHandlers.discarding());

    System.exit(response.statusCode() == 200 ? 0 : 1);
  }
}
