package edu.wpi.first.smartdashboard;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.wpiutil.RuntimeDetector;
import edu.wpi.first.wpiutil.RuntimeLoader;
import edu.wpi.first.wpiutil.WPIUtilJNI;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeLoader {
  public static native boolean addDllSearchDirectory(String directory);

  public static void LoadLibraries(String... librariesToLoad) throws IOException {
    WPIUtilJNI.Helper.setExtractOnStaticLoad(false);
    NetworkTablesJNI.Helper.setExtractOnStaticLoad(false);

    // Extract everything
    TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
    };
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> map;
    try (var stream = NativeLoader.class.getResourceAsStream("/ResourceInformation.json")) {
      map = mapper.readValue(stream, typeRef);
    }

    String hash = (String) map.get("hash");
    var platformPath = Paths.get(RuntimeDetector.getPlatformPath());
    var platform = platformPath.getName(0).toString();
    var arch = platformPath.getName(1).toString();

    var platformMap = (Map<String, List>) map.get(platform);

    var fileList = (List<String>) platformMap.get(arch);

    System.out.println(arch);
    System.out.println(platformMap);
    System.out.println(fileList);

    var defaultExtractionRoot = RuntimeLoader.getDefaultExtractionRoot();
    var extractionPath = Paths.get(defaultExtractionRoot, platform, arch, hash);
    var extractionPathString = extractionPath.toString();

    List<String> extractedFiles = new ArrayList<>();

    for (var file : fileList) {
      try (var stream = NativeLoader.class.getResourceAsStream(file)) {

        if (stream == null) {
          throw new NullPointerException();
        }
        var outputFile = Paths.get(extractionPathString, new File(file).getName());
        extractedFiles.add(outputFile.toString());
        if (outputFile.toFile().exists()) {
          continue;
        }
        System.out.println(outputFile);
        outputFile.getParent().toFile().mkdirs();

        try (var os = Files.newOutputStream(outputFile)) {
          byte[] buffer = new byte[0xFFFF]; // 64K copy buffer
          int readBytes;
          while ((readBytes = stream.read(buffer)) != -1) { // NOPMD
            os.write(buffer, 0, readBytes);
          }
        }
      }
    }

    if (RuntimeDetector.isWindows()) {
      var extractorFile = Paths.get(extractionPathString, "WindowsLoaderHelper.dll").toString();
      System.load(extractorFile);
      addDllSearchDirectory(extractionPathString);
      // Load windows, set dll directory
    }

    for (var library : librariesToLoad) {
      for (var extractedFile : extractedFiles) {
        if (extractedFile.contains(library)) {
          // Load it
          System.out.println("Loading " + extractedFile);
          System.load(extractedFile);
        }
      }
    }
  }
}
