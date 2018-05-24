package com.crawljax.core.plugin;

import com.crawljax.core.CrawlerContext;
import com.crawljax.core.state.Eventable;
import com.crawljax.core.state.StateVertex;

/**
 * PreFireEventPlugin
 */
public interface PreFireEventPlugin extends Plugin {

    /**
     * Calls next Event
     * @param context current context
     * @param nextEvent next event
     */
    void preFireEvent(CrawlerContext context, Eventable nextEvent);

}