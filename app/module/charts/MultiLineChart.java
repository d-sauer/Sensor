package module.charts;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import module.charts.MultiLineChart.MultiLineChartDataSet.MultiLineChartDataSetValue;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import util.DateUtils;
import util.data.WDate;

/**
 * <pre>
 *     minDate = min,
 *     maxDate = max,
 *     minValue = min,
 *     maxValue = max,
 *     data = [
 *             { 
 *                 color : "#FF0000",  
 *                 name : "heat",
 *                 values : [ { dateTime:"2012-11-22", value : 1.0  }, {dateTime:"2012-11-23", value : 50.0 }, { dateTime:"2012-11-24", value : 30.0 } ]
 *             },
 *             { 
 *                 color : "#00FF00",  
 *                 name : "cool",
 *                 values : [ { dateTime:"2012-11-22", value : 1.0  }, {dateTime:"2012-11-23", value : 20.0 }, { dateTime:"2012-11-24", value : 40.0 } ]
 *             }
 *            ];
 * </pre>
 */
public class MultiLineChart {

    public Long minDate;
    public Long maxDate;
    public Double minValue;
    public Double maxValue;
    public String yAxisLabel;
    private List<MultiLineChartDataSet> lDataSet = new LinkedList<MultiLineChartDataSet>();

    public static class MultiLineChartDataSet {
        private MultiLineChart parent;
        public List<MultiLineChart.MultiLineChartDataSet.MultiLineChartDataSetValue> dataSetValues = new LinkedList<MultiLineChart.MultiLineChartDataSet.MultiLineChartDataSetValue>();
        public String color;
        public String name;
        private Tick graphTick;
        private WDate previousDate;
        private SimpleDateFormat inFormat;
        private SimpleDateFormat outFormat;

        public MultiLineChartDataSet(MultiLineChart parent) {
            this.parent = parent;
        }

        public class MultiLineChartDataSetValue {
            public Long dateMs;
            public Double value;
            public Long tick;
            private MultiLineChartDataSet parentDataSet;

            @Override
            public String toString() {
                return String.format("dateMs:%d  value:%f  tick: %s", dateMs, value, tick);
            }
            
            public void doTick(WDate previousDate, WDate currentDate) {
                if (parentDataSet.graphTick != null) {
                    if (parentDataSet.graphTick.doTick(previousDate, currentDate))
                        this.tick = currentDate.getTimeInMillis();
                    if (parentDataSet.graphTick.isLastTick(previousDate, currentDate))
                        this.tick = currentDate.getTimeInMillis();
                }
            }
        }

        public Tick setTick(Date dateFrom, Date dateTo) {
            return setTick(dateFrom, dateTo, null);
        }
        
        public Tick setTick(Date dateFrom, Date dateTo, Integer maxTicks) {
            if (graphTick == null) {
                graphTick = new Tick(dateFrom, dateTo);
            }
            
            if (maxTicks != null) {
                graphTick.setMaxTicks(maxTicks);
            }
            
            return graphTick;
        }
        
        public Tick getTick() {
            return graphTick;
        }
        
        public MultiLineChartDataSetValue addValue(Long dateMs, Double value) {
            MultiLineChartDataSetValue v = new MultiLineChartDataSetValue();
            v.parentDataSet = this;
            v.dateMs = dateMs;
            v.value = value;

            if (dateMs != null && value != null) {
                // odredi min i max
                if (parent.minDate == null)
                    parent.minDate = v.dateMs;
                else if (v.dateMs < parent.minDate)
                    parent.minDate = v.dateMs;

                if (parent.maxDate == null)
                    parent.maxDate = v.dateMs;
                else if (v.dateMs > parent.maxDate)
                    parent.maxDate = v.dateMs;

                if (parent.minValue == null)
                    parent.minValue = v.value;
                else if (v.value < parent.minValue)
                    parent.minValue = v.value;

                if (parent.maxValue == null)
                    parent.maxValue = v.value;
                else if (v.value > parent.maxValue)
                    parent.maxValue = v.value;
            }
            
            dataSetValues.add(v);

            return v;
        }
        
        public MultiLineChartDataSetValue addValueAndTick(Long dateMs, Double value) {
            MultiLineChartDataSetValue dsv = addValue(dateMs, value);
            
            WDate currentDate = new WDate();
            currentDate.set(dateMs);
            currentDate.setInFormat(this.inFormat);
            currentDate.setOutFormat(this.inFormat);
            
            if (graphTick != null) {
                if (previousDate == null) {
                    previousDate = new WDate();
                    previousDate.setFormat(currentDate.getInFormat());
                }
                dsv.doTick(previousDate, currentDate);
            }
            
            return dsv;
        }
    }

    /**
     * Definira koja vremena se prikazuju na grafu, svaki tick je jedan element po X osi grafa
     * @author davor
     *
     */
    public static class Tick {
        private int tickUnit = Calendar.DATE;
        private int tickMax = 1; // 1 day
        private Integer maxTickCount;
        private Integer tickCount = 0;
        
        public Tick(Date dateFrom, Date dateTo) {
            calculateTick(dateFrom, dateTo);
        }
        
        public void setMaxTicks(int maxTicks) {
            maxTickCount = maxTicks;
        }
        
        private void calculateTick(Date dateFrom, Date dateTo) {
            Long daysDelta = (dateTo.getTime() - dateFrom.getTime()) / (24 * 60 * 60 * 1000);
            
            if (daysDelta == 0) {
                tickUnit = Calendar.MINUTE;
                tickMax = 119;
            }
            else if (daysDelta <= 31) {
                tickUnit = Calendar.DATE;
                tickMax = 1;
            }
            else {
                tickUnit = Calendar.MONTH;
                tickMax = 1;
            }
        }
        
