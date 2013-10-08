
package com.matsuhiro.android.download;

public class InvalidYoutubeLinkException extends DownloadException {
	
	private static final long serialVersionUID = 1L;

    public InvalidYoutubeLinkException(String message) {

        super(message);
    }
}
