package com.jayway.angrytoday.dto;

import java.util.Set;

public class AngryPostDTO {

    private String id;
    private String message;
    private Set<String> tags;

    public String getMessage() {
        return message;
    }

    public void setMessage( String message ) {
        this.message = message;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
