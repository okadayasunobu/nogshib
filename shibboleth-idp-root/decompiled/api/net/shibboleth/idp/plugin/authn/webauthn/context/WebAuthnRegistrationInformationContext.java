/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.shared.annotation.constraint.Live
 *  net.shibboleth.shared.annotation.constraint.NotEmpty
 *  org.opensaml.messaging.context.BaseContext
 */
package net.shibboleth.idp.plugin.authn.webauthn.context;

import java.util.Collection;
import java.util.LinkedHashSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import org.opensaml.messaging.context.BaseContext;

public class WebAuthnRegistrationInformationContext
extends BaseContext {
    @Nonnull
    private final Collection<String> classifiedMessages = new LinkedHashSet<String>();

    @Nonnull
    @Live
    public Collection<String> getClassifiedMessages() {
        return this.classifiedMessages;
    }

    public boolean isClassifiedMessage(@Nonnull @NotEmpty String msg) {
        return this.classifiedMessages.contains(msg);
    }

    @Nonnull
    public WebAuthnRegistrationInformationContext addClassifiedMessage(@Nonnull @NotEmpty String msg) {
        this.classifiedMessages.remove(msg);
        this.classifiedMessages.add(msg);
        return this;
    }

    @Nullable
    public String getLastClassifiedMessage() {
        return this.classifiedMessages.stream().reduce((first, second) -> second).orElse(null);
    }

    public void reset() {
        this.classifiedMessages.clear();
    }
}

