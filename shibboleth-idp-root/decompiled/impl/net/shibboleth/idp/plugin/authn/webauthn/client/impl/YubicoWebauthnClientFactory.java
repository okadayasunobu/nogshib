/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicates
 *  com.yubico.fido.metadata.FidoMetadataService
 *  com.yubico.webauthn.CredentialRepository
 *  com.yubico.webauthn.RelyingParty
 *  com.yubico.webauthn.RelyingParty$RelyingPartyBuilder
 *  com.yubico.webauthn.attestation.AttestationTrustSource
 *  com.yubico.webauthn.data.COSEAlgorithmIdentifier
 *  com.yubico.webauthn.data.PublicKeyCredentialParameters
 *  com.yubico.webauthn.data.RelyingPartyIdentity
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.GuardedBy
 *  javax.annotation.concurrent.ThreadSafe
 *  net.shibboleth.idp.plugin.authn.webauthn.client.WebAuthnAuthenticationClient
 *  net.shibboleth.shared.annotation.constraint.NonnullAfterInit
 *  net.shibboleth.shared.annotation.constraint.NonnullElements
 *  net.shibboleth.shared.annotation.constraint.NotLive
 *  net.shibboleth.shared.collection.CollectionSupport
 *  net.shibboleth.shared.component.AbstractInitializableComponent
 *  net.shibboleth.shared.component.ComponentInitializationException
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  net.shibboleth.shared.primitive.NonnullSupplier
 *  net.shibboleth.shared.primitive.StringSupport
 *  org.slf4j.Logger
 *  org.springframework.beans.factory.FactoryBean
 */
package net.shibboleth.idp.plugin.authn.webauthn.client.impl;

import com.google.common.base.Predicates;
import com.yubico.fido.metadata.FidoMetadataService;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.attestation.AttestationTrustSource;
import com.yubico.webauthn.data.COSEAlgorithmIdentifier;
import com.yubico.webauthn.data.PublicKeyCredentialParameters;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import net.shibboleth.idp.plugin.authn.webauthn.client.WebAuthnAuthenticationClient;
import net.shibboleth.idp.plugin.authn.webauthn.client.impl.YubicoWebAuthnAuthenticationClient;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.AbstractInitializableComponent;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.NonnullSupplier;
import net.shibboleth.shared.primitive.StringSupport;
import org.slf4j.Logger;
import org.springframework.beans.factory.FactoryBean;

