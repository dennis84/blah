import clone from 'clone'
import {post} from '../../http'

/**
 * Search users.
 *
 * @param {Object} The widget state
 * @param {Object} Query options
 *
 * @return {Promise} The model wrapped in a promise
 */
function search(model, options = {}) {
  return post('/users', options).then(data => {
    var m = clone(model)
    data.map(user => {
      user.events = user.events.map(event => {
        event.date = new Date(event.date)
        return event
      }).sort((a,b) => b.date - a.date)
    })

    m.users = data
    return m
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

export {
  search,
  open
}
