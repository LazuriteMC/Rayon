package dev.lazurite.rayon.impl.transporter.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.Identifier;

public final class PatternBufferEvents {
    public static final Event<PatternReceived> PATTERN_RECEIVED = EventFactory.createArrayBacked(PatternReceived.class, (callbacks) -> (identifier) -> {
        for (PatternReceived event : callbacks) {
            event.onReceived(identifier);
        }
    });

    private PatternBufferEvents() { }

    @FunctionalInterface
    public interface PatternReceived {
        void onReceived(Identifier identifier);
    }
}
