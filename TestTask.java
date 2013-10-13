package dentex.youtube.downloader.queue;

import java.util.Random;

import android.util.Log;

public class TestTask implements Runnable {

	private static final String DEBUG_TAG = "TestTask";
	
	private static final Random random = new Random();
	
	private int mLengthSec;
	
	public TestTask(int lengthSec) {
		lengthSec = random.nextInt(3) + 1;
		mLengthSec = lengthSec;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(mLengthSec * 1000);
			
			// it's a good idea to always catch Throwable
			// in isolated "codelets" like Runnable or Thread
			// otherwise the exception might be sunk by some
			// agent that actually runs your Runnable - you
			// never know what it might be.
		} catch (Throwable t) {
			Log.e(DEBUG_TAG, "Error in TestTask", t);
		}
	}
}
