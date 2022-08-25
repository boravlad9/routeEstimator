package code.roadEstimator.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class Coordinates {
    private double latitude, longitude, destLat, destLong;
    @JsonFormat
            (shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Date date;

    public Coordinates() {
    }

    public Coordinates(double latitude, double longitude, double destLat, double destLong, Date date) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.destLat = destLat;
        this.destLong = destLong;
        this.date = date;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDestLat() {
        return destLat;
    }

    public void setDestLat(double destLat) {
        this.destLat = destLat;
    }

    public double getDestLong() {
        return destLong;
    }

    public void setDestLong(double destLong) {
        this.destLong = destLong;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
