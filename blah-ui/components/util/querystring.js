function parse(obj) {
  if(typeof obj !== 'object') return ''
  return Object.keys(obj).map(function(name) {
    return name + '=' + obj[name]
  }).join('&')
}

module.exports = parse
