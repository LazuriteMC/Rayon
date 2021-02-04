package dev.lazurite.rayon.impl.transporter.api.event;

import dev.lazurite.rayon.impl.transporter.api.pattern.Pattern;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.Identifier;

public final class PatternBufferEvents {
    public static final Event<PatternReceived> PATTERN_RECEIVED = EventFactory.createArrayBacked(PatternReceived.class, (callbacks) -> (identifier, pattern) -> {
        for (PatternReceived event : callbacks) {
            event.onReceived(identifier, pattern);
        }
    });

    private PatternBufferEvents() { }

    @FunctionalInterface
    public interface PatternReceived {
        void onReceived(Identifier identifier, Pattern pattern);
    }
}
