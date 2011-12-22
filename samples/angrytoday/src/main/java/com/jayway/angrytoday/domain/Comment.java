package com.jayway.angrytoday.domain;

import java.util.ArrayList;
import java.util.List;

public class Comment {

    private String comment;
    private List<Comment> subComments;

    public Comment( String comment ) {
        this.comment = comment;
    }

    public void add( String subComment ) {
        if ( subComments == null ) {
            subComments = new ArrayList<Comment>();
        }
        subComments.add( new Comment( subComment ));
    }

    public String getComment() {
        return comment;
    }

    public List<Comment> getSubComments() {
        return subComments;
    }

}
