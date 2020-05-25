package com.dust.sensor;

import model.DustReport;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.*;

@Controller
@ComponentScan
public class MainController {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm", Locale.ENGLISH);

    private static List<List<Map<Object, Object>>> list;
    private static List<Map<Object, Object>> dataPoints1;

    public static final int DEFAULT_TIMEOUT = 60; // constant timeout to measure every minute

    @Autowired
    private DustRepository repo;


    @RequestMapping(value = {"/chart"}, method = RequestMethod.GET)
    public String index(Model model) {
        System.out.println("List all reports form database ...");
        updateList(repo.findAll());
        model.addAttribute("dataPointsList", list);
        return "chart";
    }


    @RequestMapping("/timeout")
    public String updateTimeout(@RequestParam("timeout") String timeout, Model model) {
        System.out.println("timeout:" + timeout);
        internalStartUpdate(Long.parseLong(timeout));
        model.addAttribute("dataPointsList", list);
        return "chart";
    }

    @RequestMapping("/start")
    public String sensorStart(Model model) {
        System.out.println("Start sensor");
        internalStartUpdate(DEFAULT_TIMEOUT);
        model.addAttribute("dataPointsList", list);
        return "chart";
    }

    @RequestMapping("/stop")
    public String sensorStop(Model model) {
        if (!SensorApplication.isSensorMissing) {
            SensorApplication.serial.stopRunnable();
            System.out.println("Runnable is stopped");
        } else {
            System.out.println("Sensor is missing could not start or stop anything!!!!");
        }
        model.addAttribute("dataPointsList", list);

        return "chart";
    }


    @RequestMapping("/chart")
    public String filterDate(@RequestParam("start") String fromDate,
                             @RequestParam("end") String toDate, Model model) {

        LocalDateTime date1 = LocalDateTime.parse(fromDate, formatter);
        System.out.println(date1);
        LocalDateTime date2 = LocalDateTime.parse(toDate, formatter);
        System.out.println(date2);
        updateList(repo.findAllByDateBetween(date1, date2));
        model.addAttribute("dataPointsList", list);
        return "chart";
    }




    @RequestMapping("/lastValue")
    public String getLastValue(Model model){

        DustReport latest=repo.findTopByOrderByDateDesc();
        System.out.println("Latest report: "+latest);

        model.addAttribute("lastDateReport", latest.getDate());
        model.addAttribute("lastVoltReport", latest.getVolt()); //this is always '0', so you can use only density
        model.addAttribute("lastDensReport", latest.getDens());
        model.addAttribute("dataPointsList", list); // this line adds all point to the chart !!
        return "chart";
    }


    private void updateList(List<DustReport> report) {

        System.out.println("Updating chart view list: " + report.size());
        for (DustReport rep: report) {
            System.out.println(rep);
        }
        Map<Object, Object> map;
        list = new ArrayList<>();
        dataPoints1 = new ArrayList<>();
        for (DustReport r : report) {
            map = new TreeMap<>();
            map.put("x", r.getDate());
            map.put("y", r.getDens());
            dataPoints1.add(map);
        }
        list.add(dataPoints1);
    }

    private void internalStartUpdate(long timeout) {
        if (!SensorApplication.isSensorMissing) {
            System.out.println("Set timeout...");
            SensorApplication.serial.setMilliseconds(timeout * 1000);
            if (!SensorApplication.serial.isRunning) {
                SensorApplication.serial.isRunning = true;
                System.out.println("Starting runnable... ");
                SensorApplication.serial.run();
            } else {
                System.out.println("Sensor is already running");
            }
        } else {
            System.out.println("Sensor is missing could not start or stop anything!!!!");
        }

    }


}