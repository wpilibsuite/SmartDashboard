package edu.wpi.first.smartdashboard.extensions;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

import javax.swing.*;

import edu.wpi.first.smartdashboard.gui.*;
import edu.wpi.first.smartdashboard.gui.elements.*;
import edu.wpi.first.smartdashboard.types.*;

/**
 * This class searches through the ./lib and ./lib/extensions folders and adds
 * to the class path all of the jars it finds.  Then it searches the ./extensions folder
 * for any jars, adding them to the class path and then searching through them to find any
 * internal {@link StaticWidget StaticWidgets} or {@link Widget Widgets}.
 * @author Joe Grinstead
 */
public class FileSniffer {
    public static final String EXTENSIONS_FOLDER = "./extensions";

    public static void findExtensions(ProgressMonitor monitor, int min, int max) {
        monitor.setNote("Loading Extensions");
        File extensions = new File(EXTENSIONS_FOLDER);
        File lib1 = new File("./lib");
        File lib2 = new File("./extensions/lib");

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

        for (File lib : new File[]{lib1, lib2, extensions}) {
            if (!lib.exists()) {
                monitor.setProgress(min + (max - min) / 5);
                continue;
            }
            System.out.println("Searching Folder:" + lib);
            monitor.setNote("Searching Folder:" + lib);

            File[] files = lib.listFiles(new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    return name.endsWith(".jar");
                }
            });
            if (files == null) {
                monitor.setProgress(min + (max - min) / 5);
                continue;
            }

            for (File file : files) {
                System.out.println("Adding Jar:" + file);

                try {
                    method.invoke(sysloader, new Object[]{file.toURI().toURL()});
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            monitor.setProgress(min + (max - min) / 5);
        }

        if (!extensions.exists()) {
            System.out.println("No Extension Folder");
            monitor.setProgress(max);
            return;
        }

        File[] files = extensions.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });

        double fileCount = 0;
        for (File file : files) {
            System.out.println("Searching Jar:" + file);
            monitor.setProgress((int) ((min + max) / 2.0 * (1.0 + fileCount++ / files.length)));
            monitor.setNote("Searching Jar:" + file);

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

                            System.out.println("Custom Widget:" + clazz.getSimpleName());
                        } catch (ClassCastException ex) {
                            try {
                                Class<? extends StaticWidget> element = clazz.asSubclass(StaticWidget.class);
                                DisplayElementRegistry.registerStaticWidget(element);

                                System.out.println("Custom Static Widget:" + clazz.getSimpleName());
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
}
