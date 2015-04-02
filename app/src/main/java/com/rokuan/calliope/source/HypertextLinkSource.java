package com.rokuan.calliope.source;

import com.rokuan.calliope.extract.HypertextLinkExtractor;
import com.rokuan.calliope.extract.TextExtractor;

import java.net.URL;

/**
 * Created by LEBEAU Christophe on 01/04/2015.
 */
public class HypertextLinkSource extends SourceObject implements HypertextLinkExtractor, TextExtractor {
    private URL linkURL;

    public HypertextLinkSource(URL url) {
        super(ObjectType.LINK);
        linkURL = url;
    }

    @Override
    public URL getURL() {
        return linkURL;
    }

    @Override
    public String getText() {
        return linkURL.toString();
    }
}
