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
import java.util.List;

import fr.gouv.etalab.mastodon.client.API;
import fr.gouv.etalab.mastodon.client.Entities.Account;
import fr.gouv.etalab.mastodon.client.Entities.Results;
import fr.gouv.etalab.mastodon.interfaces.OnPostActionInterface;


/**
 * Created by Thomas on 29/04/2017.
 * Makes actions for post calls
 */

public class PostActionAsyncTask extends AsyncTask<Void, Void, Void> {

    private OnPostActionInterface listener;
    private int statusCode;
    private API.StatusAction apiAction;
    private String targetedId;
    private String comment;
    private fr.gouv.etalab.mastodon.client.Entities.Status status;
    private API api;
    private Account account;
    private fr.gouv.etalab.mastodon.client.Entities.Status remoteStatus;
    private WeakReference<Context> contextReference;

    public PostActionAsyncTask(Context context, API.StatusAction apiAction, String targetedId, OnPostActionInterface onPostActionInterface){
        this.contextReference = new WeakReference<>(context);
        this.listener = onPostActionInterface;
        this.apiAction = apiAction;
        this.targetedId = targetedId;
    }

    public PostActionAsyncTask(Context context, Account account, API.StatusAction apiAction, String targetedId, OnPostActionInterface onPostActionInterface){
        this.contextReference = new WeakReference<>(context);
        this.listener = onPostActionInterface;
        this.apiAction = apiAction;
        this.targetedId = targetedId;
        this.account = account;
    }

    public PostActionAsyncTask(Context context, Account account, fr.gouv.etalab.mastodon.client.Entities.Status remoteStatus, API.StatusAction apiAction, OnPostActionInterface onPostActionInterface){
        this.contextReference = new WeakReference<>(context);
        this.listener = onPostActionInterface;
        this.apiAction = apiAction;
        this.remoteStatus = remoteStatus;
        this.account = account;
    }

    public PostActionAsyncTask(Context context, API.StatusAction apiAction, String targetedId, fr.gouv.etalab.mastodon.client.Entities.Status status, String comment, OnPostActionInterface onPostActionInterface){
        contextReference = new WeakReference<>(context);
        this.listener = onPostActionInterface;
        this.apiAction = apiAction;
        this.targetedId = targetedId;
        this.comment = comment;
        this.status = status;
    }

    @Override
    protected Void doInBackground(Void... params) {

        //Remote action
        if( account !=null)
            api = new API(contextReference.get(), account.getInstance(), account.getToken());
        else
            api = new API(contextReference.get());
        if( remoteStatus != null){
            Results search = api.search(remoteStatus.getUri());
            if( search != null){
                List<fr.gouv.etalab.mastodon.client.Entities.Status> remoteStatuses = search.getStatuses();
                if( remoteStatuses != null && remoteStatuses.size() > 0 ){
                    fr.gouv.etalab.mastodon.client.Entities.Status statusTmp = remoteStatuses.get(0);
                    this.targetedId = statusTmp.getId();
                    statusCode = api.postAction(apiAction, targetedId);
                }
            }
        } else {

            if (apiAction == API.StatusAction.REPORT)
                statusCode = api.reportAction(status, comment);
            else if (apiAction == API.StatusAction.CREATESTATUS)
                statusCode = api.statusAction(status);
            else
                statusCode = api.postAction(apiAction, targetedId);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        listener.onPostAction(statusCode, apiAction, targetedId, api.getError());
    }

}
