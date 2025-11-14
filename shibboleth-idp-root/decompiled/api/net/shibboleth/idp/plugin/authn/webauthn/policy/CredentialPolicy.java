/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.shibboleth.shared.component.IdentifiedComponent
 *  org.opensaml.profile.context.ProfileRequestContext
 */
package net.shibboleth.idp.plugin.authn.webauthn.policy;

import javax.annotation.Nonnull;
import net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord;
import net.shibboleth.shared.component.IdentifiedComponent;
import org.opensaml.profile.context.ProfileRequestContext;

public interface CredentialPolicy
extends IdentifiedComponent {
    public CredentialPolicyOutcome evaluate(@Nonnull EnhancedCredentialRecord var1, @Nonnull ProfileRequestContext var2);

    public static enum CredentialPolicyOutcome {
        ACCEPT,
        REJECT,
        IGNORE;


        @Nonnull
        public static CredentialPolicyOutcome of(boolean outcome) {
            return outcome ? ACCEPT : REJECT;
        }
    }
}

