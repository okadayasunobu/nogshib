/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.shibboleth.shared.annotation.constraint.NotEmpty
 */
package net.shibboleth.idp.plugin.authn.webauthn.authn;

import javax.annotation.Nonnull;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

public final class AssertionResult {
    private final boolean success;
    @Nonnull
    @NotEmpty
    private final String username;
    private final boolean userVerified;
    private final boolean signatureCounterValid;
    private final byte[] userId;

    private AssertionResult(Builder builder) {
        this.success = builder.success;
        this.username = builder.username;
        this.signatureCounterValid = builder.signatureCounterValid;
        this.userId = builder.userId;
        this.userVerified = builder.userVerified;
    }

    public final boolean isSuccess() {
        return this.success;
    }

    @Nonnull
    public final String getUsername() {
        return this.username;
    }

    public final boolean isSignatureCounterValid() {
        return this.signatureCounterValid;
    }

    @Nonnull
    public byte[] getUserId() {
        return this.userId;
    }

    public boolean isUserVerified() {
        return this.userVerified;
    }

    public static ISuccessStage builder() {
        return new Builder();
    }

    public static final class Builder
    implements ISuccessStage,
    IUsernameStage,
    ISignatureCounterValidStage,
    IUserIdStage,
    IUserVerifiedStage,
    IBuildStage {
        private boolean success;
        private String username;
        private boolean signatureCounterValid;
        private byte[] userId;
        private boolean userVerified;

        private Builder() {
        }

        @Override
        public IUsernameStage withSuccess(boolean successIn) {
            this.success = successIn;
            return this;
        }

        @Override
        public ISignatureCounterValidStage withUsername(String uname) {
            this.username = uname;
            return this;
        }

        @Override
        public IUserIdStage withSignatureCounterValid(boolean sigCounterValid) {
            this.signatureCounterValid = sigCounterValid;
            return this;
        }

        @Override
        public IUserVerifiedStage withUserId(byte[] id) {
            this.userId = id;
            return this;
        }

        @Override
        public IBuildStage withUserVerified(boolean uv) {
            this.userVerified = uv;
            return this;
        }

        @Override
        public AssertionResult build() {
            return new AssertionResult(this);
        }
    }

    public static interface IBuildStage {
        public AssertionResult build();
    }

    public static interface IUserVerifiedStage {
        public IBuildStage withUserVerified(boolean var1);
    }

    public static interface IUserIdStage {
        public IUserVerifiedStage withUserId(byte[] var1);
    }

    public static interface ISignatureCounterValidStage {
        public IUserIdStage withSignatureCounterValid(boolean var1);
    }

    public static interface IUsernameStage {
        public ISignatureCounterValidStage withUsername(String var1);
    }

    public static interface ISuccessStage {
        public IUsernameStage withSuccess(boolean var1);
    }
}

