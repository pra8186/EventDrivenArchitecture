package com.eventdriven.core.service;

import java.util.List;

/**
 * Result of validating a user's tax profile completeness.
 */
public class ValidationResult {

    private final List<String> presentItems;
    private final List<String> missingItems;
    private final boolean complete;

    public ValidationResult(List<String> presentItems, List<String> missingItems, boolean complete) {
        this.presentItems = presentItems;
        this.missingItems = missingItems;
        this.complete = complete;
    }

    public List<String> getPresentItems() { return presentItems; }
    public List<String> getMissingItems() { return missingItems; }
    public boolean isComplete() { return complete; }
}
