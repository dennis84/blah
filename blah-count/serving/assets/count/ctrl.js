var clone = require('clone')
var xhr = require('xhr')

function count(model, options) {
  var promises = []

  function prevRequest(prev) {
    return post(options.baseUrl + '/count', mkQuery(prev))
      .then(function(data) {
        return {title: prev.title, count: data.count}
      })
  }

  if(options.prevs) {
    for(var i in options.prevs) {
      var prev = options.prevs[i]
      promises.push(prevRequest(prev))
    }
  }

  promises.push(post(options.baseUrl + '/count', mkQuery(options))
    .then(function(data) {
      return data.count
    }))

  return Promise.all(promises)
    .then(function(values) {
      var m = clone(model)
      m.prevs = []
      m.count = values.pop()
      for(var i in values) {
        var value = values[i]
        m.prevs.push({title: value.title, count: value.count})
      }

      return m
    })
}

function grouped(model, options) {
  return post(options.baseUrl + '/count', mkQuery(options))
    .then(function(data) {
      var m = clone(model)
      m.groups = data
      return m
    })
}

function post(url, params) {
  return new Promise(function(resolve, reject) {
    xhr.post(url, {
      json: params
    }, function(err, resp, body) {
      if(err) reject(err)
      resolve(body)
    })
  })
}

function mkQuery(options) {
  var query = {}
  if(options.filterBy) {
    query.filterBy = options.filterBy
  }

  if(options.groupBy && options.groupBy.length > 0) {
    query.groupBy = options.groupBy
  }

  if(options.collection) {
    query.collection = options.collection
  }

  return query
}

module.exports = {
  count: count,
  grouped: grouped
}
