package com.jayway.forest.samples.bank.spring;

import com.jayway.forest.roles.Resource;
import com.jayway.forest.samples.bank.model.Account;

public interface ResourceWithAccount extends Resource {
	public Account getAccount();
}
