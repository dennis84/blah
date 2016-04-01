import {h} from 'virtual-dom'
import {hook} from '../../hook'
import donut from '../chart/donut'

/**
 * Returns a div with a pie chart.
 *
 * ```
 * <div class="chart"><svg></div>
 * ```
 *
 * @param {Int} value
 *
 * @return {VNode}
 */
function progressBar(value) {
  value = value > 100 ? 100 : value
  var data = [
    {key: 'a', value: value},
    {key: 'b', value: 100 - value}
  ]

  return h('div.circle-progress-bar', {
    hook: hook(node => {
      setTimeout(() => {
        node.innerHTML = ''
        donut(node, data, {donutWidth: 10})
      }, 0)
    })
  })
}

export default progressBar
