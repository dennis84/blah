import d3 from 'd3'

function render(node, data) {
  var margin = [20, 20, 80, 40]
  var width = node.offsetWidth - margin[1] - margin[3]
  var height = node.offsetHeight - margin[0] - margin[2]

  var x = d3.scale.ordinal()
    .domain(data.map((d) => d.key))
    .rangeRoundBands([0, width], 0.1)

  var y = d3.scale.linear()
    .domain([0, d3.max(data, (d) => d.value)])
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

  var svg = d3.select(node)
    .append('svg')
      .attr('width', width + margin[1] + margin[3])
      .attr('height', height + margin[0] + margin[2])
    .append('g')
      .attr('transform', `translate(${margin[3]},${margin[0]})`)

  svg.append('g')
		.attr('class', 'x axis')
	  .attr('transform', `translate(0, ${height})`)
	  .call(xAxis)
    .selectAll('.tick text')
      .style('text-anchor', 'end')
      .attr('dx', '-10px')
      .attr('dy', '-6px')
      .attr('transform', 'rotate(-90)')

  svg.append('g')
    .attr('class', 'y axis')
    .call(yAxis)

  svg.selectAll('.bar').data(data)
    .enter().append('rect')
      .attr('class', 'bar')
      .attr('x', (d) => x(d.key))
      .attr('width', x.rangeBand())
      .attr('y', (d) => y(d.value))
      .attr('height', (d) => height - y(d.value))
}

export default render
