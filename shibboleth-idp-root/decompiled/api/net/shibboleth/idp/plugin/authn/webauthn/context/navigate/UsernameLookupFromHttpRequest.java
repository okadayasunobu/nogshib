/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.servlet.http.HttpServletRequest
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.shared.annotation.constraint.NotEmpty
 *  net.shibboleth.shared.annotation.constraint.ThreadSafeAfterInit
 *  net.shibboleth.shared.component.AbstractIdentifiableInitializableComponent
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.primitive.NonnullSupplier
 *  net.shibboleth.shared.primitive.StringSupport
 *  org.opensaml.profile.context.ProfileRequestContext
 */
package net.shibboleth.idp.plugin.authn.webauthn.context.navigate;

import jakarta.servlet.http.HttpServletRequest;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.annotation.constraint.ThreadSafeAfterInit;
import net.shibboleth.shared.component.AbstractIdentifiableInitializableComponent;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.NonnullSupplier;
import net.shibboleth.shared.primitive.StringSupport;
import org.opensaml.profile.context.ProfileRequestContext;

@ThreadSafeAfterInit
public class UsernameLookupFromHttpRequest
extends AbstractIdentifiableInitializableComponent
implements Function<ProfileRequestContext, String> {
    @Nonnull
    @NotEmpty
    private String usernameFieldName = "j_username";
    @Nullable
    private NonnullSupplier<HttpServletRequest> httpServletRequestSupplier;

    @Nullable
    public HttpServletRequest getHttpServletRequest() {
        this.checkComponentActive();
        if (this.httpServletRequestSupplier != null) {
            return (HttpServletRequest)this.httpServletRequestSupplier.get();
        }
        return null;
    }

    public void setHttpServletRequestSupplier(@Nullable NonnullSupplier<HttpServletRequest> requestSupplier) {
        this.checkSetterPreconditions();
        this.httpServletRequestSupplier = requestSupplier;
    }

    public void setUsernameFieldName(@Nonnull String name) {
        this.checkSetterPreconditions();
        this.usernameFieldName = (String)Constraint.isNotNull((Object)StringSupport.trimOrNull((String)name), (String)"Username form field name cannot be null or empty");
    }

    @Override
    public String apply(@Nullable ProfileRequestContext prc) {
        this.checkComponentActive();
        HttpServletRequest request = this.getHttpServletRequest();
        if (request != null) {
            return request.getParameter(this.usernameFieldName);
        }
        return null;
    }
}

