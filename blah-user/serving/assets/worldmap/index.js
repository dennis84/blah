var d3 = require('d3')
var Datamap = require('datamaps')

function WorldMap(node, events) {
  var h1 = document.createElement('h1')
  h1.classList.add('title', 'is-1', 'has-text-centered')
  h1.appendChild(document.createTextNode('World Map'))
  node.appendChild(h1)

  var container = document.createElement('div')
  node.appendChild(container)

  node.classList.add('world-map')
  var map = null

  setTimeout(function draw() {
    var ratio = 750 / 500
    var width = container.offsetWidth
    var height = width / ratio
    container.innerHTML = ''

    map = new Datamap({
      element: container,
      projection: 'mercator',
      width: width,
      height: height,
      geographyConfig: {
        highlightOnHover: false,
        popupOnHover: false
      }
    })

    map.addPlugin('pins', function(layer, data) {
      var self = this
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

      function fadeInOut() {
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

    window.addEventListener('resize', draw)
  }, 0)

  function onUser(d) {
    map.pins([{user: d.user, lat: d.lat, lng: d.lng}])
  }

  events.on('user', onUser)

  this.destroy = function() {
    events.removeListener('user', onUser)
  }
}

module.exports = WorldMap
