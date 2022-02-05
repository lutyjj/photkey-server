package com.lutyjj.photkey.storage;

import com.lutyjj.photkey.models.Photo;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for photos. Contains methods for storing and retrieving photos.
 */
@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

  /**
   * Finds all photos where location contains the given string. Order by date descending.
   *
   * @param location location to search for
   * @return list of photos
   */
  List<Photo> findAllByLocationContainsOrderByDateDesc(String location);

  /**
   * Finds all photos between the given dates. Order by date descending.
   *
   * @param dateStart start date
   * @param dateEnd   end date
   * @return list of photos
   */
  List<Photo> findAllByDateBetweenOrderByDateDesc(Date dateStart, Date dateEnd);

  /**
   * Finds all photos. Order by date descending.
   *
   * @return list of photos
   */
  List<Photo> findAllByOrderByDateDesc();

  /**
   * Checks if the photo with such name exists.
   *
   * @param name name of the photo to check
   * @return true if photo exists, false otherwise
   */
  boolean existsByName(String name);

  /**
   * Finds photo by name.
   *
   * @param imgName name of the photo
   * @return photo
   */
  Optional<Photo> findByName(String imgName);
}
