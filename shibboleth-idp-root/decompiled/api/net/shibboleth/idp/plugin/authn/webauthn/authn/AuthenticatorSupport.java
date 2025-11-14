/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.webauthn.data.ByteArray
 *  com.yubico.webauthn.data.exception.HexException
 *  javax.annotation.Nullable
 */
package net.shibboleth.idp.plugin.authn.webauthn.authn;

import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.exception.HexException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public final class AuthenticatorSupport {
    private static final Pattern AAGUID_PATTERN = Pattern.compile("^([0-9a-fA-F]{8})-?([0-9a-fA-F]{4})-?([0-9a-fA-F]{4})-?([0-9a-fA-F]{4})-?([0-9a-fA-F]{12})$");

    private AuthenticatorSupport() {
    }

    @Nullable
    public static ByteArray parse(String value) {
        Matcher matcher = AAGUID_PATTERN.matcher(value);
        if (matcher.find()) {
            try {
                return ByteArray.fromHex((String)matcher.group(1)).concat(ByteArray.fromHex((String)matcher.group(2))).concat(ByteArray.fromHex((String)matcher.group(3))).concat(ByteArray.fromHex((String)matcher.group(4))).concat(ByteArray.fromHex((String)matcher.group(5)));
            }
            catch (HexException e) {
                return null;
            }
        }
        return null;
    }
}

