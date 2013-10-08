package dentex.youtube.downloader.utils;

import java.util.Arrays;

import android.text.TextUtils;

public class ExpSigDecipherers {
	
	// --------------------- helpers ---------------------
    
	public static String[] clone(String[] sig, int pos) {
    	return Arrays.copyOfRange(sig, pos, sig.length);
    }
	
	public static String[] swap(String[] arr, int index) {
    	String c = arr[0];
    	arr[0] = arr[index % arr.length];
    	arr[index] = c;
    	return arr;
    }
	
	/*
     * method reverseArray(String[] a) adapted from Stack Overflow:
	 * http://stackoverflow.com/questions/13674466/reverse-the-contents-of-array
	 * 
	 * Q: http://stackoverflow.com/users/1871089/user1871089
	 * A: http://stackoverflow.com/users/1870638/andreih
	 */
	
    public static String[] reverse(String[] a) {
    	int i = 0;
    	int  j = a.length - 1;
    	for (i = 0; i < a.length / 2; i++, j--) {
    		String temp = a[i];
    		a[i] = a[j];
    		a[j] = temp;
    	}
    	return a;
    }
    
    // --------------------- deciphers examples ---------------------
    
	public static String decipher1(String sig) {
		
	    /*class com.google.youtube.util.SignatureDecipher
	    {
	        function SignatureDecipher () {
	        }
	        static function decipher(str) {
	            var _local3 = str.split("");
	            _local3 = reverse_15888(_local3);
	            _local3 = clone_15888(_local3, 2);
	            _local3 = reverse_15888(_local3);
	            return(_local3.join(""));
	        }
	        static function clone_15888(arr, len) {
	            return(arr.slice(len));
	        }
	        static function reverse_15888(arr) {
	            arr.reverse();
	            return(arr);
	        }
	    }*/
    	
    	String[] sigS = sig.split("");
    	sigS = reverse(sigS);
    	sigS = clone(sigS, 2);
    	sigS = reverse(sigS);
    	
    	return TextUtils.join("", sigS);
    }
    
    public static String decipher2(String sig) {
    
		/*class com.google.youtube.util.SignatureDecipher
		{
		    function SignatureDecipher () {
		    }
		        static function decipher(str) {
		        var _local3 = str.split("");
		        _local3 = swap_15897(_local3, 24);
		        _local3 = swap_15897(_local3, 53);
		        _local3 = clone_15897(_local3, 2);
		        _local3 = swap_15897(_local3, 31);
		        _local3 = swap_15897(_local3, 4);
		        return(_local3.join(""));
		    }
		    static function clone_15897(arr, len) {
		        return(arr.slice(len));
		    }
		    static function swap_15897(arr, index) {
		        var _local4 = arr[0];
		        var _local5 = arr[index % arr.length];
		        arr[0] = _local5;
		        arr[index] = _local4;
		        return(arr);
		    }
		}*/
    
    	String[] sigS = sig.split("");
    	sigS = swap(sigS, 24);
    	sigS = swap(sigS, 53);
    	sigS = clone(sigS, 2);
    	sigS = swap(sigS, 31);
    	sigS = swap(sigS, 4);
    	
    	return TextUtils.join("", sigS);
    }
}
