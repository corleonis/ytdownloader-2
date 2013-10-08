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

package dentex.youtube.downloader;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

public class ShareListAdapter extends ArrayAdapter<String> {

	private List<String> items;
	
	public ShareListAdapter(List<String> items, Context ctx) {
		super(ctx, android.R.layout.simple_list_item_1, items);
		this.items = items;
	}
	
	public int getCount() {
		return items.size();
	}

	/*public long getItemId(int position) {
		return items.get(position).hashCode();
	}*/
}

