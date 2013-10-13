package dentex.youtube.downloader.queue;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class QueueActivity extends Activity 
	implements QueueThreadListener, OnClickListener {
	
	String DEBUG_TAG = "QueueActivity";
	
	private QueueThread queueThread;
	private Handler handler;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Create and launch the download thread
        queueThread = new QueueThread(this);
        queueThread.start();
        
        // Create the Handler. It will implicitly bind to the Looper
        // that is internally created for this thread (since it is the UI thread)
        handler = new Handler();
    }
    
    @Override
	protected void onDestroy() {
		super.onDestroy();
				
		// request the thread to stop
		queueThread.requestStop();
    }

	// note! this might be called from another thread
	@Override
	public void handleQueueThreadUpdate() {
		// we want to modify the progress bar so we need to do it from the UI thread 
		// how can we make sure the code runs in the UI thread? use the handler!
		handler.post(new Runnable() {
			@Override
			public void run() {
				int total = queueThread.getTotalQueued();
				int completed = queueThread.getTotalCompleted();

				// in case of use of a progress bar:
				//progressBar.setMax(total);
				//progressBar.setProgress(completed);
				
				Log.i(DEBUG_TAG, String.format("tasks completed: %d of %d", completed, total));
				
				// vibrate for fun
				if (completed == total) {
					((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(100);
				}
			}
		});
	}

	@Override
	public void onClick(View source) {
		//if (source.getId() == R.id.schedule_button) {
			//int rand = new Random().nextInt(3) + 1;
			
			//for (int i = 0; i < totalTasks; ++i) {
				//queueThread.enqueueTask(new TestTask(rand));
			//}
		//}
	}
}