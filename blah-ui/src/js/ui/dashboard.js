import {h} from 'virtual-dom'
import Masonry from 'masonry-layout'
import debounce from 'debounce'
import {hook} from '../hook'
import * as widgets from './widgets'

function container(title, ws) {
  return h('div.container', [
    h('h1.center-h', title),
    h('div.widgets', {
      hook: hook(debounce((node) => {
        if(undefined === node.masonry) {
          node.masonry = new Masonry(node, {
            itemSelector: '.widget'
          })
        } else {
          node.masonry.reloadItems()
          node.masonry.layout()
        }
      }), 100)
    }, ws)
  ])
}

function render(model, chan, conn) {
  return h('main', [
    container('Pageviews', [
      widgets.browserStats(model, chan, conn),
      widgets.pageviews(model, chan, conn),
      widgets.countAll(model, chan, conn),
      widgets.platformStats(model, chan, conn),
      widgets.pageviewDiff(model, chan, conn),
      widgets.mobileOsStats(model, chan, conn)
    ]),
    container('User Stats', [
      widgets.visitorsToday(model, chan, conn),
      widgets.visitorsByCountry(model, chan, conn),
      widgets.uniqueVisitors(model, chan, conn)
    ]),
    container('Misc', [
      widgets.itemsFunnel(model, chan, conn),
      widgets.totalRevenue(model, chan, conn),
      widgets.recommendationsWidget(model, chan, conn)
    ])
  ])
}

export default render
