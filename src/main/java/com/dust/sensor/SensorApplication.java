package com.dust.sensor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import serial.connection.Serial;

import java.io.IOException;


@SpringBootApplication
public class SensorApplication {

    private static  Serial serial;
    private DustRepository repo;

    @Autowired
    public void setDustRepository(DustRepository repo) {
        this.repo = repo;
        serial = new Serial(repo);
    }

    public static void main(String[] args) {
        SpringApplication.run(SensorApplication.class, args);
        serial.setMilliseconds(9000);
        try {
            serial.initialize();
            serial.run();
        } catch (IOException e) {
            System.out.println("Sensor is missing and only data from the database will be visualized !!!");
        }
        System.out.println("Visualize chart");

    }


}
