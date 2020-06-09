package com.intercom.sdk;

import android.os.Bundle;
import android.support.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * INI读写实现
 * 如果是linux的配置，只有一个section，就是""
 * JINIParser.Section c = .get("");
 * key & section 不区分大小写
 */

public class JINIParser {
    public static final int kNullType = 0;
    //key=value
    public static final int kTypeValue = 1;
    //comment, ; or #开头的注释语句
    public static final int kTypeComment = 2;

    private boolean withUTF8BOM;

    private List<Section> sections;

    public static JINIParser loadFromFile(File file) {
        byte[] data = readFileToBytes(file);
        return loadFromBuffer(data);
    }

    public static JINIParser loadFromString(String stream) {
        byte[] data = stream.getBytes();
        return loadFromBuffer(data);
    }

    public static JINIParser loadFromBuffer(byte[] data) {
        JINIParser parser = new JINIParser();
        if (data != null) {
            String stream;
            parser.withUTF8BOM = hasUTF8BOM(data);
            if (parser.withUTF8BOM) {
                stream = new String(data, 3, data.length - 3, StandardCharsets.UTF_8);
            } else {
                stream = new String(data, StandardCharsets.UTF_8);
            }
            parser.parser(stream);
        }
        return parser;
    }

    public JINIParser() {
        this.withUTF8BOM = false;
        this.sections = new ArrayList<>();
    }

    public void clear() {
        this.sections.clear();
    }

    /**
     * return Section with name
     *
     * @param name:Section name
     * @param create       : true: create it if NOT found;
     * @return always return Section
     */
    public Section get(String name, boolean create) {
        for (Section c : sections) {
            if (c.name.equalsIgnoreCase(name)) {
                return c;
            }
        }
        if (create) {
            Section c = new Section(name);
            sections.add(c);
            return c;
        }
        return null;
    }

    /**
     * return Section with name
     *
     * @param name:
     * @return return null if NOT found
     */
    public Section get(String name) {
        return get(name, false);
    }

    public void remove(String name) {
        for (Section c : sections) {
            if (c.name.equalsIgnoreCase(name)) {
                sections.remove(c);
                break;
            }
        }
    }

    public void add(Section c) {
        remove(c.name);
        sections.add(c);
    }

    public List<String> getSections() {
        List<String> l = new ArrayList<>();
        for (Section c : sections) {
            l.add(c.name);
        }
        return l;
    }

    public List<Section> getAll() {
        return sections;
    }

