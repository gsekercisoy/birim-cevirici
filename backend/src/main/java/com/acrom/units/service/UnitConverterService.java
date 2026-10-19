package com.acrom.units.service;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class UnitConverterService {

    /** kategori -> (birim -> [görünen ad, katsayı] veya sıcaklıkta özel işaret) */
    private static final Map<String, Map<String, Object[]>> TABLE = new LinkedHashMap<>();

    static {
        Map<String, Object[]> len = new LinkedHashMap<>();
        len.put("mm", new Object[]{"Milimetre", 0.001});
        len.put("cm", new Object[]{"Santimetre", 0.01});
        len.put("m", new Object[]{"Metre", 1.0});
        len.put("km", new Object[]{"Kilometre", 1000.0});
        len.put("inch", new Object[]{"İnç", 0.0254});
        len.put("ft", new Object[]{"Fit", 0.3048});
        len.put("mi", new Object[]{"Mil", 1609.344});
        len.put("nmi", new Object[]{"Deniz mili", 1852.0});
        TABLE.put("uzunluk", len);

        Map<String, Object[]> w = new LinkedHashMap<>();
        w.put("mg", new Object[]{"Miligram", 0.000001});
        w.put("g", new Object[]{"Gram", 0.001});
        w.put("kg", new Object[]{"Kilogram", 1.0});
        w.put("t", new Object[]{"Ton", 1000.0});
        w.put("lb", new Object[]{"Libre", 0.45359237});
        w.put("oz", new Object[]{"Ons", 0.028349523});
        TABLE.put("agirlik", w);

        Map<String, Object[]> t = new LinkedHashMap<>();
        t.put("C", new Object[]{"Santigrat", "C"});
        t.put("F", new Object[]{"Fahrenhayt", "F"});
        t.put("K", new Object[]{"Kelvin", "K"});
        t.put("Ra", new Object[]{"Rankine", "Ra"});
        TABLE.put("sicaklik", t);

        Map<String, Object[]> v = new LinkedHashMap<>();
        v.put("ml", new Object[]{"Mililitre", 0.001});
        v.put("l", new Object[]{"Litre", 1.0});
        v.put("m3", new Object[]{"Metreküp", 1000.0});
        v.put("cup", new Object[]{"Bardak (ABD)", 0.236588});
        v.put("pt", new Object[]{"Pint (ABD)", 0.473176});
        v.put("gal", new Object[]{"Galon (ABD)", 3.785412});
        TABLE.put("hacim", v);

        Map<String, Object[]> sp = new LinkedHashMap<>();
        sp.put("ms", new Object[]{"Metre/sn", 1.0});
        sp.put("kmh", new Object[]{"Km/saat", 0.2777778});
        sp.put("mph", new Object[]{"Mil/saat", 0.44704});
        sp.put("kn", new Object[]{"Knot", 0.5144444});
        sp.put("c", new Object[]{"Işık hızı", 299792458.0});
        TABLE.put("hiz", sp);

        Map<String, Object[]> d = new LinkedHashMap<>();
        d.put("b", new Object[]{"Bit", 1.0});
        d.put("B", new Object[]{"Bayt", 8.0});
        d.put("KB", new Object[]{"Kilobayt", 8 * 1024.0});
        d.put("MB", new Object[]{"Megabayt", 8 * 1024 * 1024.0});
        d.put("GB", new Object[]{"Gigabayt", 8 * 1024 * 1024 * 1024.0});
        d.put("TB", new Object[]{"Terabayt", 8 * 1024.0 * 1024 * 1024 * 1024});
        TABLE.put("veri", d);
    }

    public Map<String, Map<String, String>> categories() {
        Map<String, Map<String, String>> out = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, Object[]>> cat : TABLE.entrySet()) {
            Map<String, String> units = new LinkedHashMap<>();
            for (Map.Entry<String, Object[]> u : cat.getValue().entrySet()) {
                units.put(u.getKey(), String.valueOf(u.getValue()[0]));
            }
            out.put(cat.getKey(), units);
        }
        return out;
    }

    public double convert(String unit, double value, String fromUnit, String toUnit) {
        Map<String, Object[]> units = TABLE.get(unit);
        if (units == null || !units.containsKey(fromUnit) || !units.containsKey(toUnit)) {
            throw new IllegalArgumentException("Bilinmeyen birim: " + unit + " / " + fromUnit + " / " + toUnit);
        }

        if ("sicaklik".equals(unit)) {
            double k = toKelvin(value, fromUnit);
            return fromKelvin(k, toUnit);
        }
        double base = value * (Double) units.get(fromUnit)[1];
        return base / (Double) units.get(toUnit)[1];
    }

    private double toKelvin(double v, String unit) {
        if ("C".equals(unit)) return v + 273.15;
        if ("F".equals(unit)) return (v - 32) * 5 / 9 + 273.15;
        if ("Ra".equals(unit)) return v * 5 / 9;
        return v; // K
    }

    private double fromKelvin(double k, String unit) {
        if ("C".equals(unit)) return k - 273.15;
        if ("F".equals(unit)) return (k - 273.15) * 9 / 5 + 32;
        if ("Ra".equals(unit)) return k * 9 / 5;
        return k; // K
    }

    public Map<String, Double> convertAll(String unit, double value, String fromUnit) {
        Map<String, Double> out = new LinkedHashMap<>();
        for (String u : TABLE.get(unit).keySet()) {
            out.put(u, convert(unit, value, fromUnit, u));
        }
        return out;
    }
}
