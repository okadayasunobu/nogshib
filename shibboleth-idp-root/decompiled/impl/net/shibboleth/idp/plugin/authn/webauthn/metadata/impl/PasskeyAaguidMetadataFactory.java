/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.type.TypeReference
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.GuardedBy
 *  net.shibboleth.shared.component.AbstractIdentifiableInitializableComponent
 *  net.shibboleth.shared.resource.Resource
 *  org.springframework.beans.FatalBeanException
 *  org.springframework.beans.factory.FactoryBean
 */
package net.shibboleth.idp.plugin.authn.webauthn.metadata.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import net.shibboleth.idp.plugin.authn.webauthn.metadata.AaguidEntry;
import net.shibboleth.idp.plugin.authn.webauthn.metadata.impl.PasskeyAaguidMetadataService;
import net.shibboleth.shared.component.AbstractIdentifiableInitializableComponent;
import net.shibboleth.shared.resource.Resource;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.FactoryBean;

public class PasskeyAaguidMetadataFactory
extends AbstractIdentifiableInitializableComponent
implements FactoryBean<PasskeyAaguidMetadataService> {
    @Nullable
    @GuardedBy(value="this")
    private Resource passkeyAaguidFile;

    public synchronized void setPasskeyAaguidFile(@Nullable Resource file) {
        this.checkSetterPreconditions();
        this.passkeyAaguidFile = file;
    }

    @Nullable
    public synchronized Resource getPasskeyAaguidFile() {
        return this.passkeyAaguidFile;
    }

    public PasskeyAaguidMetadataService getObject() throws Exception {
        ObjectMapper metadataObjMapper = new ObjectMapper();
        Resource localAaguidResource = this.getPasskeyAaguidFile();
        try {
            if (localAaguidResource != null) {
                Map metadataObject = (Map)metadataObjMapper.readValue(localAaguidResource.getFile(), (TypeReference)new TypeReference<HashMap<String, AaguidEntry>>(){});
                return new PasskeyAaguidMetadataService(metadataObject);
            }
            throw new FatalBeanException("Can not construct the Passkey AAGUID metadata provider, no JSON file specified");
        }
        catch (Exception e) {
            throw new FatalBeanException("Can not construct the Passkey AAGUID metadata provider", (Throwable)e);
        }
    }

    public Class<?> getObjectType() {
        return PasskeyAaguidMetadataService.class;
    }
}

