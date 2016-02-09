package kz.greetgo.mvc.core;

import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.interfaces.TunnelExecutor;
import kz.greetgo.mvc.interfaces.TunnelExecutorGetter;
import kz.greetgo.mvc.model.UploadInfo;
import kz.greetgo.mvc.util.Base64Util;
import kz.greetgo.mvc.util.BytesUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileResourceTunnelExecutorGetter implements TunnelExecutorGetter {

  public boolean useETag = false;

  public final List<String> wellComeFiles = new ArrayList<>();

  private final List<String> resourceDirList = new ArrayList<>();

  private static Map<File, FileCache> cache = new ConcurrentHashMap<>();

  public FileResourceTunnelExecutorGetter(String... resourceDirs) {
    Collections.addAll(resourceDirList, resourceDirs);
  }

  @Override
  public TunnelExecutor getTunnelExecutor(RequestTunnel tunnel) {

    for (String resourceDir : resourceDirList) {
      File file = new File(resourceDir + tunnel.getTarget());
      if (file.isFile()) return executorForFile(file, tunnel);
      if (file.isDirectory()) {
        final TunnelExecutor exe = executorForDir(file, tunnel);
        if (exe != null) return exe;
      }
    }

    return null;
  }

  private TunnelExecutor executorForDir(File dir, RequestTunnel tunnel) {
    if (!dir.exists()) return null;

    for (String wellComeFile : wellComeFiles) {
      final File file = new File(dir.getPath() + File.separator + wellComeFile);
      if (file.exists()) return executorForFile(file, tunnel);
    }

    return null;
  }

  private TunnelExecutor executorForFile(final File file, final RequestTunnel tunnel) {
    if (!file.exists()) return null;

    return new TunnelExecutor() {
      @Override
      public void execute() {
        try {
          shoveFileInTunnel(file, tunnel);
          tunnel.setExecuted(true);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }

      @Override
      public UploadInfo getUploadInfo() {
        return null;
      }
    };
  }

  private class FileCache {
    private final File file;

    public FileCache(File file) {
      this.file = file;
    }

    volatile byte[] fileContent = null;
    volatile long prevLastModifiedAt = 0;

    public void shoveIn(RequestTunnel tunnel) throws Exception {
      if (!file.exists()) {
        if (fileContent != null) {
          shoveContentIn(tunnel);
          return;
        }
        throw new RuntimeException("File does not exists: " + file);
      }

      final long lastModified = file.lastModified();

      if (fileContent != null && lastModified == prevLastModifiedAt) {
        prevLastModifiedAt = lastModified;

        shoveContentIn(tunnel);
        return;
      }

      prevLastModifiedAt = lastModified;

      readFileContent();

      shoveContentIn(tunnel);
    }

    private synchronized void readFileContent() throws Exception {
      ByteArrayOutputStream bout = new ByteArrayOutputStream((int) file.length());

      try (FileInputStream in = new FileInputStream(file)) {
        byte[] buffer = new byte[1024 * 8];
        while (true) {
          final int count = in.read(buffer);
          if (count < 0) break;
          bout.write(buffer, 0, count);
        }
      }

      fileContent = bout.toByteArray();
    }

    private void shoveContentIn(RequestTunnel tunnel) throws Exception {
      String eTag = null;
      if (useETag) {
        final String currentETag = tunnel.getRequestHeader("If-None-Match");
        eTag = getWeakETag();
        if (currentETag != null && currentETag.equals(eTag)) {
          tunnel.setResponseStatus(304);//NOT MODIFIED
          tunnel.setResponseHeader("ETag", eTag);
          tunnel.setExecuted(true);
          return;
        }
      }

      if (prevLastModifiedAt > 0) {
        final long lastModifiedFromHeader = tunnel.getRequestDateHeader("If-Modified-Since");
        if (lastModifiedFromHeader > 0 && prevLastModifiedAt / 1000 <= lastModifiedFromHeader / 1000) {
          tunnel.setResponseStatus(304);//NOT MODIFIED
          tunnel.setExecuted(true);
          return;
        }
      }

      if (eTag != null) tunnel.setResponseHeader("ETag", eTag);

      tunnel.setResponseContentType(Files.probeContentType(file.toPath()));
      final OutputStream out = tunnel.getResponseOutputStream();
      out.write(fileContent);
      out.flush();
      tunnel.flushBuffer();

      tunnel.setExecuted(true);
    }

    public String getWeakETag() {
      StringBuilder b = new StringBuilder(32);
      b.append("W/\"");

      String name = file.getName();
      int length = name.length();
      long lowHash = 0;
      for (int i = 0; i < length; i++) {
        lowHash = 31 * lowHash + name.charAt(i);
      }

      byte[] bytes = new byte[16];
      BytesUtil.putLong(bytes, 0, prevLastModifiedAt ^ lowHash);
      BytesUtil.putLong(bytes, 8, file.length() ^ lowHash);
      b.append(Base64Util.bytesToBase64(bytes));
      b.append('"');
      return b.toString();
    }
  }

  private void shoveFileInTunnel(File file, RequestTunnel tunnel) throws Exception {
    FileCache fileCache = cache.get(file);
    if (fileCache == null) {
      fileCache = new FileCache(file);
      cache.put(file, fileCache);
    }

    fileCache.shoveIn(tunnel);
  }
}
