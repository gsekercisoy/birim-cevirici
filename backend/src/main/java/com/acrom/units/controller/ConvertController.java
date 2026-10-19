package com.acrom.units.controller;

import com.acrom.units.service.UnitConverterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ConvertController {

    private final UnitConverterService service;

    public ConvertController(UnitConverterService service) {
        this.service = service;
    }

    @GetMapping("/categories")
    public Map<String, Map<String, String>> categories() {
        return service.categories();
    }

    @GetMapping("/convert")
    public Map<String, Object> convert(@RequestParam String unit,
                                       @RequestParam String from,
                                       @RequestParam String to,
                                       @RequestParam double value) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("unit", unit);
        out.put("from", from);
        out.put("to", to);
        out.put("value", value);
        out.put("result", service.convert(unit, value, from, to));
        return out;
    }

    @GetMapping("/convert/all")
    public Map<String, Object> convertAll(@RequestParam String unit,
                                          @RequestParam String from,
                                          @RequestParam double value) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("unit", unit);
        out.put("from", from);
        out.put("value", value);
        out.put("results", service.convertAll(unit, value, from));
        return out;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> bad(IllegalArgumentException ex) {
        Map<String, String> err = new LinkedHashMap<>();
        err.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(err);
    }
}
