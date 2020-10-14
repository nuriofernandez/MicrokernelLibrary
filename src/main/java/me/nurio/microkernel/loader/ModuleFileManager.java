package me.nurio.microkernel.loader;

import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleFileManager {

    @Getter private ModuleYamlParser yamler = new ModuleYamlParser();

    public File getModulesFolder() {
        File file = new File("modules");
        file.mkdir();
        return file;
    }

    public List<File> getModulesJarFiles() {
        File modulesFolder = getModulesFolder();
        if (modulesFolder.listFiles().length == 0) return new ArrayList<>();

        return Arrays.stream(modulesFolder.listFiles())
            .filter(file -> file.getName().endsWith(".jar"))
            .collect(Collectors.toList());
    }

}