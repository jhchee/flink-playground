package github.jhchee.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class Location implements Serializable {
    private double longitude;
    private double latitude;

    public Location(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "Location{" +
               "longitude='" + longitude + '\'' +
               ", latitude='" + latitude + '\'' +
               '}';
    }
}
