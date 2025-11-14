/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.webauthn.RegisteredCredential
 *  com.yubico.webauthn.data.ByteArray
 *  com.yubico.webauthn.data.PublicKeyCredentialDescriptor
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.plugin.authn.webauthn.exception.CredentialRepositoryException
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.CredentialRecord
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.WebAuthnCredentialRepository
 *  net.shibboleth.shared.annotation.constraint.NonnullAfterInit
 *  net.shibboleth.shared.annotation.constraint.NotEmpty
 *  net.shibboleth.shared.annotation.constraint.NotLive
 *  net.shibboleth.shared.annotation.constraint.ThreadSafeAfterInit
 *  net.shibboleth.shared.annotation.constraint.Unmodifiable
 *  net.shibboleth.shared.collection.CollectionSupport
 *  net.shibboleth.shared.component.AbstractIdentifiableInitializableComponent
 *  net.shibboleth.shared.component.ComponentInitializationException
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.logic.ConstraintViolationException
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  net.shibboleth.shared.primitive.NonnullSupplier
 *  org.opensaml.storage.EnumeratableStorageService
 *  org.opensaml.storage.StorageCapabilities
 *  org.opensaml.storage.StorageRecord
 *  org.opensaml.storage.StorageSerializer
 *  org.opensaml.storage.StorageService
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.storage.impl;

import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.plugin.authn.webauthn.exception.CredentialRepositoryException;
import net.shibboleth.idp.plugin.authn.webauthn.storage.CredentialRecord;
import net.shibboleth.idp.plugin.authn.webauthn.storage.WebAuthnCredentialRepository;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.ThreadSafeAfterInit;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.AbstractIdentifiableInitializableComponent;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.logic.ConstraintViolationException;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.NonnullSupplier;
import org.opensaml.storage.EnumeratableStorageService;
import org.opensaml.storage.StorageCapabilities;
import org.opensaml.storage.StorageRecord;
import org.opensaml.storage.StorageSerializer;
import org.opensaml.storage.StorageService;
import org.slf4j.Logger;

