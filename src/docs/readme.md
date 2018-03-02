# RouteMaster

Handy routing controller and webserver for the truly excellent [nanohttpd](https://github.com/NanoHttpd/nanohttpd) tiny, easily embed-able HTTP Java server, with module and templating support.

See [https://synapticloop.github.io/routemaster/](https://synapticloop.github.io/routemaster/) for full documentation

![routemaster home page](https://raw.github.com/synapticloop/routemaster/master/src/main/wiki/images/nano-httpd-home.png)

# Templating with `Templar` templating language

Yes, you can now do some server side includes, using the synapticloop templar templating language!

A quick example to use includes:

```
{import src/main/html/templar-handler.snippet}
```

# Module support

To make it quicker and easier to get started with routemaster, we added in module 
functionality so that it can be easily extended, just create a directory named 
`modules` in the same directory from whence the server was started and copy the 
modules into it.  These modules will be automatically loaded and deployed.

### Instructions:

Download the `routemaster-<version>-server.jar`  from the [github releases directory](https://github.com/synapticloop/routemaster/releases) *(which contains all of the dependencies that are required)*, download modules that you require and place them in the `modules` directory from where you launched the routemaster server.  

These will be automatically registered and ready to go.

For an example module with everything you need to build and deploy, see [https://github.com/synapticloop/routemaster-module-example](https://github.com/synapticloop/routemaster-module-example).

### Some of the available modules

**Thumbnailer** [routemaster-module-thumbnailer](https://github.com/synapticloop/routemaster-module-thumbnailer) automatically generate thumbnails of images on the fly.

**Templar Handler** [routemaster-module-templar-handler](https://github.com/synapticloop/routemaster-module-templar-handler) add templar templating language handling to the routemaster server.

**Static file lister** [routemaster-module-static-file-lister](https://github.com/synapticloop/routemaster-module-static-file-lister) add in static file listing and navigation.


## Options

Following the NanoHTTPD, you may pass in the following options

```
- h      
--host   The host to bind to - default 127.0.0.1

-p
--port   The port to bind to - default 5474

-d
--dir    The directory that the html files reside in

```

## As an aside

The name ```RouteMaster``` pays homage to the original London buses [(see the wikipedia entry)](http://en.wikipedia.org/wiki/Routemaster) which would take you wherever you would like to go.

The favicon is also related, showing a depiction of the back top window of a routemaster bus.
