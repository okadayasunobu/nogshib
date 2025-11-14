/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.fido.metadata.AAGUID
 *  com.yubico.webauthn.data.AuthenticatorAssertionResponse
 *  com.yubico.webauthn.data.AuthenticatorData
 *  com.yubico.webauthn.data.AuthenticatorDataFlags
 *  com.yubico.webauthn.data.ByteArray
 *  com.yubico.webauthn.data.ClientAssertionExtensionOutputs
 *  com.yubico.webauthn.data.PublicKeyCredential
 *  com.yubico.webauthn.data.PublicKeyCredentialRequestOptions
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.authn.context.AuthenticationContext
 *  net.shibboleth.idp.authn.context.SubjectCanonicalizationContext
 *  net.shibboleth.idp.authn.impl.AbstractAuditingValidationAction
 *  net.shibboleth.idp.authn.principal.UsernamePrincipal
 *  net.shibboleth.idp.plugin.authn.webauthn.authn.AssertionResult
 *  net.shibboleth.idp.plugin.authn.webauthn.client.WebAuthnAuthenticationClient
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext
 *  net.shibboleth.idp.plugin.authn.webauthn.exception.AssertionFailureException
 *  net.shibboleth.idp.plugin.authn.webauthn.impl.WebAuthnAllowedKeyTypeValidator
 *  net.shibboleth.idp.plugin.authn.webauthn.impl.WebAuthnMetadataService
 *  net.shibboleth.idp.plugin.authn.webauthn.principal.WebAuthnUserIdPrinicpal
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.CredentialRecord
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.WebAuthnCredentialRepository
 *  net.shibboleth.shared.annotation.constraint.NonnullAfterInit
 *  net.shibboleth.shared.annotation.constraint.NonnullBeforeExec
 *  net.shibboleth.shared.component.ComponentInitializationException
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.logic.PredicateSupport
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.messaging.context.BaseContext
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.profile.action.ActionSupport
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.impl;

