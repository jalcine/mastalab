/* Copyright 2017 Thomas Schneider
 *
 * This file is a part of Mastalab
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * Mastalab is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Mastalab; if not,
 * see <http://www.gnu.org/licenses>. */
package fr.gouv.etalab.mastodon.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import fr.gouv.etalab.mastodon.client.API;
import fr.gouv.etalab.mastodon.client.APIResponse;
import fr.gouv.etalab.mastodon.interfaces.OnRetrieveFeedsInterface;


/**
 * Created by Thomas on 23/04/2017.
 * Retrieves toots on the instance
 */

public class RetrieveFeedsAsyncTask extends AsyncTask<Void, Void, Void> {


    private Type action;
    private APIResponse apiResponse;
    private String max_id;
    private OnRetrieveFeedsInterface listener;
    private String targetedID;
    private String tag;
    private boolean showMediaOnly = false;
    private boolean showPinned = false;
    private WeakReference<Context> contextReference;

    public enum Type{
        HOME,
        LOCAL,
        PUBLIC,
        HASHTAG,
        USER,
        FAVOURITES,
        ONESTATUS,
        CONTEXT,
        TAG
    }

    public RetrieveFeedsAsyncTask(Context context, Type action, String max_id, OnRetrieveFeedsInterface onRetrieveFeedsInterface){
        this.contextReference = new WeakReference<>(context);
        this.action = action;
        this.max_id = max_id;
        this.listener = onRetrieveFeedsInterface;
    }

    public RetrieveFeedsAsyncTask(Context context, Type action, String targetedID, String max_id, boolean showMediaOnly, boolean showPinned, OnRetrieveFeedsInterface onRetrieveFeedsInterface){
        this.contextReference = new WeakReference<>(context);
        this.action = action;
        this.max_id = max_id;
        this.listener = onRetrieveFeedsInterface;
        this.targetedID = targetedID;
        this.showMediaOnly = showMediaOnly;
        this.showPinned = showPinned;
    }
    public RetrieveFeedsAsyncTask(Context context, Type action, String tag, String targetedID, String max_id, OnRetrieveFeedsInterface onRetrieveFeedsInterface){
        this.contextReference = new WeakReference<>(context);
        this.action = action;
        this.max_id = max_id;
        this.listener = onRetrieveFeedsInterface;
        this.targetedID = targetedID;
        this.tag = tag;
    }


    @Override
    protected Void doInBackground(Void... params) {
        API api = new API(this.contextReference.get());
        switch (action){
            case HOME:
                apiResponse = api.getHomeTimeline(max_id);
                break;
            case LOCAL:
                apiResponse = api.getPublicTimeline(true, max_id);
                break;
            case PUBLIC:
                apiResponse = api.getPublicTimeline(false, max_id);
                break;
            case FAVOURITES:
                apiResponse = api.getFavourites(max_id);
                break;
            case USER:
                if( showMediaOnly)
                    apiResponse = api.getStatusWithMedia(targetedID, max_id);
                else if (showPinned)
                    apiResponse = api.getPinnedStatuses(targetedID, max_id);
                else
                    apiResponse = api.getStatus(targetedID, max_id);
                break;
            case ONESTATUS:
                apiResponse = api.getStatusbyId(targetedID);
                break;
            case TAG:
                apiResponse = api.getPublicTimelineTag(tag, false, max_id);
                break;
            case HASHTAG:
                break;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        listener.onRetrieveFeeds(apiResponse);
    }
}
