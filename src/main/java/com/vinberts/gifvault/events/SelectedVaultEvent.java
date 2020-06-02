package com.vinberts.gifvault.events;

import com.vinberts.gifvault.views.VaultCell;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 *
 */
public class SelectedVaultEvent extends Event {
    private static final long serialVersionUID = 20200520829L;

    private VaultCell gifVault;

    public VaultCell getGifVault() {
        return gifVault;
    }

    public static final EventType<SelectedVaultEvent> SELECTED =
            new EventType<>(Event.ANY, "SELECTED");

    public static final EventType<SelectedVaultEvent> UNSELECTED =
            new EventType<>(Event.ANY, "UNSELECTED");

    public SelectedVaultEvent() {
        super(SELECTED);
    }

    public SelectedVaultEvent(final Object source, final EventTarget target, final EventType<? extends Event> eventType, final VaultCell gifVault) {
        super(source, target, eventType);
        this.gifVault = gifVault;
    }

    @Override
    public SelectedVaultEvent copyFor(final Object newSource, final EventTarget newTarget) {
        return (SelectedVaultEvent) super.copyFor(newSource, newTarget);
    }

    @Override
    public EventType<? extends SelectedVaultEvent> getEventType() {
        return (EventType<? extends SelectedVaultEvent>) super.getEventType();
    }
}
