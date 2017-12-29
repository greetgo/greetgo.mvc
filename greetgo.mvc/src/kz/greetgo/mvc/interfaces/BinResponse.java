package kz.greetgo.mvc.interfaces;

import java.io.OutputStream;

/**
 * This interface provides a convenient way to realize binary output
 */
public interface BinResponse {
  /**
   * <p>Define output filename. Really called:</p>
   * <p>
   * <code>
   * response.setHeader("Content-Disposition", "attachment; filename=" + outgoingFileName);
   * </code>
   * </p>
   *
   * @param filename output filename
   */
  void setFilename(String filename);

  /**
   * <p>Defines content type. Really called:</p>
   * <p>
   * <code>
   * response.setHeader("Content-Type", outgoingContentType);
   * </code>
   * </p>
   *
   * @param contentType outgoing content type
   */
  void setContentType(String contentType);

  /**
   * Called {@link #setContentType(String)} with value from extension of filename. This method must be called after
   * call method {@link #setContentType(String)}, else generates exception
   */
  void setContentTypeByFilenameExtension();

  /**
   * Provides output stream. This method can be called many times - all times return the same value
   *
   * @return output stream to load data onto client
   */
  OutputStream out();

  /**
   * Flushes output buffers
   */
  void flushBuffers();
}
