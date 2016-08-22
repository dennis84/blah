import {h} from 'virtual-dom'
import nav from './nav'
import widget from '../widget'
import * as error from './common/error'
import theme from './theme'
import component from './common/component'

function render(model, update, conn, storage) {
  return h('div.container', {
    theme: theme(model)
  }, [
    nav(model, update, conn, storage),
    h('h1.has-text-centered', 'World Map'),
    (model.error) ? error.unknown() : component(WorldMap, {
      className: 'world-map'
    })
  ])
}

export default render
