var d3 = require('d3')
var Datamap = require('datamaps')
var WebSocketListener = require('../util/websocket')

function WorldMap(node, ws) {
  var conn = new WebSocketListener(ws)

  setTimeout(function draw() {
    var ratio = 750 / 500
    var width = node.offsetWidth
    var height = width / ratio
    node.innerHTML = ''

    var map = new Datamap({
      element: node,
      projection: 'mercator',
      width: width,
      height: height,
      geographyConfig: {
        highlightOnHover: false,
        popupOnHover: false
      }
    })

    map.addPlugin('pins', function(layer, data, options) {
      var self = this
      var svg = this.svg
      var bubbles = layer.selectAll('image.datamaps-pins')
        .data(data, JSON.stringify)

      bubbles.enter()
        .append('circle')
        .attr('class', 'pin')
        .attr('fill-opacity', 0)
        .attr('r', 20)
        .attr('cx', function(d) {
          return self.latLngToXY(d.lat, d.lng)[0]
        })
        .attr('cy', function(d) {
          return self.latLngToXY(d.lat, d.lng)[1]
        })
        .each(fadeInOut)

      function fadeInOut(d) {
        var circle = d3.select(this)
        circle.transition()
          .duration(300)
          .attr('r', 1)
          .attr('fill-opacity', 1)
          .ease('in')
          .transition()
          .duration(20000)
          .attr('fill-opacity', 0)
          .each('end', function() {
            circle.remove()
          })
      }
    })

    conn.on('user', function(d) {
      map.pins([{user: d.user, lat: d.lat, lng: d.lng}])
    })

    window.addEventListener('resize', draw)
  }, 0)
}

module.exports = WorldMap
