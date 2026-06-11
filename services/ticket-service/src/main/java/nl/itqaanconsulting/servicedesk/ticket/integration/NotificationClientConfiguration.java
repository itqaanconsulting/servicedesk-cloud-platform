package nl.itqaanconsulting.servicedesk.ticket.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
class NotificationClientConfiguration {

    @Bean
    RestClient notificationRestClient(
            RestClient.Builder builder,
            @Value("${clients.notification-service.base-url}") String baseUrl,
            @Value("${clients.notification-service.connect-timeout}") Duration connectTimeout,
            @Value("${clients.notification-service.read-timeout}") Duration readTimeout
    ) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(connectTimeout)
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(readTimeout);

        return builder
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }
}
