package com.umaplay.androidscreencast.util;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import javafx.scene.input.KeyCode;

import java.io.*;


/**
 * Created by user on 10/19/2015.
 */
public class Command {

    public static String sendToDroid(IDevice device, String command) throws IOException {
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

    public static String sendToRuntime(String command) {
        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String response = "";
            String line = null;
            while ((line = in.readLine()) != null) {
                response += line;
            }
            return response;
        } catch (IOException e)  {
            return e.getMessage();
        }
    }

    public static String getKeyEvent(KeyCode keyCode) {
        switch (keyCode){
            //special characters here
            case F1:
                return "82"; //menu
            case ESCAPE:
            case F2:
                return "4";
            case F3:
                return "3"; // home
            case F10:
                return "26"; //power
            case F11:
                return "27"; //camera

            case UP:
                return "19";
            case DOWN:
                return "20";
            case LEFT:
                return "21";
            case RIGHT:
                return "22";

            case ENTER:
                return "66";
            case SPACE:
                return "62";
            case BACK_SPACE:
                return "67";
            case DELETE:
                return "112";
//            case SHIFT:
//                return "59";
            case TAB:
                return "61";

            case NUM_LOCK:
                return "78";
            case HOME:
                return "122";
            case END:
                return "123";
            case PAGE_UP:
                return "92";
            case PAGE_DOWN:
                return "23";


        }

        return null;
//        1 -->  "KEYCODE_SOFT_LEFT"
//        2 -->  "KEYCODE_SOFT_RIGHT"
//        5 -->  "KEYCODE_CALL"
//        6 -->  "KEYCODE_ENDCALL"
//        23 -->  "KEYCODE_DPAD_CENTER"
//        24 -->  "KEYCODE_VOLUME_UP"
//        25 -->  "KEYCODE_VOLUME_DOWN"
//        28 -->  "KEYCODE_CLEAR"
//        57 -->  "KEYCODE_ALT_LEFT"
//        58 -->  "KEYCODE_ALT_RIGHT"
//        59 -->  "KEYCODE_SHIFT_LEFT"
//        60 -->  "KEYCODE_SHIFT_RIGHT"
//        63 -->  "KEYCODE_SYM"
//        64 -->  "KEYCODE_EXPLORER"
//        65 -->  "KEYCODE_ENVELOPE"
//        68 -->  "KEYCODE_GRAVE"
//        69 -->  "KEYCODE_MINUS"
//        70 -->  "KEYCODE_EQUALS"
//        71 -->  "KEYCODE_LEFT_BRACKET"
//        72 -->  "KEYCODE_RIGHT_BRACKET"
//        73 -->  "KEYCODE_BACKSLASH"
//        74 -->  "KEYCODE_SEMICOLON"
//        75 -->  "KEYCODE_APOSTROPHE"
//        76 -->  "KEYCODE_SLASH"
//        77 -->  "KEYCODE_AT"
//        78 -->  "KEYCODE_NUM"

    }
}
