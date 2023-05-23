package com.jcyu.eventfinder.ui.search;

import org.json.JSONObject;

public class ArtistAlbumItem {
    public JSONObject artistDetail;
    public JSONObject album;

    public ArtistAlbumItem(JSONObject artistDetail, JSONObject album) {
        this.artistDetail = artistDetail;
        this.album = album;
    }
}
