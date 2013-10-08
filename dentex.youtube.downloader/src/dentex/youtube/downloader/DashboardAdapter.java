package dentex.youtube.downloader;

/** 
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
	
	------------------------------------------------------------------
	
 	* DashboardAdapter - reworked class using as starting point:
 	* https://github.com/survivingwithandroid/Surviving-with-android/tree/master/SimpleList
 	* 
 	* Copyright (C) 2012 jfrankie (http://www.survivingwithandroid.com)
 	* Copyright (C) 2012 Surviving with Android (http://www.survivingwithandroid.com)
 	*
 	* Licensed under the Apache License, Version 2.0 (the "License");
 	* you may not use this file except in compliance with the License.
 	* You may obtain a copy of the License at
 	*
 	*      http://www.apache.org/licenses/LICENSE-2.0
 	*
 	* Unless required by applicable law or agreed to in writing, software
 	* distributed under the License is distributed on an "AS IS" BASIS,
 	* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 	* See the License for the specific language governing permissions and
 	* limitations under the License.
 **/

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DashboardAdapter extends ArrayAdapter<DashboardListItem> implements Filterable {

	private final String BLUE = "#3674F2";
	private final String RED = "#E50300";
	private final String GREEN = "#00AD21";
	private final String YELLOW = "#F5D900";
	private final String ORANGE = "#F57600";
	private List<DashboardListItem> itemsList;
	private Context context;
	private Filter itemsFilter;
	private List<DashboardListItem> origItemsList;
	public ArrayList<DashboardListItem> filteredList;
	
	public DashboardAdapter(List<DashboardListItem> itemsList, Context ctx) {
		super(ctx, R.layout.activity_dashboard_list_item, itemsList);
		this.itemsList = itemsList;
		this.context = ctx;
		this.origItemsList = new ArrayList<DashboardListItem>(itemsList);
	}
	
	public int getCount() {
		return itemsList.size();
	}

	public DashboardListItem getItem(int position) {
		return itemsList.get(position);
	}

	/*public long getItemId(int position) {
		return itemsList.get(position).hashCode();
	}*/

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		ItemHolder holder = new ItemHolder();
		
		// First let's verify the convertView is not null
		if (convertView == null) {
			// This a new view we inflate the new layout
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.activity_dashboard_list_item, null);
			// Now we can fill the layout with the right values
			TextView tv1D = (TextView) v.findViewById(R.id.filename_D);
			TextView tv1L = (TextView) v.findViewById(R.id.filename_L);
			String theme = YTD.settings.getString("choose_theme", "D");
	    	if (theme.equals("D")) {
	    		tv1L.setVisibility(View.GONE);
	    		holder.filename = tv1D;
	    	} else {
	    		tv1D.setVisibility(View.GONE);
	    		holder.filename = tv1L;
	    	}
			TextView tv2 = (TextView) v.findViewById(R.id.size);
			TextView tv3 = (TextView) v.findViewById(R.id.path);
			TextView tv4 = (TextView) v.findViewById(R.id.status);
			TextView tv5 = (TextView) v.findViewById(R.id.speed);
			
			ProgressBar pb = (ProgressBar) v.findViewById(R.id.pb);
			
			ImageView th = (ImageView) v.findViewById(R.id.thumb);
			//ImageView ov = (ImageView) v.findViewById(R.id.overlay);

			holder.size = tv2;
			holder.path = tv3;
			holder.status = tv4;
			holder.speed = tv5;
			
			holder.pb = pb;
			
			holder.thumb = th;
			//holder.overlay = ov;
			
			v.setTag(holder);
		} else {
			holder = (ItemHolder) v.getTag();
		}
		
		DashboardListItem dli = itemsList.get(position);
		
		holder.filename.setText(dli.getFilename());
		
		int dr;
		if (dli.getType().equals("VIDEO")) {
			dr = R.drawable.ic_video;
		} else {
			dr = R.drawable.ic_audio;
		}
		
		// ------------------------------------------------------------------------
		Drawable compound = context.getResources().getDrawable(dr);
		compound.setBounds( 0, 0, 40, 40);
		holder.status.setCompoundDrawables(null, null, compound, null);
		//                                           ^
		//                                           |
		//                                   |---------------|
		// entry type icon: thumb overlay OR compound drawable to "status" TextView
		//                  |-----------|
		//                        |
		//                        v
		//holder.overlay.setImageDrawable(context.getResources().getDrawable(dr));
		// ------------------------------------------------------------------------
		
		holder.size.setText(dli.getSize());
		holder.path.setText(dli.getPath());
		
		if (dli.getSpeed() != 0) {
			holder.speed.setText(String.valueOf(dli.getSpeed()) + " KB/s");
		} else {
			if (dli.getStatus().equals(context.getString(R.string.json_status_in_progress))) dli.setProgress(-1);
			holder.speed.setText("");
		}
		holder.status.setText(dli.getStatus());
		if (dli.getStatus().equals(context.getString(R.string.json_status_completed)))
			holder.status.setTextColor(Color.parseColor(GREEN));
		else if (dli.getStatus().equals(context.getString(R.string.json_status_failed)))
			holder.status.setTextColor(Color.parseColor(RED));
		else if (dli.getStatus().equals(context.getString(R.string.json_status_in_progress)))
			holder.status.setTextColor(Color.parseColor(BLUE));
		else if (dli.getStatus().equals(context.getString(R.string.json_status_imported)))
			holder.status.setTextColor(Color.parseColor(YELLOW));
		else if (dli.getStatus().equals(context.getString(R.string.json_status_paused)))
			holder.status.setTextColor(Color.parseColor(ORANGE));
		
		if (dli.getProgress() == 100) {
			holder.pb.setVisibility(View.GONE);
		} else if (dli.getProgress() == -1) {
			holder.pb.setVisibility(View.VISIBLE);
			holder.pb.setIndeterminate(true);
		} else {
			holder.pb.setVisibility(View.VISIBLE);
			holder.pb.setIndeterminate(false);
			holder.pb.setProgress(dli.getProgress());
		}
		
		int height = 180;
		if (DashboardActivity.isLandscape) height = 320;
		
		int phRes = DashboardActivity.isLandscape ? R.drawable.placeholder_320x180 : R.drawable.placeholder_180x180;
		
		if (YTD.reduceFactor == 1.44) {
			phRes = DashboardActivity.isLandscape ? R.drawable.placeholder_222x125 : R.drawable.placeholder_125x125;
		} else if (YTD.reduceFactor == 2) {
			phRes = DashboardActivity.isLandscape ? R.drawable.placeholder_160x90 : R.drawable.placeholder_90x90;
		} else if (YTD.reduceFactor == 3) {
			phRes = DashboardActivity.isLandscape ? R.drawable.placeholder_107x60 : R.drawable.placeholder_60x60;
		}
		Drawable ph = context.getResources().getDrawable(phRes);
		
		File thumb = new File(getContext().getDir(YTD.THUMBS_FOLDER, 0), dli.getYtId() + ".png");
		
		//Picasso.with(getContext()).setDebugging(true);
		Picasso.with(getContext())
				.load(thumb)
				.placeholder(ph)
				.error(ph)
				.resize((int) (height/YTD.reduceFactor), (int) (180/YTD.reduceFactor))
				.centerCrop()
				.into(holder.thumb);

		return v;
	}
	
	/* *********************************
	 * We use the holder pattern        
	 * It makes the view faster and avoid finding the component
	 * **********************************/
	
	private static class ItemHolder {
		public TextView filename;
		public TextView size;
		public TextView path;
		public TextView status;
		public ProgressBar pb;
		public ImageView thumb;
		public TextView speed;
		//public ImageView overlay;
	}

	public class ItemsFilter extends Filter {
	    @SuppressLint("DefaultLocale")
		@Override
	    public FilterResults performFiltering(CharSequence constraint) {
	    	FilterResults results = new FilterResults();
	        if (TextUtils.isEmpty(constraint)) {
	            results.values = origItemsList;
	            results.count = origItemsList.size();
	        } else {
	            filteredList = new ArrayList<DashboardListItem>();
	             
	            for (DashboardListItem p : itemsList) {
	                if (p.getFilename().toUpperCase().startsWith(constraint.toString().toUpperCase()))
	                    filteredList.add(p);
	            }
	             
	            results.values = filteredList;
	            results.count = filteredList.size();
	        }
	        return results;
	    }
	 
	    @SuppressWarnings("unchecked")
		@Override
	    public void publishResults(CharSequence constraint,FilterResults results) {
	    	itemsList = (List<DashboardListItem>) results.values;
	        notifyDataSetChanged();
	    }
	}
	
	@Override
	public Filter getFilter() {
	    if (itemsFilter == null)
	        itemsFilter = new ItemsFilter();
	     
	    return itemsFilter;
	}
	
	public void resetData() {
		itemsList = origItemsList;
	}	
}