        public boolean doTick(Date previousDate, Date currentDate) {
            return doTick(previousDate, currentDate, true);
        }
        
        public boolean doTick(Date previousDate, Date currentDate,boolean setPrevValueToCurrent) {
            return doTick(new WDate(previousDate), new WDate(currentDate), setPrevValueToCurrent);
        }
        
        public boolean doTick(WDate previousDate, WDate currentDate) {
            return doTick(previousDate, currentDate, true);
        }
        
        public boolean doTick(WDate previousDate, WDate currentDate, boolean setPrevValueToCurrent) {
            boolean doTick = false;
            if (maxTickCount!=null)
                tickCount++;
            
            if (previousDate.get() == null)
                doTick = true;
            else {
                Long delta = null;
                if (tickUnit == Calendar.MINUTE)
                    delta = Math.abs(currentDate.getTimeInMillis() - previousDate.getTimeInMillis());
                else {
                    delta = Math.abs(currentDate.getDateInMillis() - previousDate.getDateInMillis());
                }

                if (tickUnit == Calendar.MINUTE && DateUtils.getMinutes(delta) > tickMax)
                    doTick = true;
                else if (tickUnit == Calendar.DATE && DateUtils.getDays(delta) >= tickMax)
                    doTick = true;
                else if (tickUnit == Calendar.MONTH && previousDate.getCalendar().get(tickUnit) != currentDate.getCalendar().get(tickUnit))
                    doTick = true;
                else
                    doTick = false;
            }
            
            if (setPrevValueToCurrent && doTick)
                previousDate.set(currentDate);
            
            return doTick;
        }
        
        public boolean isLastTick() {
            if (maxTickCount != null) {
                if (maxTickCount.intValue() == tickCount.intValue())
                    return true;
                else
                    return false;
            } else {
                return false;
            }
        }
        
        public boolean isLastTick(WDate previousDate, WDate currentDate) {
            if (maxTickCount != null && maxTickCount.intValue() == tickCount.intValue()) {
                Long msDelta = Math.abs(currentDate.getTimeInMillis() - previousDate.getTimeInMillis());
                
                if (tickUnit == Calendar.MINUTE && DateUtils.getHours(msDelta) >= 2)
                    return true;
                else if (tickUnit == Calendar.DATE && DateUtils.getHours(msDelta) >= 12)
                    return true;
                else if (tickUnit == Calendar.MONTH && DateUtils.getHours(msDelta) >= 24)
                    return true;
                else
                    return false;
            } else {
                return false;
            }
        }
    }
    
    public MultiLineChartDataSet addDataSetWithTick(String dataSetName, String color, Date dateFrom, Date dateTo, Integer maxTicks, String dateFormatInOut) {
        MultiLineChartDataSet ds = addDataSet(dataSetName, color);
        ds.setTick(dateFrom, dateTo, maxTicks);
        
        ds.inFormat = new SimpleDateFormat(dateFormatInOut);
        ds.outFormat = ds.inFormat;
        
        return ds;
    }
    
    public MultiLineChartDataSet addDataSet(String dataSetName, String color) {
        MultiLineChartDataSet dataSet = new MultiLineChartDataSet(this);
        dataSet.name = dataSetName;
        dataSet.color = color;

        lDataSet.add(dataSet);
        return dataSet;
    }

    public ObjectNode getJSON() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();

        // chart data
        Field[] fields = this.getClass().getFields();
        for (Field field : fields) {
            Object of = field.get(this);
            // rootNode.put(field.getName(), of != null ? of.toString() : "");
            addField(field, of, rootNode, List.class);
        }

        // Data Sets
        ArrayNode jsDataSets = rootNode.putArray("data");
        for (MultiLineChartDataSet dataSet : lDataSet) {
            ObjectNode jsDataSet = jsDataSets.objectNode();
            jsDataSets.add(jsDataSet);

            fields = dataSet.getClass().getFields();
            for (Field field : fields) {
                Object of = field.get(dataSet);
                addField(field, of, jsDataSet, List.class);
            }

            // values
            ArrayNode jsDataSetValues = jsDataSet.putArray("values");
            for (MultiLineChartDataSetValue dsValue : dataSet.dataSetValues) {
                ObjectNode jsDataSetValue = jsDataSets.objectNode();
                jsDataSetValues.add(jsDataSetValue);

                fields = dsValue.getClass().getFields();

                for (Field field : fields) {
                    Object of = field.get(dsValue);
                    addField(field, of, jsDataSetValue, List.class);
                }
            }

        }

        // rootNode.put("y_axis_label", "Degree Day");
        //
        // ArrayNode ar = rootNode.putArray("data");
        // for(DBSensorDataDegreeDay dd : lst) {
        // ObjectNode jn = mapper.createObjectNode();
        // ar.add(jn);
        //
        // jn.put("dateTime",
        // DateUtils.getFormatedDateTime(dd.dateTime.getTime()));
        // jn.put("heat", dd.heat);
        // jn.put("cool", dd.cool);
        // }

        return rootNode;
    }

    public void addField(Field field, Object value, ObjectNode jsonNode, Class... ignoreClass) {
        if (value != null) {
            if (value instanceof Double)
                jsonNode.put(field.getName(), (Double) value);
            else if (value instanceof Long)
                jsonNode.put(field.getName(), (Long) value);
            else if (value instanceof String)
                jsonNode.put(field.getName(), value.toString());
            else {
                boolean ignore = false;
                for (Class c : ignoreClass) {
                    if (field.getType().isAssignableFrom(c)) {
                        ignore = true;
                        break;
                    }
                }

                if (ignore == false)
                    jsonNode.put(field.getName(), value.toString());
            }
        } else {
            jsonNode.putNull(field.getName());
        }
    }

}
