var h = require('snabbdom/h').default
var distanceInWordsToNow = require('date-fns/distance_in_words_to_now')
var ctrl = require('./ctrl')

function button(job, update, options) {
  if('queued' === job.status || job.clicked) {
    return h('a.button.is-loading', 'Loa')
  }

  if(-1 !== job.status.indexOf('running')) {
    return h('a.button.is-danger', {
      on: {
        click: function() {
          update(ctrl.stop, job.name, options)
        }
      }
    }, 'Stop')
  }

  return h('a.button.is-primary', {
    on: {
      click: function() {
        update(ctrl.run, job.name, options)
      }
    }
  }, 'Run')
}

function jobs(xs, update, options) {
  if(undefined === xs || 0 == xs.length) {
    return h('div.is-empty')
  }

  return h('div.people-list', xs.map(function(job) {
    var lastSuccess = job.lastSuccess
    if(lastSuccess) lastSuccess = distanceInWordsToNow(lastSuccess)
    return h('div.card.is-fullwidth', [
      h('header.card-header', [
        h('p.card-header-title', job.name),
        h('p.last-success', h('i', 'Last success: ' + (lastSuccess || '-'))),
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
