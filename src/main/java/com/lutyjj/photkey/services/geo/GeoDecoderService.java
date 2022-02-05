package com.lutyjj.photkey.services.geo;

import com.lutyjj.photkey.models.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Service for decoding location from coordinates
 */
@Service
public record GeoDecoderService(WebClient mapApi) {

  private static final Logger LOG = LoggerFactory.getLogger(GeoDecoderService.class);

  @Autowired
  public GeoDecoderService {
  }

  /**
   * Gets city name from latitude and longitude.
   *
   * @param latitude  latitude
   * @param longitude longitude
   * @return city name
   */
  public String getCity(double latitude, double longitude) {
    var location =
        mapApi
            .get()
            .uri(
                "?format=json&lat="
                    + latitude
                    + "&lon="
                    + longitude
                    + "&zoom=10&accept-language=en&addressdetails=0")
            .retrieve()
            .bodyToMono(Location.class)
            .share()
            .block();

    if (location != null) {
      LOG.info("Nominatim API response: {}", location.getCity());
      return location.getCity();
    }
    return null;
  }
}
