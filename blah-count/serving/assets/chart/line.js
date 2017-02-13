var scale = require('d3-scale')
var axis = require('d3-axis')
var selection = require('d3-selection')
var array = require('d3-array')
var shape = require('d3-shape')
var init = require('./init')

function render(node, data) {
  var margin = [20, 20, 80, 40]
  var width = node.offsetWidth - margin[1] - margin[3]
  var height = node.offsetHeight - margin[0] - margin[2]

  var x = scale.scalePoint()
    .domain(data.map(function(d) {
      return d.key
    }))
    .range([0, width])

  var y = scale.scaleLinear()
    .domain(array.extent(data, function(d) {
      return d.value
    }))
    .range([height, 0])

  var xAxis = axis.axisBottom(x)
    .ticks(24)

  var yAxis = axis.axisLeft(y)
    .ticks(5)
    .tickSizeInner(-width)

  var line = shape.line()
    .x(function(d) {
      return x(d.key)
    })
    .y(function(d) {
      return y(d.value)
    })

  var area = shape.area()
    .x(function(d) {
      return x(d.key)
    })
    .y0(height)
    .y1(function(d) {
      return y(d.value)
    })

  var graph = selection.select(node)
    .append('svg')
      .attr('width', width + margin[1] + margin[3])
      .attr('height', height + margin[0] + margin[2])
    .append('g')
      .attr('transform', 'translate(' + margin[3] + ',' + margin[0] + ')')

  graph.selectAll('dot').data(data)
    .enter().append('circle')
      .attr('class', 'circle')
      .attr('r', 3.5)
      .attr('cx', function(d) {
        return x(d.key)
      })
      .attr('cy', function(d) {
        return y(d.value)
      })

  graph.append('g')
    .attr('class', 'x-axis')
    .attr('transform', 'translate(0,' + height + ')')
    .call(xAxis)
    .selectAll('.tick text')
      .style('text-anchor', 'end')
      .attr('dx', '-10px')
      .attr('dy', '-6px')
      .attr('transform', 'rotate(-90)')

  graph.append('g')
    .attr('class', 'y-axis')
    .attr('transform', 'translate(0,0)')
    .call(yAxis)

  graph.append('path')
    .datum(data)
    .attr('class', 'area')
    .attr('d', area)

  graph.append('path')
    .datum(data)
    .attr('class', 'line')
    .attr('d', line)
}

module.exports = init(render)
