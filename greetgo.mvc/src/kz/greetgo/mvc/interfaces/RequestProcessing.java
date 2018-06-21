package kz.greetgo.mvc.interfaces;

import kz.greetgo.mvc.builder.ExecDefinition;

import java.util.List;

public interface RequestProcessing {
  void processRequest(RequestTunnel tunnel) throws Exception;

  List<ExecDefinition> execDefinitionList();
}
