var xhr = require('xhr')
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

  xhr.post(options.baseUrl + '/collection', {
    json: {name: options.collection}
  }, function(err, resp, body) {
    if(err) return
    var now = Date.now()
    var data = []
    var existing = {}

    for(var i in body) {
      var key = new Date(body[i].date).getTime()
      var second = Math.floor(key / 1000)
      existing[second] = {key: key, value: body[i].count}
    }

    for(var i = 60; i > 0; i--) {
      var key = now - (i * 1000)
      var second = Math.floor(key / 1000)
      data.push(existing[second] ? existing[second] : {key: key, value: 0})
    }

    var chart = new RealtimeChart(chartElem, data)
    chart.start(ws)

    ws.on('collection_count', function(data) {
      if(options.collection && options.collection !== data.name) return
      chart.insert({
        key: new Date(data.date).getTime(),
        value: data.count
      })
    })
  })

  widget.appendChild(h3)
  widget.appendChild(chartElem)
  node.parentNode.replaceChild(widget, node)
}

module.exports = CollectionCount