@ThreadSafeAfterInit
public class IdPStorageServiceCredentialRespository
extends AbstractIdentifiableInitializableComponent
implements WebAuthnCredentialRepository {
    @Nonnull
    @NotEmpty
    private static final String STORAGE_CONTEXT = "net.shibboleth.idp.plugin.authn.webauthn";
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(IdPStorageServiceCredentialRespository.class);
    @NonnullAfterInit
    private StorageSerializer<Set<CredentialRecord>> serializer;
    @NonnullAfterInit
    private EnumeratableStorageService storageService;
    @NonnullAfterInit
    private ReentrantReadWriteLock lock;

    public void setStorageService(@Nonnull StorageService service) {
        EnumeratableStorageService ess;
        this.checkSetterPreconditions();
        Constraint.isNotNull((Object)service, (String)"The Storage Service can not be null");
        if (!(service instanceof EnumeratableStorageService)) {
            throw new ConstraintViolationException("Credential repository requires an EnumeratableStorageService type");
        }
        this.storageService = ess = (EnumeratableStorageService)service;
        StorageCapabilities caps = this.storageService.getCapabilities();
        if (caps instanceof StorageCapabilities) {
            if (!caps.isServerSide()) {
                this.log.info("Use of client-side storage can make it difficult/impossible to transfer key registrations from one browser to another, which can hinder portability");
            }
            if (!caps.isClustered()) {
                this.log.info("Use of non-clustered storage service will result in per-node lockout behavior");
            }
        }
    }

    public void setSerializer(@Nonnull StorageSerializer<Set<CredentialRecord>> storageSerializer) {
        this.checkSetterPreconditions();
        this.serializer = (StorageSerializer)Constraint.isNotNull(storageSerializer, (String)"serializer can not be null");
    }

    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        if (this.serializer == null) {
            throw new ComponentInitializationException("Storage serializer can not be null");
        }
        if (this.storageService == null) {
            throw new ComponentInitializationException("Storage service can not be null");
        }
        this.lock = new ReentrantReadWriteLock(true);
    }

    protected void doDestroy() {
        this.lock = null;
        super.doDestroy();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(@Nullable String username) {
        this.checkComponentActive();
        if (username == null) {
            return CollectionSupport.emptySet();
        }
        ReentrantReadWriteLock.ReadLock readLock = this.lock.readLock();
        try {
            readLock.lock();
            Set set = (Set)((NonnullSupplier)this.getRegistrationsByUsername(username).stream().map(reg -> reg.toPublicKeyCredentialDescriptor()).collect(CollectionSupport.nonnullCollector(Collectors.toUnmodifiableSet()))).get();
            return set;
        }
        finally {
            readLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Optional<ByteArray> getUserHandleForUsername(@Nullable String username) {
        this.checkComponentActive();
        if (username == null) {
            return Optional.empty();
        }
        ReentrantReadWriteLock.ReadLock readLock = this.lock.readLock();
        try {
            readLock.lock();
            Optional<ByteArray> optional = this.getRegistrationsByUsername(username).stream().findAny().map(reg -> reg.getUserIdentity().getId());
            return optional;
        }
        finally {
            readLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
        this.checkComponentActive();
        ReentrantReadWriteLock.ReadLock readLock = this.lock.readLock();
        try {
            readLock.lock();
            Optional<String> optional = this.getRegistrationsByUserHandle(userHandle).stream().findAny().map(CredentialRecord::getUsername);
            return optional;
        }
        finally {
            readLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
        this.checkComponentActive();
        ReentrantReadWriteLock.ReadLock readLock = this.lock.readLock();
        try {
            readLock.lock();
            Optional<RegisteredCredential> optional = this.lookupAll(credentialId).stream().filter(cred -> cred.getUserHandle().equals((Object)userHandle)).findAny();
            return optional;
        }
        finally {
            readLock.unlock();
        }
    }

    private Collection<CredentialRecord> getRegistrationsByUserHandle(ByteArray userHandle) {
        this.checkComponentActive();
        ReentrantReadWriteLock.ReadLock readLock = this.lock.readLock();
        try {
            readLock.lock();
            HashSet<CredentialRecord> foundCredentials = new HashSet<CredentialRecord>();
            for (String usernameKey : this.storageService.getContextKeys(STORAGE_CONTEXT, null)) {
                assert (usernameKey != null);
                Set foundCredentialsForUser = (Set)((NonnullSupplier)this.getRegistrationsByUsername(usernameKey).stream().filter(cred -> userHandle.equals((Object)cred.getUserIdentity().getId())).collect(CollectionSupport.nonnullCollector(Collectors.toUnmodifiableSet()))).get();
                foundCredentials.addAll(foundCredentialsForUser);
            }
            HashSet<CredentialRecord> hashSet = foundCredentials;
            return hashSet;
        }
        catch (IOException e) {
            throw new CredentialRepositoryException((Exception)e);
        }
        finally {
            readLock.unlock();
        }
    }

    @Nonnull
    public Set<RegisteredCredential> lookupAll(ByteArray credentialId) {
        this.checkComponentActive();
        ReentrantReadWriteLock.ReadLock readLock = this.lock.readLock();
        try {
            readLock.lock();
            HashSet<RegisteredCredential> foundCredentials = new HashSet<RegisteredCredential>();
            for (String usernameKey : this.storageService.getContextKeys(STORAGE_CONTEXT, null)) {
                assert (usernameKey != null);
                Set foundCredentialsForUser = (Set)((NonnullSupplier)this.getRegistrationsByUsername(usernameKey).stream().filter(reg -> reg.getCredential().getCredentialId().equals((Object)credentialId)).map(CredentialRecord::getCredential).collect(CollectionSupport.nonnullCollector(Collectors.toUnmodifiableSet()))).get();
                foundCredentials.addAll(foundCredentialsForUser);
            }
            HashSet<RegisteredCredential> hashSet = foundCredentials;
            return hashSet;
        }
        catch (IOException e) {
            throw new CredentialRepositoryException((Exception)e);
        }
        finally {
            readLock.unlock();
        }
    }

    @Nonnull
    public Set<CredentialRecord> getRegistrationsByUsername(String username) {
        this.checkComponentActive();
        ReentrantReadWriteLock.ReadLock readLock = this.lock.readLock();
        try {
            readLock.lock();
            StorageRecord registration = this.storageService.read(STORAGE_CONTEXT, username);
            if (registration != null) {
                Set set = (Set)registration.getValue(this.serializer, STORAGE_CONTEXT, username);
                return set;
            }
            Set set = CollectionSupport.emptySet();
            return set;
        }
        catch (IOException e) {
            throw new CredentialRepositoryException((Exception)e);
        }
        finally {
            readLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    public Optional<CredentialRecord> getRegistrationByUsernameAndCredentialId(String username, ByteArray id) {
        this.checkComponentActive();
        ReentrantReadWriteLock.ReadLock readLock = this.lock.readLock();
        try {
            readLock.lock();
            Set<CredentialRecord> existingRegistrations = this.getRegistrationsByUsername(username);
            Optional<CredentialRecord> registration = existingRegistrations.stream().filter(credReg -> id.equals((Object)credReg.getCredential().getCredentialId())).findFirst();
            assert (registration != null);
            Optional<CredentialRecord> optional = registration;
            return optional;
        }
        finally {
            readLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    public Optional<CredentialRecord> getRegistrationByUserHandleAndCredentialId(ByteArray credentialId, ByteArray userHandle) {
        this.checkComponentActive();
        ReentrantReadWriteLock.ReadLock readLock = this.lock.readLock();
        try {
            readLock.lock();
            Collection<CredentialRecord> existingRegistrations = this.getRegistrationsByUserHandle(userHandle);
            Optional<CredentialRecord> registration = existingRegistrations.stream().filter(credReg -> credentialId.equals((Object)credReg.getCredential().getCredentialId())).findFirst();
            assert (registration != null);
            Optional<CredentialRecord> optional = registration;
            return optional;
        }
        finally {
            readLock.unlock();
        }
    }

    public boolean addRegistrationByUsername(@Nonnull String username, @Nonnull CredentialRecord reg) {
        this.checkComponentActive();
        ReentrantReadWriteLock.WriteLock writeLock = this.lock.writeLock();
        try {
            writeLock.lock();
            Set<CredentialRecord> existingRegistrations = this.getRegistrationsByUsername(username);
            if (!existingRegistrations.isEmpty()) {
                LinkedHashSet<CredentialRecord> updateSet = new LinkedHashSet<CredentialRecord>(existingRegistrations);
                updateSet.add(reg);
                boolean bl = this.storageService.update(STORAGE_CONTEXT, username, updateSet, this.serializer, null);
                return bl;
            }
            LinkedHashSet<CredentialRecord> addSet = new LinkedHashSet<CredentialRecord>(1);
            addSet.add(reg);
            boolean bl = this.storageService.create(STORAGE_CONTEXT, username, addSet, this.serializer, null);
            return bl;
        }
        catch (IOException e) {
            throw new CredentialRepositoryException((Exception)e);
        }
        finally {
            writeLock.unlock();
        }
    }

    public boolean removeRegistrationByUsername(String username, CredentialRecord credentialRegistration) {
        this.checkComponentActive();
        ReentrantReadWriteLock.WriteLock writeLock = this.lock.writeLock();
        try {
            writeLock.lock();
            Set<CredentialRecord> existingRegistrations = this.getRegistrationsByUsername(username);
            if (!existingRegistrations.isEmpty()) {
                LinkedHashSet<CredentialRecord> updateSet = new LinkedHashSet<CredentialRecord>(existingRegistrations);
                updateSet.remove(credentialRegistration);
                if (updateSet.isEmpty()) {
                    boolean bl = this.storageService.delete(STORAGE_CONTEXT, username);
                    return bl;
                }
                assert (this.serializer != null);
                boolean bl = this.storageService.update(STORAGE_CONTEXT, username, updateSet, this.serializer, null);
                return bl;
            }
            boolean bl = false;
            return bl;
        }
        catch (IOException e) {
            throw new CredentialRepositoryException((Exception)e);
        }
        finally {
            writeLock.unlock();
        }
    }

    public int removeRegistrationByCredentialId(ByteArray credentialId) {
        this.checkComponentActive();
        ReentrantReadWriteLock.WriteLock writeLock = this.lock.writeLock();
        try {
            writeLock.lock();
            int removalCount = 0;
            for (String usernameKey : this.storageService.getContextKeys(STORAGE_CONTEXT, null)) {
                assert (usernameKey != null);
                Set<CredentialRecord> existingRegistrations = this.getRegistrationsByUsername(usernameKey);
                if (existingRegistrations.isEmpty()) {
                    this.log.trace("No existing registrations, nothing to remove");
                    continue;
                }
                List matchingRegistrations = (List)((NonnullSupplier)existingRegistrations.stream().filter(reg -> reg.getCredential().getCredentialId().equals((Object)credentialId)).collect(CollectionSupport.nonnullCollector(Collectors.toList()))).get();
                if (matchingRegistrations.size() != 1) continue;
                CredentialRecord registrationToRemove = (CredentialRecord)matchingRegistrations.get(0);
                LinkedHashSet<CredentialRecord> updateSet = new LinkedHashSet<CredentialRecord>(existingRegistrations);
                updateSet.remove(registrationToRemove);
                if (updateSet.isEmpty()) {
                    if (!this.storageService.delete(STORAGE_CONTEXT, usernameKey)) continue;
                    ++removalCount;
                    continue;
                }
                assert (this.serializer != null);
                if (!this.storageService.update(STORAGE_CONTEXT, usernameKey, updateSet, this.serializer, null)) continue;
                ++removalCount;
            }
            int n = removalCount;
            return n;
        }
        catch (IOException e) {
            throw new CredentialRepositoryException((Exception)e);
        }
        finally {
            writeLock.unlock();
        }
    }

    public boolean removeRegistrationByUsernameAndCredentialId(String username, ByteArray credentialId) {
        this.checkComponentActive();
        ReentrantReadWriteLock.WriteLock writeLock = this.lock.writeLock();
        try {
            writeLock.lock();
            Set<CredentialRecord> existingRegistrations = this.getRegistrationsByUsername(username);
            if (!existingRegistrations.isEmpty()) {
                LinkedHashSet<CredentialRecord> updateSet = new LinkedHashSet<CredentialRecord>(existingRegistrations);
                List matchingRegistrations = (List)((NonnullSupplier)existingRegistrations.stream().filter(reg -> reg.getCredential().getCredentialId().equals((Object)credentialId)).collect(CollectionSupport.nonnullCollector(Collectors.toList()))).get();
                if (matchingRegistrations.size() != 1) {
                    boolean bl = false;
                    return bl;
                }
                updateSet.remove(matchingRegistrations.iterator().next());
                if (updateSet.isEmpty()) {
                    boolean bl = this.storageService.delete(STORAGE_CONTEXT, username);
                    return bl;
                }
                assert (this.serializer != null);
                boolean bl = this.storageService.update(STORAGE_CONTEXT, username, updateSet, this.serializer, null);
                return bl;
            }
            boolean bl = false;
            return bl;
        }
        catch (IOException e) {
            throw new CredentialRepositoryException((Exception)e);
        }
        finally {
            writeLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean updateSignatureCounter(@Nonnull String username, @Nonnull ByteArray credentialId, long newSignatureCount) {
        ReentrantReadWriteLock.WriteLock writeLock = this.lock.writeLock();
        try {
            writeLock.lock();
            Optional<CredentialRecord> credential = this.getRegistrationByUsernameAndCredentialId(username, credentialId);
            if (credential.isEmpty()) {
                this.log.warn("Can not update signature count for user '{}' and credential '{}'. No existing credential found.", (Object)username, (Object)credentialId.getBase64());
                boolean bl = false;
                return bl;
            }
            RegisteredCredential updatedCredential = credential.get().getCredential().toBuilder().signatureCount(newSignatureCount).build();
            assert (updatedCredential != null);
            CredentialRecord updatedRegistration = credential.get().withCredential(updatedCredential);
            CredentialRecord existingCredential = credential.get();
            assert (existingCredential != null && updatedRegistration != null);
            if (!this.removeRegistrationByUsername(username, existingCredential)) {
                this.log.warn("Can not update signature count for user '{}' and credential '{}'. Can not remove existing signature count.", (Object)username, (Object)credentialId.getBase64());
                boolean bl = false;
                return bl;
            }
            if (!this.addRegistrationByUsername(username, updatedRegistration)) {
                this.log.warn("Can not update signature count for user '{}' and credential '{}'. Can not add new signature count.", (Object)username, (Object)credentialId.getBase64());
                boolean bl = false;
                return bl;
            }
            boolean bl = true;
            return bl;
        }
        finally {
            writeLock.unlock();
        }
    }

    @Nonnull
    @NotLive
    @Unmodifiable
    public Set<CredentialRecord> getAllRegistrations() {
        this.checkComponentActive();
        ReentrantReadWriteLock.ReadLock readLock = this.lock.readLock();
        try {
            readLock.lock();
            HashSet allCredentials = new HashSet();
            for (String usernameKey : this.storageService.getContextKeys(STORAGE_CONTEXT, null)) {
                assert (usernameKey != null);
                allCredentials.addAll((Collection)((NonnullSupplier)this.getRegistrationsByUsername(usernameKey).stream().filter(Objects::nonNull).collect(CollectionSupport.nonnullCollector(Collectors.toUnmodifiableSet()))).get());
            }
            Set set = CollectionSupport.copyToSet(allCredentials);
            return set;
        }
        catch (IOException e) {
            throw new CredentialRepositoryException((Exception)e);
        }
        finally {
            readLock.unlock();
        }
    }
}

