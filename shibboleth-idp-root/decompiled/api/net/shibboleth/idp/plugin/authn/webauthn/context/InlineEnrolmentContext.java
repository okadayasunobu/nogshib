/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.opensaml.messaging.context.BaseContext
 */
package net.shibboleth.idp.plugin.authn.webauthn.context;

import java.net.URL;
import javax.annotation.Nullable;
import org.opensaml.messaging.context.BaseContext;

public class InlineEnrolmentContext
extends BaseContext {
    @Nullable
    private URL ssoUrl;

    public InlineEnrolmentContext setSsoUrl(@Nullable URL url) {
        this.ssoUrl = url;
        return this;
    }

    @Nullable
    public URL getSsoUrl() {
        return this.ssoUrl;
    }
}

