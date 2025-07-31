package com.atlassian.sample.api;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class CommentApplication extends Application {
    @Override
    public Set<Object> getSingletons() {
        Set<Object> set = new HashSet<>();
        set.add(new CommentResource());
        return set;
    }
}
