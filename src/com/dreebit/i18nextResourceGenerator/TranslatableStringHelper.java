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

    private void provideAttributes(String string) {
        Pattern p = Pattern.compile("(.*?)(\\:)(.*)");
        Matcher m = p.matcher(string);

        if (m.find()) {
            this.namespace = m.group(1);
            this.name = m.group(3);
        } else {
            this.namespace = UtilKeys.DEFAULT_NAMESPACE;
            this.name = string;
        }
    }
}
