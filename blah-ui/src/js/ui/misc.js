import {h} from 'virtual-dom'
import * as widgets from './widgets'
import masonry from './masonry'
import nav from './nav'
import * as error from './common/error'
import theme from './theme'

function render(update, conn, storage, model) {
  return h('div.container', {
    theme: theme(model)
  }, [
    nav(model, update, conn, storage),
    h('h1.is-text-centered', 'Misc'),
    (model.error) ? error.unknown() : masonry({
      className: 'widgets',
      itemSelector: '.widget'
    }, [
      widgets.itemsFunnel(model, update, conn),
      widgets.totalRevenue(model, update, conn),
      widgets.recommendationsWidget(model, update, conn)
    ])
  ])
}

export default render
