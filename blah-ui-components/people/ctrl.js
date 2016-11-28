var clone = require('clone')
var xhr = require('xhr')

/**
 * Search users.
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function search(model, options) {
  return new Promise(function(resolve) {
    xhr.post(options.baseUrl + '/users', {
      json: mkQuery(options)
    }, function(err, resp, body) {
      var m = clone(model)
      body.map(function(user) {
        user.events = user.events.map(function(event) {
          event.date = new Date(event.date)
          return event
        }).sort(function(a,b) {
          return b.date - a.date
        })
      })

      m.users = body
      resolve(m)
    })
  })
}

/**
 * Sets the `opened` flag in the model.
 *
 * @param {Object} The widget state
 * @param {Object} The user
 *
 * @return {Object} The updated model
 */
function open(model, user) {
  var m = clone(model)
  for(var i in model.users) {
    if(model.users[i] == user) {
      m.users[i].opened = !user.opened
    } else {
      m.users[i].opened = false
    }
  }

  return m
}

function mkQuery(options) {
  var query = {}
  query.filterBy = options.filterBy
  return query
}

module.exports = {
  search: search,
  open: open
}
