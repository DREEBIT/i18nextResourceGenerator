package com.dreebit.i18nextResourceGenerator;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.vfs.VirtualFile;
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
    private HashMap<String, ArrayList<String>> translateableStrings;

    public void actionPerformed(AnActionEvent e) {
        UtilKeys.project = e.getProject();
        UtilKeys.projectBasePath = e.getProject().getBaseDir().getPath();

        System.out.println(UtilKeys.projectBasePath + UtilKeys.DE_RESOURCE_PATH);

        VirtualFile[] selectedFiles = e.getData(DataKeys.VIRTUAL_FILE_ARRAY);
        allFiles = new ArrayList<File>();

        for (VirtualFile v : selectedFiles) {
            this.walk(v.getPath());
        }

        FileContentProvider fileContentProvider = new FileContentProvider();
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

        translateableStrings = new HashMap<String, ArrayList<String>>();

        for(String s : matches) {
            TranslatableStringHelper translatableStringHelper = new TranslatableStringHelper(s);
            String ns = translatableStringHelper.getNamespace();
            String name = translatableStringHelper.getName();

            /*
            if(translateableStrings.containsKey(ns)) {
                translateableStrings.get(ns).add(name);
            } else {
                ArrayList<String> arrayList = new ArrayList<String>();
                arrayList.add(name);
                translateableStrings.put(ns, arrayList);
            }
            */

            try {
                File f = this.getFileFromNamespace(ns);
                String resourceContent = fileContentProvider.getFileContent(f);
                JSONObject jsonObject = new JSONObject(resourceContent);

                System.out.println("'" + name + "' is in object? " + jsonObject.has(name));

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }


        }

        // this.printOutAllStrings();
    }

    private File getFileFromNamespace(String namespace) {
        File enResource = new File(UtilKeys.projectBasePath + UtilKeys.EN_RESOURCE_PATH);
        File[] enResources = enResource.listFiles();

        for (File f : enResources) {
            if (this.getFileNameWithoutExtension(f.getName()).equals(namespace)) {
                return f;
            }
        }

        return new File("");
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
}
