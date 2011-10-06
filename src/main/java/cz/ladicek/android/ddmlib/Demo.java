/*
 * Copyright 2011 Ladislav Thon
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
