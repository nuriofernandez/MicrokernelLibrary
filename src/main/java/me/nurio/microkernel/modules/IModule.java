package me.nurio.microkernel.modules;

public interface IModule {

    String getName();

    String getAuthor();

    void onEnable();

    void onDisable();

}