package me.nurio.microkernel.loader;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class ModuleFileManager {

    public File getModulesFolder() {
        File file = new File("modules");
        file.mkdir();
        return file;
    }

    public List<File> getModulesFiles() {
        File modulesFolder = getModulesFolder();

        if (modulesFolder.listFiles().length == 0) return new ArrayList<>();
        return Arrays.stream(modulesFolder.listFiles())
            .filter(file -> file.getName().endsWith(".jar"))
            .collect(Collectors.toList());
    }

    @SneakyThrows
    public String getMainClassPath(File path) {
        try (JarFile jarFile = new JarFile(path)) {
            JarEntry entry = jarFile.getJarEntry("module.yml");
            if (entry == null) return null;
            InputStream input = jarFile.getInputStream(entry);

            // This code is temporary, I hope hope hope.
            return new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))
                .lines()
                .filter(line -> line.startsWith("main: "))
                .map(line -> line.split("main: ")[1])
                .map(line -> line.replaceAll("\"", ""))
                .map(line -> line.replaceAll(" ", ""))
                .findFirst()
                .orElse(null);
        }
    }

}