package Settings;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static Data.CommonData.*;

public class TextStrings {

    public static void GetStrings(String lang){
        Properties userProps = new Properties();
        FileInputStream propsFile;
        try {
            switch(lang.toLowerCase()){
                case "ukr":
                    propsFile = new FileInputStream(resourcesPath+"MessageFiles/ukr.properties");
                    break;
                case "eng":
                    propsFile = new FileInputStream(resourcesPath+"MessageFiles/eng.properties");
                    break;
                case "rus":
                default:
                    propsFile = new FileInputStream(resourcesPath+"MessageFiles/rus.properties");
                    break;
            }
            userProps.load(new BufferedReader(new InputStreamReader(propsFile, StandardCharsets.UTF_8)));
            for(String key : userProps.stringPropertyNames()){
                textStringsMap.put(key ,userProps.getProperty(key));
            }
            propsFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
