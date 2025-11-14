/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.fido.metadata.FidoMetadataDownloader
 *  com.yubico.fido.metadata.FidoMetadataService
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.GuardedBy
 *  net.shibboleth.shared.annotation.constraint.Live
 *  net.shibboleth.shared.annotation.constraint.NonnullAfterInit
 *  net.shibboleth.shared.collection.CollectionSupport
 *  net.shibboleth.shared.component.AbstractIdentifiableInitializableComponent
 *  net.shibboleth.shared.component.ComponentInitializationException
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  net.shibboleth.shared.resource.Resource
 *  net.shibboleth.shared.spring.resource.ResourceHelper
 *  org.opensaml.security.x509.X509Support
 *  org.slf4j.Logger
 *  org.springframework.beans.FatalBeanException
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.core.io.FileSystemResource
 *  org.springframework.core.io.Resource
 *  org.springframework.util.FileCopyUtils
 */
package net.shibboleth.idp.plugin.authn.webauthn.metadata.impl;

import com.yubico.fido.metadata.FidoMetadataDownloader;
import com.yubico.fido.metadata.FidoMetadataService;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.AbstractIdentifiableInitializableComponent;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.spring.resource.ResourceHelper;
import org.opensaml.security.x509.X509Support;
import org.slf4j.Logger;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

