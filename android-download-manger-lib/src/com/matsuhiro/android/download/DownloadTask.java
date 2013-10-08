
package com.matsuhiro.android.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.conn.ConnectTimeoutException;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.matsuhiro.android.connect.NetworkUtils;
import com.matsuhiro.android.storage.StorageUtils;

public class DownloadTask extends AsyncTask<Void, Integer, Long> {

    public final static int TIME_OUT = 30000;

    private final static int BUFFER_SIZE = 1024 * 8;

    private static final String TAG = DownloadTask.class.getSimpleName();

    private static final boolean DEBUG = true;

    public static final String TEMP_SUFFIX = ".download";

    private HttpURLConnection mConnection = null;

    private File mFile;

    private File mTempFile;

    private String mUrlString;

    private URL mURL;

    private DownloadTaskListener mListener;

    private Context mContext;

    private long mDownloadSize;

    private long mPreviousFileSize;

    private long mTotalSize;

    private int mDownloadPercent;
    
    private long mNetworkSpeed;

    private long mPreviousTime;

    private long mTotalTime;

    private Throwable mError = null;
    //private Throwable mIpError = null;

    private boolean mInterrupt = false;

	private long downloadId = -1;

	private boolean mCheckLink;

    private final class ProgressReportingRandomAccessFile extends RandomAccessFile {
        private int progress = 0;

        public ProgressReportingRandomAccessFile(File file, String mode)
                throws FileNotFoundException {
            super(file, mode);
        }

        @Override
        public void write(byte[] buffer, int offset, int count) throws IOException {
            super.write(buffer, offset, count);
            progress += count;
            publishProgress(progress);
        }
    }

    /*public DownloadTask(Context context, long id, String url, String name, String path, boolean checkIp) 
    		throws MalformedURLException {
        this(context, id, url, name, path, null, checkIp);
    }

    public DownloadTask(Context context, long id, String url, String path, DownloadTaskListener listener, boolean checkIp)
            throws MalformedURLException {
        this(context, id, url, null, path, listener, checkIp);
    }*/

    public DownloadTask(Context context, long id, String url, String name, String path, DownloadTaskListener listener, boolean checkLink) 
    		throws MalformedURLException {
    	
        super();
        this.mUrlString = url;
        this.mListener = listener;
        this.mURL = new URL(url);
        this.mCheckLink = checkLink;
        
        if (TextUtils.isEmpty(name))
            name = new File(mURL.getFile()).getName();
        
        this.mFile = new File(path, name);
        this.mTempFile = new File(path, name + TEMP_SUFFIX);
        
        this.mContext = context;
        this.downloadId = id;
    }

    /*public String getUrl() {
        return mUrlString;
    }

    public boolean isInterrupt() {
        return mInterrupt;
    }

    public int getDownloadPercent() {
        return mDownloadPercent;
    }*/
    
    public long getDownloadId() {
    	return this.downloadId;
    }

    /*public Map<Long, Integer> getDownloadPercentMap() {
		return Maps.mDownloadPercentMap;
	}*/

	public long getDownloadSize() {
        return mDownloadSize + mPreviousFileSize;
    }
	
	/*public Map<Long, Long> getDownloadSizeMap() {
		return Maps.mDownloadSizeMap;
	}

    public long getTotalSize() {
        return mTotalSize;
    }

    public Map<Long, Long> getTotalSizeMap() {
    	return Maps.mTotalSizeMap;
	}

	public long getDownloadSpeed() {
        return this.mNetworkSpeed;
    }

    public long getTotalTime() {
        return this.mTotalTime;
    }

    public DownloadTaskListener getListener() {
        return this.mListener;
    }*/
    
    public String getDownloadedFileName() {
    	return this.mFile.getName();
    }

    @Override
    protected void onPreExecute() {
        mPreviousTime = System.currentTimeMillis();
        if (mListener != null)
            mListener.preDownload(this);
    }

