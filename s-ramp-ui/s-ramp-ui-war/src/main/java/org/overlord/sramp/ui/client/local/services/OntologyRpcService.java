/*
 * Copyright 2013 JBoss Inc
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
package org.overlord.sramp.ui.client.local.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.overlord.sramp.ui.client.local.services.rpc.DelegatingErrorCallback;
import org.overlord.sramp.ui.client.local.services.rpc.DelegatingRemoteCallback;
import org.overlord.sramp.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.sramp.ui.client.shared.beans.OntologyBean;
import org.overlord.sramp.ui.client.shared.beans.OntologySummaryBean;
import org.overlord.sramp.ui.client.shared.exceptions.SrampUiException;
import org.overlord.sramp.ui.client.shared.services.IOntologyService;

/**
 * Client-side service for making RPC calls to the remote ontology service.  This service
 * also caches the ontologies with the expectation that they rarely change.  Consumers
 * should call the clearCache() method to force a re-fetch of data.
 *
 * @author eric.wittmann@redhat.com
 */
@ApplicationScoped
public class OntologyRpcService {

    @Inject
    private Caller<IOntologyService> remoteOntologyService;

    private List<OntologySummaryBean> summaryCache;
    private Map<String, OntologyBean> ontologyCache;

    /**
     * Constructor.
     */
    public OntologyRpcService() {
    }

    /**
     * Invalidates/clears the cache.
     */
    public void clearCache() {
        summaryCache = null;
        ontologyCache = null;
    }

    /**
     * @see org.overlord.sramp.ui.client.shared.services.IOntologyService#list()
     */
    public void list(final IRpcServiceInvocationHandler<List<OntologySummaryBean>> handler) {
        if (summaryCache != null) {
            handler.onReturn(summaryCache);
        }
        else {
            RemoteCallback<List<OntologySummaryBean>> successCallback = new DelegatingRemoteCallback<List<OntologySummaryBean>>(handler) {
                @Override
                public void callback(List<OntologySummaryBean> response) {
                    summaryCache = new ArrayList<OntologySummaryBean>(response);
                    super.callback(response);
                }
            };
            ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
            try {
                remoteOntologyService.call(successCallback, errorCallback).list();
            } catch (SrampUiException e) {
                errorCallback.error(null, e);
            }
        }
    }

    /**
     * @see org.overlord.sramp.ui.client.shared.services.IOntologyService#get(java.lang.String)
     */
    public void get(String uuid, final IRpcServiceInvocationHandler<OntologyBean> handler) {
        if (ontologyCache != null && ontologyCache.containsKey(uuid)) {
            handler.onReturn(ontologyCache.get(uuid));
        } else {
            RemoteCallback<OntologyBean> successCallback = new DelegatingRemoteCallback<OntologyBean>(handler);
            ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
            try {
                remoteOntologyService.call(successCallback, errorCallback).get(uuid);
            } catch (SrampUiException e) {
                errorCallback.error(null, e);
            }
        }
    }

    /**
     * Gets all of the ontologies in a single async shot.
     * @param handler
     */
    public void getAll(final IRpcServiceInvocationHandler<List<OntologyBean>> handler) {
        list(new IRpcServiceInvocationHandler<List<OntologySummaryBean>>() {
            int ontologyIndex = 0;
            List<OntologyBean> allOntologies = new ArrayList<OntologyBean>();
            @Override
            public void onReturn(final List<OntologySummaryBean> summaries) {
                if (summaries.isEmpty()) {
                    handler.onReturn(allOntologies);
                } else {
                    final IRpcServiceInvocationHandler<OntologyBean> handy = new IRpcServiceInvocationHandler<OntologyBean>() {
                        @Override
                        public void onReturn(OntologyBean ontology) {
                            // add the ontology to the rval
                            allOntologies.add(ontology);
                            // get the next ontology in the list (unless we've got them all)
                            ontologyIndex++;
                            if (ontologyIndex >= summaries.size()) {
                                handler.onReturn(allOntologies);
                            } else {
                                get(summaries.get(ontologyIndex).getUuid(), this);
                            }
                        }
                        @Override
                        public void onError(Throwable error) {
                            handler.onError(error);
                        }
                    };
                    get(summaries.get(ontologyIndex).getUuid(), handy);
                }
            }
            @Override
            public void onError(Throwable error) {
                handler.onError(error);
            }
        });
    }

    /**
     * @see org.overlord.sramp.ui.client.shared.services.IOntologyService#update(OntologyBean)
     */
    public void update(OntologyBean ontology, final IRpcServiceInvocationHandler<Void> handler) {
        RemoteCallback<Void> successCallback = new DelegatingRemoteCallback<Void>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            remoteOntologyService.call(successCallback, errorCallback).update(ontology);
        } catch (SrampUiException e) {
            errorCallback.error(null, e);
        }
    }

}
