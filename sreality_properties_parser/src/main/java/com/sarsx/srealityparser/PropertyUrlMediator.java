package com.sarsx.srealityparser;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

public class PropertyUrlMediator extends AbstractMediator {
    @Override
    public boolean mediate(MessageContext messageContext) {
        Parser parser = new Parser();
        StringBuilder builder = new StringBuilder();
        builder.append("<srealityPropertyUrl>");
        parser.getPropertiesUrl().forEach(builder::append);
        builder.append("</srealityPropertyUrl>");
        messageContext.setProperty("srealityPropertyUrlList", builder.toString());
        return true;
    }
}
