import h from 'snabbdom/h'
import nav from './nav'
import * as error from './error'
import component from './component'
import container from './container'
import {SERVING_URL} from './../config'

function render(model, update, conn, storage) {
  return container(model, [
    nav(model, update, conn, storage),
    h('h1.has-text-centered', 'Sign Up Funnel'),
    (model.error) ? error.unknown() : component(Funnel, {}, conn.ws, {
      baseUrl: SERVING_URL,
      className: 'size-3of3 funnel',
      name: 'signup',
      steps: ['landingpage', 'signup', 'dashboard']
    })
  ])
}

export default render
