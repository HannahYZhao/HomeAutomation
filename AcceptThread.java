package com.example.homeautomation;

private class AcceptThread extends Thread {
    private final BluetoothServerSocket mmServerSocket;

    public AcceptThread() {
        // Use a temporary object that is later assigned to mmServerSocket
        // because mmServerSocket is final.
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code.
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Socket's listen() method failed", e);
        }
        mmServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned.
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                Log.e(TAG, "Socket's accept() method failed", e);
                break;
            }

            if (socket != null) {
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.
                manageMyConnectedSocket(socket);
                mmServerSocket.close();
                break;
            }
        }
    }

    // Closes the connect socket and causes the thread to finish.
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }

    public static class MyDeviceSelectionActivity extends Activity {
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
}