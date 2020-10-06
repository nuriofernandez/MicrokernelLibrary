package me.nurio.microkernel.modules;

public interface Module {

    String getName();

    String getAuthor();

    void onEnable();

    void onDisable();

}