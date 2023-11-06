package github.jhchee.events;

import github.jhchee.pojo.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
public class UserEvent implements Serializable {
    protected String userId;
    protected Instant eventTime;

    public UserEvent() {

    }
    public UserEvent(String userId, Instant eventTime) {
        this.userId = userId;
        this.eventTime = eventTime;
    }
}

