import h from 'snabbdom/h'
import nav from './nav'
import * as error from './error'
import component from './component'
import container from './container'

function render(model, update, conn, storage) {
  return container(model, [
    nav(model, update, conn, storage),
    h('h1.has-text-centered', 'World Map'),
    (model.error) ? error.unknown() : component(WorldMap, {
      props: {className: 'world-map'}
    }, conn.ws)
  ])
}

export default render
