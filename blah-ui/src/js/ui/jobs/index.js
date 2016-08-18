import {h} from 'virtual-dom'
import moment from 'moment'
import {mount} from '../../hook'
import {list, run, stop} from './ctrl'
import debounce from 'debounce'

function button(job, update) {
  if('queued' === job.status || job.clicked) {
    return h('a.button.is-loading', 'Loa')
  }

  if('running' === job.status) {
    return h('a.button.is-danger', {
      onclick: e => update(stop, job.name)
    }, 'Stop')
  }

  return h('a.button.is-primary', {
    onclick: e => update(run, job.name)
  }, 'Run')
}

function jobs(xs, update) {
  if(undefined === xs || 0 == xs.length) return
  return h('div.people-list', xs.map(job => {
    var lastSuccess = job.lastSuccess
    if(lastSuccess) lastSuccess = moment(lastSuccess).fromNow()
    return h('div.card.is-fullwidth', [
      h('header.card-header', [
        h('p.card-header-title', job.name),
        h('i', `Last success: ${lastSuccess || '-'}`),
        button(job, update)
      ])
    ])
  }))
}

function render(model, update, options = {}) {
  return h('div.jobs', {
    mount: mount(node => {
      setInterval(() => update(list), 5000),
      update(list)
    }),
  }, jobs(model.jobs, update))
}

export default render
