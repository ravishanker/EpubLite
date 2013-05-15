package com.fovea.epublite;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONObject;

import android.net.Uri;

public class BookContents {
	JSONObject raw = null;
	JSONArray chapters;
	File updateDir = null;
	
	BookContents(JSONObject raw) {
		this.raw = raw;
		chapters = raw.optJSONArray("chapters");
	}
	
	BookContents(JSONObject raw, File updateDir) {
		this.raw = raw;
		this.updateDir = updateDir;
		chapters = raw.optJSONArray("chapters");
				
	}
	
	int getChapterCount() {
		return(chapters.length());
	}
	
	String getChapterFile(int position) {
		JSONObject chapter = chapters.optJSONObject(position);
		
		if (updateDir != null) {
			return(Uri.fromFile(
					new File(updateDir, chapter.optString("file"))).toString());					
		}
		return("file:///android_asset/book/" + chapter.optString("file"));
	}
	
	String getTitle() {
		return(raw.optString("title"));
	}
}
