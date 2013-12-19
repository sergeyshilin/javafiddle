package com.javafiddle.runner;

import java.security.AccessControlContext;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.util.PropertyPermission;

public class LaunchPermissions {
    public static AccessControlContext getSecureContext() {
        Permissions perms = new Permissions();
        perms.add(new RuntimePermission(
                "accessDeclaredMembers"));
        perms.add(new RuntimePermission(
                "accessClassInPackage.sun.misc"));
        perms.add(new RuntimePermission(
                "createClassLoader"));
        perms.add(new PropertyPermission(
                "java.util.ArrayList", "read"));
        perms.add(new PropertyPermission(
                "com.sun.script.java.sourcepath", "read"));
        perms.add(new PropertyPermission(
                "com.sun.script.java.classpath", "read"));
        perms.add(new PropertyPermission(
                "java.class.path", "read"));
        perms.add(new PropertyPermission(
                "java.endorsed.dirs", "read"));
        perms.add(new PropertyPermission(
                "sun.boot.class.path", "read"));
        perms.add(new PropertyPermission(
                "java.ext.dirs", "read"));
        perms.add(new PropertyPermission(
                "nonBatchMode", "read"));
        perms.add(new PropertyPermission(
                "com.sun.script.java.mainClass", "read"));
//        perms.add(new FilePermission(
//                "TestApp.java", "read"));
//        perms.add(new FilePermission(
//                "/C:/Program Files/Java/jdk1.6.0_24/-",
//                "read"));
//        perms.add(new FilePermission(
//                "/C:/windows/Sun/Java/-", "read"));
//        perms.add(new FilePermission(
//                "/C:/Users/entend/scripting/trunk/engines/java/build/-",
//                "read"));
//        perms.add(new FilePermission(
//                "/C:/Users/entend/NetBeansProjects/TestJavaEmbedded/-",
//                "read"));
//        perms.add(new FilePermission(
//                "TestApp", "read"));
        ProtectionDomain domain = new ProtectionDomain(
                new CodeSource(null,
                        (java.security.cert.Certificate[]) null),
                perms);
        return new AccessControlContext(
                new ProtectionDomain[]{domain});
    }
}
