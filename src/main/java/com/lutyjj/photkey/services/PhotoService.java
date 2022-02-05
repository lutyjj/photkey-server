package com.lutyjj.photkey.services;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.lutyjj.photkey.models.Photo;
import com.lutyjj.photkey.services.geo.GeoDecoderService;
import com.lutyjj.photkey.storage.PhotoRepository;
import com.lutyjj.photkey.util.FileUploadUtil;
import com.lutyjj.photkey.util.meta.MetadataReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service for photo management.
 */
@Service
public class PhotoService {

  private final Logger LOG = LoggerFactory.getLogger(PhotoService.class);
  private final PhotoRepository photoRepository;
  private final GeoDecoderService geoDecoderService;
  private final SimpleDateFormat dateFormat;

  @Value("${upload-dir}")
  private String uploadDir;

  @Autowired
  public PhotoService(PhotoRepository photoRepository, GeoDecoderService geoDecoderService,
      @Value("${date-pattern}") String datePattern) {
    this.photoRepository = photoRepository;
    this.geoDecoderService = geoDecoderService;
    this.dateFormat = new SimpleDateFormat(datePattern);
  }

  /**
   * Get all photos.
   *
   * @return list of photos
   */
  public List<Photo> getPhotos() {
    return photoRepository.findAllByOrderByDateDesc();
  }


  /**
   * Saves photo as MultipartFile to both local storage and database.
   *
   * @param photoSrc photo MultipartFile
   * @return photo
   * @throws IOException              on file writing error
   * @throws ImageProcessingException on metadata reading error
   */
  public Photo savePhoto(MultipartFile photoSrc)
      throws IOException, ImageProcessingException {
    var photo = createPhotoFromSrc(photoSrc);
    photoRepository.save(photo);
    FileUploadUtil.saveFile(photo.getLocalPath(), photo.getName(), photoSrc);
    return photo;
  }

  /**
   * Creates photo from MultipartFile.
   *
   * @param photoSrc photo MultipartFile
   * @return photo
   * @throws ImageProcessingException on metadata reading error
   * @throws IOException              on file reading error
   */
  private Photo createPhotoFromSrc(MultipartFile photoSrc)
      throws ImageProcessingException, IOException {
    var metadata = ImageMetadataReader.readMetadata(photoSrc.getInputStream());
    var date = MetadataReader.readDate(metadata);
    if (date == null) {
      date = new Date();
    }
    var location = MetadataReader.readLocation(metadata, geoDecoderService);

    var formattedDate = dateFormat.format(date);
    var localPath = uploadDir + formattedDate + "/";

    return new Photo(date, photoSrc.getOriginalFilename(), location, localPath);
  }

  /**
   * Gets photo by id.
   *
   * @param id photo id
   * @return photo
   */
  public Optional<Photo> getPhotoById(Long id) {
    return photoRepository.findById(id);
  }

  /**
   * Gets photos by location.
   *
   * @param location photo location
   * @return list of photos
   */
  public List<Photo> getPhotosByLocation(String location) {
    return photoRepository.findAllByLocationContainsOrderByDateDesc(location);
  }

  /**
   * Gets photo source by id.
   *
   * @param id photo id
   * @return photo source
   * @throws IOException on file reading error
   */
  public Resource getPhotoSrcById(Long id) throws IOException {
    var photo = photoRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    var stream = new FileInputStream(photo.getLocalPath() + photo.getName());
    return new ByteArrayResource(stream.readAllBytes());
  }

  /**
   * Gets photo source by name.
   *
   * @param imgName photo name
   * @return photo source
   * @throws IOException on file reading error
   */
  public Resource getPhotoSrcByName(String imgName) throws IOException {
    var photo = photoRepository.findByName(imgName)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    var stream = new FileInputStream(photo.getLocalPath() + photo.getName());
    return new ByteArrayResource(stream.readAllBytes());
  }

  /**
   * Gets photos by date.
   *
   * @param date photo date
   * @return list of photos
   * @throws ParseException on date parsing error
   */
  public List<Photo> getPhotosByDate(String date) throws ParseException {
    var parsedDate = dateFormat.parse(date);
    var nextDay = new Date(parsedDate.getTime() + TimeUnit.DAYS.toMillis(1));
    return photoRepository.findAllByDateBetweenOrderByDateDesc(parsedDate, nextDay);
  }

  /**
   * Checks if photo with such name exists.
   *
   * @param name photo name
   * @return true if photo with such name exists, false otherwise
   */
  public boolean existsByName(String name) {
    return photoRepository.existsByName(name);
  }
}
