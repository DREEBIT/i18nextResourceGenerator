package com.dreebit.i18nextResourceGenerator;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by stefanmeschke on 13.01.14.
 */
public class ContextMenuAction extends AnAction {

    private List<File> allFiles;
    private ArrayList<String> translateableStrings;
    private Integer numberOfAddedStrings;
    private FileReadWriteProvider fileContentProvider = new FileReadWriteProvider();

    public void actionPerformed(AnActionEvent e) {
        UtilKeys.project = e.getProject();
        UtilKeys.projectBasePath = e.getProject().getBaseDir().getPath();

        VirtualFile[] selectedFiles = e.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);
        //VirtualFile[] selectedFiles = e.getProject().getBaseDir().getChildren();

        allFiles = new ArrayList<File>();

        for (VirtualFile v : selectedFiles) {
            this.walk(v.getPath());
        }


        String fileContent = "";

        Iterator<File> it = allFiles.iterator();
        while (it.hasNext()) {
            File f = it.next();
            try {
                fileContent += fileContentProvider.getFileContent(f);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        Set<String> matches = this.getStringsFromPattern(fileContent, Pattern.compile("(Locale.get\\()(?:\\'|\\\")(.*?)(?:\\'|\\\")(\\))"));


        translateableStrings = new ArrayList<String>();

        for(String s : matches) {
            translateableStrings.add(s);
        }

        ArrayList<String> languages = this.getLanguages();

        numberOfAddedStrings = 0;
        for(String lang : languages) {
            this.writeResourcesForGivenLanguage(lang);
        }

        String message = "";
        if(numberOfAddedStrings>0)
            if(numberOfAddedStrings==1)
                message = "<strong>Resources has been created.</strong><br>Added " + numberOfAddedStrings + " string";
            else
                message = "<strong>Resources has been created.</strong><br>Added " + numberOfAddedStrings + " strings";
        else
            message = "<strong>Nothing was created</strong>";

        this.showMessage(message);

        VirtualFileManager.getInstance().syncRefresh();
    }

    private ArrayList<String> getLanguages() {
        ArrayList<String> languages = new ArrayList<String>();
        File resource = new File(UtilKeys.projectBasePath + UtilKeys.RESOURCE_PATH);
        File[] resources = resource.listFiles();

        for (File f : resources) {
            if(!f.isFile())
                languages.add(f.getName());
        }

        return languages;
    }

    private File getFileFromNamespaceAndLanguage(String namespace, String language) {
        File resource = new File(UtilKeys.projectBasePath + UtilKeys.RESOURCE_PATH + File.separator + language);
        File[] resources = resource.listFiles();

        for (File f : resources) {
            if (this.getFileNameWithoutExtension(f.getName()).equals(namespace)) {
                return f;
            }
        }

        File newFile = new File(UtilKeys.projectBasePath + UtilKeys.RESOURCE_PATH + File.separator + language + File.separator + namespace + ".json");
        try {
            newFile.createNewFile();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        return newFile;
    }

    public void walk(String path) {

        File root = new File(path);
        File[] list = root.listFiles();

        if (list == null) return;

        for (File f : list) {
            if (f.isDirectory()) {
                walk(f.getAbsolutePath());
            } else {
                allFiles.add(f);
            }
        }
    }

    public Set<String> getStringsFromPattern(String string, Pattern pattern)  {
        Matcher matcher = pattern.matcher(string);

        Set<String> listMatches = new HashSet<String>();

        while(matcher.find())
        {
            listMatches.add(matcher.group(2));
        }

        return listMatches;
    }

    private String getFileNameWithoutExtension(String filename) {
        int index = filename.lastIndexOf('.');
        if (index != -1)
            filename = filename.substring(0, index);

        return filename;
    }

    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
            return true;
        } catch(JSONException ex) {
            return false;
        }
    }

    public void showMessage(String htmlText) {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(UtilKeys.project);
        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(htmlText, MessageType.INFO, null)
                .setFadeoutTime(2000)
                .createBalloon()
                .show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.atLeft);
    }

    private void writeResourcesForGivenLanguage(String language){

        for(String s : translateableStrings) {
            Pattern pattern = Pattern.compile("(.*)(?:\\'|\\\")(\\,)(.*)");
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                s = matcher.group(1);
            } else {

            }

            TranslatableStringHelper translatableStringHelper = new TranslatableStringHelper(s);
            String ns = translatableStringHelper.getNamespace();
            String name = translatableStringHelper.getName();
            String key =  translatableStringHelper.getKey();
            boolean hasKey =  translatableStringHelper.hasKey();

            try {
                File f = this.getFileFromNamespaceAndLanguage(ns, language);
                String resourceContent = fileContentProvider.getFileContent(f);
                JSONObject jsonObject;

                try {
                    if(this.isJSONValid(resourceContent)) {
                        jsonObject = new JSONObject(resourceContent);
                    } else {
                        jsonObject = new JSONObject("{}");
                    }

                    if(hasKey) {
                        if(jsonObject.has(key)){
                            try {
                                JSONObject keyObject = new JSONObject(jsonObject.get(key).toString());
                                if(!keyObject.has(name)) {
                                    keyObject.put(name, UtilKeys.DEFAULT_PLACEHOLDER);
                                    jsonObject.put(key, keyObject);
                                    numberOfAddedStrings++;
                                }
                            } catch (JSONException jsonExeption) {
                                jsonExeption.printStackTrace();
                            }
                        } else {
                            JSONObject newKeyObject = new JSONObject();
                            newKeyObject.put(name, UtilKeys.DEFAULT_PLACEHOLDER);
                            jsonObject.put(key, newKeyObject);
                            numberOfAddedStrings++;
                        }
                    } else {
                        if(!jsonObject.has(name)){
                            jsonObject.put(name, UtilKeys.DEFAULT_PLACEHOLDER);
                            numberOfAddedStrings++;
                        }
                    }

                    fileContentProvider.writeFileWithContent(f, jsonObject.toString(4));

                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
