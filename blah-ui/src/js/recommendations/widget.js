import {h} from 'virtual-dom'
import {mount} from '../hook'
import {recommendations} from './ctrl'
import debounce from 'debounce'

function views(xs) {
  if(undefined === xs) return
  return xs.map((item) => {
    return h('div', [
      h('span', item.page),
      h('span.label.label-danger.pull-right', String(item.score))
    ])
  })
}

function render(model, update, conn, options) {
  if(undefined === options) options = {}
  return h('div.widget.widget-recommendations', {
    mount: mount((node) => {
      if(options.user) update(recommendations, options)
    })
  }, [
    h('h3', 'Recommendations'),
    h('input.form-control.input-lg', {
      value: options.user,
      placeholder: 'Enter username',
      oninput: debounce((e) => {
        if(!e.target.value) return
        update(recommendations, {user: e.target.value})
      }, 500)
    }),
    h('div.recommendations', views(model.views))
  ])
}

export default render
