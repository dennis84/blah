var scale = require('d3-scale')
var axis = require('d3-axis')
var selection = require('d3-selection')
var array = require('d3-array')
var shape = require('d3-shape')
var init = require('./init')

function render(node, data) {
  if(0 === data.length) return
  var margin = [20, 20, 80, 40]
  var width = node.offsetWidth - margin[1] - margin[3]
  var height = node.offsetHeight - margin[0] - margin[2]

  var first = data[0]
  var segments = data.map(function(segment, i) {
    return {id: i, values: segment}
  })

  var x = scale.scalePoint()
    .domain(first.map(function(d) {
      return d.key
    }))
    .range([0, width])

  var y = scale.scaleLinear()
    .domain([
      array.min(segments, function(c) {
        return array.min(c.values, function(d) {
          return d.value
        })
      }),
      array.max(segments, function(c) {
        return array.max(c.values, function(d) {
          return d.value
        })
      }),
    ])
    .range([height, 0])

  var z = scale.scaleOrdinal()
    .domain(segments.map(function(c) {
      return c.id
    }))

  var color = scale.scaleOrdinal()
    .range(Array.apply(null, {length: 26}).map(function(x, i) {
      return String.fromCharCode(97 + i)
    }))

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

  var segment = graph.selectAll('.segment')
    .data(segments)
    .enter().append('g')
      .attr('class', function(d) {
        return 'segment segment-' + color(d.id)
      })

  segment.append('path')
    .attr('d', function(d) {
      return area(d.values)
    })
    .style('stroke', function(d) {
      return z(d.id)
    })
    .attr('class', 'area')

  segment.append('path')
    .attr('d', function(d) {
      return line(d.values)
    })
    .style('stroke', function(d) {
      return z(d.id)
    })
    .attr('class', 'line')

  var tooltip = selection.select(node)
    .append('div')
    .attr('class', 'tooltip box')
    .style('visibility', 'hidden')

  segment.selectAll('dot')
    .data(function(d) {
      return d.values
    })
    .enter()
      .append('circle')
      .attr('class', 'circle')
      .attr('r', 3.5)
      .attr('cx', function(d) {
        return x(d.key)
      })
      .attr('cy', function(d) {
        return y(d.value)
      })
      .on('mouseover', function(d) {
        tooltip
          .style('visibility', 'visible')
          .style('top', y(d.value) - 30 + 'px')
          .style('left',x(d.key) + 'px')
          .html(d.key + ': <b>' + d.value + '</b>')
        selection.select(this).attr('r', 6)
      })
      .on('mouseout', function() {
        tooltip.style('visibility', 'hidden')
        selection.select(this).attr('r', 3.5)
      })
}

module.exports = init(render)
