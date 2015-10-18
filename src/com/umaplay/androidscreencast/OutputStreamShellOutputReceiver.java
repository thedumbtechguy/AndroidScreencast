package com.umaplay.androidscreencast;

import com.android.ddmlib.IShellOutputReceiver;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamShellOutputReceiver implements IShellOutputReceiver {

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
