package kz.greetgo.mvc.core;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class MimeUtilTest {

  @DataProvider
  public Object[][] contentTypeFromFilename_DataProvider() {
    return new Object[][]{
      {"   asd/hello.txt   ", "text/plain"},
      {"   asd/hello.html  ", "text/html"},
      {"   asd/hello.htm   ", "text/html"},
      {"   asd/hello.json  ", "application/json"},
      {"   asd/hello.pdf   ", "application/pdf"},
      {"   ASD/HELLO.PDF   ", "application/pdf"},


      {"   asd/hello.doc    ", "application/msword"},
      {"   asd/hello.dot    ", "application/msword"},
      {"   asd/hello.docx   ", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
      {"   asd/hello.dotx   ", "application/vnd.openxmlformats-officedocument.wordprocessingml.template"},
      {"   asd/hello.docm   ", "application/vnd.ms-word.document.macroEnabled.12"},
      {"   asd/hello.dotm   ", "application/vnd.ms-word.template.macroEnabled.12"},
      {"   asd/hello.xls    ", "application/vnd.ms-excel"},
      {"   asd/hello.xlt    ", "application/vnd.ms-excel"},
      {"   asd/hello.xla    ", "application/vnd.ms-excel"},
      {"   asd/hello.xlsx   ", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
      {"   asd/hello.xltx   ", "application/vnd.openxmlformats-officedocument.spreadsheetml.template"},
      {"   asd/hello.xlsm   ", "application/vnd.ms-excel.sheet.macroEnabled.12"},
      {"   asd/hello.xltm   ", "application/vnd.ms-excel.template.macroEnabled.12"},
      {"   asd/hello.xlam   ", "application/vnd.ms-excel.addin.macroEnabled.12"},
      {"   asd/hello.xlsb   ", "application/vnd.ms-excel.sheet.binary.macroEnabled.12"},
      {"   asd/hello.ppt    ", "application/vnd.ms-powerpoint"},
      {"   asd/hello.pot    ", "application/vnd.ms-powerpoint"},
      {"   asd/hello.pps    ", "application/vnd.ms-powerpoint"},
      {"   asd/hello.ppa    ", "application/vnd.ms-powerpoint"},
      {"   asd/hello.pptx   ", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
      {"   asd/hello.potx   ", "application/vnd.openxmlformats-officedocument.presentationml.template"},
      {"   asd/hello.ppsx   ", "application/vnd.openxmlformats-officedocument.presentationml.slideshow"},
      {"   asd/hello.ppam   ", "application/vnd.ms-powerpoint.addin.macroEnabled.12"},
      {"   asd/hello.pptm   ", "application/vnd.ms-powerpoint.presentation.macroEnabled.12"},
      {"   asd/hello.potm   ", "application/vnd.ms-powerpoint.template.macroEnabled.12"},
      {"   asd/hello.ppsm   ", "application/vnd.ms-powerpoint.slideshow.macroEnabled.12"},

    };
  }

  @Test(dataProvider = "contentTypeFromFilename_DataProvider")
  public void contentTypeFromFilename(String filename, String expectedContentType) throws Exception {
    String actualContentType = MimeUtil.mimeTypeFromFilename(filename.trim());
    assertThat(actualContentType).isEqualTo(expectedContentType);
  }
}