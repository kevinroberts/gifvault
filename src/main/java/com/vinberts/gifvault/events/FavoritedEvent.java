package com.vinberts.gifvault.events;

import com.vinberts.gifvault.data.GifVault;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 *
 */
public class FavoritedEvent extends Event {
    private static final long serialVersionUID = 20200520826L;

    private GifVault gifVault;

    public GifVault getGifVault() {
        return gifVault;
    }

    public static final EventType<FavoritedEvent> FAVORITED =
            new EventType<>(Event.ANY, "FAVORITED");

    public static final EventType<FavoritedEvent> UNFAVORITED =
            new EventType<>(Event.ANY, "UNFAVORITED");

    public FavoritedEvent() {
        super(FAVORITED);
    }

    public FavoritedEvent(final Object source, final EventTarget target, final EventType<? extends Event> eventType, final GifVault gifVault) {
        super(source, target, eventType);
        this.gifVault = gifVault;
    }

    @Override
    public FavoritedEvent copyFor(final Object newSource, final EventTarget newTarget) {
        return (FavoritedEvent) super.copyFor(newSource, newTarget);
    }

    @Override
    public EventType<? extends FavoritedEvent> getEventType() {
        return (EventType<? extends FavoritedEvent>) super.getEventType();
    }
}
