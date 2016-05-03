import {h} from 'virtual-dom'
import d3 from 'd3'
import Datamap from 'datamaps'
import {mount} from '../../hook'

function render(model, update, conn, options = {}) {
  return h('div.world-map', {
    mount: mount(node => {
      setTimeout(function draw() {
        node.innerHTML = ''
        var map = new Datamap({
          element: node,
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
            .attr('cx', d => self.latLngToXY(d.lat, d.lng)[0])
            .attr('cy', d => self.latLngToXY(d.lat, d.lng)[1])
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
              .each('end', () => circle.remove())
          }
        })

        conn.on('user', d => {
          map.pins([{user: d.user, lat: d.lat, lng: d.lng}])
        })

        window.addEventListener('resize', draw)
      }, 0)
    })
  })
}

export default render