    @Override
    protected Long doInBackground(Void... params) {
    	//mThreadId = Thread.currentThread().getId();
    	//Log.v(TAG, "doInBackground thread ID: " + mThreadId);
    	
        long result = -1;
        try {
            result = download();
        } catch (NetworkErrorException e) {
            mError = e;
        } catch (FileAlreadyExistException e) {
            mError = e;
        } catch (NoMemoryException e) {
            mError = e;
        } catch (IOException e) {
            mError = e;
        } catch (SpecifiedUrlIsNotFoundException e) {
            mError = e;
        } catch (OtherHttpErrorException e) {
            mError = e;
        } catch (InvalidYoutubeLinkException e) {
        	//mIpError = e;
        	mError = e;
		} finally {
            if (mConnection != null) {
                mConnection.disconnect();
                mConnection = null;
            }
        }
        
        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {

        if (progress.length > 1) {
            mTotalSize = progress[1];
            Maps.mTotalSizeMap.put(downloadId, mTotalSize);
            if (mTotalSize == -1) {
                if (mListener != null) {
                    mListener.errorDownload(this, mError);
                    return;
                }
            }
        }
        mTotalTime = System.currentTimeMillis() - mPreviousTime;
        
        mDownloadSize = progress[0];
        Maps.mDownloadSizeMap.put(downloadId, mDownloadSize + mPreviousFileSize);
        
        if (mTotalSize == 0) {
            mDownloadPercent = -1;
        } else {
            mDownloadPercent = (int) ((mDownloadSize + mPreviousFileSize) * 100 / mTotalSize);
        }
        Maps.mDownloadPercentMap.put(downloadId, mDownloadPercent);
        
        mNetworkSpeed = mDownloadSize / mTotalTime;
        Maps.mNetworkSpeedMap.put(downloadId, mNetworkSpeed);
        if (mListener != null)
            mListener.updateProcess(this);
    }

    @Override
    protected void onPostExecute(Long result) {
        if (result == -1 || mInterrupt || mError != null/* || mIpError != null*/) {
            if (DEBUG && mError != null) {
                Log.w(TAG, "Download failed. " + mError.getMessage());
                /*if (mListener != null) {
                    mListener.errorDownload(this, mError);
                }*/
            }
            if (mListener != null) {
                mListener.errorDownload(this, mError);
            }
            /*if (DEBUG && mIpError != null) {
                Log.w(TAG, "Download resumed. " + mIpError.getMessage());
                
                if (mListener != null) {
                    mListener.resumeFromDifferentIp(this, mIpError);
                }
            }*/
            return;
        }
        // finish download
        mTempFile.renameTo(mFile);
        if (mListener != null)
            mListener.finishDownload(this);
    }

    @Override
    public void onCancelled() {
        super.onCancelled();
        mInterrupt = true;
    }

    private long download() throws NetworkErrorException, IOException,
            SpecifiedUrlIsNotFoundException, FileAlreadyExistException, 
            NoMemoryException, OtherHttpErrorException, InvalidYoutubeLinkException {

        if (DEBUG) {
            Log.v(TAG, "totalSize: " + mTotalSize);
        }

        /*
         * check network
         */
        if (!NetworkUtils.isNetworkAvailable(mContext)) {
            throw new NetworkErrorException("Network blocked.");
        }
        
        /*
         * check expire time
         */
        if (mCheckLink) {
        	long exp = NetworkUtils.findLinkExpireTime(mUrlString);
        	Log.i(TAG, "link expires at: " + exp);
        	long ct = System.currentTimeMillis() / 1000;
        	Log.i(TAG, "current time is: " + ct);
        	
        	if (ct > exp - 120 /* 2 min as buffer */) {
        		throw new InvalidYoutubeLinkException("Youtube link expired.");
        	}
        }
        
        /*
         * check ip
         */
        if (mCheckLink) {
	        String linkIp = NetworkUtils.findLinkIp(mUrlString);
	        Log.i(TAG, "initial request IP: " + linkIp);
	        String actualExtIp = NetworkUtils.getExternalIpAddress();
	        Log.i(TAG, "current request IP: " + actualExtIp);
	        
			if (!linkIp.equals(actualExtIp) || actualExtIp == null) {
				throw new InvalidYoutubeLinkException("IP is different from initial request.");
			}
        }
        
		/*
         * check file length
         */
        String userAgent = NetworkUtils.getUserAgent(mContext);
        mConnection = (HttpURLConnection) mURL.openConnection();
        mConnection.setRequestMethod("GET");
        mConnection.setRequestProperty("User-Agent", userAgent);
        mConnection.setRequestProperty("Accept-Encoding", "identity");
        if (mTempFile.exists()) {
            mPreviousFileSize = mTempFile.length();
            mConnection.setRequestProperty("Range", "bytes=" + mPreviousFileSize + "-");
        }
        mConnection.connect();

        int responseCode = mConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
            throw new SpecifiedUrlIsNotFoundException("Not found: " + mUrlString);
        } else if (responseCode != HttpURLConnection.HTTP_OK
                && responseCode != HttpURLConnection.HTTP_PARTIAL) {
            String responseCodeString = Integer.toString(responseCode);
            throw new OtherHttpErrorException("http error code: " + responseCodeString, responseCodeString);
        }

        boolean isRangeDownload = false;
        int length = mConnection.getContentLength();
        if (responseCode == HttpURLConnection.HTTP_PARTIAL) {
            length += mPreviousFileSize;
            isRangeDownload = true;
        }

        if (mFile.exists() && length == mFile.length()) {
            if (DEBUG) {
                Log.w(TAG, "Output file already exists. Skipping download.");
            }
            throw new FileAlreadyExistException("Output file already exists. Skipping download.");
        }

        /*
         * check memory
         */
        long storage = StorageUtils.getAvailableStorage();
        if (DEBUG) {
            Log.v(TAG, "storage:" + storage + " totalSize:" + length);
        }

        if (length - mPreviousFileSize > storage) {
            throw new NoMemoryException("SD card no memory.");
        }

        RandomAccessFile outputStream = new ProgressReportingRandomAccessFile(mTempFile, "rw");

        InputStream inputStream = mConnection.getInputStream();

        publishProgress(0, length);
        
        int bytesCopied = copy(inputStream, outputStream, isRangeDownload);

        if ((mPreviousFileSize + bytesCopied) != mTotalSize && mTotalSize != -1 && !mInterrupt) {
            throw new IOException("Download incomplete: " + bytesCopied + " != " + mTotalSize);
        }

        if (DEBUG) {
            Log.d(TAG, "Download completed successfully.");
        }

        return bytesCopied;
    }

