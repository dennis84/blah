import h from 'snabbdom/h'
import nav from './nav'
import component from './component'
import container from './container'
import * as error from './error'
import {SERVING_URL} from './../config'

function render(model, update, ws, storage) {
  return container(model, [
    nav(model, update, ws, storage),
    h('h1.has-text-centered', 'Jobs'),
    (model.error) ? error.unknown() : component(Jobs, {}, {
      baseUrl: SERVING_URL
    })
  ])
}

export default render
