package com.dust.sensor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import serial.connection.Serial;

import java.io.IOException;


@SpringBootApplication
public class SensorApplication extends SpringBootServletInitializer {

    public static Serial serial;
     static boolean isSensorMissing;
     //TODO autowire here !
    private DustRepository repo;

    @Autowired
    public void setDustRepository(DustRepository repo) {
        System.out.println("Setting Dust repository");
        this.repo = repo;
        serial = new Serial(repo);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SensorApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(SensorApplication.class, args);
        serial.setMilliseconds(9000);
        try {
            serial.initialize();
            serial.run();
        } catch (IOException e) {
            isSensorMissing = true;
            System.out.println("Sensor is missing and only data from the database will be visualized !!!");
        }
        System.out.println("Visualize chart");

    }


}
