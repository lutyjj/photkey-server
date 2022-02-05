package com.lutyjj.photkey.util.meta;

import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.lutyjj.photkey.services.geo.GeoDecoderService;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class MetadataReader {

  /**
   * Reads the GPS data from the metadata. Decodes longitude and latitude to country and city via
   * the GeoDecoderService.
   *
   * @param metadata          metadata to read from
   * @param geoDecoderService service to use to decode the GPS data
   * @return city where the photo was taken
   */
  public static String readLocation(Metadata metadata, GeoDecoderService geoDecoderService) {
    var gps = metadata.getFirstDirectoryOfType(GpsDirectory.class);
    
    if (gps != null) {
      var geoLocation = gps.getGeoLocation();
      if (geoLocation != null) {
        return geoDecoderService.getCity(
            geoLocation.getLatitude(), geoLocation.getLongitude());
      }
    }
    return null;
  }

  /**
   * Reads the date from the metadata.
   *
   * @param metadata metadata to read from
   * @return date when the photo was taken
   */
  public static Date readDate(Metadata metadata) {
    var exif = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

    return exif != null ? exif.getDateOriginal() : null;
  }
}
