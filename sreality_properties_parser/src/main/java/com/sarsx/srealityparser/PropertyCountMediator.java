package com.sarsx.srealityparser;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

public class PropertyCountMediator extends AbstractMediator {
    @Override
    public boolean mediate(MessageContext messageContext) {
        Parser parser = new Parser();
        messageContext.setProperty("srealityPropertyCount", String.valueOf(parser.getPropertyCount()));
        return true;
    }
}
