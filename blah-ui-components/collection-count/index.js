var RealtimeChart = require('./chart')

function CollectionCount(node, ws, options) {
  var widget = document.createElement('div')
  widget.classList.add('widget')
  widget.classList.add('widget-collection-count')

  if(options.class) {
    for (name in options.class) {
      if(true === options.class[name]) {
        widget.classList.add(name)
      }
    }
  }

  var h3 = document.createElement('h3')
  h3.appendChild(document.createTextNode(options.title))

  var chartElem = document.createElement('div')
  chartElem.classList.add('chart')

  setTimeout(function draw() {
    var chart = new RealtimeChart(chartElem)
    chart.start(ws)

    ws.on('collection_count', function(data) {
      if(options.collection && options.collection !== data.name) return
      chart.insert({
        key: new Date(data.date).getTime(),
        value: data.count
      })
    })
  }, 0)

  widget.appendChild(h3)
  widget.appendChild(chartElem)
  node.parentNode.replaceChild(widget, node)
}

module.exports = CollectionCount
