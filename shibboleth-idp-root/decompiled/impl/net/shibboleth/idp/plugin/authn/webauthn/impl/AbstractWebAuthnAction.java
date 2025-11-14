/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.fido.metadata.AAGUID
 *  com.yubico.fido.metadata.FidoMetadataService
 *  com.yubico.fido.metadata.MetadataBLOBPayloadEntry
 *  com.yubico.webauthn.data.ByteArray
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.plugin.authn.webauthn.client.WebAuthnAuthenticationClient
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.CredentialRecord
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.WebAuthnCredentialRepository
 *  net.shibboleth.idp.profile.AbstractProfileAction
 *  net.shibboleth.shared.annotation.constraint.NonnullAfterInit
 *  net.shibboleth.shared.annotation.constraint.NonnullBeforeExec
 *  net.shibboleth.shared.annotation.constraint.NonnullElements
 *  net.shibboleth.shared.annotation.constraint.NotEmpty
 *  net.shibboleth.shared.annotation.constraint.NotLive
 *  net.shibboleth.shared.annotation.constraint.Unmodifiable
 *  net.shibboleth.shared.collection.CollectionSupport
 *  net.shibboleth.shared.component.ComponentInitializationException
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.profile.action.ActionSupport
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.impl;

import com.yubico.fido.metadata.AAGUID;
import com.yubico.fido.metadata.FidoMetadataService;
import com.yubico.fido.metadata.MetadataBLOBPayloadEntry;
import com.yubico.webauthn.data.ByteArray;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.plugin.authn.webauthn.client.WebAuthnAuthenticationClient;
import net.shibboleth.idp.plugin.authn.webauthn.metadata.AaguidEntry;
import net.shibboleth.idp.plugin.authn.webauthn.metadata.impl.PasskeyAaguidMetadataService;
import net.shibboleth.idp.plugin.authn.webauthn.storage.CredentialRecord;
import net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord;
import net.shibboleth.idp.plugin.authn.webauthn.storage.WebAuthnCredentialRepository;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.annotation.constraint.NonnullBeforeExec;
import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class AbstractWebAuthnAction<T>
extends AbstractProfileAction {
    @Nonnull
    @NotEmpty
    private final Logger log = LoggerFactory.getLogger(AbstractWebAuthnAction.class);
    @Nonnull
    private Function<ProfileRequestContext, T> webauthnContextLookupStrategy;
    @NonnullBeforeExec
    private T webauthnContext;
    @NonnullAfterInit
    private WebAuthnAuthenticationClient webAuthnClient;
    @Nullable
    private WebAuthnCredentialRepository credentialRepository;
    @Nullable
    private FidoMetadataService fidoMetadataService;
    @Nullable
    private PasskeyAaguidMetadataService aaguidService;

    protected AbstractWebAuthnAction(@Nonnull Function<ProfileRequestContext, T> defaultStrategy) {
        this.webauthnContextLookupStrategy = (Function)Constraint.isNotNull(defaultStrategy, (String)"Default WebAuthn lookup strategy can not be null");
    }

    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        if (this.webAuthnClient == null) {
            throw new ComponentInitializationException("WebAuthn Client can not be null");
        }
    }

    public void setWebAuthnClient(@Nonnull WebAuthnAuthenticationClient client) {
        this.checkSetterPreconditions();
        this.webAuthnClient = (WebAuthnAuthenticationClient)Constraint.isNotNull((Object)client, (String)"WebAuthn client can not be null");
    }

    @NonnullAfterInit
    protected WebAuthnAuthenticationClient getWebAuthnClient() {
        this.checkComponentActive();
        return this.webAuthnClient;
    }

    public void setAaguidService(@Nullable PasskeyAaguidMetadataService service) {
        this.checkSetterPreconditions();
        this.aaguidService = service;
    }

    @Nullable
    public PasskeyAaguidMetadataService getAaguidService() {
        return this.aaguidService;
    }

    public void setFidoMetadataService(@Nullable FidoMetadataService service) {
        this.checkSetterPreconditions();
        this.fidoMetadataService = service;
    }

    @Nullable
    protected FidoMetadataService getFidoMetadataService() {
        this.checkComponentActive();
        return this.fidoMetadataService;
    }

    public void setCredentialRepository(@Nonnull WebAuthnCredentialRepository repository) {
        this.checkSetterPreconditions();
        this.credentialRepository = (WebAuthnCredentialRepository)Constraint.isNotNull((Object)repository, (String)"Credential respository can not be null");
    }

    @Nullable
    protected WebAuthnCredentialRepository getCredentialRepository() {
        return this.credentialRepository;
    }

    public void setWebAuthnContextLookupStrategy(@Nonnull Function<ProfileRequestContext, T> strategy) {
        this.checkSetterPreconditions();
        this.webauthnContextLookupStrategy = (Function)Constraint.isNotNull(strategy, (String)"WebAuthnContextLookuplookup strategy cannot be null");
    }

    protected final boolean doPreExecute(@Nonnull ProfileRequestContext profileRequestContext) {
        if (!super.doPreExecute(profileRequestContext)) {
            return false;
        }
        this.webauthnContext = this.webauthnContextLookupStrategy.apply(profileRequestContext);
        if (this.webauthnContext == null) {
            this.log.warn("{} No WebAuthn context returned by lookup strategy", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidAuthenticationContext");
            return false;
        }
        assert (this.webauthnContext != null);
        return this.doPreExecute(profileRequestContext, this.webauthnContext);
    }

    protected final void doExecute(@Nonnull ProfileRequestContext profileRequestContext) {
        assert (this.webauthnContext != null);
        this.doExecute(profileRequestContext, this.webauthnContext);
    }

    protected boolean doPreExecute(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull T context) {
        return true;
    }

    protected void doExecute(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull T context) {
    }

    @Nonnull
    @NotLive
    @NonnullElements
    protected Set<MetadataBLOBPayloadEntry> getAuthenticatorMetadata(ByteArray authenticatorId) {
        FidoMetadataService localMetadataService = this.getFidoMetadataService();
        if (localMetadataService != null) {
            Set found = localMetadataService.findEntries(new AAGUID(authenticatorId));
            if (found == null) {
                return CollectionSupport.emptySet();
            }
            return CollectionSupport.copyToSet((Collection)found);
        }
        return CollectionSupport.emptySet();
    }

    @Nullable
    protected AaguidEntry getAaguidMetadata(@Nullable AAGUID aaguid) {
        if (aaguid == null) {
            return null;
        }
        PasskeyAaguidMetadataService metadataService = this.getAaguidService();
        if (metadataService != null) {
            return metadataService.getEntry(aaguid);
        }
        return null;
    }

    @Nonnull
    @NotLive
    @Unmodifiable
    protected Collection<EnhancedCredentialRecord> enhancedCredentialRecord(Collection<CredentialRecord> credentials) {
        HashSet enhancedCredentialRegistrations = new HashSet(credentials.size());
        credentials.stream().filter(Objects::nonNull).forEach(cred -> {
            assert (cred != null);
            EnhancedCredentialRecord enhancedRecord = new EnhancedCredentialRecord(cred);
            byte[] aaguid = cred.getAaguid();
            if (aaguid != null && aaguid.length == 16) {
                enhancedRecord.setAuthenticatorMetadata(this.getAuthenticatorMetadata(new ByteArray(aaguid)));
                enhancedRecord.setAaguidMetadata(this.getAaguidMetadata(new AAGUID(new ByteArray(aaguid))));
            }
            enhancedCredentialRegistrations.add(enhancedRecord);
        });
        return CollectionSupport.copyToList(enhancedCredentialRegistrations);
    }
}

