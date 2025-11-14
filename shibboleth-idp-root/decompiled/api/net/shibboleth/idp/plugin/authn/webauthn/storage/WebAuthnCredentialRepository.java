/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.webauthn.CredentialRepository
 *  com.yubico.webauthn.data.ByteArray
 *  javax.annotation.Nonnull
 *  javax.annotation.concurrent.ThreadSafe
 *  net.shibboleth.shared.annotation.constraint.NonnullElements
 *  net.shibboleth.shared.annotation.constraint.NotLive
 *  net.shibboleth.shared.annotation.constraint.Unmodifiable
 */
package net.shibboleth.idp.plugin.authn.webauthn.storage;

import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.data.ByteArray;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import net.shibboleth.idp.plugin.authn.webauthn.storage.CredentialRecord;
import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;

@ThreadSafe
public interface WebAuthnCredentialRepository
extends CredentialRepository {
    @Nonnull
    @NonnullElements
    @NotLive
    public Set<CredentialRecord> getRegistrationsByUsername(@Nonnull String var1);

    public boolean addRegistrationByUsername(@Nonnull String var1, @Nonnull CredentialRecord var2);

    public boolean updateSignatureCounter(@Nonnull String var1, @Nonnull ByteArray var2, long var3);

    @Nonnull
    public Optional<CredentialRecord> getRegistrationByUsernameAndCredentialId(@Nonnull String var1, @Nonnull ByteArray var2);

    public Optional<CredentialRecord> getRegistrationByUserHandleAndCredentialId(@Nonnull ByteArray var1, @Nonnull ByteArray var2);

    public boolean removeRegistrationByUsername(@Nonnull String var1, @Nonnull CredentialRecord var2);

    public int removeRegistrationByCredentialId(@Nonnull ByteArray var1);

    @Nonnull
    @NotLive
    @Unmodifiable
    public Set<CredentialRecord> getAllRegistrations();

    public boolean removeRegistrationByUsernameAndCredentialId(@Nonnull String var1, @Nonnull ByteArray var2);
}

