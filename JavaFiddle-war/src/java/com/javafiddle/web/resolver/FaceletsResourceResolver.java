package com.javafiddle.web.resolver;


import java.net.URL;
import javax.faces.view.facelets.ResourceResolver;

public class FaceletsResourceResolver extends ResourceResolver {

    private ResourceResolver parent;
    private String basePath;

    public FaceletsResourceResolver(ResourceResolver parent) {
        this.parent = parent;
        this.basePath = "META-INF/resources"; // TODO: Make configureable?
    }

    @Override
    public URL resolveUrl(String path) {
        URL url = parent.resolveUrl(path); // Resolves from WAR which would also do META-INF/resources of JARs in WAR.

        if (url == null) {
            url = getClass().getResource("/" + basePath + path); // Resolves from JARs in WAR when base path is not META-INF/resources.
        }

        if (url == null) {
            url = Thread.currentThread().getContextClassLoader().getResource(basePath + path); // Resolves also from anywhere else in classpath.
        }

        return url;
    }

}