/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.ThreadSafe
 *  net.shibboleth.shared.component.ComponentInitializationException
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  net.shibboleth.shared.security.IdentifierGenerationStrategy
 *  net.shibboleth.shared.security.IdentifierGenerationStrategy$ParameterSpec
 *  net.shibboleth.shared.security.IdentifierGenerationStrategy$ProviderType
 *  net.shibboleth.shared.security.RandomIdentifierParameterSpec
 *  org.apache.commons.codec.BinaryEncoder
 *  org.apache.commons.codec.DecoderException
 *  org.apache.commons.codec.binary.Hex
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.impl;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.security.IdentifierGenerationStrategy;
import net.shibboleth.shared.security.RandomIdentifierParameterSpec;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

@ThreadSafe
public final class RandomUserIdGenerator
implements Function<ProfileRequestContext, byte[]> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(RandomUserIdGenerator.class);
    private final IdentifierGenerationStrategy idGeneratorStrategy;

    public RandomUserIdGenerator() throws ComponentInitializationException {
        try {
            this.idGeneratorStrategy = IdentifierGenerationStrategy.getInstance((IdentifierGenerationStrategy.ProviderType)IdentifierGenerationStrategy.ProviderType.SECURE, (IdentifierGenerationStrategy.ParameterSpec)new RandomIdentifierParameterSpec(null, Integer.valueOf(64), (BinaryEncoder)new Hex()));
        }
        catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
            throw new ComponentInitializationException((Exception)e);
        }
    }

    @Override
    @Nullable
    public byte[] apply(ProfileRequestContext input) {
        String userIdStringHex = this.idGeneratorStrategy.generateIdentifier(false);
        try {
            return Hex.decodeHex((String)userIdStringHex);
        }
        catch (DecoderException e) {
            this.log.warn("Unable to generate user.id", (Throwable)e);
            return null;
        }
    }
}

