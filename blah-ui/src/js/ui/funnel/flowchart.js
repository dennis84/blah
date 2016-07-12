import h from 'virtual-dom/h'
import {hook} from '../../hook'
import mermaid from 'mermaid'

/**
 * Generate flowchart data from funnel response.
 * ```
 * a[A] -- 10 --> b[B]
 * b[B] -- 5 --> c[C]
 * c[C] -- 3 --> d[D]
 * ```
 *
 * @param {Array} funnel data
 *
 * @return {String}
 */
function mkGraph(xs) {
  var data = 'graph TD\n'
  var chars = {}

  function getChar(x) {
    if(chars[x]) return chars[x]
    var nextChar = String.fromCharCode(97 + Object.keys(chars).length)
    chars[x] = nextChar
    return nextChar
  }

  for(var i in xs) {
    var item = xs[i]
    var parent = item.parent ? item.parent : 'x'
    var parentChar = getChar(parent)
    var itemChar = getChar(item.item)
    data += `${parentChar}((${parent})) -- ${item.count} --> ${itemChar}((${item.item}))\n`
  }

  return data
}

function flowchart(model, options) {
  if(undefined === model.items) return
  var data = mkGraph(model.items)
  return h('div.flowchart', {
    hook: hook(node => {
      mermaid.mermaidAPI.render('flowchart', data, svg => {
        node.innerHTML = svg
      })
    })
  })
}

export default flowchart
