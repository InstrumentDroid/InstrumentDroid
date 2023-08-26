package com.example.asm.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ManifestParser {

    public static List<String> getActivityNames(File manifestFile) throws ParserConfigurationException, SAXException, IOException {
        List<String> activityNames = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(manifestFile);
        NodeList activityList = doc.getElementsByTagName("activity");
        for (int i = 0; i < activityList.getLength(); i++) {
            Element activity = (Element) activityList.item(i);
            String activityName = activity.getAttribute("android:name");
            if(activityName.startsWith(".")) {
                activityName = activityName.replaceFirst("\\.", getPackageName(manifestFile) + ".");
            }
            activityName = activityName.replace('.', '/');
            activityNames.add(activityName);
        }
        return activityNames;
    }

    public static List<String> getMainActivityName(File manifestFile) throws ParserConfigurationException, SAXException, IOException {
        List<String> mainActivityNames = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(manifestFile);
        NodeList activityList = doc.getElementsByTagName("activity");
        for (int i = 0; i < activityList.getLength(); i++) {
            Element activity = (Element) activityList.item(i);
            NodeList intentFilterList = activity.getElementsByTagName("intent-filter");
            for (int j = 0; j < intentFilterList.getLength(); j++) {
                Element intentFilter = (Element) intentFilterList.item(j);
                NodeList categoryList = intentFilter.getElementsByTagName("category");
                for (int k = 0; k < categoryList.getLength(); k++) {
                    Element category = (Element) categoryList.item(k);
                    String categoryName = category.getAttribute("android:name");
                    if (categoryName.equals("android.intent.category.LAUNCHER")) {
                        String mainActivityName = activity.getAttribute("android:name");
                        if(mainActivityName.startsWith(".")) {
                            mainActivityName = mainActivityName.replaceFirst("\\.", getPackageName(manifestFile) + ".");
                        }
                        mainActivityName = mainActivityName.replace('.', '/');
                        mainActivityNames.add(mainActivityName);
                    }
                }
            }
        }
        return mainActivityNames;
    }

    public static String getPackageName(File manifestFile) throws ParserConfigurationException, SAXException, IOException {
        String packageName = "";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(manifestFile);
        Element manifest = doc.getDocumentElement();
        packageName = manifest.getAttribute("package").replace('.', '/');
        return packageName;
    }
}

