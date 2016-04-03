import d3 from 'd3'

function render(node, data) {
  var margin = [20, 20, 80, 40]
  var width = node.offsetWidth - margin[1] - margin[3]
  var height = node.offsetHeight - margin[0] - margin[2]

  var x = d3.scale.ordinal()
    .domain(data.map(d => d.key))
    .rangePoints([0, width])

  var y = d3.scale.linear()
    .domain(d3.extent(data, d => d.value))
    .range([height, 0])

  var xAxis = d3.svg.axis()
    .scale(x)
    .orient('bottom')
    .ticks(24)

  var yAxis = d3.svg.axis()
    .scale(y)
    .orient('left')
    .ticks(5)
    .innerTickSize(-width)

  var line = d3.svg.line()
    .x(d => x(d.key))
    .y(d => y(d.value))

  var graph = d3.select(node)
    .append('svg')
      .attr('width', width + margin[1] + margin[3])
      .attr('height', height + margin[0] + margin[2])
    .append('g')
      .attr('transform', `translate(${margin[3]},${margin[0]})`)

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
    .attr('class', 'line')
    .attr('d', line)
}

export default render
