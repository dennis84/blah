var d3 = require('d3')

var nodes = [
  {id: 'Foo', group: 1},
  {id: 'Bar', group: 1},
  {id: 'Baz', group: 1},
  {id: 'Qux', group: 1},
]

var links = [
  {source: 'Foo', target: 'Bar', value: 1},
  {source: 'Foo', target: 'Baz', value: 1},
  {source: 'Foo', target: 'Qux', value: 1},
]

function chart(node) {
  var width = 990
  var height = 400

  var simulation = d3.forceSimulation()
    .force('charge', d3.forceManyBody())
    .force('center', d3.forceCenter(width / 2, height / 2))
    .force('link', d3.forceLink().distance(80).id(function(d) {
      return d.id
    }))

  var svg = d3.select(node)
    .append('svg')
      .attr('width', width)
      .attr('height', height)

  var link = svg.append('g')
    .attr('class', 'links')
    .selectAll('line')
    .data(links)
    .enter()
      .append('line')
        .attr('stroke-width', function(d) {
          return Math.sqrt(d.value)
        })

  var node = svg.append('g')
    .attr('class', 'nodes')
    .selectAll('circle')
    .data(nodes)
    .enter().append('circle')
      .attr('r', 30)
      .call(d3.drag()
        .on('start', dragstarted)
        .on('drag', dragged)
        .on('end', dragended))

  node.append('title').text(function(d) {
    return d.id
  })

  simulation
    .nodes(nodes)
    .on('tick', ticked)

  simulation.force('link')
    .links(links)

  function ticked() {
    link
      .attr('x1', function(d) { return d.source.x })
      .attr('y1', function(d) { return d.source.y })
      .attr('x2', function(d) { return d.target.x })
      .attr('y2', function(d) { return d.target.y })

    node
      .attr('cx', function(d) { return d.x })
      .attr('cy', function(d) { return d.y })
  }

  function dragstarted(d) {
    if(!d3.event.active) simulation.alphaTarget(0.3).restart()
    d.fx = d.x
    d.fy = d.y
  }

  function dragged(d) {
    d.fx = d3.event.x
    d.fy = d3.event.y
  }

  function dragended(d) {
    if(!d3.event.active) simulation.alphaTarget(0)
    d.fx = null
    d.fy = null
  }
}

module.exports = chart
