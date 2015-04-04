package com.rokuan.calliope.extract;

import java.net.URL;

/**
 * Created by LEBEAU Christophe on 02/04/2015.
 */
public interface HypertextLinkExtractor {
    public URL getURL();
    public void getURLAsync(HypertextLinkExtractionListener listener);
}
