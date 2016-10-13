var d3 = require('d3')

var nodes = [
  {id: 'Foo', cpu_usage: 0, mem_usage: 0, instances: 1},
  {id: 'Bar', cpu_usage: 20, mem_usage: 20, instances: 1},
  {id: 'Baz', cpu_usage: 30, mem_usage: 60, instances: 1},
  {id: 'Buz', cpu_usage: 30, mem_usage: 60, instances: 1},
  {id: 'Qux 1', cpu_usage: 70, mem_usage: 30, instances: 4},
  {id: 'Qux 2', cpu_usage: 20, mem_usage: 10, instances: 4},
  {id: 'Qux 3', cpu_usage: 20, mem_usage: 10, instances: 4}
]

var links = [
  {source: 'Foo', target: 'Bar'},
  {source: 'Foo', target: 'Baz'},
  {source: 'Foo', target: 'Buz'},
  {source: 'Foo', target: 'Qux 1'},
  {source: 'Qux 1', target: 'Qux 2'},
  {source: 'Qux 1', target: 'Qux 3'}
]

function chart(node) {
  var width = 990
  var height = 400

  var simulation = d3.forceSimulation()
    .force('charge', d3.forceManyBody())
    .force('center', d3.forceCenter(width / 2, height / 2))
    .force("collide", d3.forceCollide().radius(function(d) {
      return 30
    }).iterations(2))
    .force('link', d3.forceLink()
      .distance(80)
      .id(function(d) { return d.id })
      .strength(1))

  var svg = d3.select(node)
    .append('svg')
      .attr('width', width)
      .attr('height', height)

  var linkSvg = svg.append('g')
    .attr('class', 'links')
    .selectAll('line')
    .data(links)
    .enter()
      .append('line')
        .attr('stroke-width', function(d) {
          return Math.sqrt(d.value)
        })

  var nodeSvg = svg.append('g')
    .attr('class', 'nodes')
    .selectAll('.node')
    .data(nodes)
    .enter()
      .append('g')
      .attr('class', 'node')
      .call(d3.drag()
        .on('start', dragstarted)
        .on('drag', dragged)
        .on('end', dragended))
      .each(function(d, i) {
        var cpuUsageColor = d3.scaleOrdinal()
          .range(Array.apply(null, {length: 26}).map(function(x, i) {
            return String.fromCharCode(97 + i)
          }))
        var cpuUsageArc = d3.arc()
          .outerRadius(30)
          .innerRadius(28)
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
          .outerRadius(28)
          .innerRadius(26)
        var memUsagePie = d3.pie()
          .sort(null)
          .value(function(d) {
            return d
          })

        var elem = d3.select(this)

        elem.selectAll('.arc-cpu-usage')
          .data(cpuUsagePie([d.cpu_usage, 100 - d.cpu_usage]))
          .enter().append('g')
          .attr('class', function(d) {
            return 'arc arc-cpu-usage arc-' + cpuUsageColor(d.data)
          })
          .append('path')
          .attr('d', cpuUsageArc)

        elem.selectAll('.mem-cpu-usage')
          .data(cpuUsagePie([d.mem_usage, 100 - d.mem_usage]))
          .enter().append('g')
          .attr('class', function(d) {
            return 'arc mem-cpu-usage arc-' + memUsageColor(d.data)
          })
          .append('path')
          .attr('d', memUsageArc)

        elem
          .append('circle')
          .attr('r', 26)
      })

  simulation
    .nodes(nodes)
    .on('tick', ticked)

  simulation.force('link')
    .links(links)

  function ticked() {
    linkSvg
      .attr('x1', function(d) { return d.source.x })
      .attr('y1', function(d) { return d.source.y })
      .attr('x2', function(d) { return d.target.x })
      .attr('y2', function(d) { return d.target.y })

    nodeSvg.attr('transform', function(d) {
      return 'translate(' + d.x + ',' + d.y + ')'
    })
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
