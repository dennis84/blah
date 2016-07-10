import {h} from 'virtual-dom'
import nav from './nav'
import widget from '../widget'
import funnel from './funnel/funnel'
import * as error from './common/error'
import theme from './theme'

function render(model, update, conn, storage) {
  return h('div.container', {
    theme: theme(model)
  }, [
    nav(model, update, conn, storage),
    h('h1.has-text-centered', 'Funnel'),
    (model.error) ? error.unknown() : widget(funnel, model, update, {}, conn, {
      className: 'size-3of3',
      name: 'signup',
      steps: ['landingpage', 'signup', 'dashboard'],
      title: 'Sign Up Funnel'
    })
  ])
}

export default render
