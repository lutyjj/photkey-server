package com.lutyjj.photkey.controllers;

import com.drew.imaging.ImageProcessingException;
import com.lutyjj.photkey.models.Photo;
import com.lutyjj.photkey.services.PhotoService;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller for photo related requests.
 */
@RestController
@RequestMapping(path = "/api/photos")
public class PhotoController {

  private final static Logger LOG = LoggerFactory.getLogger(PhotoController.class);
  private final PhotoService photoService;

  @Autowired
  public PhotoController(PhotoService photoService) {
    this.photoService = photoService;
  }

  /**
   * Get all photos on the server.
   *
   * @return list of all photos
   */
  @GetMapping
  public List<Photo> getPhotos() {
    var photos = photoService.getPhotos();
    LOG.info("Photos found: {}", photos.size());
    return photos;
  }

  /**
   * Check if the photo exists.
   *
   * @param name name of the photo to check
   * @return true if the photo exists, false otherwise
   */
  @GetMapping(
      path = "/search",
      params = "name")
  public boolean existsByName(@RequestParam String name) {
    var result = photoService.existsByName(name);
    LOG.info("Photo with name {} exists: {}", name, result);
    return result;
  }

  /**
   * Get photo by id.
   *
   * @param id id of the photo
   * @return photo
   */
  @GetMapping(params = "id")
  public Optional<Photo> getPhotoById(@RequestParam Long id) {
    var photo = photoService.getPhotoById(id);
    LOG.info("Photo with id {} found: {}", id, photo.isPresent());
    return photo;
  }

  /**
   * Get list of all photos taken at given location.
   *
   * @param location location of the photos
   * @return list of photos
   */
  @GetMapping(params = "location")
  public List<Photo> getPhotosByLocation(@RequestParam String location) {
    var photos = photoService.getPhotosByLocation(location);
    LOG.info("Photos with location {} found: {}", location, photos.size());
    return photos;
  }

  /**
   * Get list of all photos taken at given date.
   *
   * @param date date of the photos
   * @return list of photos
   */
  @GetMapping(params = "date")
  public List<Photo> getPhotosByDate(@RequestParam String date) {
    try {
      var photos = photoService.getPhotosByDate(date);
      LOG.info("Photos with date {} found: {}", date, photos.size());
      return photos;
    } catch (ParseException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  /**
   * Get photo source file from given file name.
   *
   * @param imgName name of the photo
   * @return photo source file
   */
  @GetMapping(
      path = "{name}",
      produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
  public Resource getSrcByName(@PathVariable(value = "name") String imgName) {
    try {
      var src = photoService.getPhotoSrcByName(imgName);
      LOG.info("Photo with name {} found.", imgName);
      return src;
    } catch (IOException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Photo not found.", e);
    }
  }

  /**
   * Save photo to the database and local storage.
   *
   * @param multipartFile photo file
   * @return result of the operation
   */
  @PostMapping
  public ResponseEntity<?> savePhoto(@RequestParam("src") MultipartFile multipartFile) {
    try {
      var photo = photoService.savePhoto(multipartFile);
      LOG.info("Photo with name {} saved.", photo.getName());
      return ResponseEntity.ok(photo);
    } catch (UnknownHostException e) {
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
          "Nominatim service is unavailable", e);
    } catch (IOException e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Error while saving photo to local storage", e);
    } catch (ImageProcessingException e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Error while processing photo", e);
    } catch (DataIntegrityViolationException e) {
      throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
          "Error while saving photo to database", e);
    }
  }
}
