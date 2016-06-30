import * as d3 from 'd3'
import init from './init'

function render(node, data) {
  var margin = [20, 20, 80, 40]
  var width = node.offsetWidth - margin[1] - margin[3]
  var height = node.offsetHeight - margin[0] - margin[2]

  var x = d3.scalePoint()
    .domain(data.map(d => d.key))
    .range([0, width])

  var y = d3.scaleLinear()
    .domain(d3.extent(data, d => d.value))
    .range([height, 0])

  var xAxis = d3.axisBottom(x)
    .ticks(24)

  var yAxis = d3.axisLeft(y)
    .ticks(5)
    .tickSizeInner(-width)

  var line = d3.line()
    .x(d => x(d.key))
    .y(d => y(d.value))

  var area = d3.area()
    .x(d => x(d.key))
    .y0(height)
    .y1(d => y(d.value))

  var graph = d3.select(node)
    .append('svg')
      .attr('width', width + margin[1] + margin[3])
      .attr('height', height + margin[0] + margin[2])
    .append('g')
      .attr('transform', `translate(${margin[3]},${margin[0]})`)

  graph.selectAll('dot').data(data)
    .enter().append('circle')
		  .attr('class', 'circle')
      .attr('r', 3.5)
      .attr('cx', (d) => x(d.key))
      .attr('cy', (d) => y(d.value))

  graph.append('g')
		.attr('class', 'x-axis')
	  .attr('transform', `translate(0, ${height})`)
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

export default init(render)
