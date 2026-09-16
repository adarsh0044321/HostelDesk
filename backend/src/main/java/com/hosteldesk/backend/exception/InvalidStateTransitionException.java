package com.hosteldesk.backend.exception;

import com.hosteldesk.backend.entity.IssueStatus;

public class InvalidStateTransitionException extends RuntimeException {
    private final IssueStatus from;
    private final IssueStatus to;

    public InvalidStateTransitionException(IssueStatus from, IssueStatus to, String message) {
        super(String.format("Invalid state transition from %s to %s: %s", from, to, message));
        this.from = from;
        this.to = to;
    }

    public IssueStatus getFrom() { return from; }
    public IssueStatus getTo() { return to; }
}
