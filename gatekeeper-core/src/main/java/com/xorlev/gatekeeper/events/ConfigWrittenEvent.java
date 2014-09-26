package com.xorlev.gatekeeper.events;

import lombok.Value;

/**
 * 2014-09-25
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
@Value
public class ConfigWrittenEvent {
    private final ClustersUpdatedEvent event;
}
