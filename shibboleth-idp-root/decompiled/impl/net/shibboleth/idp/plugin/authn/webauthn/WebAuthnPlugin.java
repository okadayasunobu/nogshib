/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.plugin.IdPPlugin
 *  net.shibboleth.idp.plugin.impl.FirstPartyIdPPlugin
 *  net.shibboleth.profile.module.ModuleException
 *  net.shibboleth.profile.plugin.PluginException
 */
package net.shibboleth.idp.plugin.authn.webauthn;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nonnull;
import net.shibboleth.idp.plugin.IdPPlugin;
import net.shibboleth.idp.plugin.authn.webauthn.WebAuthnModule;
import net.shibboleth.idp.plugin.impl.FirstPartyIdPPlugin;
import net.shibboleth.profile.module.ModuleException;
import net.shibboleth.profile.plugin.PluginException;

public class WebAuthnPlugin
extends FirstPartyIdPPlugin {
    public WebAuthnPlugin(@Nonnull Class<? extends IdPPlugin> claz) throws IOException, PluginException {
        super(claz);
    }

    public WebAuthnPlugin() throws IOException, PluginException {
        super(WebAuthnPlugin.class);
        try {
            WebAuthnModule module = new WebAuthnModule();
            Set<WebAuthnModule> moduleAsCollection = Collections.singleton(module);
            assert (moduleAsCollection != null);
            this.setEnableOnInstall(moduleAsCollection);
            this.setDisableOnRemoval(moduleAsCollection);
        }
        catch (IOException e) {
            throw e;
        }
        catch (ModuleException e) {
            throw new PluginException((Exception)((Object)e));
        }
    }
}

