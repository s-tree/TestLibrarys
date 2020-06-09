package com.intercom.util;

import com.intercom.base.Log;
import com.intercom.base.annotations.CalledByNative;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * source code from :
 * https://android.googlesource.com/platform/tools/tradefederation/+/master/src/com/android/tradefed/util/TarUtil.java
 */

public class TarUtil {
  private static final String TAG = "cr.TarUtil";
  /**
   * Untar a tar file into a directory.
   * tar.gz file need to up {@link #unGzip(File, File)} first.
   *
   * @param inputFile The tar file to extract
   * @param outputDir the directory where to put the extracted files.
   * @return The list of {@link File} untarred.
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static List<File> unTar(final File inputFile, final File outputDir)
    throws FileNotFoundException, IOException {
    Log.i(TAG,"Untaring %s to dir %s.", inputFile.getAbsolutePath(),
      outputDir.getAbsolutePath());
    final List<File> untaredFiles = new LinkedList<File>();
    final InputStream is = new FileInputStream(inputFile);
    TarArchiveInputStream debInputStream = null;
    try {
      debInputStream = (TarArchiveInputStream)
        new ArchiveStreamFactory().createArchiveInputStream("tar", is);
      TarArchiveEntry entry = null;
      while ((entry = (TarArchiveEntry)debInputStream.getNextEntry()) != null) {
        final File outputFile = new File(outputDir, entry.getName());
        if (entry.isDirectory()) {
          Log.i("Attempting to write output directory %s.",
            outputFile.getAbsolutePath());
          if (!outputFile.exists()) {
            Log.i("Attempting to create output directory %s.",
              outputFile.getAbsolutePath());
            if (!outputFile.mkdirs()) {
              throw new IllegalStateException(
                String.format("Couldn't create directory %s.",
                  outputFile.getAbsolutePath()));
            }
          }
        } else {
          Log.i("Creating output file %s.", outputFile.getAbsolutePath());

          mkdirsRWX(outputFile.getParentFile());

          final OutputStream outputFileStream = new FileOutputStream(outputFile);
          IOUtils.copy(debInputStream, outputFileStream);
          outputFileStream.close();
          outputFile.setWritable(true,false);
          outputFile.setReadable(true,false);
        }
        untaredFiles.add(outputFile);
      }
    } catch (ArchiveException ae) {
      // We rethrow the ArchiveException through a more generic one.
      throw new IOException(ae);
    } finally {
      debInputStream.close();
      is.close();
    }
    return untaredFiles;
  }
  /**
   * UnGZip a file: a tar.gz file will become a tar file.
   *
   * @param inputFile The {@link File} to ungzip
   * @param outputDir The directory where to put the ungzipped file.
   * @return a {@link File} pointing to the ungzipped file.
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static File unGzip(final File inputFile, final File outputDir)
    throws FileNotFoundException, IOException {
    Log.i("Ungzipping %s to dir %s.", inputFile.getAbsolutePath(),
      outputDir.getAbsolutePath());

    mkdirsRWX(outputDir);
    // rename '-3' to remove the '.gz' extension.
    final File outputFile = new File(outputDir, inputFile.getName().substring(0,
      inputFile.getName().length() - 3));
    GZIPInputStream in = null;
    FileOutputStream out = null;
    try {
      in = new GZIPInputStream(new FileInputStream(inputFile));
      out = new FileOutputStream(outputFile);
      IOUtils.copy(in, out);
    } finally {
      in.close();
      out.close();
    }
    return outputFile;
  }

  public static boolean mkdirsRWX(File file) {
    File parent = file.getParentFile();
    if (parent != null && !parent.isDirectory()) {
      // parent doesn't exist.  recurse upward, which should both mkdir and chmod
      if (!mkdirsRWX(parent)) {
        // Couldn't mkdir parent, fail
        Log.w(TAG, String.format("Failed to mkdir parent dir %s.", parent));
        return false;
      }
    }
    // by this point the parent exists.  Try to mkdir file
    if (file.isDirectory() || file.mkdir()) {
      file.setReadable(true,false);
      file.setWritable(true,false);
    }
    return file.isDirectory();
  }

  public static String ext(String filename){
    int index = filename.lastIndexOf(".");
    if (index == -1) {
      return null;
    }
    String result = filename.substring(index + 1);
    return result;
  }

  @CalledByNative
  private static boolean unGzipTar(String inputFileName,String outputDirName)
    throws FileNotFoundException, IOException {
    String extName = ext(inputFileName);
    if(extName == null)
      return false;

    File input = new File(inputFileName);
    File tarDir = input.getParentFile();
    File output = new File(outputDirName);
    List<File> result = null;
    File tar = null;
    try {
       if (extName.equalsIgnoreCase("gz")) {
         tar = unGzip(input, tarDir);
         if(tar != null){
           result = unTar(tar,output);
         }
       }else if(extName.equalsIgnoreCase("tar")){
         result = unTar(input,output);
       }
    }catch (IOException e){
      e.printStackTrace();
    }finally {
      if(tar != null){
        tar.delete();
      }
    }
    return result != null;
  }
}
