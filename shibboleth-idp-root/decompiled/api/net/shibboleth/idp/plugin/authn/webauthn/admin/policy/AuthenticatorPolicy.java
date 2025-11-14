/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.fido.metadata.AAGUID
 *  javax.annotation.Nonnull
 *  net.shibboleth.shared.component.IdentifiedComponent
 *  org.opensaml.profile.context.ProfileRequestContext
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.policy;

import com.yubico.fido.metadata.AAGUID;
import javax.annotation.Nonnull;
import net.shibboleth.shared.component.IdentifiedComponent;
import org.opensaml.profile.context.ProfileRequestContext;

public interface AuthenticatorPolicy
extends IdentifiedComponent {
    public AuthenticatorPolicyOutcome evaluate(@Nonnull AAGUID var1, @Nonnull ProfileRequestContext var2);

    public static enum AuthenticatorPolicyOutcome {
        ACCEPT,
        REJECT,
        IGNORE;


        @Nonnull
        public static AuthenticatorPolicyOutcome of(boolean outcome) {
            return outcome ? ACCEPT : REJECT;
        }
    }
}

