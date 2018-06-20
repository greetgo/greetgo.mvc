package kz.greetgo.mvc.war.example.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.XStream;
import kz.greetgo.mvc.annotations.ParSession;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.mvc.annotations.ToXml;
import kz.greetgo.mvc.interfaces.MethodInvokedResult;
import kz.greetgo.mvc.interfaces.MethodInvoker;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.interfaces.SessionParameterGetter;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * В этом классе реализована обработка методов контроллеров
 */
public class ViewsExample implements kz.greetgo.mvc.interfaces.Views {

  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Этот метод вызывается после вызова метода контроллера помеченного аннотацией {@link ToJson}.
   *
   * @param object сюда подаётся объект, который вернул метод контроллера
   * @param tunnel это тунель запроса (через него можно управлять процессом запроса)
   * @param method ссылка на метод контроллера, который был только-что вызван
   * @return эта строка будет отправлена в качестве тела ответа на запрос, зашифрованной в кодировке UTF-8.
   * @throws Exception нужно чтобы не ставить надоедливые try/catch-блоки
   */
  @Override
  public String toJson(Object object, RequestTunnel tunnel, Method method) throws Exception {
    System.out.println("-----------> Calling method " + getClass() + ".toJson(...)");
    return convertToJson(object);
  }

  private String convertToJson(Object object) throws Exception {
    if (object == null) return null;
    return objectMapper.writer().writeValueAsString(object);
  }

  /**
   * Этот метод вызывается после вызова метода контроллера помеченного аннотацией {@link ToXml}.
   *
   * @param object сюда подаётся объект, который вернул метод контроллера
   * @param tunnel это тунель запроса (через него можно управлять процессом запроса)
   * @param method ссылка на метод контроллера, который был только-что вызван
   * @return эта строка будет отправлена в качестве тела ответа на запрос, зашифрованной в кодировке UTF-8.
   * @throws Exception нужно чтобы не ставить надоедливые try/catch-блоки
   */
  @Override
  public String toXml(Object object, RequestTunnel tunnel, Method method) throws Exception {
    System.out.println("-----------> Calling method " + getClass() + ".toXml(...)");
    //Здесь нужно object преобразовать в XML и вернуть
    XStream xstream = new XStream();
    return xstream.toXML(object);
  }

  /**
   * Этот метод вызывается, каждый раз при обработке запроса. Метод контроллера ещё не вызван, и его нужно вызвать
   * из этого метода. А можно и не вызвать, например потому-что нет прав или ещё по какой причине.
   *
   * @param methodInvoker исполнитель метода контроллера - вспомогательный объект, в котором подготовлено всё
   *                      необходимое для вызова метода контроллера,
   *                      и для изучения вызываемого метода контроллера. Например можно посмотреть какие аннотации есть
   *                      у метода и провести дополнительные манипуляции.
   * @throws Exception нужно чтобы не ставить надоедливые try/catch-блоки
   */
  @Override
  public void performRequest(MethodInvoker methodInvoker) throws Exception {

    System.out.println("     " + methodInvoker.tunnel().getRequestMethod() + " " + methodInvoker.tunnel().getTarget());

    //вызываем этот метод, чтобы в дальнейшем можно было получить момент непосредственно перед вызовом метода контроллера
    beforeRequest();

    //вызываем метод контроллера и получаем результат вызова метода
    MethodInvokedResult invokedResult = methodInvoker.invoke();

    //пытаемся зарендерить результат поведением по-умолчанию. Таким поведением являются аннотации: ToJson, ToXml, AsIs
    if (invokedResult.tryDefaultRender()) {
      //поведение по-умолчанию получилось применить. Это значит что запрос полностью обработан и
      //больше ничего делать не нужно - выходим
      return;
    }

    //здесь нужно обработать специфичный результат работы метода контроллера, например прорендерить JSP или оттобразить
    //ошибку, или ещё что-то

    //смотрим была ли ошибка в метода
    if (invokedResult.error() != null) {
      //обрабатываем ошибку
      performError(methodInvoker, invokedResult);
    } else {
      //обрабатываем нормальное поведение
      performRender(methodInvoker, invokedResult);
    }
  }

  /**
   * Этот метод вызывается всегда перед вызовом метода контроллера
   *
   * @throws Exception нужно чтобы не ставить надоедливые try/catch-блоки
   */
  protected void beforeRequest() throws Exception {}

  /**
   * Этот метод вызывается, когда необходимо заполнить параметр метода контроллера
   * помеченный аннотацией {@link ParSession}
   *
   * @param context информация о параметре: что за параметр, его тип и пр.
   * @param tunnel  тунель запроса - дан для того, чтобы можно было получить какие-нибудь данные для параметра
   * @return значение этого параметра: оно будет подставлено в этот параметр
   */
  @Override
  public Object getSessionParameter(SessionParameterGetter.ParameterContext context, RequestTunnel tunnel) {
    if ("personId".equals(context.parameterName())) {
      if (context.expectedReturnType() != Long.class) {
        throw new RuntimeException("Session parameter `personId` must be a Long");
      }
      return 543265L;
    }

    if ("role".equals(context.parameterName())) {
      if (context.expectedReturnType() != String.class) {
        throw new RuntimeException("Session parameter `role` must be a string");
      }
      return "role value taken from session";
    }

    throw new RuntimeException("Unknown session parameter " + context.parameterName());
  }

  /**
   * Здесь происходиь рендеринг вьюшки запроса
   *
   * @param methodInvoker исполнитель метода контроллера
   * @param invokedResult результаты вызова метода контроллера
   */
  private void performRender(MethodInvoker methodInvoker, MethodInvokedResult invokedResult) {
    assert invokedResult.error() == null;
    //возвращённое методом контроллера значение
    Object returnedValue = invokedResult.returnedValue();
    if (returnedValue == null) return;

    //обрабатываем только строки. Не понятно как обрабатывать другие типы.
    if (!(returnedValue instanceof String)) {
      throw new IllegalArgumentException("Cannot view " + returnedValue.getClass()
        + " with value " + returnedValue);
    }

    //предполагаем, что возвращённое значение - это локальный путь к jsp-файлу, например: jsp/hello.jsp
    String place = (String) returnedValue;

    RequestTunnel tunnel = methodInvoker.tunnel();

    //заполняем данные для вьюшки, которые будут доступны через $ например $hello - в методе их добавляли в MvcModel
    for (Map.Entry<String, Object> e : methodInvoker.model().data.entrySet()) {
      tunnel.requestAttributes().set(e.getKey(), e.getValue());
    }

    //форвардим на рендеринг jsp-файла
    tunnel.forward("/jsp/" + place, true);
  }

  /**
   * Обрабатывается ошибка запроса
   *
   * @param methodInvoker информация о методе контроллера для обработки запроса
   * @param invokedResult результаты вызова метода контроллера
   * @throws Exception нужно чтобы не ставить надоедливые try/catch-блоки
   */
  private void performError(MethodInvoker methodInvoker, MethodInvokedResult invokedResult) throws Exception {
    Throwable error = invokedResult.error();
    assert error != null;

    error.printStackTrace();

    RequestTunnel tunnel = methodInvoker.tunnel();
    tunnel.requestAttributes().set("ERROR_TYPE", error.getClass().getSimpleName());

    {
      tunnel.setResponseStatus(500);
      try (final PrintWriter writer = tunnel.getResponseWriter()) {
        writer.println("Internal server error: " + error.getMessage());
        writer.println();
        error.printStackTrace(writer);
      }

      error.printStackTrace();
    }

  }

}