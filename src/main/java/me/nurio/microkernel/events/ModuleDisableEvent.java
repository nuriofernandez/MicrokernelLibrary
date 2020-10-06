package me.nurio.microkernel.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.nurio.events.handler.Event;
import me.nurio.events.handler.EventCancellable;

@Getter
@RequiredArgsConstructor
public class ModuleDisableEvent extends Event implements EventCancellable {

    @Getter @Setter private boolean cancelled;

    private final String name;
    private final String author;
    private final String classPath;

}