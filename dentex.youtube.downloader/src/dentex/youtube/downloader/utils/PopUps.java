/***
 	Copyright (c) 2012-2013 Samuele Rini
 	
	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with this program. If not, see http://www.gnu.org/licenses
	
	***
	
	https://github.com/dentex/ytdownloader/
    https://sourceforge.net/projects/ytdownloader/
	
	***
	
	Different Licenses and Credits where noted in code comments.
*/

package dentex.youtube.downloader.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;
import dentex.youtube.downloader.R;

public class PopUps {
	
	static int icon;

	public static void showPopUp(String title, String message, String type, Context context) {
        
	    AlertDialog.Builder helpBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.BoxTheme));
	    helpBuilder.setTitle(title);
	    helpBuilder.setMessage(message);
	
	    if ( type == "alert" ) {
	        icon = android.R.drawable.ic_dialog_alert;
	    } else if ( type == "info" ) {
	        icon = android.R.drawable.ic_dialog_info;
	    }
	
	    helpBuilder.setIcon(icon);
	    helpBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	
	        public void onClick(DialogInterface dialog, int which) {
	            // Do nothing but close the dialog
	        }
	    });
	
	    AlertDialog helpDialog = helpBuilder.create();
	    if (! ((Activity) context).isFinishing()) {
	    	helpDialog.show();
	    } else {
	    	Utils.logger("w", "PopUp not showed", "PopUps");
	    }
	}
}
