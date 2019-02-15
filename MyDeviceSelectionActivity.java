package com.example.homeautomation;

public class MyDeviceSelectionActivity extends Activity {
    private CompanionDeviceManager deviceManager;
    private AssociationRequest pairingRequest;
    private BluetoothDeviceFilter deviceFilter;

    private static final int SELECT_DEVICE_REQUEST_CODE = 42;

    @override
    public void onCreate() {
        // ...
        deviceManager = getSystemService(CompanionDeviceManager.class);

        // To skip filtering based on name and supported feature flags (UUIDs),
        // don't include calls to setNamePattern() and addServiceUuid(),
        // respectively. This example uses Bluetooth.
        deviceFilter = new BluetoothDeviceFilter.Builder()
                .setNamePattern(Pattern.compile("My device"), null)
                .addServiceUuid(new ParcelUuid(new UUID(0x123abcL, -1L)))
                .build();

        // The argument provided in setSingleDevice() determines whether a single
        // device name or a list of device names is presented to the user as
        // pairing options.
        pairingRequest = new AssociationRequest.Builder()
                .addDeviceFilter(deviceFilter)
                .setSingleDevice(true)
                .build();

        // When the app tries to pair with the Bluetooth device, show the
        // appropriate pairing request dialog to the user.
        deviceManager.associate(pairingRequest,
                new CompanionDeviceManager.Callback() {
                    @Override
                    public void onDeviceFound(IntentSender chooserLauncher) {
                        startIntentSenderForResult(chooserLauncher,
                                SELECT_DEVICE_REQUEST_CODE, null, 0, 0, 0);
                    }
                },
                null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_DEVICE_REQUEST_CODE &&
                resultCode == Activity.RESULT_OK) {
            // User has chosen to pair with the Bluetooth device.
            BluetoothDevice deviceToPair =
                    data.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE);
            deviceToPair.createBond();

            // ... Continue interacting with the paired device.
        }
    }
}