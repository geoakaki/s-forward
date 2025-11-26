package com.smsforwarder;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class RuleManager {
    private static final String PREFS_NAME = "ForwardingRules";
    private static final String KEY_RULES = "rules";
    private SharedPreferences prefs;

    public RuleManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public List<ForwardingRule> getAllRules() {
        Set<String> rulesJson = prefs.getStringSet(KEY_RULES, new HashSet<String>());
        List<ForwardingRule> rules = new ArrayList<>();

        for (String json : rulesJson) {
            ForwardingRule rule = ForwardingRule.fromJson(json);
            if (rule != null) {
                rules.add(rule);
            }
        }

        return rules;
    }

    public void addRule(ForwardingRule rule) {
        if (rule.getId() == null || rule.getId().isEmpty()) {
            rule.setId(UUID.randomUUID().toString());
        }

        Set<String> rulesJson = new HashSet<>(prefs.getStringSet(KEY_RULES, new HashSet<String>()));
        rulesJson.add(rule.toJson());

        prefs.edit().putStringSet(KEY_RULES, rulesJson).apply();
    }

    public void updateRule(ForwardingRule rule) {
        deleteRule(rule.getId());
        addRule(rule);
    }

    public void deleteRule(String id) {
        Set<String> rulesJson = new HashSet<>(prefs.getStringSet(KEY_RULES, new HashSet<String>()));
        Set<String> newRules = new HashSet<>();

        for (String json : rulesJson) {
            ForwardingRule rule = ForwardingRule.fromJson(json);
            if (rule != null && !rule.getId().equals(id)) {
                newRules.add(json);
            }
        }

        prefs.edit().putStringSet(KEY_RULES, newRules).apply();
    }

    public List<ForwardingRule> getEnabledRules() {
        List<ForwardingRule> allRules = getAllRules();
        List<ForwardingRule> enabledRules = new ArrayList<>();

        for (ForwardingRule rule : allRules) {
            if (rule.isEnabled()) {
                enabledRules.add(rule);
            }
        }

        return enabledRules;
    }
}
