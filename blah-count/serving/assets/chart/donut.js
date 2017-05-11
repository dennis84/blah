var scale = require('d3-scale')
var selection = require('d3-selection')
var shape = require('d3-shape')
var xtend = require('xtend')
var init = require('./init')

function render(node, data, options) {
  options = xtend({donutWidth: 40}, options)
  var width = node.offsetWidth
  var height = node.offsetHeight
  var radius = Math.min(width, height) / 2
  var total = data.map(function(x) {
    return x.value
  }).reduce(function(a,b) {
    return a + b
  }) 

  var color = scale.scaleLinear()
    .domain([1, data.length])
    .range([1, data.length])

  var arc = shape.arc()
    .outerRadius(radius)
    .innerRadius(radius - options.donutWidth)

  var pie = shape.pie()
    .sort(null)
    .value(function(d) {
      return d.value
    })

  var svg = selection.select(node)
    .append('svg')
      .attr('width', width)
      .attr('height', height)
    .append('g')
      .attr('transform', 'translate(' + width / 2 + ',' + height / 2 + ')')

  var g = svg.selectAll('.arc').data(pie(data))
    .enter().append('g')
      .attr('class', function(d, i) {
        return 'arc arc-' + color(i + 1)
      })

  g.append('path')
    .attr('d', arc)

  g.append('text')
    .attr('transform', function(d) {
      return 'translate(' + arc.centroid(d) + ')'
    })
    .text(function(d) {
      return Math.round(d.data.value / total * 100) + '%'
    })
}

module.exports = init(render)
