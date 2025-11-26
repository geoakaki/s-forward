package com.smsforwarder;

public class ForwardingRule {
    private String id;
    private String senderFilter;
    private String forwardNumber;
    private boolean enabled;

    public ForwardingRule(String id, String senderFilter, String forwardNumber, boolean enabled) {
        this.id = id;
        this.senderFilter = senderFilter;
        this.forwardNumber = forwardNumber;
        this.enabled = enabled;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderFilter() {
        return senderFilter;
    }

    public void setSenderFilter(String senderFilter) {
        this.senderFilter = senderFilter;
    }

    public String getForwardNumber() {
        return forwardNumber;
    }

    public void setForwardNumber(String forwardNumber) {
        this.forwardNumber = forwardNumber;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String toJson() {
        return "{\"id\":\"" + id + "\",\"senderFilter\":\"" + senderFilter +
               "\",\"forwardNumber\":\"" + forwardNumber + "\",\"enabled\":" + enabled + "}";
    }

    public static ForwardingRule fromJson(String json) {
        try {
            json = json.trim();
            if (json.startsWith("{") && json.endsWith("}")) {
                json = json.substring(1, json.length() - 1);
            }

            String id = extractValue(json, "id");
            String senderFilter = extractValue(json, "senderFilter");
            String forwardNumber = extractValue(json, "forwardNumber");
            boolean enabled = Boolean.parseBoolean(extractValue(json, "enabled"));

            return new ForwardingRule(id, senderFilter, forwardNumber, enabled);
        } catch (Exception e) {
            return null;
        }
    }

    private static String extractValue(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) {
            searchKey = "\"" + key + "\":";
            startIndex = json.indexOf(searchKey);
            if (startIndex == -1) return "";
            startIndex += searchKey.length();
            int endIndex = json.indexOf(",", startIndex);
            if (endIndex == -1) endIndex = json.indexOf("}", startIndex);
            return json.substring(startIndex, endIndex).trim();
        }
        startIndex += searchKey.length();
        int endIndex = json.indexOf("\"", startIndex);
        return json.substring(startIndex, endIndex);
    }
}
