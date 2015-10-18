package com.umaplay.androidscreencast;

import com.android.ddmlib.IDevice;
import com.sun.xml.internal.ws.util.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by user on 10/18/2015.
 */
public class Injector {
    private static final int PORT = 1324;
    private static final String LOCAL_AGENT_JAR_LOCATION = "/MyInjectEventApp.jar";
    private static final String REMOTE_AGENT_JAR_LOCATION = "/data/local/tmp/InjectAgent.jar";
    private static final String AGENT_MAIN_CLASS = "net.srcz.android.screencast.client.Main";
    IDevice device;

    public static Socket s;
    OutputStream os;


    public ScreenCaptureThread screencapture;

    public Injector(IDevice d) throws IOException {
        this.device = d;
        this.screencapture = new ScreenCaptureThread(d);
    }





    /**
     * @return true if there was a client running
     */
    private static boolean killRunningAgent() {
        try {
            Socket s = new Socket("127.0.0.1", PORT);
            OutputStream os = s.getOutputStream();
            os.write("quit\n".getBytes());
            os.flush();
            os.close();
            s.close();
            return true;
        } catch (Exception ex) {
            // ignor?
        }
        return false;
    }

    public void close() {
        try {
            if (os != null) {
                os.write("quit\n".getBytes());
                os.flush();
                os.close();
            }
            s.close();
        } catch (Exception ex) {
            // ignored
        }
        screencapture.interrupt();
        try {
            s.close();
        } catch (Exception ex) {
            // ignored
        }
        try {
            synchronized (device) {
				/*
				 * if(device != null) device.removeForward(PORT, PORT);
				 */
            }
        } catch (Exception ex) {
            // ignored
        }
    }

    public void injectMouse(int action, float x, float y) throws IOException {
        long downTime = 10;
        long eventTime = 10;

        int metaState = -1;

        String cmdList1 = "pointer/" + downTime + "/" + eventTime + "/"
                + action + "/" + x + "/" + y + "/" + metaState;
        injectData(cmdList1);
    }


    public void injectKeycode(int type, int keyCode) {
        String cmdList = "key/" + type + "/" + keyCode;
        injectData(cmdList);
    }

    private void injectData(String data) {
        try {
            if (os == null) {
                System.out.println("Injector is not running yet...");
                return;
            }
            os.write((data + "\n").getBytes());
            os.flush();
        } catch (Exception sex) {
            try {
                s = new Socket("127.0.0.1", PORT);
                os = s.getOutputStream();
                os.write((data + "\n").getBytes());
                os.flush();
            } catch(Exception ex) {
                // ignored
            }
        }
    }



    private void launchProg(String cmdList) throws IOException {
        String fullCmd = "export CLASSPATH=" + REMOTE_AGENT_JAR_LOCATION;
        fullCmd += "; exec app_process /system/bin " + AGENT_MAIN_CLASS + " "
                + cmdList;
        System.out.println(fullCmd);
        device.executeShellCommand(fullCmd,
                new OutputStreamShellOutputReceiver(System.out));
        System.out.println("Prog ended");
        device.executeShellCommand("rm " + REMOTE_AGENT_JAR_LOCATION,
                new OutputStreamShellOutputReceiver(System.out));
    }
}
