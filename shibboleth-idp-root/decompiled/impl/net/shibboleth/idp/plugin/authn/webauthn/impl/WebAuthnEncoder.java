/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonProcessingException
 *  com.yubico.webauthn.data.AuthenticatorTransport
 *  com.yubico.webauthn.data.PublicKeyCredentialCreationOptions
 *  com.yubico.webauthn.data.PublicKeyCredentialRequestOptions
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord
 *  net.shibboleth.shared.annotation.constraint.NotEmpty
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yubico.webauthn.data.AuthenticatorTransport;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.PublicKeyCredentialRequestOptions;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.slf4j.Logger;

public final class WebAuthnEncoder {
    @Nonnull
    private static final Logger LOG = LoggerFactory.getLogger(WebAuthnEncoder.class);

    private WebAuthnEncoder() {
    }

    public static String serializePublicKeyCredentialOptionsAsJSON(@Nullable PublicKeyCredentialCreationOptions options) {
        if (options != null) {
            try {
                return options.toCredentialsCreateJson();
            }
            catch (JsonProcessingException e) {
                LOG.debug("Unable to serialize PublicKeyCredentialOptions", (Throwable)e);
            }
        }
        return "";
    }

    public static String serializePublicKeyCredentialRequestOptionsAsJSON(@Nullable PublicKeyCredentialRequestOptions options) {
        if (options != null) {
            try {
                return options.toCredentialsGetJson();
            }
            catch (JsonProcessingException e) {
                LOG.debug("Unable to serialize PublicKeyCredentialOptions", (Throwable)e);
            }
        }
        return "";
    }

    @Nonnull
    @NotEmpty
    public static String formatInstant(@Nullable Instant time) {
        if (time == null) {
            return "";
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm").withZone(ZoneId.systemDefault());
            String isoFormat = formatter.format(time);
            assert (isoFormat != null);
            return isoFormat;
        }
        catch (Exception e) {
            return "";
        }
    }

    @Nonnull
    public static String formatDiscoverable(@Nullable Optional<Boolean> discoverable) {
        String formatted;
        if (discoverable == null) {
            return "not-set";
        }
        String string = formatted = discoverable.isEmpty() ? "unknown" : Boolean.toString(discoverable.get());
        assert (formatted != null);
        return formatted;
    }

    public static boolean isAuthenticatorMetadataAttached(EnhancedCredentialRecord cred) {
        if (cred == null) {
            return false;
        }
        return !cred.getAuthenticatorMetadata().isEmpty();
    }

    @Nonnull
    public static String formatTransports(@Nullable Set<AuthenticatorTransport> transports) {
        if (transports == null) {
            return "";
        }
        return transports.stream().map(AuthenticatorTransport::getId).collect(Collectors.joining(","));
    }
}

