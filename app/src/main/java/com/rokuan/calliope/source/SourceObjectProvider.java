package com.rokuan.calliope.source;

/**
 * Created by Lebeau Lucie on 21/05/15.
 */
public interface SourceObjectProvider {
    SourceObject getLastSourceOfType(SourceObject.ObjectType type);
    void addSource(SourceObject src);
}
