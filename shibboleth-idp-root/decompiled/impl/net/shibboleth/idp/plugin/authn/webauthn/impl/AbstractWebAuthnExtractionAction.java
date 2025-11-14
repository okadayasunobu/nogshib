/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.profile.AbstractProfileAction
 *  net.shibboleth.shared.annotation.constraint.NotEmpty
 *  net.shibboleth.shared.collection.CollectionSupport
 *  net.shibboleth.shared.collection.Pair
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  net.shibboleth.shared.primitive.StringSupport
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.collection.Pair;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;
import org.slf4j.Logger;

public class AbstractWebAuthnExtractionAction
extends AbstractProfileAction {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(AbstractWebAuthnExtractionAction.class);
    @Nonnull
    private List<Pair<Pattern, String>> transforms = CollectionSupport.emptyList();
    private boolean uppercase = false;
    private boolean lowercase = false;
    private boolean trim = false;

    public void setTransforms(@Nullable Collection<Pair<String, String>> newTransforms) {
        this.checkSetterPreconditions();
        if (newTransforms != null) {
            this.transforms = new ArrayList<Pair<Pattern, String>>();
            for (Pair<String, String> p : newTransforms) {
                Pattern pattern = Pattern.compile(StringSupport.trimOrNull((String)((String)p.getFirst())));
                this.transforms.add((Pair<Pattern, String>)new Pair((Object)pattern, (Object)((String)Constraint.isNotNull((Object)StringSupport.trimOrNull((String)((String)p.getSecond())), (String)"Replacement expression cannot be null"))));
            }
        } else {
            this.transforms = CollectionSupport.emptyList();
        }
    }

    public void setUppercase(boolean flag) {
        this.checkSetterPreconditions();
        this.uppercase = flag;
    }

    public void setLowercase(boolean flag) {
        this.checkSetterPreconditions();
        this.lowercase = flag;
    }

    public void setTrim(boolean flag) {
        this.checkSetterPreconditions();
        this.trim = flag;
    }

    @Nullable
    @NotEmpty
    protected String applyTransforms(@Nullable String input) {
        if (input == null) {
            return null;
        }
        String s = input;
        if (this.trim) {
            this.log.debug("{} Trimming whitespace of input string '{}'", (Object)this.getLogPrefix(), (Object)s);
            s = s.trim();
        }
        if (this.lowercase) {
            this.log.debug("{} Converting input string '{}' to lowercase", (Object)this.getLogPrefix(), (Object)s);
            s = s.toLowerCase();
        } else if (this.uppercase) {
            this.log.debug("{} Converting input string '{}' to uppercase", (Object)this.getLogPrefix(), (Object)s);
            s = s.toUpperCase();
        }
        if (this.transforms.isEmpty()) {
            return s;
        }
        for (Pair<Pattern, String> p : this.transforms) {
            Pattern pattern = (Pattern)p.getFirst();
            if (pattern == null) continue;
            Matcher m = pattern.matcher(s);
            this.log.debug("{} Applying replacement expression '{}' against input '{}'", new Object[]{this.getLogPrefix(), pattern.pattern(), s});
            s = m.replaceAll((String)p.getSecond());
            this.log.debug("{} Result of replacement is '{}'", (Object)this.getLogPrefix(), (Object)s);
        }
        return s;
    }
}

