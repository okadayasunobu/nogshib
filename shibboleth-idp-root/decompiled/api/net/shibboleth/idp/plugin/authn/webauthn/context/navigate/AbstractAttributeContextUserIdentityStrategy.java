/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.attribute.IdPAttribute
 *  net.shibboleth.idp.attribute.context.AttributeContext
 *  net.shibboleth.profile.context.RelyingPartyContext
 *  net.shibboleth.shared.annotation.constraint.NotEmpty
 *  net.shibboleth.shared.component.AbstractIdentifiableInitializableComponent
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.context.navigate;

import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.attribute.IdPAttribute;
import net.shibboleth.idp.attribute.context.AttributeContext;
import net.shibboleth.profile.context.RelyingPartyContext;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.component.AbstractIdentifiableInitializableComponent;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public abstract class AbstractAttributeContextUserIdentityStrategy<T>
extends AbstractIdentifiableInitializableComponent
implements Function<ProfileRequestContext, T> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(AbstractAttributeContextUserIdentityStrategy.class);
    @Nonnull
    private Function<ProfileRequestContext, AttributeContext> attributeContextLookupStrategy;
    @Nullable
    @NotEmpty
    private String attributeId;
    private boolean useUnfilteredAttributes;

    protected AbstractAttributeContextUserIdentityStrategy() {
        Function acls = new ChildContextLookup(AttributeContext.class).compose((Function)new ChildContextLookup(RelyingPartyContext.class));
        assert (acls != null);
        this.attributeContextLookupStrategy = acls;
        this.useUnfilteredAttributes = true;
    }

    public boolean isUseUnfilteredAttributes() {
        this.checkComponentActive();
        return this.useUnfilteredAttributes;
    }

    public void setUseUnfilteredAttributes(boolean flag) {
        this.checkSetterPreconditions();
        this.useUnfilteredAttributes = flag;
    }

    public void setAttributeId(@Nullable String id) {
        this.checkSetterPreconditions();
        this.attributeId = id;
    }

    @Nullable
    @NotEmpty
    protected String getAttributeId() {
        this.checkComponentActive();
        return this.attributeId;
    }

    public void setAttributeContextLookupStrategy(@Nonnull Function<ProfileRequestContext, AttributeContext> strategy) {
        this.checkSetterPreconditions();
        this.attributeContextLookupStrategy = (Function)Constraint.isNotNull(strategy, (String)"AttributeContext lookup strategy cannot be null");
    }

    @Nullable
    protected IdPAttribute getAttribute(@Nonnull ProfileRequestContext profileRequestContext) {
        Map attributes;
        this.checkComponentActive();
        AttributeContext attrContext = this.attributeContextLookupStrategy.apply(profileRequestContext);
        if (attrContext == null) {
            this.log.trace("{}: Attribute '{}' could not be found, no attribute context", (Object)this.getId(), (Object)this.attributeId);
            return null;
        }
        Map map = attributes = this.useUnfilteredAttributes ? attrContext.getUnfilteredIdPAttributes() : attrContext.getIdPAttributes();
        if (this.attributeId != null) {
            for (IdPAttribute attribute : attributes.values()) {
                if (attribute == null || attribute.getValues().isEmpty() || !attribute.getId().equals(this.attributeId)) continue;
                return attribute;
            }
            this.log.trace("{}: Attribute '{}' could not be found", (Object)this.getId(), (Object)this.attributeId);
            return null;
        }
        this.log.trace("{}: Attribute to find has not been set", (Object)this.getId());
        return null;
    }
}

