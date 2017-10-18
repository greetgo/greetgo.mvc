package kz.greetgo.mvc.interfaces;

import kz.greetgo.mvc.core.RequestMethod;
import kz.greetgo.mvc.model.UploadInfo;
import kz.greetgo.util.events.EventHandlerList;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Агрегирует в седе и запрос и ответ
 */
public interface RequestTunnel {

  /**
   * Предоставляет часть request URI без контекста приложения
   *
   * @return часть request URI без контекста приложения
   */
  String getTarget();

  /**
   * Предоаставляет райтер в поток ответа
   *
   * @return райтер в поток ответа
   */
  PrintWriter getResponseWriter();

  /**
   * Предоаставляет поток ответа
   *
   * @return поток ответа
   */
  OutputStream getResponseOutputStream();

  /**
   * Предоставляет значения указанного параметра
   *
   * @param paramName имя указанного параметра
   * @return значения указанного параметра
   */
  String[] getParamValues(String paramName);

  /**
   * Предоставляет ридер для чтения данных с тела запроса
   *
   * @return ридер для чтения данных с тела запроса
   */
  BufferedReader getRequestReader();

  /**
   * Предоставляет поток с тела запроса
   *
   * @return поток с тела запроса
   */
  InputStream getRequestInputStream();

  /**
   * Предоаставляет параметр как файл загрузки
   *
   * @param paramName имя параметра
   * @return параметр как файл загрузки
   */
  Upload getUpload(String paramName);

  /**
   * Посылает в ответ запроса ридирект
   *
   * @param reference содержание ридиректа
   */
  void sendRedirect(String reference);

  /**
   * Устанавливает загаловок в ответ запроса о размере тела ответа запроса
   *
   * @param length размер тела ответа запроса
   */
  void setResponseContentLength(int length);

  void enableMultipartSupport(UploadInfo uploadInfo);

  void removeMultipartData();

  /**
   * Считывает заголовок, в котором указан тип тела запроса
   *
   * @return тип тела запроса
   */
  String getRequestContentType();

  boolean isExecuted();

  void setExecuted(boolean executed);

  /**
   * Предоставляет HTTP-метод запроса
   *
   * @return HTTP-метод запроса
   */
  RequestMethod getRequestMethod();

  /**
   * Предоставляет доступ к кукисам запроса и позволяет добавлять кукисы в ответ
   *
   * @return доступ к кукисам запроса и возможность добавления кукисов в ответ
   */
  TunnelCookies cookies();

  EventHandlerList eventBeforeCompleteHeaders();

  /**
   * Сброс буферов в ответ запроса
   */
  void flushBuffer();

  /**
   * Устанавливает заголовок ответа, в котором указывается тип тела ответа
   *
   * @param contentType значение заголовока ответа, в котором указывается тип тела ответа
   */
  void setResponseContentType(String contentType);

  /**
   * Предоставляет доступ к параметру заголовка запроса
   *
   * @param headerName имя параметра
   * @return значение параметра
   */
  String getRequestHeader(String headerName);

  /**
   * Отправляет параметр в заголовок ответа как дату
   *
   * @param headerName  имя отправляемого параметра
   * @param headerValue значение, как величина new Date().getTime()
   */
  void setResponseDateHeader(String headerName, long headerValue);

  /**
   * Отправляет заголовок со статусом ответа
   *
   * @param statusCode статус ответа
   */
  void setResponseStatus(int statusCode);

  /**
   * Отправляет параметр в заголовок ответа
   *
   * @param headerName  имя отправляемого параметра
   * @param headerValue значение отправляемого параметра
   */
  void setResponseHeader(String headerName, String headerValue);

  /**
   * Получает параметр из заголовка запроса, как величину new Date().getTime()
   *
   * @param headerName имя запрашиваемого параметра
   * @return значение, как величина new Date().getTime()
   */
  long getRequestDateHeader(String headerName);

  /**
   * Производит форвардинг
   *
   * @param reference                    дислокация форвардинга
   * @param executeBeforeCompleteHeaders признак установки заголовков
   */
  void forward(String reference, boolean executeBeforeCompleteHeaders);

  /**
   * Добавляет/устанавливает атрибут в инфраструктуру запроса
   *
   * @param name  имя атрибута
   * @param value значение атрибута
   */
  void setRequestAttribute(String name, Object value);
}
