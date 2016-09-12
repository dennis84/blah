import {h} from 'virtual-dom'
import nav from './nav'
import * as error from './error'
import theme from './theme'
import component from './component'

function render(model, update, conn, storage) {
  return h('div.container', {
    theme: theme(model)
  }, [
    nav(model, update, conn, storage),
    h('h1.has-text-centered', 'World Map'),
    (model.error) ? error.unknown() : component(WorldMap, {
      className: 'world-map'
    }, conn.ws)
  ])
}

export default render
