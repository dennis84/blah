import {h} from 'virtual-dom'
import nav from './nav'
import widget from '../widget'
import jobs from './jobs/index'
import * as error from './common/error'
import theme from './theme'

function render(model, update, conn, storage) {
  return h('div.container', {
    theme: theme(model)
  }, [
    nav(model, update, conn, storage),
    h('h1.has-text-centered', 'Jobs'),
    (model.error) ? error.unknown() : widget(jobs, model, update)
  ])
}

export default render
