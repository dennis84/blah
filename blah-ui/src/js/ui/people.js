import {h} from 'virtual-dom'
import nav from './nav'
import widget from '../widget'
import component from './common/component'
import * as error from './common/error'
import theme from './theme'
import {SERVING_URL} from './../config'

function render(model, update, conn, storage) {
  return h('div.container', {
    theme: theme(model)
  }, [
    nav(model, update, conn, storage),
    h('h1.has-text-centered', 'People'),
    (model.error) ? error.unknown() : component(People, {
      className: 'people'
    }, {
      baseUrl: SERVING_URL
    })
  ])
}

export default render
