package ru.itis.balckjack.messages;

import java.util.Map;

public abstract class Message {
    private final String messageType;
    private Map<String, Object> attributes;

    public Message(String messageType) {
        this.messageType = messageType;
    }

    public String toQuery() {
        StringBuilder query = new StringBuilder();
        query.append("type:")
                .append(messageType);

        if (attributes != null)
            query.append(",attributes:{")
                    .append(attributesString())
                    .append("}");

        return query.toString();
    }

    private String attributesString() {
        StringBuilder attributesString = new StringBuilder();

        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            attributesString.append(entry.getKey())
                    .append(":")
                    .append(entry.getValue())
                    .append(",");
        }

        return attributesString.deleteCharAt(attributesString.length()-1).toString();
    }
}
