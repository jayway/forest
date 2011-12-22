package com.jayway.angrytoday.resources;

import com.jayway.angrytoday.domain.AngryPost;
import com.jayway.angrytoday.domain.Comment;
import com.jayway.angrytoday.dto.AngryPostDTO;
import com.jayway.angrytoday.repository.AngryTodayRepository;
import com.jayway.forest.exceptions.NotFoundException;
import com.jayway.forest.roles.*;

import java.util.ArrayList;
import java.util.List;

public class AngryPostResource implements IdResource, CreatableResource<String> {

    private AngryPost post;

    public AngryPostResource(AngryPost post) {
        if ( post == null ) throw new NotFoundException();
        this.post = post;
    }

    public AngryPostDTO description() {
        return AngryTodayRepository.INSTANCE.toDto( post );
    }

    public List<Linkable> comments() {
        List<Linkable> links = new ArrayList<Linkable>();
        handleList( links, post.getComments(), "" );
        return links;
    }

    private void handleList( List<Linkable> links, List<Comment> list, String path ) {
        if ( list == null ) return;
        for (int i = 0; i < list.size(); i++) {
            String subPath = path+i+"/";
            links.add( new Linkable( subPath, list.get(i).getComment() ) );
            handleList( links, list.get(i).getSubComments(), subPath );
        }
    }

    public Resource id(String id) {
        return new AngryCommentResource( id, post.getComments() );

    }

    public Linkable create(String comment) {
        post.addComment( comment );
        return new Linkable( ""+(post.getComments().size()-1), comment );
    }

    public void tag( String tag ) {
        post.addTag( tag );
    }

    public void untag( String tag ) {
        post.unTag( tag );
    }

}