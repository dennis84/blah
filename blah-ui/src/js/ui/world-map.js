import {h} from 'virtual-dom'
import nav from './nav'
import widget from '../widget'
import world from './map/world'
import * as error from './common/error'
import theme from './theme'

function render(model, update, conn, storage) {
  return h('div.container', {
    theme: theme(model)
  }, [
    nav(model, update, conn, storage),
    h('h1.is-text-centered', 'World Map'),
    (model.error) ? error.unknown() : widget(world, model, update, {}, conn)
  ])
}

export default render
