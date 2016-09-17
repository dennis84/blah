import h from 'snabbdom/h'
import nav from './nav'
import * as error from './error'
import component from './component'
import container from './container'

function render(model, update, ws, storage) {
  return container(model, [
    nav(model, update, ws, storage),
    h('h1.has-text-centered', 'World Map'),
    (model.error) ? error.unknown() : component(WorldMap, {
      class: {'world-map': true}
    }, ws)
  ])
}

export default render
