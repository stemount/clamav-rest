package fi.solita.clamav;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ClamAVProxy {

  @Value("${clamd.host}")
  private String hostname;

  @Value("${clamd.port}")
  private int port;

  @Value("${clamd.timeout}")
  private int timeout;

  /**
   * @return Clamd status.
   */
  @RequestMapping("/")
  public @ResponseBody Map<String, Boolean> ping() throws IOException {
    ClamAVClient a = new ClamAVClient(hostname, port, timeout);

    HashMap<String, Boolean> map = new HashMap<String, Boolean>();

    map.put("status", a.ping());

    return map;
  }

  @RequestMapping(value="/scan", method=RequestMethod.POST, produces="application/json")
  public @ResponseBody Map<String, Boolean> handleFileUpload(@RequestParam("name") String name,
                                                             @RequestParam("file") MultipartFile file) throws IOException {

    HashMap<String, Boolean> map = new HashMap<String, Boolean>();

    if (!file.isEmpty()) {
      ClamAVClient a = new ClamAVClient(hostname, port, timeout);
      byte[] r = a.scan(file.getInputStream());

      map.put("infected", !ClamAVClient.isCleanReply(r));
    } else {
      throw new IllegalArgumentException("empty file");
    }

    return map;

  }

}
