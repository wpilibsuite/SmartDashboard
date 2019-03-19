package edu.wpi.first.smartdashboard.extensions;

import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.gui.Widget;
import edu.wpi.first.smartdashboard.types.DisplayElementRegistry;
import edu.wpi.first.smartdashboard.types.NamedDataType;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.ProgressMonitor;

/**
 * This class searches for library and extension jars and adds them to the
 * system class loader. It then searches within extension jars for
 * {@link StaticWidget StaticWidget}s or {@link Widget Widget}s, and registers
 * them in the dashboard.
 *
 * @author Joe Grinstead
 */
public class FileSniffer {
  private static final File EXTENSION_DIR = new File(getUserHomeDir(), "SmartDashboard/extensions");
  private static final File[] LIBRARY_DIRS = {
    new File("./lib"),
    new File(EXTENSION_DIR, "lib"),
    EXTENSION_DIR
  };

  public static final FileSnifferClassLoader classLoader = new FileSnifferClassLoader(
      ClassLoader.getSystemClassLoader()
  );

  public static void findExtensions(ProgressMonitor monitor, int min, int max) {
    monitor.setNote("Loading Extensions");

    FilenameFilter jarFileFilter = new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(".jar");
      }
    };

    for (File libDir : LIBRARY_DIRS) {
      if (!libDir.exists()) {
        monitor.setProgress(min + (max - min) / 5);
        continue;
      }
      System.out.println("Searching for library jars in: " + libDir);
      monitor.setNote("Searching for library jars in: " + libDir);

      File[] libJars = libDir.listFiles(jarFileFilter);

      if (libJars == null) {
        monitor.setProgress(min + (max - min) / 5);
        continue;
      }

      for (File file : libJars) {
        System.out.println("Adding Library Jar: " + file);

        try {
          classLoader.addURL(file.toURI().toURL());
        } catch (MalformedURLException ex) {
          ex.printStackTrace();
        }
      }

      monitor.setProgress(min + (max - min) / 5);
    }

    if (!EXTENSION_DIR.exists()) {
      System.out.println("No Extension Folder");
      monitor.setProgress(max);
      return;
    }

    File[] extensionJars = EXTENSION_DIR.listFiles(jarFileFilter);
    if (extensionJars == null) {
      return;
    }

    double fileCount = 0;
    for (File file : extensionJars) {
      System.out.println("Searching for extensions in: " + file);
      monitor.setProgress((int) ((min + max) / 2.0 * (1.0 + fileCount++ / extensionJars.length)));
      monitor.setNote("Searching for extensions in: " + file);

      try {
        classLoader.addURL(file.toURI().toURL());
        JarFile jarFile = new JarFile(file);
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
          JarEntry entry = entries.nextElement();
          if (entry.isDirectory()) {
            continue;
          }
          if (entry.getName().endsWith(".class")) {
            try {
              String className = entry.getName();
              className = className.substring(0, className.length() - 6);
              className = className.replace('/', '.');
              Class<?> clazz = classLoader.loadClass(className);
              if (Widget.class.isAssignableFrom(clazz)) {
                System.out.println("Custom Widget Loaded: " + clazz.getSimpleName());
                DisplayElementRegistry.registerWidget(clazz.asSubclass(Widget.class));
              } else if (StaticWidget.class.isAssignableFrom(clazz)) {
                System.out.println("Custom Static Widget Loaded: " + clazz.getSimpleName());
                DisplayElementRegistry.registerStaticWidget(clazz.asSubclass(StaticWidget.class));
              } else if (NamedDataType.class.isAssignableFrom(clazz)) {
                try {
                  Object ret = clazz.asSubclass(NamedDataType.class).getMethod("get").invoke(null);
                  if (ret == null) {
                    System.out.println(
                        "ERROR: custom named data type " + clazz.getSimpleName()
                        + " failed to load"
                    );
                  } else {
                    System.out.println("Custom Named Data Type Loaded: " + clazz.getSimpleName());
                  }
                } catch (Exception e) {
                  e.printStackTrace();
                }
              }
            } catch (ClassNotFoundException e) {
              e.printStackTrace();
            }
          }
        }
      } catch (MalformedURLException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    monitor.setProgress(max);
  }

  private static File getUserHomeDir() {
    final String homeDirPath;
    if (isWindows()) {
      homeDirPath = System.getenv("USERPROFILE");
    } else {
      homeDirPath = System.getProperty("user.home");
    }

    return new File(homeDirPath);
  }

  private static boolean isWindows() {
    return System.getProperty("os.name").matches("(?i)Windows.*");
  }
}
