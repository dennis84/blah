var h = require('snabbdom/h')
var moment = require('moment')
var ctrl = require('./ctrl')

function button(job, update, options) {
  if('queued' === job.status || job.clicked) {
    return h('a.button.is-loading', 'Loa')
  }

  if('running' === job.status) {
    return h('a.button.is-danger', {
      on: {
        click: function(e) {
          update(ctrl.stop, job.name, options)
        }
      }
    }, 'Stop')
  }

  return h('a.button.is-primary', {
    on: {
      click: function(e) {
        update(ctrl.run, job.name, options)
      }
    }
  }, 'Run')
}

function jobs(xs, update, options) {
  if(undefined === xs || 0 == xs.length) return []
  return h('div.people-list', xs.map(function(job) {
    var lastSuccess = job.lastSuccess
    if(lastSuccess) lastSuccess = moment(lastSuccess).fromNow()
    return h('div.card.is-fullwidth', [
      h('header.card-header', [
        h('p.card-header-title', job.name),
        h('i', 'Last success: ' + (lastSuccess || '-')),
        button(job, update, options)
      ])
    ])
  }))
}

function render(model, update, options) {
  return h('div.jobs', [
    jobs(model.jobs, update, options)
  ])
}

module.exports = render
