package com.elderbyte.josc.driver.fs;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class PathRelativizer {
    public static Path relativize(Path fullpath, Path base){
        String baseStr = base.toString();
        String fullStr = fullpath.toString();
        if(fullStr.startsWith(baseStr)){
            String relative = fullStr.substring(baseStr.length());
            if(relative.startsWith("/") || relative.startsWith("\\")){
                relative = relative.substring(1);
            }
         return Paths.get(relative);
        }else{
            throw new IllegalArgumentException("Cant relativize fullpath: '" + fullpath + "' to base: '" + baseStr + "'");
        }
    }
}
