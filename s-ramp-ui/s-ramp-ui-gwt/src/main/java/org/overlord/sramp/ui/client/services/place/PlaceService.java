/*
 * Copyright 2012 JBoss Inc
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
package org.overlord.sramp.ui.client.services.place;

import org.overlord.sramp.ui.client.PlaceHistoryMapperImpl;
import org.overlord.sramp.ui.client.services.AbstractService;
import org.overlord.sramp.ui.client.services.IServiceLifecycleListener;
import org.overlord.sramp.ui.client.services.ServiceLifecycleContext;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryMapper;

/**
 * Concrete implementation of the place service ({@link IPlaceService}).
 *
 * @author eric.wittmann@redhat.com
 */
public class PlaceService extends AbstractService implements IPlaceService {

	private PlaceHistoryMapper placeHistoryMapper;
	private PlaceController placeController;

	/**
	 * Constructor.
	 */
	public PlaceService() {
	}
	
	/**
	 * @see org.overlord.sramp.ui.client.services.place.IPlaceService#goTo(com.google.gwt.place.shared.Place)
	 */
	@Override
	public void goTo(Place place) {
		getPlaceController().goTo(place);
	}

	/**
	 * @see org.overlord.sramp.ui.client.services.IService#start(org.overlord.sramp.ui.client.services.ServiceLifecycleContext, org.overlord.sramp.ui.client.services.IServiceLifecycleListener)
	 */
	@Override
	public void start(ServiceLifecycleContext context, IServiceLifecycleListener serviceListener) {
		placeHistoryMapper = new PlaceHistoryMapperImpl();
		placeController = context.getClientFactory().getPlaceController();
		super.start(context, serviceListener);
	}

	/**
	 * @see org.overlord.sramp.ui.client.services.place.IPlaceService#getPlaceHistoryMapper()
	 */
	@Override
	public PlaceHistoryMapper getPlaceHistoryMapper() {
		return placeHistoryMapper;
	}

	/**
	 * @see org.overlord.sramp.ui.client.services.place.IPlaceService#generatePlaceToken(com.google.gwt.place.shared.Place)
	 */
	@Override
	public String generatePlaceToken(Place place) {
		return placeHistoryMapper.getToken(place);
	}
	
	/**
	 * @see org.overlord.sramp.ui.client.services.place.IPlaceService#getPlaceController()
	 */
	@Override
	public PlaceController getPlaceController() {
		return placeController;
	}
}
