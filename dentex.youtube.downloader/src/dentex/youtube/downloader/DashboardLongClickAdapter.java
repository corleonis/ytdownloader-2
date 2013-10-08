package dentex.youtube.downloader;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/* class DashboardLongClickAdapter adapted from Stack Overflow:
 * 
 * http://stackoverflow.com/questions/11300381/disabled-listitems-in-alertdialog-does-not-show-up-as-grayed-out-items
 * 
 * Q: http://stackoverflow.com/users/1497029/mrinal
 * A: none, as of 2013-07-20
 */

public class DashboardLongClickAdapter extends ArrayAdapter<CharSequence> {

    static int[] disabledOptions;

    private DashboardLongClickAdapter(Context context, int textViewResId, CharSequence[] strings, int[] disabledOptions) {
        super(context, textViewResId, strings);
        DashboardLongClickAdapter.disabledOptions = disabledOptions;
    }

    public static DashboardLongClickAdapter createFromResource(
    		Context context, 
    		int textArrayResId, 
    		int textViewResId,
            int[] disabledOptions) {

        CharSequence[] strings = context.getResources().getTextArray(textArrayResId);

        return new DashboardLongClickAdapter(context, textViewResId, strings, disabledOptions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        view.setEnabled(isEnabled(position));
        return view;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
    	if (disabledOptions != null) {
	    	for (int i = 0; i < disabledOptions.length; i++) {
	    		if (position == disabledOptions[i]) return false;
	    	}
    	}
        return true;
    }
}
