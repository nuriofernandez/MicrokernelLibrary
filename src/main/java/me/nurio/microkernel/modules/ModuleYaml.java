package me.nurio.microkernel.modules;

import lombok.*;

/**
 * This class represents a module.yml file inside a Module jar file.
 */
@NoArgsConstructor
public class ModuleYaml {

    /**
     * Module's main class path.
     */
    @Getter private String main;

}