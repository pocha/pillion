package com.getpillion.smoothie;

/*
import org.lucasr.smoothie.SimpleItemLoader;

import com.getpillion.FeedBO;
import com.getpillion.RouteAdapter.FeedViewHolder;


import uk.co.senab.bitmapcache.BitmapLruCache;
import uk.co.senab.bitmapcache.CacheableBitmapDrawable;

import android.view.View;
import android.widget.Adapter;

public class ImageLoader extends SimpleItemLoader<String, CacheableBitmapDrawable> {
    final BitmapLruCache mCache;

    public ImageLoader(BitmapLruCache cache) {
        mCache = cache;
    }

    @Override
    public CacheableBitmapDrawable loadItemFromMemory(String url) {
    	try {
    		return mCache.getFromMemoryCache(url);
    	} catch (Exception ex) {
    		return null;
    	}
    }

    @Override
    public String getItemParams(Adapter adapter, int position) {
    	String selectedVal = (String) adapter.getItem(position);
    	FeedBO feed = new FeedBO();
		feed.populateFromString(selectedVal);
        return feed.getAppIcon();
    }

    @Override
    public CacheableBitmapDrawable loadItem(String url) {
    	try {
	        CacheableBitmapDrawable wrapper = mCache.get(url);
	        if (wrapper == null) {
	            wrapper = mCache.put(url, HttpHelper.loadImage(url));
	        }
	
	        return wrapper;
    	} catch (Exception ex) {
    		return null;
    	}
    }

    @Override
    public void displayItem(View itemView, CacheableBitmapDrawable result, boolean fromMemory) {
    	
    	try {
    		FeedViewHolder holder = (FeedViewHolder) itemView.getTag();
    		//result.setTileModeXY(TileMode.CLAMP, TileMode.CLAMP);
        
	        if (fromMemory) {
	            holder.appIcon.setImageDrawable(result);
	        } else {

	            holder.appIcon.setImageDrawable(result);
	        }
    	} catch (Exception ex) { }
		
    }
}
*/