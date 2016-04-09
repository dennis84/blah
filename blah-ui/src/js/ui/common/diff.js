import {h} from 'virtual-dom'

/**
 * Returns a div with the diff.
 *
 * ```
 * <div class="count">+100</div>
 * <!-- or -->
 * <div class="count">75%</div>
 * ```
 *
 * @param {Int} value
 * @param {Boolean} percentage
 *
 * return {VNode}
 */
function diff(value, percentage = false) {
  if(true === percentage) {
    value = Number.isFinite(value) ? value.toFixed(2) + '%' : '?'
  } else {
    var plusMinus = '±'
    if(value < 0) plusMinus = '-'
    else if(value > 0) plusMinus = '+'
    value = plusMinus + Math.abs(value)
  }

  return h('div.widget-value', value)
}

export default diff