    public boolean sectionExist(String name) {
        for (Section c : sections) {
            if (c.name.equalsIgnoreCase(name))
                return true;
        }
        return false;
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        for (Section c : sections) {
            if (c.isEmpty())
                continue;
            output.append(c.toString());
        }
        return output.toString();
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public int size() {
        return sections.size();
    }

    public boolean commit(String filename) {
        return commit(filename, withUTF8BOM);
    }

    public boolean commit(File file) {
        return commit(file, withUTF8BOM);
    }

    public boolean commit(String filename, boolean with_utf8_bom) {
        File file = new File(filename);
        return commit(file, with_utf8_bom);
    }

    public boolean commit(File file, boolean with_utf8_bom) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        if (with_utf8_bom) {
            output.write(0xef);
            output.write(0xbb);
            output.write(0xbf);
        }

        boolean result = write(file, output);
        try {
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean write(File file, ByteArrayOutputStream out) {
        boolean result = false;
        try {
            String str = toString();
            out.write(str.getBytes(StandardCharsets.UTF_8));
            result = writeFile(file, out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void parser(String stream) {
        Section c = new Section("");
        StringTokenizer t = new StringTokenizer(stream, "\r\n");
        while (t.hasMoreTokens()) {
            String line = t.nextToken();
            if (line.matches("\\[.*\\]")) {
                String name = line.replaceFirst("\\[(.*)\\]", "$1").trim();
                if (!name.equalsIgnoreCase(c.name)) {
                    if (!c.isEmpty()) {
                        sections.add(c);
                    }
                    c = new Section(name);
                }
            } else {
                KVBean bean = c.parserLine(line);
                if (bean != null) {
                    c.addBean(bean);
                }
            }
        }
        if (!c.isEmpty()) {
            sections.add(c);
        }
    }


    public static class Section {
        public final String name;
        private List<KVBean> kv;

        public Section(String name) {
            this.name = name;
            this.kv = new ArrayList<>();
        }

        public void reset(List<KVBean> kv) {
            this.kv.clear();
            this.kv = kv;
        }

        private KVBean parserLine(String line) {
            if (line.matches(".*=.*")) {
                int i = line.indexOf('=');
                String key = line.substring(0, i).trim();
                String value = line.substring(i + 1).trim();
                if (key.isEmpty())
                    return null;

                KVBean bean = new KVBean();
                if (key.charAt(0) == '#' || key.charAt(0) == ';') {
                    bean.type = kTypeComment;
                    bean.annotation = key.charAt(0);
                    key = key.substring(1).trim();
                }
                bean.key = key;
                bean.value = value;
                return bean;
            }

            if (!line.isEmpty() && (line.charAt(0) == '#' || line.charAt(0) == ';')) {
                KVBean bean = new KVBean();
                bean.type = kTypeComment;
                bean.key = ""; //pure comment
                bean.annotation = line.charAt(0);
                bean.value = line.substring(1);
                return bean;
            }
            return null;
        }

        public void addLine(@NonNull String line) {
            if (line.charAt(0) == '#' || line.charAt(0) == ';') {
                addComment(line);
            } else {
                KVBean bean = parserLine(line);
                if (bean != null) {
                    addKey(bean.key, bean.value);
                }
            }
        }

        public void addKey(String key, String value) {
            KVBean bean = getBean(key);
            if (bean != null) {
                bean.value = value;
                bean.type = kTypeValue;
            } else {
                bean = new KVBean();
                bean.key = key;
                bean.value = value;
                kv.add(bean);
            }
        }

        public void addKey(String key, int value) {
            addKey(key, String.valueOf(value));
        }

        public void addKey(String key, long value) {
            addKey(key, String.valueOf(value));
        }

        public void addKey(String key, boolean value) {
            addKey(key, value ? "1" : "0");
        }

        public void addComment(String comment) {
            KVBean bean = parserLine(comment);
            if (bean == null)
                return;

            if (!bean.key.isEmpty()) {
                KVBean c = getBean(bean.key);
                if (c != null) {
                    c.key = bean.key;
                    c.value = bean.value;
                    c.annotation = bean.annotation;
                    c.type = kTypeComment;
                    return;
                }
            }
            kv.add(bean);
        }

        public void comment(String key) {
            for (KVBean bean : kv) {
                if (bean.type == kTypeComment)
                    continue;

                if (bean.key.equalsIgnoreCase(key)) {
                    bean.type = kTypeComment;
                    break;
                }
            }
        }

        public void unComment(String key) {
            for (KVBean bean : kv) {
                if (bean.type != kTypeComment)
                    continue;

                if (bean.key.equalsIgnoreCase(key)) {
                    bean.type = kTypeValue;
                    break;
                }
            }
        }

        public void remove(String key) {
            for (KVBean bean : kv) {
                if (bean.type == kTypeComment)
                    continue;
                if (bean.key.equalsIgnoreCase(key)) {
                    kv.remove(bean);
                    break;
                }
            }
        }

        public void removeComment(String key) {
            for (KVBean bean : kv) {
                if (bean.type != kTypeComment)
                    continue;
                if (bean.key.equalsIgnoreCase(key)) {
                    kv.remove(bean);
                    break;
                }
            }
        }

        public List<String> getKeys() {
            List<String> l = new ArrayList<>();
            for (KVBean bean : kv) {
                if (bean.type == kTypeComment)
                    continue;
                l.add(bean.key);
            }
            return l;
        }

        public List<KVBean> getAll() {
            return kv;
        }

        public boolean keyExist(String key) {
            return getBean(key) != null;
        }

        public int keyType(String key) {
            KVBean bean = getBean(key);
            if (bean == null)
                return kNullType;
            return bean.type;
        }

        @Override
        public String toString() {
            StringBuilder output = new StringBuilder();
            if (!name.isEmpty()) {
                output.append(String.format("[%s]\r\n", name));
            }
            for (KVBean bean : kv) {
                if (bean.type == kTypeComment) {
                    if ((bean.key == null || bean.key.isEmpty()) && (bean.value == null || bean.value.isEmpty()))
                        continue;

                    if (bean.value == null || bean.value.isEmpty()) {
                        output.append(String.format("%c%s\r\n", bean.annotation, bean.key));
                    } else if (bean.key == null || bean.key.isEmpty()) {
                        output.append(String.format("%c%s\r\n", bean.annotation, bean.value));
                    } else {
                        output.append(String.format("%c%s=%s\r\n", bean.annotation, bean.key, bean.value));
                    }
                } else {
                    output.append(String.format("%s=%s\r\n", bean.key, bean.value));
                }
            }
            return output.toString();
        }

        public Bundle toBundle() {
            Bundle bundle = new Bundle();
            for (KVBean bean : kv) {
                if (bean.type == kTypeComment)
                    continue;
                bundle.putString(bean.key, bean.value);
            }
            return bundle;
        }

        public boolean isEmpty() {
            return kv.isEmpty();
        }

        public int length() {
            return kv.size();
        }

        public String get(String key) {
            KVBean bean = getBean(key);
            if (bean != null && bean.type != kTypeComment) {
                return bean.value;
            }
            return null;
        }

        public int get(String key, int def) {
            String value = get(key);
            if (value == null)
                return def;
            return utils.stringToInteger(value, def);
        }

        public long get(String key, long def) {
            String value = get(key);
            if (value == null)
                return def;
            return utils.stringToLong(value, def);
        }

        public boolean get(String key, boolean def) {
            String value = get(key);
            if (value == null)
                return def;
            return utils.stringToBoolean(value, def);
        }

        private KVBean getBean(String key) {
            for (KVBean bean : kv) {
                if (bean.key.equalsIgnoreCase(key)) {
                    return bean;
                }
            }
            return null;
        }

        private void addBean(KVBean bean) {
            kv.add(bean);
        }
    }

    public static class KVBean {
        public String key;
        public String value;
        public int type;
        public char annotation;

        private KVBean() {
            this.type = kTypeValue;
            this.annotation = '#';
            this.key = "";
            this.value = "";
        }
    }

    private static boolean hasUTF8BOM(byte[] str) {
        if (str.length < 3)
            return false;
        return (str[0] & 0xFF) == 0xEF
                && (str[1] & 0xFF) == 0xBB
                && (str[2] & 0xFF) == 0xBF;
    }

    private static byte[] readFileToBytes(File file) {
        int size = (int) file.length();
        if (size < 1)
            return null;

        byte[] data = new byte[size];
        BufferedInputStream in = null;
        int result = 0;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            result = in.read(data, 0, data.length);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result == size ? data : null;
    }

    private static boolean makeSureFile(File file) {
        if (file.exists()) {
            file.delete();
        }
        file.getParentFile().mkdirs();

        boolean result = false;
        try {
            result = file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static boolean writeFile(File file, byte[] bytes) {
        if (!makeSureFile(file))
            return false;

        FileOutputStream fos = null;

        boolean result = false;

        try {
            fos = new FileOutputStream(file);
            fos.write(bytes);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    result = false;
                }
            }
        }
        return result;
    }
}
