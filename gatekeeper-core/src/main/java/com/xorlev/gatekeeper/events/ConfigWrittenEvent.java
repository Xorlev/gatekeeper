package com.xorlev.gatekeeper.events;

import lombok.Value;

/**
 * Event emitted after configuration has been written to disk
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
@Value
public class ConfigWrittenEvent {
    private final ClustersUpdatedEvent event;
}
