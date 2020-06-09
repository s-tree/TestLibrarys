package com.intercom.sdk;

import android.content.res.AssetManager;
import android.os.Looper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class utils {

  static public int stringToInteger(String str, int def) {
    int v = def;
    try {
      v = Integer.parseInt(str);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    return v;
  }

  static public long stringToLong(String str, long def) {
    long v = def;
    try {
      v = Long.parseLong(str);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    return v;
  }

  static public boolean stringToBoolean(String str, boolean def) {
    if (str.equalsIgnoreCase("true"))
      return true;
    if (str.equalsIgnoreCase("false"))
      return false;

    int v = def ? 1 : 0;
    try {
      v = Integer.parseInt(str);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    return v > 0;
  }

  static public int[] parserStringToArray(String str, String token) {
    if (str == null)
      return null;
    String[] arr = str.split(token, 20);
    int[] output = new int[arr.length];
    for (int i = 0; i < arr.length; ++i) {
      output[i] = utils.stringToInteger(arr[i], 0);
    }
    return output;
  }

  static public String microSecondsToTime(long t) {
    long ms = (t -11644473600000000L)/ 1000;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    formatter.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
    return formatter.format(ms);
  }

  static public boolean isMainThread() {
    return Looper.getMainLooper().getThread() == Thread.currentThread();
  }

  public static byte[] readResourceFromFile(AssetManager manager, String filename){
    ByteBuffer out_buffer = null;
    InputStream is = null;
    int total_length = 0;
    try {
      byte[] buffer = new byte[1024 * 10];
      int index = -1;
      is = manager.open(filename);
      out_buffer = ByteBuffer.allocate(is.available());
      while ((index = is.read(buffer)) != -1) {
        out_buffer.put(buffer, total_length, index);
        total_length += index;
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (Exception ee) {
          ee.printStackTrace();
        }
      }
      byte[] t = new byte[total_length];
      out_buffer.rewind();
      out_buffer.get(t);
      return t;
    }
  }

  public static List<String> parseLine(char c,String line) {
    List<String> res = new ArrayList<>();
    if (line == null)
      return res;

    int index = line.indexOf(c);
    if (index < 0) {
      res.add(line);
    } else {
      res.add(line.substring(0, index));
      ++index;
      if (index < line.length() ) {
        res.add(line.substring(index, line.length()));
      } else {
        res.add("");
      }
    }
    return res;
  }
}
