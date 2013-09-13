package webdad.apps.radiodaten;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Pair;

public  class Zuordnungen {
	public static  Map<Pair<String,String>, String> getZuordnungen(Context context) {
    	String[] wellen = context.getResources().getStringArray(R.array.ar_wellen);
    	Map<Pair<String,String>, String> map = new HashMap<Pair<String, String>, String>();
    	
    	map.put(new Pair<String, String>(wellen[0], "name"), "Bayern 1");
    	map.put(new Pair<String, String>(wellen[0], "text"), "ukw1w.html");
    	map.put(new Pair<String, String>(wellen[0], "img"), "dabb1s.html");
    	map.put(new Pair<String, String>(wellen[0], "l_stream_a"), "http://gffstream.ic.llnwd.net/stream/gffstream_w1a");
    	map.put(new Pair<String, String>(wellen[0], "l_stream_b"), "http://gffstream.ic.llnwd.net/stream/gffstream_w1b");
    	map.put(new Pair<String, String>(wellen[0], "h_stream_a"), "http://gffstream.ic.llnwd.net/stream/gffstream_w10a");
    	map.put(new Pair<String, String>(wellen[0], "h_stream_b"), "http://gffstream.ic.llnwd.net/stream/gffstream_w10b");
    	
    	map.put(new Pair<String, String>(wellen[1], "name"), "Bayern 2");
    	map.put(new Pair<String, String>(wellen[1], "text"), "ukw2w.html");
    	map.put(new Pair<String, String>(wellen[1], "img"), "dabb2s.html");
    	map.put(new Pair<String, String>(wellen[1], "l_stream_a"), "http://gffstream.ic.llnwd.net/stream/gffstream_w2a");
    	map.put(new Pair<String, String>(wellen[1], "l_stream_b"), "http://gffstream.ic.llnwd.net/stream/gffstream_w2b");
    	map.put(new Pair<String, String>(wellen[1], "h_stream_a"), "http://gffstream.ic.llnwd.net/stream/gffstream_w11a");
    	map.put(new Pair<String, String>(wellen[1], "h_stream_b"), "http://gffstream.ic.llnwd.net/stream/gffstream_w11b");
    	
    	map.put(new Pair<String, String>(wellen[2], "name"), "Bayern 3");
    	map.put(new Pair<String, String>(wellen[2], "text"), "ukw3r.html");
    	map.put(new Pair<String, String>(wellen[2], "img"), "dabb3.html");
    	map.put(new Pair<String, String>(wellen[2], "l_stream_a"), "http://gffstream.ic.llnwd.net/stream/gffstream_w3a");
    	map.put(new Pair<String, String>(wellen[2], "l_stream_b"), "http://gffstream.ic.llnwd.net/stream/gffstream_w3b");
    	map.put(new Pair<String, String>(wellen[2], "h_stream_a"), "http://gffstream.ic.llnwd.net/stream/gffstream_w12a");
    	map.put(new Pair<String, String>(wellen[2], "h_stream_b"), "http://gffstream.ic.llnwd.net/stream/gffstream_w12b");

    	map.put(new Pair<String, String>(wellen[3], "name"), "Bayern Klassik");
    	map.put(new Pair<String, String>(wellen[3], "text"), "ukw4r.html");
    	map.put(new Pair<String, String>(wellen[3], "img"), "dabb4.html");
    	map.put(new Pair<String, String>(wellen[3], "l_stream_a"), "http://gffstream.ic.llnwd.net/stream/gffstream_w4a");
    	map.put(new Pair<String, String>(wellen[3], "l_stream_b"), "http://gffstream.ic.llnwd.net/stream/gffstream_w4b");
    	map.put(new Pair<String, String>(wellen[3], "h_stream_a"), "http://gffstream.ic.llnwd.net/stream/gffstream_w13a");
    	map.put(new Pair<String, String>(wellen[3], "h_stream_b"), "http://gffstream.ic.llnwd.net/stream/gffstream_w13b");
    	
    	map.put(new Pair<String, String>(wellen[4], "name"), "Bayern 5");
    	map.put(new Pair<String, String>(wellen[4], "text"), "ukw5r.html");
    	map.put(new Pair<String, String>(wellen[4], "img"), "dabb5.html");
    	map.put(new Pair<String, String>(wellen[4], "l_stream_a"), "http://gffstream.ic.llnwd.net/stream/gffstream_w5a");
    	map.put(new Pair<String, String>(wellen[4], "l_stream_b"), "http://gffstream.ic.llnwd.net/stream/gffstream_w5b");
    	map.put(new Pair<String, String>(wellen[4], "h_stream_a"), "http://gffstream.ic.llnwd.net/stream/gffstream_w14a");
    	map.put(new Pair<String, String>(wellen[4], "h_stream_b"), "http://gffstream.ic.llnwd.net/stream/gffstream_w14b");
    	
    	map.put(new Pair<String, String>(wellen[5], "name"), "Bayern Plus");
    	map.put(new Pair<String, String>(wellen[5], "text"), "dabbplus.html");
    	map.put(new Pair<String, String>(wellen[5], "img"), "dabbplus.html");
    	map.put(new Pair<String, String>(wellen[5], "l_stream_a"), "http://gffstream.ic.llnwd.net/stream/gffstream_w7a");
    	map.put(new Pair<String, String>(wellen[5], "l_stream_b"), "http://gffstream.ic.llnwd.net/stream/gffstream_w7b");
    	map.put(new Pair<String, String>(wellen[5], "h_stream_a"), "http://gffstream.ic.llnwd.net/stream/gffstream_w16a");
    	map.put(new Pair<String, String>(wellen[5], "h_stream_b"), "http://gffstream.ic.llnwd.net/stream/gffstream_w16b");
    	
    	map.put(new Pair<String, String>(wellen[6], "name"), "puls");
    	map.put(new Pair<String, String>(wellen[6], "text"), "dabon3.html");
    	map.put(new Pair<String, String>(wellen[6], "img"), "dabon3.html");
    	map.put(new Pair<String, String>(wellen[6], "l_stream_a"), "http://gffstream.ic.llnwd.net/stream/gffstream_w8a");
    	map.put(new Pair<String, String>(wellen[6], "l_stream_b"), "http://gffstream.ic.llnwd.net/stream/gffstream_w8b");
    	map.put(new Pair<String, String>(wellen[6], "h_stream_a"), "http://gffstream.ic.llnwd.net/stream/gffstream_w9a");
    	map.put(new Pair<String, String>(wellen[6], "h_stream_b"), "http://gffstream.ic.llnwd.net/stream/gffstream_w9b");
    	
		return map;
	}

}
