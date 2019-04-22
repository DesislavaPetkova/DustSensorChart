package com.dust.sensor;

import model.DustReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class MainController {

    static Map<Object, Object> map = null;
    static List<List<Map<Object, Object>>> list = new ArrayList<List<Map<Object, Object>>>();
    static List<Map<Object, Object>> dataPoints1 = new ArrayList<Map<Object, Object>>();

    public DustRepository repo;

    @Autowired
    public void setDustRepository(DustRepository repo) {
        this.repo = repo;
    }

    @RequestMapping(value = {"/", "/chart"}, method = RequestMethod.GET)
    public String index(Model model) {
        List<DustReport> report = repo.findAll();
        for (DustReport r : report ) {
            map = new TreeMap<>();
            map.put("x", r.getDate());
            map.put("y", r.getDens());
            dataPoints1.add(map);
        }
        list.add(dataPoints1);

        model.addAttribute("dataPointsList", list);

        return "chart";
    }





}