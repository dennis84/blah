import * as d3 from 'd3'
import xtend from 'xtend'
import init from './init'

function render(node, data, options = {}) {
  options = xtend({donutWidth: 40}, options)
  var width = node.offsetWidth
  var height = node.offsetHeight
  var radius = Math.min(width, height) / 2
  var total = data.map(x => x.value).reduce((a,b) => a + b) 

  var color = d3.scaleOrdinal()
    .range(Array.apply(null, {length: 26})
      .map((x,i) => String.fromCharCode(97 + i)))

  var arc = d3.arc()
    .outerRadius(radius)
    .innerRadius(radius - options.donutWidth)

  var pie = d3.pie()
    .sort(null)
    .value(d => d.value)

  var svg = d3.select(node)
    .append('svg')
      .attr('width', width)
      .attr('height', height)
    .append('g')
      .attr('transform', `translate(${width / 2},${height / 2})`)

  var g = svg.selectAll('.arc').data(pie(data))
    .enter().append('g')
      .attr('class', d => `arc arc-${color(d.data.key)}`)

  g.append('path')
    .attr('d', arc)

  g.append('text')
    .attr('transform', d => `translate(${arc.centroid(d)})`)
    .text(d => Math.round(d.data.value / total * 100) + '%')
}

export default init(render)
