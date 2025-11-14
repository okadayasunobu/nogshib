/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.authn.context.AuthenticationContext
 *  net.shibboleth.idp.plugin.authn.webauthn.context.BaseWebAuthnContext
 *  net.shibboleth.shared.annotation.constraint.NonnullAfterInit
 *  net.shibboleth.shared.component.ComponentInitializationException
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  net.shibboleth.shared.security.IdentifierGenerationStrategy
 *  net.shibboleth.shared.security.IdentifierGenerationStrategy$ParameterSpec
 *  net.shibboleth.shared.security.IdentifierGenerationStrategy$ProviderType
 *  net.shibboleth.shared.security.RandomIdentifierParameterSpec
 *  org.apache.commons.codec.BinaryEncoder
 *  org.apache.commons.codec.DecoderException
 *  org.apache.commons.codec.binary.Hex
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.profile.action.ActionSupport
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.impl;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.plugin.authn.webauthn.context.BaseWebAuthnContext;
import net.shibboleth.idp.plugin.authn.webauthn.impl.AbstractWebAuthnAction;
import net.shibboleth.idp.plugin.authn.webauthn.impl.WebAuthnSupport;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.security.IdentifierGenerationStrategy;
import net.shibboleth.shared.security.RandomIdentifierParameterSpec;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class GenerateServerChallenge
extends AbstractWebAuthnAction<BaseWebAuthnContext> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(GenerateServerChallenge.class);
    @NonnullAfterInit
    private Function<ProfileRequestContext, byte[]> challengeGeneratorStrategy;

    public GenerateServerChallenge() {
        super(new ChildContextLookup(BaseWebAuthnContext.class).compose((Function)new ChildContextLookup(AuthenticationContext.class)));
    }

    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        if (this.challengeGeneratorStrategy == null) {
            this.challengeGeneratorStrategy = new DefaultChallengeGenerator();
        }
    }

    public void setChallengeGeneratorStrategy(@Nullable Function<ProfileRequestContext, byte[]> strategy) {
        this.checkSetterPreconditions();
        if (strategy != null) {
            this.challengeGeneratorStrategy = strategy;
        }
    }

    @Override
    protected void doExecute(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull BaseWebAuthnContext context) {
        this.log.trace("{} Attempting challenge generation", (Object)this.getLogPrefix());
        byte[] challenge = this.challengeGeneratorStrategy.apply(profileRequestContext);
        if (challenge == null) {
            this.log.trace("{} Generated challenge was null", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidProfileContext");
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("{} Generated server challenge {} of size '{} bytes'", new Object[]{this.getLogPrefix(), WebAuthnSupport.toBase64OrUnknown(challenge), challenge.length});
        }
        context.setServerChallenge(challenge);
    }

    private static final class DefaultChallengeGenerator
    implements Function<ProfileRequestContext, byte[]> {
        @Nonnull
        private final Logger log = LoggerFactory.getLogger(DefaultChallengeGenerator.class);
        private final IdentifierGenerationStrategy challengeGenerator;

        public DefaultChallengeGenerator() throws ComponentInitializationException {
            try {
                this.challengeGenerator = IdentifierGenerationStrategy.getInstance((IdentifierGenerationStrategy.ProviderType)IdentifierGenerationStrategy.ProviderType.SECURE, (IdentifierGenerationStrategy.ParameterSpec)new RandomIdentifierParameterSpec(null, Integer.valueOf(32), (BinaryEncoder)new Hex()));
            }
            catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
                throw new ComponentInitializationException((Exception)e);
            }
        }

        @Override
        @Nullable
        public byte[] apply(@Nullable ProfileRequestContext input) {
            if (this.challengeGenerator == null) {
                this.log.error("Unable to generate challenge, random number generator is null");
                return null;
            }
            String challengeHexString = this.challengeGenerator.generateIdentifier(false);
            try {
                return Hex.decodeHex((String)challengeHexString);
            }
            catch (DecoderException e) {
                this.log.warn("Unable to generate challenge", (Throwable)e);
                return null;
            }
        }
    }
}

