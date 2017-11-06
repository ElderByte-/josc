[![Build Status](https://travis-ci.org/ElderByte-/josc.svg?branch=master)](https://travis-ci.org/ElderByte-/josc)

[ ![Download](https://api.bintray.com/packages/elderbyte/maven/josc-full/images/download.svg) ](https://bintray.com/elderbyte/maven/josc-full/_latestVersion)

# josc
Java Object Store Connectivity - Provides a client abstraction to several Object / Blob Stores such as S3, OpenStack Swift etc. At its core, josc defines a (simple) Object Store Client API as a set of Interfaces and base classes.
For each supported vendor, a josc driver must be provided.

## josc connection strings

A josc connection string denotes the target system and the required driver:

```
josc:s3:https://myServer:9000
```

* The josc connection string format: `josc:{protocol}:{host-url};property1=1;property2=2`
* The first two colons `:` separate the three main parts, which are the magic `josc`, the protocol and the vendor informaiton

### Supported protocols

* `s3`  S3 compatible driver - built upon [minio/minio-java](https://github.com/minio/minio-java)
* `webdav` WebDav adapter driver - built upon [lookfirst/sardine](https://github.com/lookfirst/sardine)
* `swift` OpenStack Swift driver - build upon [javaswift/joss](https://github.com/javaswift/joss) 
* `fs`  Local file system adapter, usefull for test environments or simplistic usage

Of course, you can implement and register your own drivers. The josc archtecture is specifically designed to support custom vendor implementations. 

### How open a Object Store connection

```java
public ObjectStoreClient openClient() throws ObjectStoreConnectionException {
    Map<String, String> props = new HashMap<>();
    props.put("user", "123412341231234");
    props.put("pass", "abcdefg");

    try {
        return clientFactory.buildClient("josc:s3:https://myServer.mydomain.com:9000", new JoscConnectionProperties(props));
    }catch (IllegalArgumentException e){
        throw new ObjectStoreConnectionException("Could not parse josc connection-string.", e);
    }
}
```

### Create a custom JOSC driver

* Add the `com.elderbyte.josc:josc-api` dependency to your project.
* Implement the `com.elderbyte.josc.api.JoscDriver` interface
* To automatically register the driver in josc as soon as it is on the class-path, create a `META-INF/services/com.elderbyte.josc.api.JoscDriver` file. In this file, specify your implementation class with the full package path.
