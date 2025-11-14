/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.authn.principal.CloneablePrincipal
 *  net.shibboleth.shared.annotation.ParameterName
 *  net.shibboleth.shared.annotation.constraint.NotEmpty
 *  net.shibboleth.shared.codec.Base64Support
 *  net.shibboleth.shared.codec.EncodingException
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.logic.ConstraintViolationException
 */
package net.shibboleth.idp.plugin.authn.webauthn.principal;

import com.google.common.base.MoreObjects;
import javax.annotation.Nonnull;
import net.shibboleth.idp.authn.principal.CloneablePrincipal;
import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.codec.Base64Support;
import net.shibboleth.shared.codec.EncodingException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.logic.ConstraintViolationException;

public class WebAuthnUserIdPrinicpal
implements CloneablePrincipal {
    @Nonnull
    @NotEmpty
    private String userIdBase64Encoded;

    public WebAuthnUserIdPrinicpal(@Nonnull @ParameterName(name="userId") @NotEmpty byte[] id) {
        Constraint.isNotNull((Object)id, (String)"User.id cannot be null or empty");
        try {
            this.userIdBase64Encoded = Base64Support.encode((byte[])id, (boolean)false);
        }
        catch (EncodingException e) {
            throw new ConstraintViolationException("User.id can not be base64 encoded");
        }
    }

    public WebAuthnUserIdPrinicpal(@Nonnull @ParameterName(name="userId") @NotEmpty String idBase64) {
        this.userIdBase64Encoded = (String)Constraint.isNotNull((Object)idBase64, (String)"User.id cannot be null or empty");
    }

    @Nonnull
    @NotEmpty
    public String getName() {
        return this.userIdBase64Encoded;
    }

    public int hashCode() {
        return this.userIdBase64Encoded.hashCode();
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (this == other) {
            return true;
        }
        if (other instanceof WebAuthnUserIdPrinicpal) {
            WebAuthnUserIdPrinicpal otherPrincipal = (WebAuthnUserIdPrinicpal)other;
            return this.userIdBase64Encoded.equals(otherPrincipal.getName());
        }
        return false;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("userId", (Object)this.userIdBase64Encoded).toString();
    }

    @Nonnull
    public WebAuthnUserIdPrinicpal clone() throws CloneNotSupportedException {
        WebAuthnUserIdPrinicpal copy = (WebAuthnUserIdPrinicpal)super.clone();
        copy.userIdBase64Encoded = this.userIdBase64Encoded;
        return copy;
    }
}

