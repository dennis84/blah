import {h} from 'virtual-dom'
import * as widgets from './widgets'
import masonry from './masonry'
import nav from './nav'
import * as error from './common/error'
import theme from './theme'

function render(model, update, conn, storage) {
  return h('div.container', {
    theme: theme(model)
  }, [
    nav(model, update, conn, storage),
    h('h1.has-text-centered', 'Pageviews'),
    (model.error) ? error.unknown() : masonry({
      className: 'widgets',
      itemSelector: '.widget'
    }, [
      widgets.browserStats(model, update, conn),
      widgets.pageviews(model, update, conn),
      widgets.countAll(model, update, conn),
      widgets.platformStats(model, update, conn),
      widgets.pageviewDiff(model, update, conn),
      widgets.mobileOsStats(model, update, conn)
    ])
  ])
}

export default render
