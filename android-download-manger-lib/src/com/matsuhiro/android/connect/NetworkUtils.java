
package com.matsuhiro.android.connect;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.HttpResponseCache;
import android.util.Log;

public class NetworkUtils {
	
	static String DEBUG_TAG = "NetworkUtils";
	
	static int to = 2000;  //TimeOut in milliseconds
	static String ipRegEx = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
	
	public static String getExternalIpAddress() {
		Log.d(DEBUG_TAG, "getExternalIpAddress @ checkip.amazonaws.com");
		URL amazonaws = null;
		try {
			amazonaws = new URL("http://checkip.amazonaws.com/");
		} catch (MalformedURLException e) {
			Log.e(DEBUG_TAG, "MalformedURLException @getExternalIpAddress(): " + e.getMessage());
		}
		
    	String ip = null;
    	
    	try {
			URLConnection ucon = amazonaws.openConnection();
			ucon.setReadTimeout(to);
            ucon.setConnectTimeout(to);
			//ucon.connect();
			InputStream in = new BufferedInputStream(ucon.getInputStream());
			   try {
			     ip = readStream(in, 100000);
			   } finally {
			     in.close();
			   }
    	} catch (SocketTimeoutException ste) {
    		ip = getExtIpAddress2();
		} catch (IOException e) {
			ip = "";
		}
		return ip;
    }
	
