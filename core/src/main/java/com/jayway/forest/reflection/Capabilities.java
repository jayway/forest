package com.jayway.forest.reflection;

import com.jayway.forest.roles.Linkable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Capabilities {
	private final String name;
	private final List<Capability> queries = new LinkedList<Capability>();
	private final List<Capability> commands = new LinkedList<Capability>();
	private final List<Capability> resources = new LinkedList<Capability>();
	private Object description;
    private List<Linkable> discovered;

    public void setDescriptionResult(Object description) {
		this.description = description;
	}
	public Capabilities(String name) {
		this.name = name;
	}
	public void addQuery(Capability method) {
		queries.add(method);
	}
	public void addCommand(Capability method) {
		commands.add(method);
	}
	public void addResource(Capability method) {
		resources.add(method);
	}
	public String getName() {
		return name;
	}
	public List<Capability> getQueries() {
		return queries;
	}
	public List<Capability> getCommands() {
		return commands;
	}
	public List<Capability> getResources() {
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
