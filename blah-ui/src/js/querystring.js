function parse(obj) {
  if(typeof obj !== 'object') return ''
  return Object.keys(obj).map((name) => name + '=' + obj[name]).join('&')
}

export default parse
