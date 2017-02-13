var scale = require('d3-scale')
var shape = require('d3-shape')
var axis = require('d3-axis')
var time = require('d3-time')
var timeFormat = require('d3-time-format')
var selection = require('d3-selection')
var transition = require('d3-transition')
var ease = require('d3-ease')

function Chart(node, initial) {
  var margin = [10, 10, 30, 20]
  var width = node.offsetWidth - margin[1] - margin[3]
  var height = node.offsetHeight - margin[0] - margin[2]
  var now = Date.now()
  var data = initial || []
  var maxY = 10
  var hasNewData = false

  var x = scale.scaleTime()
    .domain([now - 60000, now])
    .range([0, width])

  var y = scale.scaleLinear()
    .domain([0, maxY])
    .range([height, 0])

  var area = shape.area()
    .curve(shape.curveMonotoneX)
    .x(function(d) {
      return x(d.key)
    })
    .y0(height)
    .y1(function(d) {
      return y(d.value)
    })

  var xAxis = axis.axisBottom(x)
    .ticks(time.timeSecond.every(5))
    .tickFormat(timeFormat.timeFormat('%I:%M:%S'))

  var yAxis = axis.axisLeft(y)
    .ticks(5)
    .tickSizeInner(-width)

  var svg = selection.select(node)
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

    areaSvg
      .attr('d', area)
      .transition()
      .duration(1000)
      .ease(ease.easeLinear)
      .attr('transform', 'translate(' + x(now - 60000) + ')')
      .on('end', tick)

    xAxisSvg
      .transition()
      .duration(1000)
      .ease(ease.easeLinear)
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
