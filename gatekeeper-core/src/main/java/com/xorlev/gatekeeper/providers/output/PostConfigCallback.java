package com.xorlev.gatekeeper.providers.output;

import com.xorlev.gatekeeper.providers.discovery.ClustersUpdatedEvent;

/**
 * 2014-03-01
 *
 * @author Michael Rose <michael@fullcontact.com>
 */
public interface PostConfigCallback {
    void onConfigFinished(ClustersUpdatedEvent event);
}
