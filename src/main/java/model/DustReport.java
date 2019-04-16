package model;


import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
public class DustReport {

    @Id
    private String ID;
    private LocalDateTime date;
    private double volt;
    private double dens;

    public DustReport() {
    }

    public DustReport(LocalDateTime date, double volt, double dens) {
        this.date = date;
        this.volt = volt;
        this.dens = dens;
    }
 public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setVoltage(double voltage) {
        this.volt = voltage;
    }

    public void setDens(double dens) {
        this.dens = dens;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public double getVolt() {
        return volt;
    }

    public double getDens() {
        return dens;
    }


    @Override
    public String toString() {
        return "DustReport{" +
                "date=" + date +
                ", voltage=" + volt +
                ", density=" + dens +
                '}';
    }
}


