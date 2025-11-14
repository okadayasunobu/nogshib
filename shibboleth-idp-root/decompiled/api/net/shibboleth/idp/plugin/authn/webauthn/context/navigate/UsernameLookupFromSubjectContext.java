/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.ThreadSafe
 *  net.shibboleth.idp.authn.context.SubjectContext
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.context.navigate;

import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import net.shibboleth.idp.authn.context.SubjectContext;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

@ThreadSafe
public class UsernameLookupFromSubjectContext
implements Function<ProfileRequestContext, String> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(UsernameLookupFromSubjectContext.class);

    @Override
    public String apply(@Nullable ProfileRequestContext input) {
        if (input == null) {
            this.log.trace("Profile context was null, can not find existing username");
            return null;
        }
        SubjectContext subjectContext = (SubjectContext)input.getSubcontext(SubjectContext.class);
        if (subjectContext == null) {
            this.log.trace("Subject context was null, can not find existing username");
            return null;
        }
        String username = subjectContext.getPrincipalName();
        this.log.trace("Found existing username '{}' from subject context", (Object)username);
        return username;
    }
}

