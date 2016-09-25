var d3 = require('d3')

function Chart(node) {
  var margin = [10, 10, 30, 20]
  var width = node.offsetWidth - margin[1] - margin[3]
  var height = node.offsetHeight - margin[0] - margin[2]
  var now = Date.now()
  var data = []
  var maxY = 10
  var hasNewData = false

  var x = d3.scaleTime()
    .domain([now - 60000, now])
    .range([0, width])

  var y = d3.scaleLinear()
    .domain([0, maxY])
    .range([height, 0])

  var line = d3.line()
    .x(function(d) {
      return x(d.key)
    })
    .y(function(d) {
      return y(d.value)
    });

  var area = d3.area()
    .x(function(d) {
      return x(d.key)
    })
    .y0(height)
    .y1(function(d) {
      return y(d.value)
    })

  var xAxis = d3.axisBottom(x)
    .ticks(d3.timeSecond.every(5))
    .tickFormat(d3.timeFormat('%I:%M:%S'))

  var yAxis = d3.axisLeft(y)
    .ticks(5)
    .tickSizeInner(-width)

  var svg = d3.select(node)
    .append('svg')
      .attr('width', width + margin[1] + margin[3])
      .attr('height', height + margin[0] + margin[2])
    .append('g')
      .attr('transform', 'translate(' + margin[3] + ',' + margin[0] + ')')

  var xAxisSvg = svg.append('g')
    .attr('class', 'x-axis')
    .attr('transform', 'translate(0,' + height + ')')
    .call(xAxis)

  var yAxisSvg = svg.append('g')
    .attr('class', 'y-axis')
    .attr('transform', 'translate(0,0)')
    .call(yAxis)

  var lineSvg = svg.append('path')
    .datum(data)
    .attr('class', 'line')
    .attr('d', line)
  
  var areaSvg = svg.append('path')
    .datum(data)
    .attr('class', 'area')
    .attr('d', area)

  function tick() {
    now = Date.now()
    x.domain([now - 60000, now])

    if(!hasNewData) {
      data.push({key: now, value: 0})
    } else {
      hasNewData = false
    }

    lineSvg
      .attr("d", line)
      .transition()
      .duration(1000)
      .ease(d3.easeLinear)
      .attr("transform", "translate(" + x(now - 60000) + ")")
      .on("end", tick)

    areaSvg
      .attr("d", area)
      .transition()
      .duration(1000)
      .ease(d3.easeLinear)
      .attr("transform", "translate(" + x(now - 60000) + ")")

    xAxisSvg
      .transition()
      .duration(1000)
      .ease(d3.easeLinear)
      .call(xAxis)

    yAxisSvg
      .transition()
      .call(yAxis)

    for(var i in data) {
      if(data[i].key < now - 60000) {
        data.splice(i, 1)
      } else {
        break
      }
    }
  }

  this.start = tick
  this.insert = function(d) {
    if(d.value > maxY) {
      maxY = d.value
      y.domain([0, maxY])
    }

    for(var i = data.length - 1; i > 0; i--) {
      if(d.key > data[i].key) {
        data.splice(i + 1, 0, d)
        break
      }
    }

    hasNewData = true
  }
}

module.exports = Chart
