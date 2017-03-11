package com.elderbyte.josc.driver.fs;

import com.elderbyte.josc.spring.support.streaming.HttpStreamUtil;
import com.elderbyte.josc.spring.support.streaming.MimeTypeProvider;
import com.elderbyte.josc.spring.support.streaming.StreamResource;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * REST API adapter for presigned GET/PUT support for file system
 */
@RestController
@RequestMapping("/josc/{account}/buckets/{bucket}/{objectName}")
public class FsObjectController {

    /***************************************************************************
     *                                                                         *
     * Fields                                                                  *
     *                                                                         *
     **************************************************************************/

    private static final Logger log = LoggerFactory.getLogger(FsObjectController.class);

    @Autowired
    private MimeTypeProvider mimeTypeProvider;


    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/


    @PermitAll
    @RequestMapping(method = RequestMethod.HEAD)
    public void getBlobObjectHead(
        HttpServletRequest request,
        HttpServletResponse response,
        @PathVariable("account") String account,
        @PathVariable("bucket") String bucket,
        @PathVariable("objectName") String objectName) {

        log.info("Serving stream request...");

        StreamResource resource = getStreamResource(account, bucket, objectName)
                                        .orElseThrow(() -> new IllegalStateException("Could not find or access resource!"));

        try {
            HttpStreamUtil.sendStream(resource, request, response, false);
        } catch (IOException e) {
            log.info("Failed to serve blob object!", e);
        }
    }


    @PermitAll
    @RequestMapping(method = RequestMethod.GET)
    public void getBlobObject(
        HttpServletRequest request,
        HttpServletResponse response,
        @PathVariable("account") String account,
        @PathVariable("bucket") String bucket,
        @PathVariable("objectName") String objectName) {


        log.info("Serving stream request...");

        StreamResource resource = getStreamResource(account, bucket, objectName)
                                        .orElseThrow(() -> new IllegalStateException("Could not find or access resource!"));

        try {
            HttpStreamUtil.sendStream(resource, request, response, true);
        } catch (Exception e) {

            if(e instanceof ClientAbortException){
                log.debug("Client aborted stream " + bucket + " / " + objectName + ".");
            }else{
                log.warn("Failed to serve blob object!", e);
            }
        }
    }


    @PermitAll
    @RequestMapping(method = RequestMethod.PUT)
    public void putBlobObject(
        HttpServletRequest request,
        HttpServletResponse response,
        @PathVariable("account") String account,
        @PathVariable("bucket") String bucket,
        @PathVariable("objectName") String objectName) {

        getStreamResource(account, bucket, objectName);
        throw new IllegalStateException("Uploading not implemented yet."); // TODO
    }


    /**************************************************************************
     *                                                                         *
     * Private Methods                                                         *
     *                                                                         *
     **************************************************************************/


    private Optional<StreamResource> getStreamResource(String accountEncoded, String bucket, String objectNameEncoded){

        String basePathStr = new String(Base64.decodeBase64(accountEncoded));
        Path basePath = Paths.get(basePathStr);
        String objectName = new String(Base64.decodeBase64(objectNameEncoded));

        Path blobPath = basePath.resolve(bucket).resolve(objectName);

        if(Files.exists(blobPath)){
            return Optional.of(getStreamResource(blobPath));
        }else{
            return Optional.empty();
        }
    }


    private StreamResource getStreamResource(Path path){

        File file = path.toFile();
        String name = path.getFileName().toString();

        String mimeType = mimeTypeProvider.guessMimeType(path.toString());

        return new StreamResource(
            name,
            file.length(),
            Integer.MAX_VALUE,
            () -> {
                try{
                    return new RandomAccessFile(file, "r").getChannel();
                }catch (IOException e){
                    throw new RuntimeException("Failed to open random access input-stream.", e);
                }
            },
            mimeType);
    }



    public static String getRelativeTempUrl(Path base, String bucket, String objectName){
        String accoutEnc = Base64.encodeBase64URLSafeString(base.toString().getBytes());
        String objectNameEnc = Base64.encodeBase64URLSafeString(objectName.getBytes());
        return String.format("/josc/%s/buckets/%s/%s", accoutEnc, bucket, objectNameEnc);
    }


}
