package com.vinberts.gifvault.events;

import com.vinberts.gifvault.views.VaultCell;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 *
 */
public class DeleteVaultEvent extends Event {
    private static final long serialVersionUID = 20200520829L;

    private VaultCell vaultCell;

    public VaultCell getVaultCell() {
        return vaultCell;
    }

    public static final EventType<DeleteVaultEvent> DELETED =
            new EventType<>(Event.ANY, "DELETED");

    public DeleteVaultEvent() {
        super(DELETED);
    }

    public DeleteVaultEvent(final Object source, final EventTarget target, final EventType<? extends Event> eventType, final VaultCell vaultCell) {
        super(source, target, eventType);
        this.vaultCell = vaultCell;
    }

    @Override
    public DeleteVaultEvent copyFor(final Object newSource, final EventTarget newTarget) {
        return (DeleteVaultEvent) super.copyFor(newSource, newTarget);
    }

    @Override
    public EventType<? extends DeleteVaultEvent> getEventType() {
        return (EventType<? extends DeleteVaultEvent>) super.getEventType();
    }
}
