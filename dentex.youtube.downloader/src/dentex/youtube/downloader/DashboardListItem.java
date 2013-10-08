package dentex.youtube.downloader;
/*
 * code adapted from: 
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
 */

public class DashboardListItem {
	
	private String id;
	private String type;
	private String ytId;
	private int pos;
	private String status;
	private String path;
	private String filename;
	private String basename;
	private String audioExt;
	private String size;
	private int progress;
	private long speed;

	public DashboardListItem(String id, String type, String ytId, int pos, String status, String path, String filename, String basename, String audioExt, String size, int progress, long speed) {
		this.id = id;
		this.type = type;
		this.ytId = ytId;
		this.pos = pos;
		this.status = status;
		this.path = path;
		this.filename = filename;
		this.basename =  basename;
		this.audioExt = audioExt;
		this.size = size;
		this.progress = progress;
		this.speed = speed;
	}
	public String getId() {
		return id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getYtId() {
		return ytId;
	}
	public void setYtId(String ytId) {
		this.ytId = ytId;
	}
	public int getPos() {
		return pos;
	}
	public void setPos(int pos) {
		this.pos = pos;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String name) {
		this.path = name;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getBasename() {
		return basename;
	}
	public String getAudioExt() {
		return audioExt;
	}
	public String getSize() {
		return size;
	}
	public int getProgress() {
		return progress;
	}
	public void setProgress(int progress) {
		this.progress = progress;
	}
	public long getSpeed() {
		return speed;
	}
	public void setSpeed(long speed) {
		this.speed = speed;
	}
}

