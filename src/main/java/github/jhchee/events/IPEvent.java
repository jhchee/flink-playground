package github.jhchee.events;

import github.jhchee.pojo.Location;

import java.io.Serializable;

public class IPEvent extends UserEvent implements Serializable {
    private Location location;

    public IPEvent(String userId, String ip) {
        this.userId = userId;
        this.ip = ip;
    }

    public String getUserId() {
        return userId;
    }

    public String getIp() {
        return ip;
    }

    @Override
    public String toString() {
        return "IPEvent{" +
               "userId='" + userId + '\'' +
               ", ip='" + ip + '\'' +
               '}';
    }
}
