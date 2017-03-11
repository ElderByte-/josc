package com.elderbyte.josc.spring.support.streaming;

import java.net.URLConnection;



public class DefaultMimetypeProvider implements MimeTypeProvider {
    @Override
    public String guessMimeType(String filename) {
        return URLConnection.guessContentTypeFromName(filename);
    }
}
