var h = require('snabbdom/h').default
var hierarchy = require('d3-hierarchy')
var selection = require('d3-selection')

function chart(node, data) {
  var margin = [40, 20, 80, 40]
  var width = node.offsetWidth - margin[1] - margin[3]
  var height = node.offsetHeight - margin[0] - margin[2]

  var treeData = hierarchy.stratify()
    .id(function(d) {
      return d.item
    })
    .parentId(function(d) {
      return d.parent
    })(data)

  var treemap = hierarchy.tree()
    .size([width, height])

  var nodes = hierarchy.hierarchy(treeData)
  nodes = treemap(nodes)

  var svg = selection.select(node).append('svg')
    .attr('width', width + margin[3] + margin[1])
    .attr('height', height + margin[0] + margin[2])

  var g = svg.append('g')
    .attr('transform', 'translate(' + margin[3] + ',' + margin[0] + ')')

  g.selectAll('.link')
    .data(nodes.descendants().slice(1))
    .enter()
    .append('path')
    .attr('class', 'link')
    .attr('d', function(d) {
      return 'M' + d.x + ',' + d.y
        + 'C' + d.x + ',' + (d.y + d.parent.y) / 2
        + ' ' + d.parent.x + ',' +  (d.y + d.parent.y) / 2
        + ' ' + d.parent.x + ',' + d.parent.y
    })

  var node = g.selectAll('.node')
    .data(nodes.descendants())
    .enter()
    .append('g')
    .attr('class', function(d) {
      return 'node' + (d.children ? ' node-internal' : ' node-leaf')
    })
    .attr('transform', function(d) {
      return 'translate(' + d.x + ',' + d.y + ')'
    })

  node.append('circle')
    .attr('r', 10)

  node.append('text')
    .attr('dy', '.35em')
    .attr('y', function(d) {
      return d.children ? -20 : 20
    })
    .style('text-anchor', 'middle')
    .text(function(d) {
      return d.data.id + ' (' + d.data.data.count + ')'
    })
}

function flowchart(model) {
  if(!model.items || 0 === model.items.length) {
    return h('div.is-empty')
  }

  return h('div.flowchart', {
    hook: {
      insert: function(vnode) {
        chart(vnode.elm, model.items)
      }
    }
  })
}

module.exports = flowchart
