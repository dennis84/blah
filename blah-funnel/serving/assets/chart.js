var scale = require('d3-scale')
var axis = require('d3-axis')
var selection = require('d3-selection')
var array = require('d3-array')

function render(node, data) {
  var margin = [20, 20, 80, 40]
  var width = node.offsetWidth - margin[1] - margin[3]
  var height = node.offsetHeight - margin[0] - margin[2]

  var x = scale.scaleBand()
    .domain(data.map(function(d) {
      return d.key
    }))
    .rangeRound([0, width])
    .paddingInner(0.2)

  var y = scale.scaleLinear()
    .domain([0, array.max(data, function(d) {
      return d.value
    })])
    .range([height, 0])

  var xAxis = axis.axisBottom(x)
    .ticks(24)

  var yAxis = axis.axisLeft(y)
    .ticks(5)
    .tickSizeInner(-width)

  var svg = selection.select(node)
    .append('svg')
      .attr('width', width + margin[1] + margin[3])
      .attr('height', height + margin[0] + margin[2])
    .append('g')
      .attr('transform', 'translate(' + margin[3] + ',' + margin[0] + ')')

  svg.append('g')
    .attr('class', 'x-axis')
    .attr('transform', 'translate(0,' + height + ')')
    .call(xAxis)
    .selectAll('.tick text')
      .style('text-anchor', 'end')
      .attr('dx', '-10px')
      .attr('dy', '-6px')
      .attr('transform', 'rotate(-90)')

  svg.append('g')
    .attr('class', 'y-axis')
    .call(yAxis)

  svg.selectAll('.bar').data(data)
    .enter().append('rect')
      .attr('class', 'bar')
      .attr('x', function(d) {
        return x(d.key)
      })
      .attr('width', x.bandwidth())
      .attr('y', function(d) {
        return y(d.value)
      })
      .attr('height', function(d) {
        return height - y(d.value)
      })
}

function init(render) {
  return function(node) {
    var args = arguments
    setTimeout(function draw() {
      node.innerHTML = ''
      render.apply(null, args)
      window.addEventListener('resize', draw)
    }, 0)
  }
}

module.exports = init(render)
