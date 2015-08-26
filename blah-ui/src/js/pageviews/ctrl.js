import clone from 'clone'

function query(model) {
  var m = clone(model)
  m.count = 0
  return m
}

function incr(model) {
  var m = clone(model)
  m.count ++
  return m
}

export {
  query,
  incr
}
