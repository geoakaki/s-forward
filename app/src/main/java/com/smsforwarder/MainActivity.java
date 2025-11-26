package com.smsforwarder;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.List;

public class MainActivity extends Activity {
    private static final int PERMISSION_REQUEST_CODE = 123;
    private ListView listRules;
    private Button btnAddRule;
    private TextView tvStatus;
    private RuleManager ruleManager;
    private RuleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ruleManager = new RuleManager(this);

        listRules = findViewById(R.id.listRules);
        btnAddRule = findViewById(R.id.btnAddRule);
        tvStatus = findViewById(R.id.tvStatus);

        adapter = new RuleAdapter();
        listRules.setAdapter(adapter);

        btnAddRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddRuleDialog(null);
            }
        });

        requestSmsPermissions();
        updateStatus();
        loadRules();

        // Add default rule if no rules exist
        if (ruleManager.getAllRules().isEmpty()) {
            ForwardingRule defaultRule = new ForwardingRule(null, "GWP", "+995577000000", true);
            ruleManager.addRule(defaultRule);
            loadRules();
        }
    }

    private void loadRules() {
        adapter.notifyDataSetChanged();
        updateStatus();
    }

    private void showAddRuleDialog(final ForwardingRule editRule) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_rule, null);

        final EditText editSender = dialogView.findViewById(R.id.editSenderFilter);
        final EditText editNumber = dialogView.findViewById(R.id.editForwardNumber);
        final CheckBox checkEnabled = dialogView.findViewById(R.id.checkEnabled);

        if (editRule != null) {
            editSender.setText(editRule.getSenderFilter());
            editNumber.setText(editRule.getForwardNumber());
            checkEnabled.setChecked(editRule.isEnabled());
            builder.setTitle("Edit Rule");
        } else {
            checkEnabled.setChecked(true);
            builder.setTitle("Add New Rule");
        }

        builder.setView(dialogView);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String sender = editSender.getText().toString().trim();
            String number = editNumber.getText().toString().trim();

            if (sender.isEmpty() || number.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            ForwardingRule rule = new ForwardingRule(
                    editRule != null ? editRule.getId() : null,
                    sender,
                    number,
                    checkEnabled.isChecked()
            );

            if (editRule != null) {
                ruleManager.updateRule(rule);
            } else {
                ruleManager.addRule(rule);
            }

            loadRules();
            Toast.makeText(MainActivity.this, "Rule saved", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void requestSmsPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.RECEIVE_SMS,
                                Manifest.permission.READ_SMS,
                                Manifest.permission.SEND_SMS,
                                Manifest.permission.READ_PHONE_STATE
                        },
                        PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show();
                updateStatus();
            } else {
                Toast.makeText(this, "Permissions are required for SMS forwarding", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updateStatus() {
        boolean hasPermissions = Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED &&
                 ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED);

        List<ForwardingRule> enabledRules = ruleManager.getEnabledRules();

        if (hasPermissions && !enabledRules.isEmpty()) {
            tvStatus.setText("Status: Active\n" + enabledRules.size() + " rule(s) enabled");
            tvStatus.setTextColor(0xFF00AA00);
        } else if (!hasPermissions) {
            tvStatus.setText("Status: Inactive\nPermissions required");
            tvStatus.setTextColor(0xFFAA0000);
        } else {
            tvStatus.setText("Status: Inactive\nNo rules enabled");
            tvStatus.setTextColor(0xFFFF9900);
        }
    }

    private class RuleAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return ruleManager.getAllRules().size();
        }

        @Override
        public ForwardingRule getItem(int position) {
            return ruleManager.getAllRules().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.list_item_rule, parent, false);
            }

            final ForwardingRule rule = getItem(position);

            TextView tvSender = convertView.findViewById(R.id.tvSender);
            TextView tvNumber = convertView.findViewById(R.id.tvNumber);
            CheckBox checkEnabled = convertView.findViewById(R.id.checkEnabled);
            Button btnEdit = convertView.findViewById(R.id.btnEdit);
            Button btnDelete = convertView.findViewById(R.id.btnDelete);

            tvSender.setText("From: " + rule.getSenderFilter());
            tvNumber.setText("To: " + rule.getForwardNumber());
            checkEnabled.setChecked(rule.isEnabled());

            checkEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
                rule.setEnabled(isChecked);
                ruleManager.updateRule(rule);
                updateStatus();
            });

            btnEdit.setOnClickListener(v -> showAddRuleDialog(rule));

            btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete Rule")
                        .setMessage("Are you sure you want to delete this rule?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            ruleManager.deleteRule(rule.getId());
                            loadRules();
                            Toast.makeText(MainActivity.this, "Rule deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });

            return convertView;
        }
    }
}
