function parse(text) {
  var res = text.match(/^([a-z-]+)@(.*)$/)
    , data = null

  try {
    var data = JSON.parse(res[2])
  } catch(e) {}

  return {event: res[1], data: data}
}

module.exports = parse
