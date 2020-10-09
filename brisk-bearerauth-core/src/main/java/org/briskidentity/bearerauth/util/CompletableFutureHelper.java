package org.briskidentity.bearerauth.util;

import java.util.concurrent.CompletableFuture;

public final class CompletableFutureHelper {

    private CompletableFutureHelper() {
    }

    public static <U> CompletableFuture<U> failedFuture(Throwable ex) {
        CompletableFuture<U> completableFuture = new CompletableFuture<>();
        completableFuture.completeExceptionally(ex);
        return completableFuture;
    }

}
