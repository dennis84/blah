var d3 = require('d3')
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

  var color = d3.scaleOrdinal()
    .range(Array.apply(null, {length: 26}).map(function(x,i) {
      return String.fromCharCode(97 + i)
    }))

  var arc = d3.arc()
    .outerRadius(radius)
    .innerRadius(radius - options.donutWidth)

  var pie = d3.pie()
    .sort(null)
    .value(function(d) {
      return d.value
    })

  var svg = d3.select(node)
    .append('svg')
      .attr('width', width)
      .attr('height', height)
    .append('g')
      .attr('transform', 'translate(' + width / 2 + ',' + height / 2 + ')')

  var g = svg.selectAll('.arc').data(pie(data))
    .enter().append('g')
      .attr('class', function(d) {
        return 'arc arc-' + color(d.data.key)
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
