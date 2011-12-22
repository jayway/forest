package com.jayway.angrytoday.resources;

import com.jayway.angrytoday.domain.AngryPost;
import com.jayway.angrytoday.repository.AngryTodayRepository;
import com.jayway.forest.roles.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class RootResource implements Resource, CreatableResource<String>, IdDiscoverableResource {

    public Linkable create(String message) {
        AngryPost post = new AngryPost( message );
        AngryTodayRepository.INSTANCE.add(post);
        return link( post );
    }


    public List<AngryPostLinkable> discover() {
        Collection<AngryPost> posts = AngryTodayRepository.INSTANCE.posts();

        LinkedList<AngryPostLinkable> links = new LinkedList<AngryPostLinkable>();
        for ( AngryPost post: posts) {
            links.addFirst( link( post ));
        }

        return links;
    }

    public Resource id(String id) {
        return new AngryPostResource( AngryTodayRepository.INSTANCE.post( id ) );
    }


    private AngryPostLinkable link( AngryPost post ) {
        String comment = null;
        if ( post.getComments() != null && post.getComments().size() > 0 ) {
            comment = post.getComments().get( 0 ).getComment();
        }
        return new AngryPostLinkable( post.getId(), post.getMessage(), post.getTags(), comment );
    }

    public String description() {
        int size = AngryTodayRepository.INSTANCE.posts().size();
        return "Angry Today contain "+size+" posts";
    }
}
