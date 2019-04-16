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
        serial = new Serial();
        serial.setRepo(repo);
    }

    public static void main(String[] args) {
        SpringApplication.run(SensorApplication.class, args);
        //SensorApplication app=new SensorApplication();
        //serial = new Serial();

        serial.setMiliseconds(9000);
        serial.run();
    }


}
