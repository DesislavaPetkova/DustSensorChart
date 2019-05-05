package com.dust.sensor;

import model.DustReport;
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

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm", Locale.ITALY);

    private DustRepository repo;
    private static List<List<Map<Object, Object>>> list;
    private static List<Map<Object, Object>> dataPoints1;

    @Autowired
    public void setDustRepository(DustRepository repo) {
        this.repo = repo;
    }


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
        if (!SensorApplication.isSensorMissing) {
            if (timeout.equals("0")) {
                SensorApplication.serial.stopRunnable();
                System.out.println("Runnable is stopped");
            } else {
                System.out.println("Set timeout...");
                SensorApplication.serial.setMilliseconds(Long.parseLong(timeout) * 1000);
                if (!SensorApplication.serial.isRunning) {
                    SensorApplication.serial.isRunning = true;
                    System.out.println("Starting runnable... ");
                    SensorApplication.serial.run();
                }
            }
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
        Query query = new Query();
        query.addCriteria(Criteria.where("startDate").gte(fromDate).lt(toDate));
        updateList(repo.findByDateBetween(date1, date2));
        model.addAttribute("dataPointsList", list);
        return "chart";
    }


    private void updateList(List<DustReport> report) {

        System.out.println("Updating chart view list: " + report.size());
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


}