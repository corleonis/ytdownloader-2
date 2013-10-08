package dentex.youtube.downloader.utils;

import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.bugsense.trace.BugSenseHandler;

public class FetchUrl {
	
	String DEBUG_TAG = "FetchUrl";
	
	public String doFetch(String url) {
        try {
        	Utils.logger("d", "doFetch: trying url " + url, DEBUG_TAG);
            return downloadWebPage(url);
        } catch (IOException e) {
        	Log.e(DEBUG_TAG, "doFetch: " + e.getMessage());
	    	BugSenseHandler.sendExceptionMessage(DEBUG_TAG + "-> doFetch", e.getMessage(), e);
            return "e";
        } catch (RuntimeException re) {
        	Log.e(DEBUG_TAG, "doFetch: " + re.getMessage());
	    	BugSenseHandler.sendExceptionMessage(DEBUG_TAG + "-> doFetch", re.getMessage(), re);
	    	return "e";
        }
    }

    private String downloadWebPage(String myurl) throws IOException {
    	HttpClient httpclient = new DefaultHttpClient();
    	HttpGet httpget = new HttpGet(myurl); 
    	ResponseHandler<String> responseHandler = new BasicResponseHandler();    
    	String responseBody = httpclient.execute(httpget, responseHandler);
    	httpclient.getConnectionManager().shutdown();
    	return responseBody;
	}
}
