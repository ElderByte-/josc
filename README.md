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

* `s3`  S3 compatible driver
* `webdav` WebDav adapter driver
* `swift` OpenStack Swift driver
* `fs`  Local file system adapter, usefull for test environments or simplistic usage

Of course, you can implement and register your own drivers. The josc archtecture is specifically designed to support custom vendor implementations. 


### Create a custom JOSC driver

* Add the `com.elderbyte.josc:josc-api` dependency to your project.
* Implement the `com.elderbyte.josc.api.JoscDriver` interface
* To automatically register the driver in josc as soon as it is on the class-path, create a `META-INF/services/com.elderbyte.josc.api.JoscDriver` file. In this file, specify your implementation class with the full package path.
