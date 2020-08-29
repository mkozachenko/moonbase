package Settings;

import java.io.*;
import java.util.*;
import static Data.CommonData.resourcesPath;

public class UserSettings {
    public static String screenshotFolder, allureFolder, downloadPath;
    public static boolean clearScreenshots, prepareTests, cleanupTests;

    public void getUserSettings(){
        String fileName = "settings.properties";
        Properties userProps = getProperties(resourcesPath + fileName);
        screenshotFolder = userProps.getProperty("screenshotFolder");
        allureFolder = userProps.getProperty("allureFolder");
        downloadPath = userProps.getProperty("downloadPath");
        prepareTests = Boolean.parseBoolean(userProps.getProperty("prepareTests"));
        cleanupTests = Boolean.parseBoolean(userProps.getProperty("cleanupTests"));
        clearScreenshots = Boolean.parseBoolean(userProps.getProperty("clearScreenshots"));
    }

    public void setEnvironment(Map<String, String> properties){
        String fileName = "environment.properties";
        File envFile = new File(fileName);
        Properties allureProps = getProperties(fileName);
        try{
            FileOutputStream out = new FileOutputStream(envFile);
            for (Map.Entry<String, String> entry : properties.entrySet())
            {
                allureProps.setProperty(entry.getKey(),entry.getValue());
            }
            allureProps.store(new FileOutputStream(envFile), null);
            out.close();
        } catch (IOException ex) {
            System.err.println("Невозможно сохранить файл "+fileName+"\n");
            ex.printStackTrace();
        }
    }

    private Properties getProperties(String file){
        File path = new File(file);
        Properties properties = new Properties();
        try{
            FileInputStream propsFile = new FileInputStream(file);
            properties.load(propsFile);
            propsFile.close();
        } catch (IOException ex) {
            System.err.println("Невозможно прочитать файл "+path.getAbsolutePath()+"\n");
            ex.printStackTrace();
        }
        return properties;
    }
}
