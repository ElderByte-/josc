[![Build Status](https://travis-ci.org/ElderByte-/josc.svg?branch=master)](https://travis-ci.org/ElderByte-/josc)

# !! Alpha status (The API and the implementations are not stable yet)

# josc
Java Object Store Connectivity - Provides a client abstraction to several Object / Blob Stores such as S3, OpenStack Swift etc. At its core, josc defines a (simple) Object Store Client API as a set of Interfaces and base classes.
For each supported vendor, a josc driver must be provided.

## josc connection strings

A josc connection string denotes the target system and the required driver:

```
josc:s3:https://myServer:9000
```

* Every josc connection-string starts with `josc`
* The first two colons `:` separate the three main parts, which are the magic jost, the protocol and the vendor informaiton

### Supported protocols

* `s3`  S3 compatible driver
* `webdav` WebDav adapter driver
* `swift` OpenStack Swift driver
* `fs`  Local file system adapter, usefull for test environments or simplistic usage

Of course, you can implement and register your own drivers. The josc archtecture is specifically designed to support custom vendor implementations. 
