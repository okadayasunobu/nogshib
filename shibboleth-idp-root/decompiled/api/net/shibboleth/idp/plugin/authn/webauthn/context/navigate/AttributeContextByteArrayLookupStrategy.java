/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.attribute.IdPAttribute
 *  net.shibboleth.idp.attribute.IdPAttributeValue
 *  net.shibboleth.idp.attribute.StringAttributeValue
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.context.navigate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.annotation.Nonnull;
import net.shibboleth.idp.attribute.IdPAttribute;
import net.shibboleth.idp.attribute.IdPAttributeValue;
import net.shibboleth.idp.attribute.StringAttributeValue;
import net.shibboleth.idp.plugin.authn.webauthn.context.navigate.AbstractAttributeContextUserIdentityStrategy;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class AttributeContextByteArrayLookupStrategy
extends AbstractAttributeContextUserIdentityStrategy<byte[]> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(AttributeContextByteArrayLookupStrategy.class);

    @Override
    @Nonnull
    public byte[] apply(ProfileRequestContext profileRequestContext) {
        this.checkComponentActive();
        if (profileRequestContext == null) {
            return new byte[0];
        }
        IdPAttribute attribute = this.getAttribute(profileRequestContext);
        if (attribute == null) {
            return new byte[0];
        }
        List values = attribute.getValues();
        if (values.size() != 1) {
            this.log.warn("{}: Attribute '{}' has more than one value", (Object)this.getId(), (Object)this.getAttributeId());
            return new byte[0];
        }
        IdPAttributeValue value = (IdPAttributeValue)values.get(0);
        if (value instanceof StringAttributeValue) {
            StringAttributeValue strValue = (StringAttributeValue)value;
            this.log.debug("{}: Found attribute '{}' with value '{}'", new Object[]{this.getId(), attribute.getId(), strValue.getValue()});
            String valueAsString = strValue.getValue();
            byte[] valueAsBytes = valueAsString.getBytes(StandardCharsets.UTF_8);
            return valueAsBytes != null ? valueAsBytes : new byte[]{};
        }
        this.log.warn("{}: Attribute '{}' could not be found", (Object)this.getId(), (Object)this.getAttributeId());
        return new byte[0];
    }
}

