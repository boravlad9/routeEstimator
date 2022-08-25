package code.roadEstimator.entities;


import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Entity
public class Noduri  implements Serializable, Comparable<Noduri> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "uuid-binary")
    private UUID id;

    @Column(name = "accuracy", nullable = false)
    private float accuracy;

    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;

    @Column(name = "last_long", nullable = false)
    private float last_long;

    @Column(name = "last_lat", nullable = false)
    private float last_lat;

    @Column(name = "current_lat", nullable = false)
    private float current_lat;

    @Column(name = "current_long", nullable = false)
    private float current_long;

    @Column(name = "speed", nullable = false)
    private double speed;

    public Noduri() {
    }

    public Noduri(UUID id, int accuracy, Timestamp timestamp, float last_long, float last_lat, float current_lat, float current_long) {
        this.id = id;
        this.accuracy = accuracy;
        this.timestamp = timestamp;
        this.last_long = last_long;
        this.last_lat = last_lat;
        this.current_lat = current_lat;
        this.current_long = current_long;
    }

    public Noduri(UUID id, int accuracy, float last_long, float last_lat, float current_lat, float current_long) {
        this.id = id;
        this.accuracy = accuracy;
        this.last_long = last_long;
        this.last_lat = last_lat;
        this.current_lat = current_lat;
        this.current_long = current_long;
    }

    public Noduri(int accuracy, float last_long, float last_lat, float current_lat, float current_long) {
        this.accuracy = accuracy;
        this.last_long = last_long;
        this.last_lat = last_lat;
        this.current_lat = current_lat;
        this.current_long = current_long;
    }

    public Noduri(float accuracy, Timestamp timestamp, float last_long, float last_lat, float current_lat, float current_long) {
        this.accuracy = accuracy;
        this.timestamp = timestamp;
        this.last_long = last_long;
        this.last_lat = last_lat;
        this.current_lat = current_lat;
        this.current_long = current_long;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getLast_long() {
        return last_long;
    }

    public void setLast_long(float last_long) {
        this.last_long = last_long;
    }

    public float getLast_lat() {
        return last_lat;
    }

    public void setLast_lat(float last_lat) {
        this.last_lat = last_lat;
    }

    public float getCurrent_lat() {
        return current_lat;
    }

    public void setCurrent_lat(float current_lat) {
        this.current_lat = current_lat;
    }

    public float getCurrent_long() {
        return current_long;
    }

    public void setCurrent_long(float current_long) {
        this.current_long = current_long;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {

        this.speed = speed;
    }

    @Override
    public int compareTo(Noduri o) {
        return Double.compare(this.getSpeed(), o.getSpeed());
    }
    public boolean isWeekday() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.getTimestamp());
        return cal.getTime().getDay() == 0 || cal.getTime().getDay() == 6;
    }

    public boolean isNight() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.getTimestamp());
        if (cal.get(Calendar.AM_PM) == Calendar.PM) {
            if (cal.getTime().getHours() != 12) {
                return  cal.getTime().getHours() + 12 >= 19 || cal.getTime().getHours() + 12 <= 7;
            }
        }
        return  cal.getTime().getHours() >= 19 || cal.getTime().getHours() <= 7;
    }

    public int getHour() throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.getTimestamp());
        if (cal.get(Calendar.AM_PM) == Calendar.PM) {
            if (cal.getTime().getHours() != 12) {
                return cal.getTime().getHours() + 12;
            }
        }
        return cal.getTime().getHours();
    }

    public int getDay() {
        return this.getTimestamp().getDay();
    }

    public int getYear() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.getTimestamp());
        return cal.getTime().getYear() + 1900;
    }
}