	private static String readStream(InputStream stream, int len) {
        Reader reader = null;
        try {
			reader = new InputStreamReader(stream, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        char[] buffer = new char[len];
        try {
			reader.read(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        String content = new String(buffer);
        Pattern pattern = Pattern.compile(ipRegEx);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
        	return matcher.group();
        } else {
        	return "";
        }
    }

	public static String getExtIpAddress2() {
		Log.d(DEBUG_TAG, "getExtIpAddress2 @ checkip.org");
		URL checkiporg = null;
		try {
			checkiporg = new URL("http://checkip.org/");
		} catch (MalformedURLException e) {
			Log.e(DEBUG_TAG, "MalformedURLException @getExtIPAddress(): " + e.getMessage());
		}
		
    	String ip = null;
    	
    	try {
			URLConnection ucon = checkiporg.openConnection();
			ucon.setReadTimeout(to);
            ucon.setConnectTimeout(to);
            InputStream in = new BufferedInputStream(ucon.getInputStream());
			   try {
			     ip = readStream(in, 100000);
			   } finally {
			     in.close();
			   }
    	} catch (SocketTimeoutException ste) {
    		ip = getExtIpAddress3();
		} catch (IOException e) {
			ip = "";
		}
    	return ip;
    }
	
	public static String getExtIpAddress3() {
		Log.d(DEBUG_TAG, "getExtIpAddress3 @ checkip.com");
		URL checkipcom = null;
		try {
			checkipcom = new URL("http://www.checkip.com/");
		} catch (MalformedURLException e) {
			Log.e(DEBUG_TAG, "MalformedURLException @getExtIPAddress(): " + e.getMessage());
		}
		
    	String ip = null;
    	
    	try {
			URLConnection ucon = checkipcom.openConnection();
			ucon.setReadTimeout(to);
            ucon.setConnectTimeout(to);
            InputStream in = new BufferedInputStream(ucon.getInputStream());
			   try {
			     ip = readStream(in, 100000);
			   } finally {
			     in.close();
			   }
    	} catch (SocketTimeoutException ste) {
    		ip = getExtIpAddress4();
		} catch (IOException e) {
			ip = "";
		}
    	return ip;
    }
	
	public static String getExtIpAddress4() {
		Log.d(DEBUG_TAG, "getExtIpAddress4 @ whatismyipaddress.com");
		URL whatismyipaddress = null;
		try {
			whatismyipaddress = new URL("http://whatismyipaddress.com/");
		} catch (MalformedURLException e) {
			Log.e(DEBUG_TAG, "MalformedURLException @getExtIPAddress(): " + e.getMessage());
		}
		
    	String ip = null;
    	
    	try {
			URLConnection ucon = whatismyipaddress.openConnection();
			ucon.setReadTimeout(to);
            ucon.setConnectTimeout(to);
            InputStream in = new BufferedInputStream(ucon.getInputStream());
			   try {
			     ip = readStream(in, 100000);
			   } finally {
			     in.close();
			   }
    	} catch (SocketTimeoutException ste) {
    		ip = "";
		} catch (IOException e) {
			ip = "";
		}
    	return ip;
    }
	
	public static String findLinkIp(String link) {
    	Pattern ipPattern = Pattern.compile("ip=(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})&");
    	Matcher ipMatcher = ipPattern.matcher(link);
    	if (ipMatcher.find()) {
    		return ipMatcher.group(1);
    	} else {
    		Pattern ipPattern2 = Pattern.compile("ip=(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})$");
        	Matcher ipMatcher2 = ipPattern2.matcher(link);
	    	if (ipMatcher2.find()) {
	    		return ipMatcher2.group(1);
	    	} else {
	    		Log.w(DEBUG_TAG, "patterns not matched @ findLinkIp");
	    		return "0.0.0.0";
	    	}
    	}
	}
	
	public static long findLinkExpireTime(String link) {
		Pattern expPattern = Pattern.compile("expire=(1\\d{9})&");
    	Matcher expMatcher = expPattern.matcher(link);
    	if (expMatcher.find()) {
    		try {
				return Long.parseLong(expMatcher.group(1));
			} catch (NumberFormatException e) {
				Log.w(DEBUG_TAG, "NumberFormatException @ findLinkExpiration: falling back to a ~6 hrs expire time");
				return (System.currentTimeMillis() / 1000) + 21600 + 120; // + 6 hrs + 5 mins
			}
    	} else {
    		Log.w(DEBUG_TAG, "pattern not matched @ findLinkExpiration: returning a ~6 hrs expire time");
    		return (System.currentTimeMillis() / 1000) + 21600 + 120;
    	}
	}

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED
                            || info[i].getState() == NetworkInfo.State.CONNECTING) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static File getCacheDir(Context context, boolean internal) {
        if (internal) {
            return context.getCacheDir();
        } else {
            return context.getExternalCacheDir();
        }
    }

    public static void enableHttpResponseCache(Context context, boolean internal, long size) {
        long httpCacheSize;
        if (size <= 0) {
            httpCacheSize = 10 * 1024 * 1024; // 10 MiB
        } else {
            httpCacheSize = size;
        }
        File httpCacheDir = new File(getCacheDir(context, internal), "http");
        try {
            android.net.http.HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
        }
    }

    //@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void flushHttpResponseCache() {
        //if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            HttpResponseCache cache = HttpResponseCache.getInstalled();
            if (cache != null) {
                cache.flush();
            }
        //}
    }

    private static String mUserAgent;

    public static String getUserAgent(Context context) {
        if (mUserAgent instanceof String) {
            return mUserAgent;
        }
        mUserAgent = "Mozilla/5.0 (X11; Linux i686; rv:10.0) Gecko/20100101 Firefox/10.0";//getDefaultUserAgentString(context);
        return mUserAgent;
    }

    /*public static String getDefaultUserAgentString(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            String userAgent = NewApiWrapperForUserAgent.getUserAgentJellyBeanMR1(context);
            return userAgent;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            String userAgent = NewApiWrapperForUserAgent.getUserAgentJellyBean(context);
            return userAgent;
        }

        try {
            Constructor<WebSettings> constructor = WebSettings.class.getDeclaredConstructor(
                    Context.class, WebView.class);
            constructor.setAccessible(true);
            try {
                WebSettings settings = constructor.newInstance(context, null);
                String userAgent = settings.getUserAgentString();
                return userAgent;
            } finally {
                constructor.setAccessible(false);
            }
        } catch (Exception e) {
            String userAgent = new WebView(context).getSettings().getUserAgentString();
            return userAgent;
        }
    }

    @SuppressLint("NewApi")
    private static class NewApiWrapperForUserAgent {
        static String getUserAgentJellyBeanMR1(Context context) {
            return WebSettings.getDefaultUserAgent(context);
        }

        static String getUserAgentJellyBean(Context context) {
            try {
                @SuppressWarnings("unchecked")
                Class<? extends WebSettings> clz = (Class<? extends WebSettings>) Class
                        .forName("android.webkit.WebSettingsClassic");
                Class<?> webViewClassicClz = (Class<?>) Class
                        .forName("android.webkit.WebViewClassic");
                Constructor<? extends WebSettings> constructor = clz.getDeclaredConstructor(
                        Context.class, webViewClassicClz);
                constructor.setAccessible(true);
                try {
                    WebSettings settings = constructor.newInstance(context, null);
                    String userAgent = settings.getUserAgentString();
                    return userAgent;
                } finally {
                    constructor.setAccessible(false);
                }
            } catch (Exception e) {
                String userAgent = new WebView(context).getSettings().getUserAgentString();
                return userAgent;
            }
        }
    }*/
}
