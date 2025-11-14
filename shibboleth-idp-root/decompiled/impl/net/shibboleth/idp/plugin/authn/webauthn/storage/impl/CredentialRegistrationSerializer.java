/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonInclude$Include
 *  com.fasterxml.jackson.core.Base64Variants
 *  com.fasterxml.jackson.core.type.TypeReference
 *  com.fasterxml.jackson.databind.DeserializationFeature
 *  com.fasterxml.jackson.databind.Module
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.json.JsonMapper
 *  com.fasterxml.jackson.databind.json.JsonMapper$Builder
 *  com.fasterxml.jackson.datatype.jdk8.Jdk8Module
 *  com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.CredentialRecord
 *  net.shibboleth.shared.annotation.constraint.NotEmpty
 *  net.shibboleth.shared.annotation.constraint.NotLive
 *  net.shibboleth.shared.annotation.constraint.Unmodifiable
 *  net.shibboleth.shared.collection.CollectionSupport
 *  net.shibboleth.shared.component.AbstractInitializableComponent
 *  org.opensaml.storage.StorageSerializer
 */
package net.shibboleth.idp.plugin.authn.webauthn.storage.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nonnull;
import net.shibboleth.idp.plugin.authn.webauthn.storage.CredentialRecord;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.AbstractInitializableComponent;
import org.opensaml.storage.StorageSerializer;

public class CredentialRegistrationSerializer
extends AbstractInitializableComponent
implements StorageSerializer<Set<CredentialRecord>> {
    private final ObjectMapper jsonMapper = ((JsonMapper.Builder)((JsonMapper.Builder)((JsonMapper.Builder)((JsonMapper.Builder)((JsonMapper.Builder)JsonMapper.builder().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)).serializationInclusion(JsonInclude.Include.NON_ABSENT)).defaultBase64Variant(Base64Variants.MODIFIED_FOR_URL)).addModule((Module)new Jdk8Module())).addModule((Module)new JavaTimeModule())).build();

    @Nonnull
    @NotEmpty
    public String serialize(Set<CredentialRecord> instance) throws IOException {
        this.checkComponentActive();
        String valueAsString = this.jsonMapper.writeValueAsString(instance);
        if (valueAsString == null) {
            throw new IOException("Unable to serialize credential registration collection");
        }
        return valueAsString;
    }

    @Nonnull
    @Unmodifiable
    @NotLive
    public Set<CredentialRecord> deserialize(long version, String context, String key, String value, Long expiration) throws IOException {
        this.checkComponentActive();
        try {
            Set registrations = (Set)this.jsonMapper.readValue(value, (TypeReference)new TypeReference<Set<CredentialRecord>>(){});
            if (registrations == null) {
                throw new IOException("Unable to read credential registrations");
            }
            return CollectionSupport.copyToSet((Collection)registrations);
        }
        catch (Exception e) {
            throw new IOException("Error reading JSON string into a Credential Registration", e);
        }
    }
}

