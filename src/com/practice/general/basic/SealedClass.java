package com.practice.general.basic;

import java.time.Instant;

public class SealedClass {
    // 1. Define a sealed interface that explicitly restricts its implementations
    public sealed interface PaymentStatus permits Success, Failed, Pending {}

    // 2. Implement the permitted subtypes using compact Java Records
    // Records are implicitly final and perfectly model immutable data
    public record Success(String transactionId, double amount) implements PaymentStatus {}

    public record Failed(String errorCode, String reason) implements PaymentStatus {}

    public record Pending(Instant estimatedCompletion) implements PaymentStatus {}

    public static void main(String[] args) {
        // Instantiate the record types
        PaymentStatus successStatus = new Success("TXN-12345", 150.50);
        PaymentStatus failedStatus = new Failed("ERR_404", "Insufficient funds");
        PaymentStatus pendingStatus = new Pending(Instant.now().plusSeconds(3600)); // 1 hour from now

        // Process the states
        System.out.println(processPayment(successStatus));
        System.out.println(processPayment(failedStatus));
        System.out.println(processPayment(pendingStatus));
    }

    // 3. Leverage Java 17 pattern matching to handle the statuses exhaustively
    public static String processPayment(PaymentStatus status) {
        return switch (status) {
            case Success s -> "Payment successful! ID: " + s.transactionId() + ", Amount: $" + s.amount();
            case Failed f  -> "Payment failed due to: " + f.reason() + " (Code: " + f.errorCode() + ")";
            case Pending p -> "Payment is pending until: " + p.estimatedCompletion();
            // No default case needed! Java knows these are the only 3 possible subtypes.
        };
    }
}

