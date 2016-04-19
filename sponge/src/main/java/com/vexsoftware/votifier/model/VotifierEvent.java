package com.vexsoftware.votifier.model;

import com.vexsoftware.votifier.Votifier;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public class VotifierEvent extends AbstractEvent implements Cancellable {
    private boolean cancelled;
    private Vote vote;

    public VotifierEvent(Vote vote) {
        this.vote = vote;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Vote getVote() {
        return vote;
    }

	@Override
	public Cause getCause () {
		return Votifier.getCause();
	}
}
