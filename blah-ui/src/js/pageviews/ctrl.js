import clone from 'clone'

function query(model, options) {
  return new Promise((resolve, reject) => {
    var m = clone(model)
    m.count = 0
    resolve(m)
  })
}

function incr(model) {
  return new Promise((resolve, reject) => {
    var m = clone(model)
    m.count ++
    resolve(m)
  })
}

export {
  query,
  incr
}
