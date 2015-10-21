package com.umaplay.androidscreencast.util;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by user on 10/19/2015.
 */
public class Command {

    public static String send(IDevice device, String command) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        IShellOutputReceiver receiver = new OutputStreamShellOutputReceiver(os);

        device.executeShellCommand(command, receiver);

        return new String(os.toByteArray());
    }

    private static class OutputStreamShellOutputReceiver implements IShellOutputReceiver {

        OutputStream os;

        public OutputStreamShellOutputReceiver(OutputStream os) {
            this.os = os;
        }

        public boolean isCancelled() {
            return false;
        }

        public void flush() {
        }

        public void addOutput(byte[] buf, int off, int len) {
            try {
                os.write(buf,off,len);
            } catch(IOException ex) {
                throw new RuntimeException(ex);
            }
        }

    }
}
