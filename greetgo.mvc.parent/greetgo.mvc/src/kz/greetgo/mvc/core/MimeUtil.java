package kz.greetgo.mvc.core;

import kz.greetgo.mvc.errors.UnknownMimeTypeForExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MimeUtil {

  public static final String DEFAULT_MIME_TYPE = "text/plain";

  private static final Map<String, String> fileExtensionMap;

  static {
    //language=TEXT
    String source = "// MS Office\n" +
      "doc   application/msword\n" +
      "dot   application/msword\n" +
      "docx  application/vnd.openxmlformats-officedocument.wordprocessingml.document\n" +
      "dotx  application/vnd.openxmlformats-officedocument.wordprocessingml.template\n" +
      "docm  application/vnd.ms-word.document.macroEnabled.12\n" +
      "dotm  application/vnd.ms-word.template.macroEnabled.12\n" +
      "xls   application/vnd.ms-excel\n" +
      "xlt   application/vnd.ms-excel\n" +
      "xla   application/vnd.ms-excel\n" +
      "xlsx  application/vnd.openxmlformats-officedocument.spreadsheetml.sheet\n" +
      "xltx  application/vnd.openxmlformats-officedocument.spreadsheetml.template\n" +
      "xlsm  application/vnd.ms-excel.sheet.macroEnabled.12\n" +
      "xltm  application/vnd.ms-excel.template.macroEnabled.12\n" +
      "xlam  application/vnd.ms-excel.addin.macroEnabled.12\n" +
      "xlsb  application/vnd.ms-excel.sheet.binary.macroEnabled.12\n" +
      "ppt   application/vnd.ms-powerpoint\n" +
      "pot   application/vnd.ms-powerpoint\n" +
      "pps   application/vnd.ms-powerpoint\n" +
      "ppa   application/vnd.ms-powerpoint\n" +
      "pptx  application/vnd.openxmlformats-officedocument.presentationml.presentation\n" +
      "potx  application/vnd.openxmlformats-officedocument.presentationml.template\n" +
      "ppsx  application/vnd.openxmlformats-officedocument.presentationml.slideshow\n" +
      "ppam  application/vnd.ms-powerpoint.addin.macroEnabled.12\n" +
      "pptm  application/vnd.ms-powerpoint.presentation.macroEnabled.12\n" +
      "potm  application/vnd.ms-powerpoint.template.macroEnabled.12\n" +
      "ppsm  application/vnd.ms-powerpoint.slideshow.macroEnabled.12\n" +
      "// Open Office\n" +
      "odt application/vnd.oasis.opendocument.text\n" +
      "ott application/vnd.oasis.opendocument.text-template\n" +
      "oth application/vnd.oasis.opendocument.text-web\n" +
      "odm application/vnd.oasis.opendocument.text-master\n" +
      "odg application/vnd.oasis.opendocument.graphics\n" +
      "otg application/vnd.oasis.opendocument.graphics-template\n" +
      "odp application/vnd.oasis.opendocument.presentation\n" +
      "otp application/vnd.oasis.opendocument.presentation-template\n" +
      "ods application/vnd.oasis.opendocument.spreadsheet\n" +
      "ots application/vnd.oasis.opendocument.spreadsheet-template\n" +
      "odc application/vnd.oasis.opendocument.chart\n" +
      "odf application/vnd.oasis.opendocument.formula\n" +
      "odb application/vnd.oasis.opendocument.database\n" +
      "odi application/vnd.oasis.opendocument.image\n" +
      "oxt application/vnd.openofficeorg.extension\n" +
      "// Other\n" +
      "txt  text/plain\n" +
      "css  text/css\n" +
      "csv  text/csv\n" +
      "html text/html\n" +
      "htm  text/html\n" +
      "rtf  application/rtf\n" +
      "pdf  application/pdf\n" +
      "xml  application/xml\n" +
      "json application/json\n";

    HashMap<String, String> map = new HashMap<>();

    Arrays.stream(source.split("\n"))
      .filter(x -> x.length() > 0)
      .filter(x -> !x.startsWith("//"))
      .map(String::trim)
      .map(x -> x.split("\\s+"))
      .filter(m -> m.length == 2)
      .forEach(m -> map.put(m[0], m[1]));

    fileExtensionMap = Collections.unmodifiableMap(map);
  }

  public static String mimeTypeFromFileExtension0(String extension) {
    if (extension == null) throw new UnknownMimeTypeForExtension(null);
    String ret = fileExtensionMap.get(extension.toLowerCase());
    if (ret == null) throw new UnknownMimeTypeForExtension(extension);
    return ret;
  }

  public static String mimeTypeFromFilename(String filename) {

    if (filename == null) return DEFAULT_MIME_TYPE;
    int index = filename.lastIndexOf('.');
    if (index < 0) return DEFAULT_MIME_TYPE;

    try {
      return mimeTypeFromFileExtension0(filename.substring(index + 1));
    } catch (UnknownMimeTypeForExtension e) {
      return DEFAULT_MIME_TYPE;
    }
  }
}
