package com.lutyjj.photkey.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a photo.
 */
@Entity
@Table
public class Photo {

  @Id
  @SequenceGenerator(name = "photos_sequence", sequenceName = "photos_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "photos_sequence")
  @Getter
  @Setter
  private Long id;

  @Getter
  @Setter
  private Date date;

  @Getter
  @Setter
  @Column(unique = true)
  private String name;

  @Getter
  @Setter
  private String location;

  @JsonIgnore
  @Getter
  @Setter
  private String localPath;

  public Photo(Date date, String name, String location, String localPath) {
    this.date = date;
    this.name = name;
    this.location = location;
    this.localPath = localPath;
  }

  public Photo() {
  }
}