public class FidoMetadataServiceFactory
extends AbstractIdentifiableInitializableComponent
implements FactoryBean<FidoMetadataService> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(FidoMetadataServiceFactory.class);
    @GuardedBy(value="this")
    @NonnullAfterInit
    private net.shibboleth.shared.resource.Resource trustRootFile;
    @Nullable
    @GuardedBy(value="this")
    private net.shibboleth.shared.resource.Resource cacheFile;
    @Nullable
    @GuardedBy(value="this")
    private net.shibboleth.shared.resource.Resource metadataBlobUrl;
    @Nullable
    @GuardedBy(value="this")
    private net.shibboleth.shared.resource.Resource metadataBlobFile;
    @GuardedBy(value="this")
    private boolean verifyDownloadOnly = false;
    @GuardedBy(value="this")
    @NonnullAfterInit
    private String[] expectedLegalHeaders;
    @Nonnull
    @GuardedBy(value="this")
    private List<net.shibboleth.shared.resource.Resource> crls = CollectionSupport.emptyList();

    public FidoMetadataService getObject() throws Exception {
        FidoMetadataDownloader downloader = null;
        net.shibboleth.shared.resource.Resource localMetadataBlobUrl = this.getMetadataBlobUrl();
        net.shibboleth.shared.resource.Resource localMetadataBlobFile = this.getMetadataBlobFile();
        net.shibboleth.shared.resource.Resource localMetadataCacheFile = this.getCacheFile();
        if (localMetadataBlobFile != null && localMetadataBlobFile.exists() && localMetadataBlobFile.isReadable()) {
            this.log.debug("{}: Loading FIDO metadata blob from local file '{}'", (Object)this.getId(), (Object)localMetadataBlobFile.getFilename());
            downloader = FidoMetadataDownloader.builder().expectLegalHeader(this.getExpectedLegalHeaders()).useTrustRoot(X509Support.decodeCertificate((File)this.getTrustRootFile().getFile())).useBlob(this.loadMetadataJwt(localMetadataBlobFile)).useCrls(this.loadCrls()).verifyDownloadsOnly(this.verifyDownloadOnly).build();
        } else if (localMetadataBlobUrl != null && localMetadataCacheFile != null) {
            this.log.debug("{}: Loading FIDO metadata blob from '{}'", (Object)this.getId(), (Object)this.metadataBlobUrl);
            downloader = FidoMetadataDownloader.builder().expectLegalHeader(this.getExpectedLegalHeaders()).useTrustRoot(X509Support.decodeCertificate((File)this.getTrustRootFile().getFile())).downloadBlob(localMetadataBlobUrl.getURL()).useBlobCacheFile(localMetadataCacheFile.getFile()).useCrls(this.loadCrls()).verifyDownloadsOnly(this.verifyDownloadOnly).build();
        } else {
            throw new FatalBeanException("Local FIDO metadata blob file not specified or the metadata blob URL and local cache file not specified. Please use either a local file or a known URL");
        }
        assert (downloader != null);
        try {
            FidoMetadataService mds = FidoMetadataService.builder().useBlob(downloader.loadCachedBlob()).build();
            this.log.debug("{}: loaded FIDO metadata blob", (Object)this.getId());
            return mds;
        }
        catch (Exception e) {
            throw new FatalBeanException("Can not construct FIDO Metadata service", (Throwable)e);
        }
    }

    @Nonnull
    private Collection<CRL> loadCrls() {
        List<net.shibboleth.shared.resource.Resource> localCrls = this.getCrls();
        if (localCrls.isEmpty()) {
            return CollectionSupport.emptyList();
        }
        ArrayList<CRL> crlsConverted = new ArrayList<CRL>(localCrls.size());
        for (net.shibboleth.shared.resource.Resource crlFile : localCrls) {
            try {
                InputStream is = crlFile.getInputStream();
                try {
                    crlsConverted.addAll(X509Support.decodeCRLs((InputStream)is));
                }
                finally {
                    if (is == null) continue;
                    is.close();
                }
            }
            catch (IOException | CRLException e) {
                this.log.error("Could not decode CRL file at {}: {}", (Object)crlFile.getDescription(), (Object)e.getMessage());
                throw new FatalBeanException("Could not decode provided CRL file " + crlFile.getDescription(), (Throwable)e);
            }
        }
        return crlsConverted;
    }

    @Nonnull
    private String loadMetadataJwt(@Nonnull net.shibboleth.shared.resource.Resource file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("Metadata blob file does not exist");
        }
        return FileCopyUtils.copyToString((Reader)new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
    }

    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        if (this.trustRootFile == null) {
            throw new ComponentInitializationException("trustRootFile cannot be null");
        }
        if (this.metadataBlobUrl == null && this.metadataBlobFile == null) {
            throw new ComponentInitializationException("Metadata blob URL or file must be set");
        }
    }

    public synchronized void setCacheFile(@Nullable String file) {
        this.checkSetterPreconditions();
        if (file != null) {
            this.cacheFile = ResourceHelper.of((Resource)new FileSystemResource(file));
        }
    }

    @Nullable
    private synchronized net.shibboleth.shared.resource.Resource getCacheFile() {
        this.checkComponentActive();
        return this.cacheFile;
    }

    public synchronized void setTrustRootFile(@Nonnull net.shibboleth.shared.resource.Resource file) {
        this.checkSetterPreconditions();
        this.trustRootFile = (net.shibboleth.shared.resource.Resource)Constraint.isNotNull((Object)file, (String)"trustRootCacheFile can not be null");
    }

    @NonnullAfterInit
    private synchronized net.shibboleth.shared.resource.Resource getTrustRootFile() {
        this.checkComponentActive();
        return this.trustRootFile;
    }

    @Nullable
    private synchronized net.shibboleth.shared.resource.Resource getMetadataBlobUrl() {
        this.checkComponentActive();
        return this.metadataBlobUrl;
    }

    public synchronized void setMetadataBlobUrl(@Nullable net.shibboleth.shared.resource.Resource url) {
        this.checkSetterPreconditions();
        this.metadataBlobUrl = url;
    }

    public synchronized void setMetadataBlobFile(@Nullable net.shibboleth.shared.resource.Resource file) {
        this.checkSetterPreconditions();
        this.metadataBlobFile = file;
    }

    @Nullable
    private synchronized net.shibboleth.shared.resource.Resource getMetadataBlobFile() {
        this.checkComponentActive();
        return this.metadataBlobFile;
    }

    public synchronized void setExpectedLegalHeaders(@Nonnull String[] headers) {
        this.checkSetterPreconditions();
        this.expectedLegalHeaders = (String[])Constraint.isNotNull((Object)headers, (String)"expectedLegalHeaders can not be null");
    }

    @NonnullAfterInit
    private synchronized String[] getExpectedLegalHeaders() {
        this.checkComponentActive();
        return this.expectedLegalHeaders;
    }

    public synchronized void setCrls(@Nullable List<net.shibboleth.shared.resource.Resource> revocationLists) {
        this.checkSetterPreconditions();
        this.log.trace("Setting FIDO CRLs '{}'", revocationLists);
        this.crls = revocationLists != null ? CollectionSupport.copyToList(revocationLists) : CollectionSupport.emptyList();
    }

    @Nonnull
    @Live
    private synchronized List<net.shibboleth.shared.resource.Resource> getCrls() {
        this.checkComponentActive();
        return this.crls;
    }

    public synchronized void setVerifyDownloadOnly(boolean downloadOnly) {
        this.checkSetterPreconditions();
        this.verifyDownloadOnly = downloadOnly;
    }

    public synchronized boolean isVerifyDownloadOnly() {
        this.checkComponentActive();
        return this.verifyDownloadOnly;
    }

    public Class<?> getObjectType() {
        return FidoMetadataService.class;
    }
}

