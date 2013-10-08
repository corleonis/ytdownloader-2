package dentex.youtube.downloader.utils;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;

public class RhinoRunner {
	
	static String DEBUG_TAG = "RhinoRunner";
	
	/*
	 * methods adapted from Stack Overflow:
	 * http://stackoverflow.com/questions/3995897/rhino-how-to-call-js-function-from-java/3996115#3996115
	 * 
	 * Q:http://stackoverflow.com/users/391441/instantsetsuna
	 * A:http://stackoverflow.com/users/72673/maurice-perry
	 */
	
	/*
     * "function decryptSignature(sig)" from the Javascript Greasemonkey script 
     * http://userscripts.org/scripts/show/25105 (released under the MIT License)
     * by Gantt: http://userscripts.org/users/gantt
     */
	
	public static String decipher(String S, String function) {
		Context rhino = Context.enter();
		rhino.setOptimizationLevel(-1);
		try {
		    ScriptableObject scope = rhino.initStandardObjects();
		    
		    /*Scriptable that = rhino.newObject(scope);
		    Function fct = rhino.compileFunction(scope, function, "script", 1, null);
		    
		    Object result = fct.call(rhino, scope, that, new Object[] {S});*/
		    
		    rhino.evaluateString(scope, function, "script", 1, null);
		    Function fct = (Function)scope.get("decryptSignature", scope);
		    
		    Object result = fct.call(rhino, scope, scope, new Object[] {S});
		    
		    return (String) Context.jsToJava(result, String.class);
		    
		} finally {
		    Context.exit();
		}
	}
	
	/*public static String decipher2(String S, String a, String function) {
		Context rhino = Context.enter();
		rhino.setOptimizationLevel(-1);
		try {
		    ScriptableObject scope = rhino.initStandardObjects();
		    
		    rhino.evaluateString(scope, function, "script", 1, null);
		    Function fct = (Function)scope.get("decryptSignature", scope);
		    
		    Object result = fct.call(rhino, scope, scope, new Object[] {S, a});
		    
		    return (String) Context.jsToJava(result, String.class);
		    
		} finally {
		    Context.exit();
		}   
	}*/
	
	public static String[] obtainDecryptionArray(String code, String function) {
		Context rhino = Context.enter();
		rhino.setOptimizationLevel(-1);
		try {
		    ScriptableObject scope = rhino.initStandardObjects();

		    rhino.evaluateString(scope, function, "script", 1, null);
		    Function fct = (Function)scope.get("findSignatureCode", scope);
		    
		    Object result = fct.call(rhino, scope, scope, new Object[] {code});
		    
		    return (String[]) Context.jsToJava(result, String[].class);
		    
		} finally {
		    Context.exit();
		}   
	}
}
