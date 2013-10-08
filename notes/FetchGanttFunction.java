package dentex.youtube.downloader.utils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.bugsense.trace.BugSenseHandler;

public class FetchGanttFunction {
	
	String DEBUG_TAG = "FetchGanttFunction";
	
	public String doFetch(boolean fallingback, String url) {
        try {
            return downloadFunctionFromUrl(fallingback, url);
        } catch (IOException e) {
        	Log.e(DEBUG_TAG, "doFetch: " + e.getMessage());
	    	BugSenseHandler.sendExceptionMessage(DEBUG_TAG + "-> doFetch: ", e.getMessage(), e);
            return null;
        } catch (RuntimeException re) {
        	Log.e(DEBUG_TAG, "doFetch: " + re.getMessage());
	    	BugSenseHandler.sendExceptionMessage(DEBUG_TAG + "-> doFetch: ", re.getMessage(), re);
	    	return null;
        }
    }

    private String downloadFunctionFromUrl(boolean fallingback, String myurl) throws IOException {
    	HttpClient httpclient = new DefaultHttpClient();
    	HttpGet httpget = new HttpGet(myurl); 
    	ResponseHandler<String> responseHandler = new BasicResponseHandler();    
    	String responseBody = httpclient.execute(httpget, responseHandler);
    	httpclient.getConnectionManager().shutdown();
    	if (!fallingback) {
        	return fetchDecipheringFunction(responseBody.replaceAll("(?m)//.*?$", " "));
    	} else {
    		return responseBody;
    	}
	}

    private String fetchDecipheringFunction(String content) {
    	String function = null;
    	Pattern pattern = Pattern.compile("function isString.*?return sig;.*?\\}", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
        	function = matcher.group();
        }
        
        function = android.text.Html.fromHtml(function).toString();

        if (function == null) {
        	Log.e(DEBUG_TAG, "gantt's function: fetching error");
        } else {
        	//Utils.logger("v", function, DEBUG_TAG);
        	Utils.logger("d", "gantt's function: successfully fetched", DEBUG_TAG);
        }
    	return function;
    }
}
