/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.DeserializationFeature
 *  com.fasterxml.jackson.databind.JavaType
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  net.shibboleth.idp.plugin.authn.webauthn.impl.WebAuthnMetadataService
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.shibboleth.idp.plugin.authn.webauthn.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import net.shibboleth.idp.plugin.authn.webauthn.metadata.AaguidEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebAuthnMetadataService {
    private static final Logger log = LoggerFactory.getLogger(WebAuthnMetadataService.class);
    private static final String PROPERTIES_FILE_PATH = "conf/authn/webauthn-metadata.properties";

    private String getPasskeyAaguidFilePath() throws IOException {
        Properties properties = new Properties();
        String string = "/opt/shibboleth-idp";
        if (string == null || string.isEmpty()) {
            throw new IOException("System property 'idp.home' is not set.");
        }
        File file = new File(string, PROPERTIES_FILE_PATH);
        try (Object object = new FileInputStream(file);){
            properties.load((InputStream)object);
        }
        object = properties.getProperty("idp.authn.webauthn.metadata.aaguid.passkeyAaguidFile");
        if (object == null || ((String)object).isEmpty()) {
            throw new IOException("Passkey AAGUID file path not defined in properties.");
        }
        return ((String)object).replace("%{idp.home}", string);
    }

    public AaguidEntry getAaguidMetadata(String string) throws IOException {
        String string2 = this.getPasskeyAaguidFilePath();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        File file = new File(string2);
        if (!file.exists()) {
            throw new IOException("AAGUID JSON file not found at: " + string2);
        }
        Map map = (Map)objectMapper.readValue(file, (JavaType)objectMapper.getTypeFactory().constructMapType(Map.class, String.class, AaguidEntry.class));
        AaguidEntry aaguidEntry = (AaguidEntry)map.get(string);
        if (aaguidEntry == null) {
            throw new IOException("AAGUID entry not found for " + string);
        }
        return aaguidEntry;
    }
}

