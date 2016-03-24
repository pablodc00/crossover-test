package com.crossover.trial.weather;

/**
 * Basic airport information.
 */
public class AirportData {

    /**
     * the three letter IATA code
     */
    private String iata;

    /**
     * latitude value in degrees
     */
    private double latitude;

    /**
     * longitude value in degrees
     */
    private double longitude;

    public AirportData() {
    }

    public AirportData(String iata, double latitude, double longitude) {
        this.iata = iata;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getIata() {
        return iata;
    }

    public void setIata(String iata) {
        this.iata = iata;
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

    @Override
    public String toString() {
        return "AirportData{" +
                "iata='" + iata + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    @Override
    public int hashCode() {
        return iata != null ? iata.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AirportData that = (AirportData) o;

        if (Double.compare(that.latitude, latitude) != 0) return false;
        if (Double.compare(that.longitude, longitude) != 0) return false;

        return iata.equals(that.iata);
    }

}
