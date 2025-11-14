/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.shibboleth.idp.plugin.authn.webauthn.impl.WebAuthnAllowedKeyTypeValidator
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.shibboleth.idp.plugin.authn.webauthn.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebAuthnAllowedKeyTypeValidator {
    private static final Logger log = LoggerFactory.getLogger(WebAuthnAllowedKeyTypeValidator.class);
    private static final String PROPERTIES_FILE_PATH = "conf/authn/webauthn.properties";

    private String getAllowedKeyType() throws IOException {
        Properties properties = new Properties();
        String string = "/opt/shibboleth-idp";
        if (string == null || string.isEmpty()) {
            throw new IOException("System property 'idp.home' is not set.");
        }
        File file = new File(string, PROPERTIES_FILE_PATH);
        try (Object object = new FileInputStream(file);){
            properties.load((InputStream)object);
        }
        object = properties.getProperty("idp.authn.webauthn.allowedKeyTypes");
        if (object == null || ((String)object).isEmpty()) {
            log.info("allowedKeyTypes not defined in properties.");
            object = "any";
        }
        return object;
    }

    public boolean isKeyTypeValid(String string) throws IOException {
        String string2 = this.getAllowedKeyType();
        log.info("Authenticator keyType is {}, Allowed keyType is {}", (Object)string, (Object)string2);
        if (string2.equals("any")) {
            log.info("allowedKeyType is any");
            return true;
        }
        if (string2.equals(string)) {
            log.info("allowedKeyType == keyType");
            return true;
        }
        log.info("allowedKeyType != keyType");
        return false;
    }
}

