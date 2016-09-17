import h from 'snabbdom/h'
import nav from './nav'
import component from './component'
import * as error from './error'
import container from './container'
import {SERVING_URL} from './../config'

function render(model, update, ws, storage) {
  return container(model, [
    nav(model, update, ws, storage),
    h('h1.has-text-centered', 'People'),
    (model.error) ? error.unknown() : component(People, {
      class: {people: true}
    }, {
      baseUrl: SERVING_URL
    })
  ])
}

export default render