import com.yubico.fido.metadata.AAGUID;
import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.AuthenticatorData;
import com.yubico.webauthn.data.AuthenticatorDataFlags;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialRequestOptions;
import java.io.IOException;
import java.security.Principal;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.Subject;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.authn.context.SubjectCanonicalizationContext;
import net.shibboleth.idp.authn.impl.AbstractAuditingValidationAction;
import net.shibboleth.idp.authn.principal.UsernamePrincipal;
import net.shibboleth.idp.plugin.authn.webauthn.authn.AssertionResult;
import net.shibboleth.idp.plugin.authn.webauthn.client.WebAuthnAuthenticationClient;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext;
import net.shibboleth.idp.plugin.authn.webauthn.exception.AssertionFailureException;
import net.shibboleth.idp.plugin.authn.webauthn.impl.WebAuthnAllowedKeyTypeValidator;
import net.shibboleth.idp.plugin.authn.webauthn.impl.WebAuthnMetadataService;
import net.shibboleth.idp.plugin.authn.webauthn.metadata.AaguidEntry;
import net.shibboleth.idp.plugin.authn.webauthn.metadata.impl.PasskeyAaguidMetadataService;
import net.shibboleth.idp.plugin.authn.webauthn.principal.WebAuthnUserIdPrinicpal;
import net.shibboleth.idp.plugin.authn.webauthn.storage.CredentialRecord;
import net.shibboleth.idp.plugin.authn.webauthn.storage.WebAuthnCredentialRepository;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.annotation.constraint.NonnullBeforeExec;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.logic.PredicateSupport;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.messaging.context.BaseContext;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class ValidateWebAuthnAssertion
extends AbstractAuditingValidationAction {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(ValidateWebAuthnAssertion.class);
    @Nonnull
    private final Function<ProfileRequestContext, WebAuthnAuthenticationContext> webauthnContextLookupStrategy = new ChildContextLookup(WebAuthnAuthenticationContext.class).compose((Function)new ChildContextLookup(AuthenticationContext.class));
    @NonnullBeforeExec
    private WebAuthnAuthenticationContext context;
    @NonnullAfterInit
    private WebAuthnAuthenticationClient webAuthnClient;
    @NonnullAfterInit
    private WebAuthnCredentialRepository credentialRepository;
    @NonnullBeforeExec
    private PublicKeyCredentialRequestOptions publicKeyCredentialRequestOptions;
    @Nonnull
    private Predicate<ProfileRequestContext> updateSignatureCount = PredicateSupport.alwaysTrue();
    @Nullable
    private PasskeyAaguidMetadataService aaguidService;

    protected void doInitialize() throws ComponentInitializationException {
        if (this.webAuthnClient == null) {
            throw new ComponentInitializationException("WebAuthn client can not be null. Configuration error.");
        }
        if (this.credentialRepository == null) {
            throw new ComponentInitializationException("CredentialRepository can not be null");
        }
        super.doInitialize();
    }

    public void setCredentialRepository(@Nonnull WebAuthnCredentialRepository webAuthnCredentialRepository) {
        this.checkSetterPreconditions();
        this.credentialRepository = (WebAuthnCredentialRepository)Constraint.isNotNull((Object)webAuthnCredentialRepository, (String)"Credential respository can not be null");
    }

    public void setWebAuthnClient(@Nonnull WebAuthnAuthenticationClient webAuthnAuthenticationClient) {
        this.checkSetterPreconditions();
        this.webAuthnClient = (WebAuthnAuthenticationClient)Constraint.isNotNull((Object)webAuthnAuthenticationClient, (String)"WebAuthn client can not be null");
    }

    public void setUpdateSignatureCount(boolean bl) {
        this.checkSetterPreconditions();
        this.updateSignatureCount = bl ? PredicateSupport.alwaysTrue() : PredicateSupport.alwaysFalse();
    }

    public void setUpdateSignatureCountPredicate(@Nonnull Predicate<ProfileRequestContext> predicate) {
        this.checkSetterPreconditions();
        this.updateSignatureCount = (Predicate)Constraint.isNotNull(predicate, (String)"updateSignatureCount predicate can not be null");
    }

    protected boolean doPreExecute(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull AuthenticationContext authenticationContext) {
        this.context = this.webauthnContextLookupStrategy.apply(profileRequestContext);
        if (this.context == null) {
            this.log.warn("{} No WebAuthn context returned by lookup strategy", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidAuthenticationContext");
            return false;
        }
        this.publicKeyCredentialRequestOptions = this.context.getPublicKeyCredentialRequestOptions();
        if (this.publicKeyCredentialRequestOptions == null) {
            this.log.warn("{} No public key credential request options in context", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidAuthenticationContext");
            return false;
        }
        return true;
    }

    protected void doExecute(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull AuthenticationContext authenticationContext) {
        PublicKeyCredential publicKeyCredential = this.context.getPublicKeyCredentialAssertionResponse();
        if (publicKeyCredential == null) {
            this.log.warn("{} No PublicKeyCredential with authenticator assertion found, can not authenticate '{}'", (Object)this.getLogPrefix(), (Object)this.context.getUsername());
            this.handleError(profileRequestContext, authenticationContext, "InvalidCredentials", "InvalidCredentials");
            this.recordFailure(profileRequestContext);
            return;
        }
        try {
            PublicKeyCredentialRequestOptions publicKeyCredentialRequestOptions = this.publicKeyCredentialRequestOptions;
            assert (publicKeyCredentialRequestOptions != null);
            AssertionResult assertionResult = this.webAuthnClient.validateAuthenticatorAssertionResponse(this.context.getUsername(), this.context.getUserId(), publicKeyCredentialRequestOptions, publicKeyCredential);
            if (!assertionResult.isSuccess()) {
                throw new AssertionFailureException("Assestion was not valid");
            }
            if (!assertionResult.isSignatureCounterValid()) {
                throw new AssertionFailureException("Assestion was not valid, signature count is invalid");
            }
            if (this.updateSignatureCount.test(profileRequestContext)) {
                this.updateSignatureCount(assertionResult.getUsername(), (PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs>)publicKeyCredential);
            }
            String string = assertionResult.getUsername();
            ByteArray byteArray = publicKeyCredential.getId();
            AuthenticatorAssertionResponse authenticatorAssertionResponse = (AuthenticatorAssertionResponse)publicKeyCredential.getResponse();
            AuthenticatorData authenticatorData = authenticatorAssertionResponse.getParsedAuthenticatorData();
            AuthenticatorDataFlags authenticatorDataFlags = authenticatorData.getFlags();
            this.log.info("Flags: {}", (Object)authenticatorDataFlags);
            String string2 = authenticatorDataFlags.toString();
            boolean bl = string2.contains("AT=ture");
            this.log.info("AT Flag: {}", (Object)bl);
            Optional optional = this.credentialRepository.getRegistrationByUsernameAndCredentialId(string, byteArray);
            if (optional.isPresent()) {
                WebAuthnMetadataService webAuthnMetadataService;
                Object object;
                Object object2;
                CredentialRecord credentialRecord = (CredentialRecord)optional.get();
                ByteArray byteArray2 = null;
                if (!bl) {
                    object2 = credentialRecord.getAaguid();
                    byteArray2 = new ByteArray((byte[])object2);
                } else {
                    object2 = authenticatorData.getAttestedCredentialData();
                    object = ((Optional)object2).orElse(null);
                    byteArray2 = object.getAaguid();
                }
                object2 = new AAGUID(byteArray2);
                object = object2.toString().replace("AAGUID(", "").replace(")", "");
                this.log.info("Authenticator AAGUID: " + (AAGUID)object2);
                this.log.info("Authenticator aaguid: " + (String)object);
                String string3 = null;
                try {
                    webAuthnMetadataService = new WebAuthnMetadataService();
                    AaguidEntry aaguidEntry = webAuthnMetadataService.getAaguidMetadata((String)object);
                    String string4 = aaguidEntry.getName();
                    string3 = aaguidEntry.getType();
                    this.log.info("Authenticator Name: {}, Type: {}", (Object)string4, (Object)string3);
                }
                catch (IOException iOException) {
                    this.log.info("Error fetching metadata for AAGUID " + (String)object + ": " + iOException);
                }
                webAuthnMetadataService = new WebAuthnAllowedKeyTypeValidator();
                try {
                    if (!webAuthnMetadataService.isKeyTypeValid(string3)) {
                        this.log.info("Authenticator keyType was not valid");
                        throw new AssertionFailureException("Authenticator keyType is not valid");
                    }
                    this.log.info("Authenticator keyType was valid");
                }
                catch (IOException iOException) {
                    this.log.error("Error while validating key type", (Throwable)iOException);
                    throw new AssertionFailureException("Error while validating key type", (Throwable)iOException);
                }
            }
            this.log.info("CredentialRecord is null");
            this.log.info("{} WebAuthn authentication succeeded for '{}', authenticator verified the user '{}', userID is '{}', getId is '{}'", new Object[]{this.getLogPrefix(), assertionResult.getUsername(), assertionResult.isUserVerified(), assertionResult.getUserId(), byteArray});
            this.context.setUsername(assertionResult.getUsername());
            this.context.setUserId(assertionResult.getUserId());
            this.buildAuthenticationResult(profileRequestContext, authenticationContext);
            this.recordSuccess(profileRequestContext);
        }
        catch (AssertionFailureException assertionFailureException) {
            this.log.warn("{} Error validating authenticator assertion for '{}'", new Object[]{this.getLogPrefix(), this.context.getUsername() != null ? this.context.getUsername() : "unknown user", assertionFailureException});
            this.handleError(profileRequestContext, authenticationContext, "InvalidCredentials", "InvalidCredentials");
            this.recordFailure(profileRequestContext);
            return;
        }
    }

    private void updateSignatureCount(@Nonnull String string, @Nonnull PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> publicKeyCredential) throws AssertionFailureException {
        ByteArray byteArray = publicKeyCredential.getId();
        if (byteArray == null) {
            throw new AssertionFailureException("Can not update signature count for user '" + string + "' and credential '" + publicKeyCredential.getId() + "'. Assertion goes not contain the credential Id.");
        }
        long l = ((AuthenticatorAssertionResponse)publicKeyCredential.getResponse()).getParsedAuthenticatorData().getSignatureCounter();
        if (!this.credentialRepository.updateSignatureCounter(string, byteArray, l)) {
            throw new AssertionFailureException("Failed to update signature counter");
        }
    }

    protected void buildAuthenticationResult(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull AuthenticationContext authenticationContext) {
        super.buildAuthenticationResult(profileRequestContext, authenticationContext);
        ((SubjectCanonicalizationContext)profileRequestContext.ensureSubcontext(SubjectCanonicalizationContext.class)).setPrincipalName(this.context.getUsername());
    }

    protected Subject populateSubject(@Nonnull Subject subject) {
        byte[] byArray = this.context.getUserId();
        if (byArray != null) {
            subject.getPrincipals().add((Principal)new WebAuthnUserIdPrinicpal(byArray));
        }
        if (this.context.isSecondFactor() && subject.getPrincipals(UsernamePrincipal.class) != null) {
            this.log.trace("{} second factor usage, username principal already set", (Object)this.getLogPrefix());
            return subject;
        }
        String string = this.context.getUsername();
        assert (string != null);
        subject.getPrincipals().add((Principal)new UsernamePrincipal(string));
        return subject;
    }

    public static class WebAuthnCleanupHook
    implements Consumer<ProfileRequestContext> {
        @Override
        public void accept(ProfileRequestContext input) {
            AuthenticationContext authnCtx;
            AuthenticationContext authenticationContext = authnCtx = input != null ? (AuthenticationContext)input.getSubcontext(AuthenticationContext.class) : null;
            if (authnCtx == null) {
                return;
            }
            WebAuthnAuthenticationContext webAuthnCtx = (WebAuthnAuthenticationContext)authnCtx.getSubcontext(WebAuthnAuthenticationContext.class);
            if (webAuthnCtx == null) {
                return;
            }
            authnCtx.removeSubcontext((BaseContext)webAuthnCtx);
        }
    }
}

