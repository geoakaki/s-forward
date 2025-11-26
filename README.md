# SMS Forwarder

An Android application that automatically forwards SMS messages from specific senders to designated phone numbers using customizable rules.

## Features

- **Multiple Forwarding Rules**: Create and manage multiple SMS forwarding rules
- **Rule-based Filtering**: Filter messages by sender name or number
- **Enable/Disable Rules**: Easily toggle rules on/off without deleting them
- **Dynamic Configuration**: Add, edit, and delete rules directly from the app
- **Multi-part SMS Support**: Handles long messages automatically
- **Background Operation**: Works in the background without keeping the app open
- **Persistent Storage**: Rules are saved and survive app/device restarts

## Requirements

- Android device running Android 5.0 (API 21) or higher
- SMS permissions (granted at runtime)

## How It Works

1. The app listens for incoming SMS messages using a BroadcastReceiver
2. When an SMS arrives, it checks all enabled forwarding rules
3. If the sender matches any rule's filter, it forwards the message to the configured number
4. The forwarded message format: "From: [Original Sender]\n[Message Body]"
5. Multiple rules can match the same SMS, forwarding to different numbers

## Installation

### Download from Releases

1. Go to the [Releases](https://github.com/yourusername/s-forward/releases) page
2. Download the latest `sms-forwarder.apk`
3. Transfer to your Android device
4. Enable "Install from Unknown Sources" in device settings
5. Tap the APK file to install
6. Grant all requested permissions (SMS permissions are critical)

### Build from Source

#### Option 1: Using Android Studio

1. Install [Android Studio](https://developer.android.com/studio)
2. Open the project folder in Android Studio
3. Wait for Gradle sync to complete
4. Go to `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
5. The APK will be generated in `app/build/outputs/apk/debug/app-debug.apk`

#### Option 2: Using Command Line

1. Install [Android SDK Command Line Tools](https://developer.android.com/studio#command-tools)
2. Set ANDROID_HOME environment variable:
   ```bash
   export ANDROID_HOME=/path/to/android-sdk
   export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
   ```
3. Navigate to the project directory and run:
   ```bash
   ./gradlew assembleDebug
   ```
4. The APK will be in `app/build/outputs/apk/debug/app-debug.apk`

## Usage

### First Time Setup

1. Open the app
2. Grant all SMS permissions when prompted
3. A default rule will be created (GWP → +995577000000)
4. The app is now active and monitoring SMS

### Managing Rules

#### Add New Rule
1. Tap the "+ Add New Rule" button
2. Enter the sender filter (e.g., "GWP", "BANK", or partial phone number)
3. Enter the forward-to phone number (e.g., +995577000000)
4. Check "Enable this rule" if you want it active immediately
5. Tap "Save"

#### Edit Rule
1. Tap the "Edit" button on any rule
2. Modify the sender filter or forward number
3. Tap "Save"

#### Delete Rule
1. Tap the "Delete" button on any rule
2. Confirm deletion

#### Enable/Disable Rule
- Toggle the checkbox next to any rule to enable or disable it without deleting

### Example Rules

- **Forward bank SMS**: Sender filter = "BANK" → Forward to +995577000000
- **Forward from specific number**: Sender filter = "557700" → Forward to +995577111111
- **Forward from company**: Sender filter = "GWP" → Forward to +995577000000

## Automated Builds & Releases

This project includes a GitHub Actions workflow that automatically builds and releases the APK:

### Automatic Release on Tag

When you push a tag starting with `v` (e.g., `v1.0.0`):
```bash
git tag v1.0.0
git push origin v1.0.0
```

The workflow will:
1. Build the APK
2. Sign it (if signing secrets are configured)
3. Create a GitHub release
4. Upload the APK to the release

### Manual Build

You can also trigger a build manually from the GitHub Actions tab.

### Setting Up Signing (Optional)

To sign your APK in the CI/CD pipeline, add these secrets to your GitHub repository:

1. Go to Settings → Secrets and variables → Actions
2. Add the following secrets:
   - `SIGNING_KEY`: Base64-encoded keystore file
   - `ALIAS`: Key alias
   - `KEY_STORE_PASSWORD`: Keystore password
   - `KEY_PASSWORD`: Key password

To generate the base64-encoded keystore:
```bash
base64 -i your-keystore.jks | pbcopy  # macOS
base64 -w 0 your-keystore.jks  # Linux
```

## Permissions

The app requires the following permissions:
- `RECEIVE_SMS` - To receive incoming SMS messages
- `READ_SMS` - To read SMS content
- `SEND_SMS` - To forward messages
- `READ_PHONE_STATE` - Required for SMS operations on some devices

## Important Notes

- The app runs in the background and doesn't need to be open to forward messages
- Rules are stored locally using SharedPreferences
- Make sure the device has an active SMS plan
- SMS forwarding may incur standard messaging charges
- The app will continue working after device restart
- For Android 6.0+, you must manually grant SMS permissions
- One SMS can be forwarded to multiple numbers if multiple rules match

## Troubleshooting

**Messages not forwarding:**
- Check that all SMS permissions are granted
- Verify at least one rule is enabled
- Confirm the sender filter matches the incoming sender
- Check device SMS quota limits
- Review the rule's enabled checkbox status

**App not receiving SMS:**
- Ensure the app is not in battery optimization mode
- Check that SMS permissions are granted
- Try restarting the device
- Verify the app is not disabled in settings

**Rules not saving:**
- Make sure you tap "Save" in the dialog
- Check that both fields are filled in
- Try restarting the app

## Privacy & Security

- This app does not store any SMS data beyond the forwarding rules
- Messages are forwarded immediately and not saved
- No internet connection required
- All processing happens locally on the device
- Rules are stored in private app storage

## Technical Details

- **Language**: Java
- **Min SDK**: API 21 (Android 5.0)
- **Target SDK**: API 33 (Android 13)
- **Architecture**: BroadcastReceiver + SharedPreferences
- **Build System**: Gradle 8.0
- **CI/CD**: GitHub Actions

## Contributing

Feel free to submit issues or pull requests to improve the app.

## License

This app was created with assistance for personal use.
