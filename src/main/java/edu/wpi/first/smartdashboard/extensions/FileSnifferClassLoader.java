package edu.wpi.first.smartdashboard.extensions;

import java.net.URL;
import java.net.URLClassLoader;

//TODO write more in doc comment
/**
 * Modified version of {@link URLClassLoader} used by {@link FileSniffer}
 * 
 * @author Adrian Guerra
 */
public class FileSnifferClassLoader extends URLClassLoader {

  public FileSnifferClassLoader(ClassLoader parent) {
    super(new URL[0], parent);
  }

  public FileSnifferClassLoader() {
    super(new URL[0]);
  }

  @Override
  public void addURL(URL url) {
    super.addURL(url);
  }
}