    private int copy(InputStream input, RandomAccessFile output, boolean isRangeDownload) 
    		throws IOException, NetworkErrorException {

        if (input == null || output == null) {
            return -1;
        }

        byte[] buffer = new byte[BUFFER_SIZE];

        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
        if (DEBUG) {
            Log.v(TAG, "length " + output.length());
        }

        int count = 0, n = 0;
        long errorBlockTimePreviousTime = -1, expireTime = 0;

        try {
        	
            if (isRangeDownload) {
                output.seek(output.length());
            }

            while (!mInterrupt) {
                n = in.read(buffer, 0, BUFFER_SIZE);
                if (n == -1) {
                    break;
                }
                output.write(buffer, 0, n);
                count += n;

                /*
                 * check network
                 */
                if (!NetworkUtils.isNetworkAvailable(mContext)) {
                    throw new NetworkErrorException("Network blocked.");
                }

                if (mNetworkSpeed == 0) {
                    if (errorBlockTimePreviousTime > 0) {
                        expireTime = System.currentTimeMillis() - errorBlockTimePreviousTime;
                        if (expireTime > TIME_OUT) {
                            throw new ConnectTimeoutException("connection time out.");
                        }
                    } else {
                        errorBlockTimePreviousTime = System.currentTimeMillis();
                    }
                } else {
                    expireTime = 0;
                    errorBlockTimePreviousTime = -1;
                }
            }
        } finally {
            mConnection.disconnect();
            mConnection = null;
            output.close();
            in.close();
            input.close();
        }
        return count;
    }

    public void cancel() {
    	this.cancel(true);
        mInterrupt = true;
        Log.d(TAG, "cancel on id " + downloadId);
    }
}