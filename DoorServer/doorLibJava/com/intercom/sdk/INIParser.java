package com.intercom.sdk;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/*
 parser windows format INI config file
 [secction]
 key=value
 ...
 */

public class INIParser {
  public Map<String, Section> dictionary;

  public INIParser(String str) {
    dictionary = new HashMap<>();
    if (str != null) {
      Parser(str);
    }
  }

  public Section getSection(String section) {
    if (dictionary.isEmpty())
      return null;
    return dictionary.get(section);
  }

  public Set<String> getKeySet() {
    if (dictionary != null)
      return dictionary.keySet();
    return null;
  }

  public void removeSection(String section) {
    dictionary.remove(section);
  }

  public void addKey(String section, String key, String value) {
    Section c = getSection(section);
    if (c == null) {
      c = new Section();
      c.addKey(key, value);
      dictionary.put(section, c);
    } else {
      c.addKey(key, value);
    }
  }

  public void removeKey(String section, String key) {
    Section c = getSection(section);
    if (c != null) {
      c.removeKey(key);
      if(c.empty()){
        removeSection(section);
      }
    }
  }

  public String toString() {
    StringBuffer output = new StringBuffer();
    for (Map.Entry<String, Section> entry : dictionary.entrySet()) {
      output.append("[" + entry.getKey() + "]\r\n");
      entry.getValue().append(output);
    }
    return output.toString();
  }

  /**
   *  [section]解析出section
   */
  private static String trimSectionName(String str) {
    char[] output = new char[str.length()];
    int len = 0;
    for (int i = 0; i < str.length(); ++i) {
      char c = str.charAt(i);
      if (c == ']') {
        break;
      } else if (c != '[') {
        output[len] = c;
        ++len;
      }
    }
    String v = String.valueOf(output, 0, len);
    return v.trim();
  }

  private void Parser(String str) {
    StringTokenizer t = new StringTokenizer(str, "\r\n");
    String currentSectionName = "";
    Section currentSection = null;

    while (t.hasMoreTokens()) {
      String item = t.nextToken();
      if (item.isEmpty())
        continue;

      if (item.charAt(0) == '#' || item.charAt(0) == ';') {
        //注释行，忽略
        continue;
      }

      if (item.charAt(0) == '[') {
        //section必须从'['开始，前面不能有空格
        String sectionName = trimSectionName(item);
        if (!sectionName.equals(currentSectionName)) {
          //新的section开始
          if (!currentSectionName.isEmpty()
            && currentSection != null) {
            dictionary.put(currentSectionName, currentSection);
          }
          currentSectionName = sectionName;
          currentSection = null;
        }
        continue;
      }

      if (currentSectionName.isEmpty())
        continue;

      StringTokenizer it = new StringTokenizer(item, "=");
      if (it.countTokens() < 1)
        continue;

      String key = it.nextToken().trim();
      String value = null;
      if (it.hasMoreTokens()) {
        value = it.nextToken().trim();
      }
      if (value != null) {
        if (currentSection == null) {
          currentSection = new Section();
        }
        currentSection.addKey(key.trim(), value.trim());
      }
    }
    if (!currentSectionName.isEmpty() && currentSection != null) {
      //添加最后一个节点
      dictionary.put(currentSectionName, currentSection);
    }
  }

  public static class Section {
    public Map<String, String> dictionary;

    public Section() {
      dictionary = null;
    }

    public boolean keyExist(String key) {
      if(dictionary == null)
        return false;
      return dictionary.containsKey(key);
    }

    public boolean empty() {
      return dictionary == null || dictionary.isEmpty();
    }

    public int size() {
      return dictionary == null ? 0 : dictionary.size();
    }

    public void addKey(String key, String value) {
      if (dictionary == null) {
        dictionary = new HashMap<>();
      }
      if (keyExist(key)) {
        dictionary.remove(key);
      }
      dictionary.put(key, value);
    }

    public void removeKey(String key) {
      if (dictionary != null && keyExist(key)) {
        dictionary.remove(key);
      }
    }

    public Set<String> getKeySet() {
      if (dictionary != null)
        return dictionary.keySet();
      return null;
    }

    public String getValueOfString(String key) {
      if (dictionary != null) {
        return dictionary.get(key);
      }
      return null;
    }

    public int getValueOfInteger(String key, int def) {
      String value = getValueOfString(key);
      if (value == null)
        return def;
      return utils.stringToInteger(value, def);
    }

    public long getValueOfLong(String key, long def) {
      String value = getValueOfString(key);
      if (value == null)
        return def;
      return utils.stringToLong(value, def);
    }

    public boolean getValueOfBoolean(String key, boolean def) {
      String value = getValueOfString(key);
      if (value == null)
        return def;
      return utils.stringToBoolean(value, def);
    }

    public String toString() {
      StringBuffer output = new StringBuffer();
      for (Map.Entry<String, String> entry : dictionary.entrySet()) {
        output.append(String.format("%s=%s\r\n", entry.getKey(), entry.getValue()));
      }
      return output.toString();
    }

    public void append(StringBuffer buffer) {
      for (Map.Entry<String, String> entry : dictionary.entrySet()) {
        buffer.append(String.format("%s=%s\r\n", entry.getKey(), entry.getValue()));
      }
    }
  }
}
