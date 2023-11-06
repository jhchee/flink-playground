package github.jhchee.events;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
public class TransactionEvent extends UserEvent implements Serializable {
    private String transactionId;
    private double amount;

    public TransactionEvent() {
    }

    public TransactionEvent(String userId, Instant eventTime, String transactionId, double amount) {
        super(userId, eventTime);
        this.transactionId = transactionId;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "TransactionEvent{" +
               "transactionId='" + transactionId + '\'' +
               ", amount=" + amount +
               ", userId='" + userId + '\'' +
               ", eventTime=" + eventTime +
               '}';
    }
}
