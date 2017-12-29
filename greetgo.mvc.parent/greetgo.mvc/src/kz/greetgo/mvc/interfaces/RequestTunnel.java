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
   * Предоставляет доступ к заголовкам запроса
   *
   * @return заголовки запроса
   */
  RequestHeaders requestHeaders();

  /**
   * Предоставляет доступ параметрам запроса
   *
   * @return параметры запроса
   */
  RequestParams requestParams();

  /**
   * Предоставляет возможность для работы с атрибутами запроса
   *
   * @return атрибуты запроса
   */
  RequestAttributes requestAttributes();

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
   * Производит форвардинг
   *
   * @param reference                    дислокация форвардинга
   * @param executeBeforeCompleteHeaders признак установки заголовков
   */
  void forward(String reference, boolean executeBeforeCompleteHeaders);
}
