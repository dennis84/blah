import {h} from 'virtual-dom'
import Component from '../component'

class PageviewWidget extends Component {
  initialize(conn) {
    this.state = {count:0}
    setInterval(() => {
      this.state.count ++
    }, 2000)

    return this.renderView.bind(this, this.state)
  }

  renderView(state) {
    return h('h1', 'RV ' + state.count)
  }
}

export default PageviewWidget
