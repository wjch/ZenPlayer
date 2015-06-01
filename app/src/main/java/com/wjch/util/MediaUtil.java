package com.wjch.util;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.wjch.mp.R;
import com.wjch.vo.Song;

public class MediaUtil {
	
	private static final Uri sArtworkUri = Uri
			.parse("content://media/external/audio/albumart");
	
	private static final BitmapFactory.Options sBitmapOptions= new BitmapFactory.Options();
	private static ImageLoaderConfiguration config;
	
	public MediaUtil(Context context) {
		
		File cacheDir = StorageUtils.getCacheDirectory(context);

		config = new ImageLoaderConfiguration.Builder(context)
	    .threadPriority(Thread.NORM_PRIORITY - 2) // default
	    .tasksProcessingOrder(QueueProcessingType.FIFO) // default
	    .denyCacheImageMultipleSizesInMemory()
	    .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
	    .memoryCacheSize(2 * 1024 * 1024)
	    .memoryCacheSizePercentage(13) // default
	    .diskCache(new UnlimitedDiscCache(cacheDir)) // default
	    .diskCacheSize(50 * 1024 * 1024)
	    .diskCacheFileCount(100)
	    .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
	    .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
	    .writeDebugLogs().build();
		ImageLoader.getInstance().init(config);
		
	}

	
	
	private static List<Integer> idsArray = new ArrayList<Integer>();

	public static List<Song> getSonglist(Context context) {
		Boolean isSDPresent = android.os.Environment.getExternalStorageState()
				.equals(android.os.Environment.MEDIA_MOUNTED);
		
		if (!isSDPresent) {
			return null;
		}
		List<Song> songlist = new ArrayList<Song>();
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		while (cursor.moveToNext()) {
			int id = Integer.parseInt(cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.AudioColumns._ID)));
			String song_title = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
			String artist = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
			String album = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.ALBUM));
			String url = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.DATA));
			String duration = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
			long albumId = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
			Song song = null;
			song = new Song(id, song_title, artist, albumId, album, url,
					duration);
			songlist.add(song);
			idsArray.add(id);

		}
		if (cursor != null && !cursor.isClosed())
			cursor.close();
		return songlist;

	}

	public static void removeSong(Context context, int position) {
		context.getContentResolver()
				.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
						MediaStore.Audio.Media._ID + "="
								+ idsArray.get(position), null);
	}

	// format playing or progress time
	// but won't use this.^_^
	public static String formatTime(long time) {
		String min = time / (1000 * 60) + "";
		String sec = time % (1000 * 60) + "";
		if (min.length() < 2) {
			min = "0" + time / (1000 * 60) + "";
		} else {
			min = time / (1000 * 60) + "";
		}
		if (sec.length() == 4) {
			sec = "0" + (time % (1000 * 60)) + "";
		} else if (sec.length() == 3) {
			sec = "00" + (time % (1000 * 60)) + "";
		} else if (sec.length() == 2) {
			sec = "000" + (time % (1000 * 60)) + "";
		} else if (sec.length() == 1) {
			sec = "0000" + (time % (1000 * 60)) + "";
		}
		return min + ":" + sec.trim().substring(0, 2);
	}

	public Bitmap getArtwork(Context context, boolean allowdefault,
			Song song) {
		long album_id = song.getAlbumId();
		if (album_id < 0) {
			// get the album art directly from the file.
			if (song.getId() >= 0) {
				Bitmap bm = getArtworkFromFile(context, song);
				if (bm != null) {
					return bm;
				} else {
					String pathString = GetPicUtil.parseUrl(song.getSongname());
					// Bitmap bmp = GetPicUtil.getImageByUrl(pathString);
					ImageSize targetSize = new com.nostra13.universalimageloader.core.assist.ImageSize(
							80, 50); // result Bitmap will be fit to this size
					Bitmap bmp = ImageLoader.getInstance().loadImageSync(pathString);
					return bmp;
				}
			}
			if (allowdefault) {
				return getDefaultArtwork(context);
			}
			return null;
		}
		ContentResolver res = context.getContentResolver();
		Uri uri = ContentUris.withAppendedId(sArtworkUri, song.getAlbumId());
		if (uri != null) {
			InputStream in = null;
			try {
				in = res.openInputStream(uri);
				return BitmapFactory.decodeStream(in, null, sBitmapOptions);
			} catch (FileNotFoundException ex) {
				// The album art thumbnail does not actually exist. Maybe the
				// user deleted it, or
				// maybe it never existed to begin with.
				Bitmap bm = getArtworkFromFile(context, song);
				if (bm != null) {
					if (bm.getConfig() == null) {
						bm = bm.copy(Bitmap.Config.RGB_565, false);
						if (bm == null && allowdefault) {
							return getDefaultArtwork(context);
						}
					}
				} else {
					// if (allowdefault) {
					// bm = getDefaultArtwork(context);
					// }
					String pathString = GetPicUtil.parseUrl(song.getSongname());
					bm = GetPicUtil.getImageByUrl(pathString);
					return bm;
				}
				return bm;
			} finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (IOException ex) {
				}
			}
		}

		return null;
	}

	private static Bitmap getArtworkFromFile(Context context, Song song) {
		Bitmap bm = null;
		byte[] art = null;
		String path = null;
		long albumid = song.getAlbumId();
		int songid = song.getId();
		if (albumid < 0 && songid < 0) {
			throw new IllegalArgumentException(
					"Must specify an album or a song id");
		}
		try {
			if (albumid < 0) {

				// the standard uri
				// :content://media/external/audio/media/3/albumart
				Uri uri = Uri.parse("content://media/external/audio/media/"
						+ songid + "/albumart");
				/*
				 * ParcelFileDescriptor pfd = context.getContentResolver()
				 * .openFileDescriptor(uri, "r"); if (pfd != null) {
				 * FileDescriptor fd = pfd.getFileDescriptor(); bm =
				 * BitmapFactory.decodeFileDescriptor(fd); }
				 */
				bm = ImageLoader.getInstance()
						.loadImageSync("content://media/external/audio/media/"
								+ songid + "/albumart");
			} else {
				Uri uri = ContentUris.withAppendedId(sArtworkUri, albumid);
				/*
				 * ParcelFileDescriptor pfd = context.getContentResolver()
				 * .openFileDescriptor(uri, "r"); if (pfd != null) {
				 * FileDescriptor fd = pfd.getFileDescriptor(); bm =
				 * BitmapFactory.decodeFileDescriptor(fd); }
				 */
				// Load image, decode it to Bitmap and return Bitmap synchronously
				ImageSize targetSize = new ImageSize(80, 50); // result Bitmap will be fit to this size
				bm = ImageLoader.getInstance().loadImageSync("content://media/external/audio/albumart/"+albumid);
			}
		} finally {
			
		}
		return bm;
	}

	private static Bitmap getDefaultArtwork(Context context) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		return BitmapFactory.decodeStream(context.getResources()
				.openRawResource(R.drawable.ic_action_play), null, opts);
	}

}
