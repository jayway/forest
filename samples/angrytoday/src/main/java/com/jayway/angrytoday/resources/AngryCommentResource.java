package com.jayway.angrytoday.resources;

import com.jayway.angrytoday.domain.Comment;
import com.jayway.forest.legacy.exceptions.NotFoundException;
import com.jayway.forest.legacy.roles.CreatableResource;
import com.jayway.forest.legacy.roles.DescribedResource;
import com.jayway.forest.legacy.roles.Linkable;
import com.jayway.forest.legacy.roles.Resource;

import java.util.List;

public class AngryCommentResource implements CreatableResource<String> {

    private Comment comment;

    public AngryCommentResource( String id, List<Comment> parentComments ) {
        try {
            int index = Integer.parseInt(id);
            comment  = parentComments.get(index);
        } catch ( Exception e ) {
            throw new NotFoundException();
        }
    }

    public Object description() {
        return comment;
    }

    public Linkable create( String comment) {
        this.comment.add( comment );
        return new Linkable( ""+(this.comment.getSubComments().size() -1), comment );
    }

    public Resource id( String id ) {
        return new AngryCommentResource( id, comment.getSubComments() );
    }
}
