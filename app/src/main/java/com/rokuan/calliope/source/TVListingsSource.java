package com.rokuan.calliope.source;

import com.rokuan.calliope.api.kimonotv.TVProgram;

import java.util.List;

/**
 * Created by LEBEAU Christophe on 29/04/2015.
 */
public class TVListingsSource extends SourceObject {
    private List<TVProgram> programs;

    public TVListingsSource(List<TVProgram> ps) {
        super(ObjectType.TV_LISTING);
        programs = ps;
    }

    public List<TVProgram> getProgramList(){
        return programs;
    }
}
