package com.jayway.angrytoday;

import com.jayway.angrytoday.resources.RootResource;
import com.jayway.forest.legacy.core.Application;
import com.jayway.forest.legacy.di.grove.GroveDependencyInjectionImpl;
import com.jayway.forest.legacy.roles.Resource;
import com.jayway.forest.legacy.servlet.RestfulServlet;

public class AngryTodayRestService extends RestfulServlet {

    @Override
	public void init() {
		initForest(new Application() {
            @Override
            public Resource root() {
                return new RootResource();
            }

            @Override
            public void setupRequestContext() {
            }

        }, new GroveDependencyInjectionImpl());
	}

}
