var chart = (function(lib) {
    var multiline = lib.multiline = lib.multiline || {};

    multiline.data = { svg : null   }
    
    
    multiline.init = function(elementId, json) {
        var data = json.data;
//        var datumi = [];
//        var values = [];
        var ticks = [];
        
        // pretvori datum u milisekundama i tick u milisekundama u datumski objekt
        // napravi listu datuma i polja koji se koriste za min i max - ne koristi se više (java kod punjenja odredi min i max)
        data.forEach(function(d) {
            d.values.forEach(function(v) {
//                datumi.push(v.dateTime);
//                values.push(v.value);
                
                v.dateTime = new Date(v.dateMs);
                
                if (v.tick != null) {
                    v.tick = new Date(v.tick);
                    
                    var exists = false;
                    for(var i = 0; i < ticks.length; i++) {
                        if(ticks[i].getTime() == v.tick.getTime()) {
                            exists = true;
                        }
                    }
                    
                    if(exists == false)
                        ticks.push(v.tick);
                }
            })
        });

        var hasData = false;
        if (ticks.length != 0)
            hasData = true;
        
        var margin = {top: 50, right: 50, bottom: 70, left: 60},
        width = 700 - margin.left - margin.right,
        height = 300 - margin.top;
    
        var x = d3.time.scale()
            .range([0, width]);
        
        var y = d3.scale.linear()
            .range([height, 0]);
        
        // .ticks(N) -definira broj vidljivih markera po X osi. 5 - pet vrijednosti po X osi
        
        var format1 = d3.time.format("%d.%m.%Y");
        var format2 = d3.time.format("%H:%M");
        
        
        var xAxis = d3.svg.axis()
            .scale(x)
            .orient("bottom")
            .tickValues(ticks)
            .tickFormat(function(v) {
                                return format1(v);
                
//                            if (v.getHours() != 0)
//                                return format2(v);
//                            else
//                                return format1(v);
                        });
        
        
        var yAxis = d3.svg.axis()
            .scale(y)
            .orient("left");
        
        // mapiranje koje podatke linija treba čitati iz JSONA
        // za X os čita se d.dateTime
        // za Y os čita se d.value
        var line = d3.svg.line()
            .interpolate("basis")
            .x(function(d) {
                            var rX = x(d.dateTime); 
                            return rX; 
                           })   
            .y(function(d) { 
                            var rY = y(d.value);
                            return rY; 
                           });
        
        var svg = d3.select(document.getElementById(elementId)).append("svg")
            .attr("width", width + margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom)
          .append("g")
            .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
        
        
        // extent - pronalazi min i max za X os - datum 
//        x.domain(d3.extent(datumi));
        x.domain([json.minDate, json.maxDate]);
        
        
        // pronalazi min i max za Y os, posebno min, posebno max, dok extent pronalazi oboje
//        y.domain([d3.min(values),  d3.max(values)]);
        y.domain([json.minValue,  json.maxValue]);
        
        var ax =  svg.append("g")
                        .attr("class", "x axis")
                        .attr("transform", "translate(0," + height + ")")
                        .call(xAxis);
    
        // rotiraj samo labele na X osi
        ax.selectAll("text")
        .attr("transform", function(d) {            
            var h1 = this.getBBox().height;
            var w1 = this.getBBox().width;
            return "rotate(45)translate(" + ((w1 + h1) / 2) + "," + 0 + ")";
        }).append("tspan")
            .text(function(d) {
                    if (d.getHours() != 0)
                        return format2(d);
                    else
                        return "";
                  })
            .style("font-size", "10px")
            .attr("x", "-15")
            .attr("y", "30");

        
        svg.append("g")
            .attr("class", "y axis")
            .call(yAxis)
            .append("text")
              .attr("transform", "rotate(-90)")
              .attr("y", 6)
              .attr("dy", ".71em")
              .style("text-anchor", "end")
              .text(json.yAxisLabel);
        
        
        // nacrtaj linije
        // proslijedjuje set podataka iz varijable data
        if (hasData == true) {
            var gData = svg.selectAll(".g_data")
            .data(data)
            .enter().append("g")
            .attr("class", "g_data");
            
            // dodaje vertikalne linije na svako 5 mjesto po X osi
            /*gData.selectAll("line")
                .data(x.ticks(5))
                    .enter().append("line")
                    .attr("x1", x)
                    .attr("x2", x)
                    .attr("y1", 0)
                    .attr("y2", 250)
                    .style("stroke", "#ccc");
             */
            
            // dodaj tekst iznad vertikalnih linija
            /*
            gData.selectAll(".rule")
                .data(x.ticks(5))
                    .enter().append("text")
                    .attr("class", "rule")
                    .attr("x", function(d) {
                                    return x(d);
                               })
                    .attr("y", function(d) {
                                    var gx = x(d);
                                    var gy = gx * Math.sin(315);
                                    return gy;
                               })
                    .attr("dy", -12)
                    .attr("transform", "rotate(-45)")
                    .attr("text-anchor", "left")
                    .text(function(d) {
                            var tt = d.getDate() + "." + d.getMonth(); 
                            return tt; 
                        } )            
                    .style("font-size", "10px");
            */
            
            // crtanje grafa, temeljem proslje�enih podataka data
            gData.append("path")
            .attr("class", "line")
            .attr("d", function(d) { 
                                    var l = line(d.values); 
                                    return l; 
                                    })
            .style("stroke", function(d) { 
                                            return d.color; 
                                          });
            
            
             // nazivi na kraju linija
            gData.append("text")
              .datum(function(d) { return {name: d.name, value: d.values[d.values.length - 1]}; })
              .attr("transform", function(d) { return "translate(" + x(d.value.dateTime) + "," + y(d.value.value) + ")"; })
              .attr("x", 3)
              .attr("dy", ".35em")
              .text(function(d) { return d.name; });    
        }
    }
    
    return lib;
}(chart || {}));