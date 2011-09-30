package com.jayway.forest.reflection;

import com.jayway.forest.roles.Linkable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Capabilities {
	private final String name;
	private final List<ResourceMethod> queries = new LinkedList<ResourceMethod>();
	private final List<ResourceMethod> commands = new LinkedList<ResourceMethod>();
	private final List<ResourceMethod> resources = new LinkedList<ResourceMethod>();
	private Object description;
    private List<Linkable> discovered;

    public void setDescriptionResult(Object description) {
		this.description = description;
	}
	public Capabilities(String name) {
		this.name = name;
	}
	public void addQuery(ResourceMethod method) {
		queries.add(method);
	}
	public void addCommand(ResourceMethod method) {
		commands.add(method);
	}
	public void addResource(ResourceMethod method) {
		resources.add(method);
	}
	public String getName() {
		return name;
	}
	public List<ResourceMethod> getQueries() {
		return queries;
	}
	public List<ResourceMethod> getCommands() {
		return commands;
	}
	public List<ResourceMethod> getResources() {
		return resources;
	}
	public Object getDescriptionResult() {
		return description;
	}
    public void setDiscovered(List<Linkable> discovered ) {
        this.discovered = discovered;
    }
    public List<Linkable> getDiscovered() {
        if ( discovered == null ) return Collections.emptyList();
        return discovered;
    }
}
