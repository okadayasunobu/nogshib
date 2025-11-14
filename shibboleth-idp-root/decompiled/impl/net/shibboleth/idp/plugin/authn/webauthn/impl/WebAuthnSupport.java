/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.ThreadSafe
 *  net.shibboleth.shared.codec.Base64Support
 *  net.shibboleth.shared.codec.EncodingException
 */
package net.shibboleth.idp.plugin.authn.webauthn.impl;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import net.shibboleth.shared.codec.Base64Support;
import net.shibboleth.shared.codec.EncodingException;

@ThreadSafe
public final class WebAuthnSupport {
    private WebAuthnSupport() {
    }

    public static String toBase64UrlOrUnknown(@Nullable byte[] value) {
        if (value == null) {
            return "unknown";
        }
        try {
            return Base64Support.encodeURLSafe((byte[])value);
        }
        catch (EncodingException e) {
            return "unknown";
        }
    }

    public static String toBase64OrUnknown(@Nullable byte[] value) {
        if (value == null) {
            return "unknown";
        }
        try {
            return Base64Support.encode((byte[])value, (boolean)false);
        }
        catch (EncodingException e) {
            return "unknown";
        }
    }
}

