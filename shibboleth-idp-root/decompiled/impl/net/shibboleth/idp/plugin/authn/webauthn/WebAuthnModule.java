/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.shibboleth.idp.module.impl.PluginIdPModule
 *  net.shibboleth.profile.module.ModuleException
 */
package net.shibboleth.idp.plugin.authn.webauthn;

import java.io.IOException;
import net.shibboleth.idp.module.impl.PluginIdPModule;
import net.shibboleth.profile.module.ModuleException;

public class WebAuthnModule
extends PluginIdPModule {
    public WebAuthnModule() throws IOException, ModuleException {
        super(WebAuthnModule.class);
    }
}

