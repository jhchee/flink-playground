package github.jhchee.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlertEvent implements Serializable {
    private String userId;
    private String description;
    private RiskLevel riskLevel;

    @Override
    public String toString() {
        return "Alert{" +
               "userId='" + userId + '\'' +
               ", description='" + description + '\'' +
               ", type=" + riskLevel +
               '}';
    }

    public enum RiskLevel {
        LOW,
        MEDIUM,
        HIGH,
    }
}
