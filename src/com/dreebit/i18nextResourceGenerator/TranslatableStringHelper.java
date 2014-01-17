package com.dreebit.i18nextResourceGenerator;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by stefanmeschke on 14.01.14.
 */
public class TranslatableStringHelper {

    private String raw;
    private String name;
    private String key;
    private String namespace;
    private ArrayList<String> keys;

    public TranslatableStringHelper(String string) {
        this.raw = string;
        this.provideAttributes(this.raw);
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getName() {
        return this.name;
    }

    public String getKey() {
        return this.key;
    }

    public boolean hasKey() {
        if(this.key != null && !this.key.isEmpty()){
            return true;
        } else {
            return false;
        }
    }

    private void provideAttributes(String string) {
        // find namespace
        Pattern patternNamespace;
        Matcher matcherNamespace;
        patternNamespace = Pattern.compile("(.*)(\\:)(.*)");
        matcherNamespace = patternNamespace.matcher(string);
        if (matcherNamespace.find()) {
            this.namespace = matcherNamespace.group(1);
            this.name = matcherNamespace.group(3);
        } else {
            this.namespace = UtilKeys.DEFAULT_NAMESPACE;
            this.name = string;
        }

        //find key
        Pattern patternKey;
        Matcher matcherKey;
        patternKey = Pattern.compile("(.*)(\\.)(.*)");
        matcherKey = patternKey.matcher(this.name);
        if (matcherKey.find()) {
            this.key = matcherKey.group(1);
            this.name = matcherKey.group(3);
        }
    }
}
