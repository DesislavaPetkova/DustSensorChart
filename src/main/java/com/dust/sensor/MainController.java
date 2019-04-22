package com.dust.sensor;

import model.DustReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.*;

@Controller
@ComponentScan
public class MainController {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm", Locale.ITALY);


    public DustRepository repo;
    static List<List<Map<Object, Object>>> list;
    static List<Map<Object, Object>> dataPoints1;
    static List<DustReport> report;

    @Autowired
    public void setDustRepository(DustRepository repo) {
        this.repo = repo;
    }


    @RequestMapping(value = {"/chart"}, method = RequestMethod.GET)
    public String index(Model model) {
        System.out.println("INDEXX VIEW....... !!! enter");
        updateList(repo.findAll());
       /* Map<Object, Object> map;
        list = new ArrayList<List<Map<Object, Object>>>();
        dataPoints1 = new ArrayList<Map<Object, Object>>();
        report = repo.findAll();
        for (DustReport r : report) {
>>>>>>> -initial variant chart view
            map = new TreeMap<>();
            map.put("x", r.getDate());
            map.put("y", r.getDens());
            dataPoints1.add(map);
        }
<<<<<<< HEAD
        list.add(dataPoints1);
=======
        list.add(dataPoints1);*/


        model.addAttribute("dataPointsList", list);

        return "chart";
    }


    @RequestMapping("/charrt")
    public String updateTimeout(@RequestParam("timeout") String timeout, Model model) {
        System.out.println("timeout:" + timeout);
        if (timeout.equals("0")) {
            SensorApplication.serial.stopRunnable();
            System.out.println("Runnable is stopped");
        } else if (!timeout.isEmpty() && !timeout.equals(" ")) {
            System.out.println("Set timeout...");
            SensorApplication.serial.setMilliseconds(Long.parseLong(timeout) * 1000);
            if (!SensorApplication.serial.isRunning) {
                SensorApplication.serial.isRunning = true;
                System.out.println("Starting runnable... ");
                SensorApplication.serial.run();
            }
        } else {
            throw new IllegalArgumentException("PLease provide valid timeout ");
        }
        System.out.println(list.size());
        return "chartr";

    }


    @RequestMapping("/chart")
    public String filterDate(@RequestParam("from") String /*@DateTimeFormat(pattern = "yyyy-MM-dd") Date*/ fromDate,
                             @RequestParam("to") /*@DateTimeFormat(pattern = "yyyy-MM-dd")*/  String toDate, Model model) {
        System.out.println(fromDate);
/*String d="2019-04-17 10:00";
String d1="2019-04-17 23:00";*/
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

        System.out.println("updating chart view list " + report.size());
        Map<Object, Object> map;
        list = new ArrayList<>();
        dataPoints1 = new ArrayList<>();

        for (DustReport r : report) {
            map = new TreeMap<>();
            map.put("x", r.getDate());
            map.put("y", r.getDens());
            dataPoints1.add(map);
        }
        //System.out.println(dataPoints1.size());
        list.add(dataPoints1);

    }


}