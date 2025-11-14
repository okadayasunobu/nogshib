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

public class WebAuthnRegistrationErrorContext
extends BaseContext {
    @Nonnull
    private final Collection<String> classifiedErrors = new LinkedHashSet<String>();

    @Nonnull
    @Live
    public Collection<String> getClassifiedErrors() {
        return this.classifiedErrors;
    }

    public boolean isClassifiedError(@Nonnull @NotEmpty String error) {
        return this.classifiedErrors.contains(error);
    }

    @Nonnull
    public WebAuthnRegistrationErrorContext addClassifiedError(@Nonnull @NotEmpty String error) {
        this.classifiedErrors.remove(error);
        this.classifiedErrors.add(error);
        return this;
    }

    @Nullable
    public String getLastClassifiedError() {
        return this.classifiedErrors.stream().reduce((first, second) -> second).orElse(null);
    }

    public void reset() {
        this.classifiedErrors.clear();
    }
}

