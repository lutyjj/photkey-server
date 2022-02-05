package com.lutyjj.photkey.services.geo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for the GeoDecoder service.
 */
@Configuration
public class GeoDecoderConfig {

  @Bean
  public WebClient openStreetMapApiClient() {
    return WebClient.create("https://nominatim.openstreetmap.org/reverse");
  }
}
