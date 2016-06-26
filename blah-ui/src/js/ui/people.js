import {h} from 'virtual-dom'
import nav from './nav'
import widget from '../widget'
import explore from './people/explore'
import * as error from './common/error'
import theme from './theme'
import M from './multiselect/index'

function render(model, update, conn, storage) {
  return h('div.container', {
    theme: theme(model)
  }, [
    nav(model, update, conn, storage),
    new M({
      multiple:false,
      options:['foo', 'bar', 'baz', 'qux'],
      onchange: (e) => console.log(e)
    }),
    h('h1.has-text-centered', 'People'),
    (model.error) ? error.unknown() : widget(explore, model, update)
  ])
}

export default render
