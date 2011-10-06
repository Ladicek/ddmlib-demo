package cz.ladicek.android.ddmlib;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;

public class Demo {
    // you can call AndroidDebugBridge.init() and terminate() only once
    // createBridge() and disconnectBridge() can be called as many times as you want

    public void init() {
        AndroidDebugBridge.init(false);
    }

    public void finish() {
        AndroidDebugBridge.terminate();
    }

    public void usingWaitLoop() throws Exception {
        AndroidDebugBridge adb = AndroidDebugBridge.createBridge();

        try {
            int trials = 10;
            while (trials > 0) {
                Thread.sleep(50);
                if (adb.isConnected()) {
                    break;
                }
                trials--;
            }

            if (!adb.isConnected()) {
                System.out.println("Couldn't connect to ADB server");
                return;
            }

            trials = 10;
            while (trials > 0) {
                Thread.sleep(50);
                if (adb.hasInitialDeviceList()) {
                    break;
                }
                trials--;
            }

            if (!adb.hasInitialDeviceList()) {
                System.out.println("Couldn't list connected devices");
                return;
            }

            for (IDevice device : adb.getDevices()) {
                System.out.println("- " + device.getSerialNumber());
            }
        } finally {
            AndroidDebugBridge.disconnectBridge();
        }
    }

    public void usingDeviceChangeListener() throws Exception {
        AndroidDebugBridge.addDeviceChangeListener(new AndroidDebugBridge.IDeviceChangeListener() {
            // this gets invoked on another thread, but you probably shouldn't count on it
            public void deviceConnected(IDevice device) {
                System.out.println("* " + device.getSerialNumber());
            }

            public void deviceDisconnected(IDevice device) {
            }

            public void deviceChanged(IDevice device, int changeMask) {
            }
        });

        AndroidDebugBridge adb = AndroidDebugBridge.createBridge();

        Thread.sleep(1000);
        if (!adb.isConnected()) {
            System.out.println("Couldn't connect to ADB server");
        }

        AndroidDebugBridge.disconnectBridge();
    }

    public static void main(String[] args) throws Exception {
        Demo demo = new Demo();

        demo.init();

        // I think this is the way to go for non-interactive or short-running applications
        System.out.println("Demo using wait loop to ensure connection to ADB server and then enumerate devices synchronously");
        demo.usingWaitLoop();

        // this looks like the right way for interactive or long-running applications
        System.out.println("Demo using DeviceChangeListener to get information about devices asynchronously");
        demo.usingDeviceChangeListener();

        demo.finish();
    }
}
