/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.ThreadSafe
 *  net.shibboleth.idp.attribute.IdPAttribute
 *  net.shibboleth.idp.attribute.IdPAttributeValue
 *  net.shibboleth.idp.attribute.StringAttributeValue
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.context.navigate;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import net.shibboleth.idp.attribute.IdPAttribute;
import net.shibboleth.idp.attribute.IdPAttributeValue;
import net.shibboleth.idp.attribute.StringAttributeValue;
import net.shibboleth.idp.plugin.authn.webauthn.context.navigate.AbstractAttributeContextUserIdentityStrategy;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

@ThreadSafe
public class AttributeContextStringLookupStrategy
extends AbstractAttributeContextUserIdentityStrategy<String> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(AttributeContextStringLookupStrategy.class);

    @Override
    @Nullable
    public String apply(ProfileRequestContext profileRequestContext) {
        this.checkComponentActive();
        if (profileRequestContext == null) {
            return null;
        }
        IdPAttribute attribute = this.getAttribute(profileRequestContext);
        if (attribute == null) {
            return null;
        }
        List values = attribute.getValues();
        if (values.size() != 1) {
            this.log.warn("{}: Attribute '{}' has more than one value", (Object)this.getId(), (Object)this.getAttributeId());
            return null;
        }
        IdPAttributeValue value = (IdPAttributeValue)values.get(0);
        if (value instanceof StringAttributeValue) {
            StringAttributeValue strValue = (StringAttributeValue)value;
            this.log.debug("{}: Found attribute '{}' with value '{}'", new Object[]{this.getId(), attribute.getId(), strValue.getValue()});
            return strValue.getValue();
        }
        this.log.warn("{}: Attribute '{}' could not be found", (Object)this.getId(), (Object)this.getAttributeId());
        return null;
    }
}

