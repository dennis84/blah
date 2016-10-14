var d3 = require('d3')

function chart(node, data) {
  var width = node.clientWidth
  var height = width
  var radius = width / 2
  var donutWidth = 3

  var cpuUsageColor = d3.scaleOrdinal()
    .range(Array.apply(null, {length: 26}).map(function(x, i) {
      return String.fromCharCode(97 + i)
    }))
  var cpuUsageArc = d3.arc()
    .outerRadius(width / 2)
    .innerRadius(width / 2 - donutWidth - 0.5)
  var cpuUsagePie = d3.pie()
    .sort(null)
    .value(function(d) {
      return d
    })

  var memUsageColor = d3.scaleOrdinal()
    .range(Array.apply(null, {length: 26}).map(function(x, i) {
      return String.fromCharCode(97 + i)
    }))
  var memUsageArc = d3.arc()
    .outerRadius(width / 2 - donutWidth)
    .innerRadius(width / 2 - donutWidth * 2)
  var memUsagePie = d3.pie()
    .sort(null)
    .value(function(d) {
      return d
    })

  var svg = d3.select(node)
    .append('svg')
      .attr('width', width)
      .attr('height', height)
    .append('g')
      .attr('transform', 'translate(' + width / 2 + ',' + height / 2 + ')')

  svg.selectAll('.arc-cpu-usage')
    .data(cpuUsagePie([data.cpu_usage, 100 - data.cpu_usage]))
    .enter().append('g')
    .attr('class', function(d) {
      return 'arc arc-cpu-usage arc-' + cpuUsageColor(d.data)
    })
    .append('path')
    .attr('d', cpuUsageArc)

  svg.selectAll('.mem-cpu-usage')
    .data(cpuUsagePie([data.mem_usage, 100 - data.mem_usage]))
    .enter().append('g')
    .attr('class', function(d) {
      return 'arc mem-cpu-usage arc-' + memUsageColor(d.data)
    })
    .append('path')
    .attr('d', memUsageArc)
}

module.exports = chart
