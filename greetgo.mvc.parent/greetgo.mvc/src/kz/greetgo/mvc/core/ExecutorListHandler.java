package kz.greetgo.mvc.core;

import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.interfaces.TunnelExecutor;
import kz.greetgo.mvc.interfaces.TunnelExecutorGetter;
import kz.greetgo.mvc.interfaces.TunnelHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static kz.greetgo.mvc.core.RequestMethod.POST;

public class ExecutorListHandler implements TunnelHandler {
  public static final String MULTIPART_FORM_DATA_TYPE = "multipart/form-data";

  public final List<TunnelExecutorGetter> tunnelExecutorGetters = new ArrayList<>();

  public ExecutorListHandler() {
  }
  public ExecutorListHandler(Collection<TunnelExecutorGetter> tunnelExecutorGetters) {
    this.tunnelExecutorGetters.addAll(tunnelExecutorGetters);
  }


  public static boolean isMultipart(String contentType) {
    return contentType != null && contentType.startsWith(MULTIPART_FORM_DATA_TYPE);
  }

  @Override
  public void handleTunnel(RequestTunnel tunnel) {
    final TunnelExecutor tunnelExecutor = getTunnelHandler(tunnel);
    if (tunnelExecutor == null) return;

    boolean multipartRequest = (tunnel.getRequestMethod() == POST) && isMultipart(tunnel.getRequestContentType());

    if (multipartRequest) {
      tunnel.enableMultipartSupport(tunnelExecutor.getUploadInfo());
    }

    try {
      tunnelExecutor.execute();
      tunnel.setExecuted(true);
    } finally {

      if (multipartRequest) {
        tunnel.removeMultipartData();
      }

    }
  }

  private TunnelExecutor getTunnelHandler(RequestTunnel tunnel) {
    for (TunnelExecutorGetter tunnelExecutorGetter : tunnelExecutorGetters) {
      final TunnelExecutor tunnelExecutor = tunnelExecutorGetter.getTunnelExecutor(tunnel);
      if (tunnelExecutor != null) return tunnelExecutor;
    }
    return null;
  }
}