@ThreadSafe
public class YubicoWebauthnClientFactory
extends AbstractInitializableComponent
implements FactoryBean<WebAuthnAuthenticationClient> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(YubicoWebauthnClientFactory.class);
    @GuardedBy(value="this")
    @NonnullAfterInit
    private String relyingPartyId;
    @GuardedBy(value="this")
    @NonnullAfterInit
    private String relyingPartyName;
    @GuardedBy(value="this")
    private boolean allowOriginPort = false;
    @GuardedBy(value="this")
    private boolean allowOriginSubdomain = false;
    @GuardedBy(value="this")
    @NonnullAfterInit
    private CredentialRepository credentialRepository;
    @Nonnull
    @GuardedBy(value="this")
    @NonnullElements
    private Set<String> origins = CollectionSupport.emptySet();
    @Nonnull
    @GuardedBy(value="this")
    @NonnullElements
    private List<PublicKeyCredentialParameters> preferredPublickeyParams = CollectionSupport.listOf((Object[])new PublicKeyCredentialParameters[]{PublicKeyCredentialParameters.ES256, PublicKeyCredentialParameters.EdDSA, PublicKeyCredentialParameters.ES384, PublicKeyCredentialParameters.ES512, PublicKeyCredentialParameters.RS256, PublicKeyCredentialParameters.RS384, PublicKeyCredentialParameters.RS512});
    @GuardedBy(value="this")
    private boolean allowUntrustedAttestation;
    @Nullable
    @GuardedBy(value="this")
    private FidoMetadataService fidoMetadataService;

    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        if (this.relyingPartyId == null) {
            throw new ComponentInitializationException("relyingPartyId cannot be null");
        }
        if (this.relyingPartyName == null) {
            throw new ComponentInitializationException("relyingPartyName cannot be null");
        }
        if (this.credentialRepository == null) {
            throw new ComponentInitializationException("Credential repository cannot be null");
        }
    }

    public WebAuthnAuthenticationClient getObject() throws Exception {
        this.checkComponentActive();
        RelyingParty.RelyingPartyBuilder builder = RelyingParty.builder().identity(RelyingPartyIdentity.builder().id(this.getRelyingPartyId()).name(this.getRelyingPartyName()).build()).credentialRepository(this.getCredentialRepository()).allowOriginPort(this.isAllowOriginPort()).allowOriginSubdomain(this.isAllowOriginSubdomain()).allowUntrustedAttestation(this.isAllowUntrustedAttestation()).validateSignatureCounter(this.allowOriginPort);
        FidoMetadataService localMetadataService = this.getFidoMetadataService();
        if (localMetadataService != null) {
            builder.attestationTrustSource((AttestationTrustSource)localMetadataService);
        }
        this.log.info("Built Yubico WebAuthn Client for RelyingParty '{}', using FIDO metadata '{}', allowOriginPort '{}', allowOriginSubdomain '{}', allowUntrustedMetadata '{}'", new Object[]{this.getRelyingPartyId(), localMetadataService != null ? "yes" : "no", this.isAllowOriginPort(), this.isAllowOriginSubdomain(), this.isAllowUntrustedAttestation()});
        if (!this.getOrigins().isEmpty()) {
            RelyingParty rp = builder.origins(this.getOrigins()).build();
            assert (rp != null);
            return new YubicoWebAuthnAuthenticationClient(rp, this.getPreferredPublickeyParams());
        }
        RelyingParty rp = builder.build();
        assert (rp != null);
        return new YubicoWebAuthnAuthenticationClient(rp, this.getPreferredPublickeyParams());
    }

    public synchronized void setPreferredPublickeyParamsNative(@Nonnull @NonnullElements List<PublicKeyCredentialParameters> publickeyParams) {
        this.checkSetterPreconditions();
        this.preferredPublickeyParams = (List)Constraint.isNotNull((Object)((List)((NonnullSupplier)publickeyParams.stream().filter(Objects::nonNull).collect(CollectionSupport.nonnullCollector(Collectors.toList()))).get()), (String)"PreferredPublickeyParams can not be null");
    }

    public synchronized void setPreferredPublickeyParams(@Nonnull @NonnullElements List<String> publickeyParams) {
        this.checkSetterPreconditions();
        Collection publicKeyParamsNormalized = StringSupport.normalizeStringCollection(publickeyParams);
        this.preferredPublickeyParams = CollectionSupport.copyToList((Collection)((Collection)((NonnullSupplier)publicKeyParamsNormalized.stream().map(coseAlg -> {
            switch (coseAlg) {
                case "EdDSA": {
                    return PublicKeyCredentialParameters.builder().alg(COSEAlgorithmIdentifier.EdDSA).build();
                }
                case "ES256": {
                    return PublicKeyCredentialParameters.builder().alg(COSEAlgorithmIdentifier.ES256).build();
                }
                case "ES384": {
                    return PublicKeyCredentialParameters.builder().alg(COSEAlgorithmIdentifier.ES384).build();
                }
                case "ES512": {
                    return PublicKeyCredentialParameters.builder().alg(COSEAlgorithmIdentifier.ES512).build();
                }
                case "RS1": {
                    return PublicKeyCredentialParameters.builder().alg(COSEAlgorithmIdentifier.RS1).build();
                }
                case "RS256": {
                    return PublicKeyCredentialParameters.builder().alg(COSEAlgorithmIdentifier.RS256).build();
                }
                case "RS384": {
                    return PublicKeyCredentialParameters.builder().alg(COSEAlgorithmIdentifier.RS384).build();
                }
                case "RS512": {
                    return PublicKeyCredentialParameters.builder().alg(COSEAlgorithmIdentifier.RS512).build();
                }
            }
            return null;
        }).filter(Objects::nonNull).collect(CollectionSupport.nonnullCollector(Collectors.toList()))).get()));
    }

    @Nonnull
    @NonnullElements
    public synchronized List<PublicKeyCredentialParameters> getPreferredPublickeyParams() {
        return this.preferredPublickeyParams;
    }

    @Nonnull
    public synchronized CredentialRepository getCredentialRepository() {
        this.checkComponentActive();
        assert (this.credentialRepository != null);
        return this.credentialRepository;
    }

    @Nonnull
    @NonnullElements
    @NotLive
    public synchronized Set<String> getOrigins() {
        return this.origins;
    }

    public synchronized void setOrigins(@Nullable Set<String> allowedOrigins) {
        this.checkSetterPreconditions();
        if (allowedOrigins == null || allowedOrigins.isEmpty()) {
            return;
        }
        this.origins = (Set)((NonnullSupplier)allowedOrigins.stream().map(StringSupport::trimOrNull).filter((Predicate<String>)Predicates.notNull()).collect(CollectionSupport.nonnullCollector(Collectors.toSet()))).get();
    }

    public synchronized void setCredentialRepository(@Nonnull CredentialRepository repository) {
        this.checkSetterPreconditions();
        this.credentialRepository = (CredentialRepository)Constraint.isNotNull((Object)repository, (String)"Credential respository can not be null");
    }

    public Class<?> getObjectType() {
        return WebAuthnAuthenticationClient.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public synchronized void setRelyingPartyId(@Nonnull String relyingPartyIdentifier) {
        this.checkSetterPreconditions();
        this.relyingPartyId = (String)Constraint.isNotNull((Object)relyingPartyIdentifier, (String)"You must set a relying party ID");
    }

    @NonnullAfterInit
    private synchronized String getRelyingPartyId() {
        return this.relyingPartyId;
    }

    public synchronized void setRelyingPartyName(@Nonnull String name) {
        this.checkSetterPreconditions();
        this.relyingPartyName = (String)Constraint.isNotNull((Object)name, (String)"You must set a relying party name");
    }

    @NonnullAfterInit
    private synchronized String getRelyingPartyName() {
        this.checkComponentActive();
        return this.relyingPartyName;
    }

    private synchronized boolean isAllowOriginPort() {
        this.checkComponentActive();
        return this.allowOriginPort;
    }

    public synchronized void setAllowOriginPort(boolean allow) {
        this.checkSetterPreconditions();
        this.allowOriginPort = allow;
    }

    private synchronized boolean isAllowOriginSubdomain() {
        this.checkComponentActive();
        return this.allowOriginSubdomain;
    }

    public synchronized void setAllowOriginSubdomain(boolean allow) {
        this.checkSetterPreconditions();
        this.allowOriginSubdomain = allow;
    }

    public synchronized void setAllowUntrustedAttestation(boolean allow) {
        this.checkSetterPreconditions();
        this.allowUntrustedAttestation = allow;
    }

    private synchronized boolean isAllowUntrustedAttestation() {
        this.checkComponentActive();
        return this.allowUntrustedAttestation;
    }

    public synchronized void setFidoMetadataService(@Nullable FidoMetadataService service) {
        this.checkSetterPreconditions();
        this.fidoMetadataService = service;
    }

    @Nullable
    public synchronized FidoMetadataService getFidoMetadataService() {
        this.checkComponentActive();
        return this.fidoMetadataService;
    }
}

