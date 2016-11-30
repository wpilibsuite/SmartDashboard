package edu.wpi.first.smartdashboard.extensions;

import java.io.*;
import java.lang.String;
import java.lang.System;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.util.Enumeration;
import java.net.*;

import javax.swing.*;

import edu.wpi.first.smartdashboard.gui.*;
import edu.wpi.first.smartdashboard.gui.elements.*;
import edu.wpi.first.smartdashboard.types.*;

/**
 * This class searches for library and extension jars and adds them
 * to the system class loader. It then searches within extension jars for
 * {@link StaticWidget StaticWidget}s or {@link Widget Widget}s, and registers
 * them in the dashboard.
 *
 * @author Joe Grinstead
 */
public class FileSniffer {
    private static final File EXTENSION_DIR =
            new File(getUserHomeDir(), "SmartDashboard/extensions");
    private static final File[] LIBRARY_DIRS = {
            new File("./lib"),
            new File(EXTENSION_DIR, "lib"),
            EXTENSION_DIR
    };

    public static void findExtensions(ProgressMonitor monitor, int min, int max) {
        monitor.setNote("Loading Extensions");

        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<?> sysclass = URLClassLoader.class;

        Method method = null;
        try {
            method = sysclass.getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
            monitor.setProgress(max);
            return;
        }

        for (File libDir : LIBRARY_DIRS) {
            if (!libDir.exists()) {
                monitor.setProgress(min + (max - min) / 5);
                continue;
            }
            System.out.println("Searching for library jars in: " + libDir);
            monitor.setNote("Searching for library jars in: " + libDir);

            File[] files = libDir.listFiles(new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    return name.endsWith(".jar");
                }
            });
            if (files == null) {
                monitor.setProgress(min + (max - min) / 5);
                continue;
            }

            for (File file : files) {
                System.out.println("Adding Jar: " + file);

                try {
                    method.invoke(sysloader, new Object[]{file.toURI().toURL()});
                } catch (Exception ex) {
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

        File[] files = EXTENSION_DIR.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });

        double fileCount = 0;
        for (File file : files) {
            System.out.println("Searching for extensions in: " + file);
            monitor.setProgress((int) ((min + max) / 2.0 * (1.0 + fileCount++ / files.length)));
            monitor.setNote("Searching for extensions in: " + file);

            try {
                JarFile jar = new JarFile(file);

                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();

                    if (name.endsWith(".class")) {
                        Class<?> clazz = null;
                        try {
                            // Get rid of class
                            name = name.substring(0, name.length() - 6);
                            // Change to package name
                            name = name.replaceAll("/", ".");

                            clazz = Class.forName(name, false, sysloader);

                            Class<? extends Widget> element = clazz.asSubclass(Widget.class);
                            DisplayElementRegistry.registerWidget(element);

                            System.out.println("Custom Widget: " + clazz.getSimpleName());
                        } catch (ClassCastException ex) {
                            try {
                                Class<? extends StaticWidget> element = clazz.asSubclass(StaticWidget.class);
                                DisplayElementRegistry.registerStaticWidget(element);

                                System.out.println("Custom Static Widget: " + clazz.getSimpleName());
                            } catch (ClassCastException ex2) {
                            }
                        } catch (ClassNotFoundException ex) {
                        } catch (NoClassDefFoundError ex) {
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
                System.out.println("Error, could not add URL to system classloader");
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
