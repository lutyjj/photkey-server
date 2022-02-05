package com.lutyjj.photkey.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {
  private static final Logger LOG = LoggerFactory.getLogger(FileUploadUtil.class);

  private FileUploadUtil() {
  }

  /**
   * Writes given multipart file to the given path with the given file name.
   *
   * @param uploadDir     target directory
   * @param fileName      file name
   * @param multipartFile multipart file
   * @throws IOException on file write error
   */
  public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile)
      throws IOException {
    var uploadPath = Paths.get(uploadDir);
    LOG.info("Uploading file to {}", uploadPath);
    if (!Files.exists(uploadPath))
      Files.createDirectories(uploadPath);

    var inputStream = multipartFile.getInputStream();
    var filePath = uploadPath.resolve(fileName);
    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
    inputStream.close();
  }
}
