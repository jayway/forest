package com.jayway.restfuljersey.samples.bank.spring;

import com.jayway.forest.roles.Resource;
import com.jayway.restfuljersey.samples.bank.model.Account;

public interface ResourceWithAccount extends Resource {
	public Account getAccount();
}
