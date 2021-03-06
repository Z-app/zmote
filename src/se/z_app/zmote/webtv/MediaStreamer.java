package se.z_app.zmote.webtv;

import java.io.File;
import java.io.IOException;

import android.os.Environment;

import se.z_app.httpserver.ZmoteHTTPD;
import se.z_app.stb.MediaItem;

/**
 * Class that handles streaming media from the phone to the STB
 * 
 * @author Rasmus Holm
 */
public class MediaStreamer {
	private int port = 8080;
	private ZmoteHTTPD httpd;
	private File root;
	private String ip;
	
	private static class SingletonHolder { 
        public static final MediaStreamer INSTANCE = new MediaStreamer();
	}
	
	/**
	 * Get the instance of this class
	 * @return the instance
	 */
	public static MediaStreamer instance(){
		return SingletonHolder.INSTANCE;
	}
	
	/**
	 * Private constructor, since it's a singleton
	 */
	public void setLocalIP(String ip){
		this.ip = ip;
	}
	
	private MediaStreamer(){
		root = Environment.getExternalStorageDirectory();
		try {
			httpd = new ZmoteHTTPD(port, root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds a MediaRequestHandler with the specified file to the HTTP server in use.
	 * @param file the file to add to the server
	 * @return the MediaItem from the file
	 */
	public MediaItem addFile(File file){
		String filename = file.getName();
		String extention = "";
		int index = filename.lastIndexOf(".");
		
		if (index > 0) {
			extention = filename.substring(index);
		}
		String uri = "/"+System.currentTimeMillis()+extention;
		MediaRequestHandler handler = new MediaRequestHandler(file, uri);
		
		httpd.addHandler(handler);
		
		MediaItem item = new MediaItem();
		item.setName(file.getName());
		item.setUrl("http://" + ip + ":" + port + uri );
		System.out.println("http://" + ip + ":" + port + uri);
		return item;	
	}
	
}
