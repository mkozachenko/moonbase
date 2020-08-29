package Data;

import Settings.Selectors;
import java.io.File;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CommonData extends Selectors {

    //Appium settings
    public static Map AppiumCaps(String device) {
        Map<String, String> AppiumMap = new HashMap<String, String>(){{
            put("UDID", "no data");
            put("DEVICE_NAME", "no data");
            put("ANDROID_VERSION", "no data");
        }};
        switch (device.toLowerCase()) {
            case "xiaomi":
                AppiumMap.replace("UDID", "111111111111");
                AppiumMap.replace("DEVICE_NAME", "Redmi4X");
                AppiumMap.replace("ANDROID_VERSION", "7.1.2");
                break;
            case "ipad":
            case "tabletios":
                AppiumMap.replace("UDID", "");
                AppiumMap.replace("DEVICE_NAME", "");
                AppiumMap.replace("ANDROID_VERSION", "");
                break;
            case "LG":
            case "LG-D801":
                AppiumMap.replace("UDID", "LGD111111111");
                AppiumMap.replace("DEVICE_NAME", "LG-D801");
                AppiumMap.replace("ANDROID_VERSION", "5.0.2");
                break;
            default:
                break;
        }
        return AppiumMap;
    }

    public static Map<String, String> textStringsMap = new HashMap<>();
    public static DecimalFormat priceFormat = new DecimalFormat("##.00");
    public static String resourcesPath = new File("src/test/resources").getAbsolutePath()+"/",
            date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH-mm")),
            filename,
            debugIP = "214.10.10.10";

    public static class siteDomains{
        public static final String productionMain = "https://test.com",
            stagingMain = "https://test.dom.com",
            dev1 = "https://test.dev.com";
    }

    //CREDENTIALS
    public static final String loginUser = "user.login@test.com",
            passwordUser = "000000000";
}
