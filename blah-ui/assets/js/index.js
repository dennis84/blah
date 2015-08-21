!function e(t,n,r){function o(s,a){if(!n[s]){if(!t[s]){var u="function"==typeof require&&require;if(!a&&u)return u(s,!0);if(i)return i(s,!0);var c=new Error("Cannot find module '"+s+"'");throw c.code="MODULE_NOT_FOUND",c}var l=n[s]={exports:{}};t[s][0].call(l.exports,function(e){var n=t[s][1][e];return o(n?n:e)},l,l.exports,e,t,n,r)}return n[s].exports}for(var i="function"==typeof require&&require,s=0;s<r.length;s++)o(r[s]);return o}({1:[function(e,t,n){},{}],2:[function(e,t,n){function r(e){return e?o(e):void 0}function o(e){for(var t in r.prototype)e[t]=r.prototype[t];return e}t.exports=r,r.prototype.on=r.prototype.addEventListener=function(e,t){return this._callbacks=this._callbacks||{},(this._callbacks[e]=this._callbacks[e]||[]).push(t),this},r.prototype.once=function(e,t){function n(){r.off(e,n),t.apply(this,arguments)}var r=this;return this._callbacks=this._callbacks||{},n.fn=t,this.on(e,n),this},r.prototype.off=r.prototype.removeListener=r.prototype.removeAllListeners=r.prototype.removeEventListener=function(e,t){if(this._callbacks=this._callbacks||{},0==arguments.length)return this._callbacks={},this;var n=this._callbacks[e];if(!n)return this;if(1==arguments.length)return delete this._callbacks[e],this;for(var r,o=0;o<n.length;o++)if(r=n[o],r===t||r.fn===t){n.splice(o,1);break}return this},r.prototype.emit=function(e){this._callbacks=this._callbacks||{};var t=[].slice.call(arguments,1),n=this._callbacks[e];if(n){n=n.slice(0);for(var r=0,o=n.length;o>r;++r)n[r].apply(this,t)}return this},r.prototype.listeners=function(e){return this._callbacks=this._callbacks||{},this._callbacks[e]||[]},r.prototype.hasListeners=function(e){return!!this.listeners(e).length}},{}],3:[function(e,t,n){var r=e("./vdom/create-element.js");t.exports=r},{"./vdom/create-element.js":15}],4:[function(e,t,n){var r=e("./vtree/diff.js");t.exports=r},{"./vtree/diff.js":35}],5:[function(e,t,n){var r=e("./virtual-hyperscript/index.js");t.exports=r},{"./virtual-hyperscript/index.js":22}],6:[function(e,t,n){t.exports=function(e){var t,n=String.prototype.split,r=/()??/.exec("")[1]===e;return t=function(t,o,i){if("[object RegExp]"!==Object.prototype.toString.call(o))return n.call(t,o,i);var s,a,u,c,l=[],v=(o.ignoreCase?"i":"")+(o.multiline?"m":"")+(o.extended?"x":"")+(o.sticky?"y":""),f=0,o=new RegExp(o.source,v+"g");for(t+="",r||(s=new RegExp("^"+o.source+"$(?!\\s)",v)),i=i===e?-1>>>0:i>>>0;(a=o.exec(t))&&(u=a.index+a[0].length,!(u>f&&(l.push(t.slice(f,a.index)),!r&&a.length>1&&a[0].replace(s,function(){for(var t=1;t<arguments.length-2;t++)arguments[t]===e&&(a[t]=e)}),a.length>1&&a.index<t.length&&Array.prototype.push.apply(l,a.slice(1)),c=a[0].length,f=u,l.length>=i)));)o.lastIndex===a.index&&o.lastIndex++;return f===t.length?(c||!o.test(""))&&l.push(""):l.push(t.slice(f)),l.length>i?l.slice(0,i):l}}()},{}],7:[function(e,t,n){"use strict";function r(e){var t=e[s];return t||(t=e[s]={}),t}var o=e("individual/one-version"),i="7";o("ev-store",i);var s="__EV_STORE_KEY@"+i;t.exports=r},{"individual/one-version":9}],8:[function(e,t,n){(function(e){"use strict";function n(e,t){return e in r?r[e]:(r[e]=t,t)}var r="undefined"!=typeof window?window:"undefined"!=typeof e?e:{};t.exports=n}).call(this,"undefined"!=typeof global?global:"undefined"!=typeof self?self:"undefined"!=typeof window?window:{})},{}],9:[function(e,t,n){"use strict";function r(e,t,n){var r="__INDIVIDUAL_ONE_VERSION_"+e,i=r+"_ENFORCE_SINGLETON",s=o(i,t);if(s!==t)throw new Error("Can only have one copy of "+e+".\nYou already have version "+s+" installed.\nThis means you cannot install version "+t);return o(r,n)}var o=e("./index.js");t.exports=r},{"./index.js":8}],10:[function(e,t,n){(function(n){var r="undefined"!=typeof n?n:"undefined"!=typeof window?window:{},o=e("min-document");if("undefined"!=typeof document)t.exports=document;else{var i=r["__GLOBAL_DOCUMENT_CACHE@4"];i||(i=r["__GLOBAL_DOCUMENT_CACHE@4"]=o),t.exports=i}}).call(this,"undefined"!=typeof global?global:"undefined"!=typeof self?self:"undefined"!=typeof window?window:{})},{"min-document":1}],11:[function(e,t,n){"use strict";t.exports=function(e){return"object"==typeof e&&null!==e}},{}],12:[function(e,t,n){function r(e){return"[object Array]"===i.call(e)}var o=Array.isArray,i=Object.prototype.toString;t.exports=o||r},{}],13:[function(e,t,n){var r=e("./vdom/patch.js");t.exports=r},{"./vdom/patch.js":18}],14:[function(e,t,n){function r(e,t,n){for(var r in t){var s=t[r];void 0===s?o(e,r,s,n):u(s)?(o(e,r,s,n),s.hook&&s.hook(e,r,n?n[r]:void 0)):a(s)?i(e,t,n,r,s):e[r]=s}}function o(e,t,n,r){if(r){var o=r[t];if(u(o))o.unhook&&o.unhook(e,t,n);else if("attributes"===t)for(var i in o)e.removeAttribute(i);else if("style"===t)for(var s in o)e.style[s]="";else e[t]="string"==typeof o?"":null}}function i(e,t,n,r,o){var i=n?n[r]:void 0;if("attributes"!==r){if(i&&a(i)&&s(i)!==s(o))return void(e[r]=o);a(e[r])||(e[r]={});var u="style"===r?"":void 0;for(var c in o){var l=o[c];e[r][c]=void 0===l?u:l}}else for(var v in o){var f=o[v];void 0===f?e.removeAttribute(v):e.setAttribute(v,f)}}function s(e){return Object.getPrototypeOf?Object.getPrototypeOf(e):e.__proto__?e.__proto__:e.constructor?e.constructor.prototype:void 0}var a=e("is-object"),u=e("../vnode/is-vhook.js");t.exports=r},{"../vnode/is-vhook.js":26,"is-object":11}],15:[function(e,t,n){function r(e,t){var n=t?t.document||o:o,l=t?t.warn:null;if(e=c(e).a,u(e))return e.init();if(a(e))return n.createTextNode(e.text);if(!s(e))return l&&l("Item is not a valid virtual dom node",e),null;var v=null===e.namespace?n.createElement(e.tagName):n.createElementNS(e.namespace,e.tagName),f=e.properties;i(v,f);for(var p=e.children,d=0;d<p.length;d++){var h=r(p[d],t);h&&v.appendChild(h)}return v}var o=e("global/document"),i=e("./apply-properties"),s=e("../vnode/is-vnode.js"),a=e("../vnode/is-vtext.js"),u=e("../vnode/is-widget.js"),c=e("../vnode/handle-thunk.js");t.exports=r},{"../vnode/handle-thunk.js":24,"../vnode/is-vnode.js":27,"../vnode/is-vtext.js":28,"../vnode/is-widget.js":29,"./apply-properties":14,"global/document":10}],16:[function(e,t,n){function r(e,t,n,r){return n&&0!==n.length?(n.sort(s),o(e,t,n,r,0)):{}}function o(e,t,n,r,s){if(r=r||{},e){i(n,s,s)&&(r[s]=e);var u=t.children;if(u)for(var c=e.childNodes,l=0;l<t.children.length;l++){s+=1;var v=u[l]||a,f=s+(v.count||0);i(n,s,f)&&o(c[l],v,n,r,s),s=f}}return r}function i(e,t,n){if(0===e.length)return!1;for(var r,o,i=0,s=e.length-1;s>=i;){if(r=(s+i)/2>>0,o=e[r],i===s)return o>=t&&n>=o;if(t>o)i=r+1;else{if(!(o>n))return!0;s=r-1}}return!1}function s(e,t){return e>t?1:-1}var a={};t.exports=r},{}],17:[function(e,t,n){function r(e,t,n){var r=e.type,c=e.vNode,p=e.patch;switch(r){case d.REMOVE:return o(t,c);case d.INSERT:return i(t,p,n);case d.VTEXT:return s(t,c,p,n);case d.WIDGET:return a(t,c,p,n);case d.VNODE:return u(t,c,p,n);case d.ORDER:return l(t,p),t;case d.PROPS:return f(t,p,c.properties),t;case d.THUNK:return v(t,n.patch(t,p,n));default:return t}}function o(e,t){var n=e.parentNode;return n&&n.removeChild(e),c(e,t),null}function i(e,t,n){var r=n.render(t,n);return e&&e.appendChild(r),e}function s(e,t,n,r){var o;if(3===e.nodeType)e.replaceData(0,e.length,n.text),o=e;else{var i=e.parentNode;o=r.render(n,r),i&&o!==e&&i.replaceChild(o,e)}return o}function a(e,t,n,r){var o,i=h(t,n);o=i?n.update(t,e)||e:r.render(n,r);var s=e.parentNode;return s&&o!==e&&s.replaceChild(o,e),i||c(e,t),o}function u(e,t,n,r){var o=e.parentNode,i=r.render(n,r);return o&&i!==e&&o.replaceChild(i,e),i}function c(e,t){"function"==typeof t.destroy&&p(t)&&t.destroy(e)}function l(e,t){for(var n,r,o,i=e.childNodes,s={},a=0;a<t.removes.length;a++)r=t.removes[a],n=i[r.from],r.key&&(s[r.key]=n),e.removeChild(n);for(var u=i.length,c=0;c<t.inserts.length;c++)o=t.inserts[c],n=s[o.key],e.insertBefore(n,o.to>=u++?null:i[o.to])}function v(e,t){return e&&t&&e!==t&&e.parentNode&&e.parentNode.replaceChild(t,e),t}var f=e("./apply-properties"),p=e("../vnode/is-widget.js"),d=e("../vnode/vpatch.js"),h=e("./update-widget");t.exports=r},{"../vnode/is-widget.js":29,"../vnode/vpatch.js":32,"./apply-properties":14,"./update-widget":19}],18:[function(e,t,n){function r(e,t,n){return n=n||{},n.patch=n.patch&&n.patch!==r?n.patch:o,n.render=n.render||c,n.patch(e,t,n)}function o(e,t,n){var r=s(t);if(0===r.length)return e;var o=l(e,t.a,r),u=e.ownerDocument;n.document||u===a||(n.document=u);for(var c=0;c<r.length;c++){var v=r[c];e=i(e,o[v],t[v],n)}return e}function i(e,t,n,r){if(!t)return e;var o;if(u(n))for(var i=0;i<n.length;i++)o=v(n[i],t,r),t===e&&(e=o);else o=v(n,t,r),t===e&&(e=o);return e}function s(e){var t=[];for(var n in e)"a"!==n&&t.push(Number(n));return t}var a=e("global/document"),u=e("x-is-array"),c=e("./create-element"),l=e("./dom-index"),v=e("./patch-op");t.exports=r},{"./create-element":15,"./dom-index":16,"./patch-op":17,"global/document":10,"x-is-array":12}],19:[function(e,t,n){function r(e,t){return o(e)&&o(t)?"name"in e&&"name"in t?e.id===t.id:e.init===t.init:!1}var o=e("../vnode/is-widget.js");t.exports=r},{"../vnode/is-widget.js":29}],20:[function(e,t,n){"use strict";function r(e){return this instanceof r?void(this.value=e):new r(e)}var o=e("ev-store");t.exports=r,r.prototype.hook=function(e,t){var n=o(e),r=t.substr(3);n[r]=this.value},r.prototype.unhook=function(e,t){var n=o(e),r=t.substr(3);n[r]=void 0}},{"ev-store":7}],21:[function(e,t,n){"use strict";function r(e){return this instanceof r?void(this.value=e):new r(e)}t.exports=r,r.prototype.hook=function(e,t){e[t]!==this.value&&(e[t]=this.value)}},{}],22:[function(e,t,n){"use strict";function r(e,t,n){var r,s,u,c,l=[];return!n&&a(t)&&(n=t,s={}),s=s||t||{},r=k(e,s),s.hasOwnProperty("key")&&(u=s.key,s.key=void 0),s.hasOwnProperty("namespace")&&(c=s.namespace,s.namespace=void 0),"INPUT"!==r||c||!s.hasOwnProperty("value")||void 0===s.value||y(s.value)||(s.value=m(s.value)),i(s),void 0!==n&&null!==n&&o(n,l,r,s),new v(r,s,l,u,c)}function o(e,t,n,r){if("string"==typeof e)t.push(new f(e));else if("number"==typeof e)t.push(new f(String(e)));else if(s(e))t.push(e);else{if(!l(e)){if(null===e||void 0===e)return;throw u({foreignObject:e,parentVnode:{tagName:n,properties:r}})}for(var i=0;i<e.length;i++)o(e[i],t,n,r)}}function i(e){for(var t in e)if(e.hasOwnProperty(t)){var n=e[t];if(y(n))continue;"ev-"===t.substr(0,3)&&(e[t]=x(n))}}function s(e){return p(e)||d(e)||h(e)||g(e)}function a(e){return"string"==typeof e||l(e)||s(e)}function u(e){var t=new Error;return t.type="virtual-hyperscript.unexpected.virtual-element",t.message="Unexpected virtual child passed to h().\nExpected a VNode / Vthunk / VWidget / string but:\ngot:\n"+c(e.foreignObject)+".\nThe parent vnode is:\n"+c(e.parentVnode),t.foreignObject=e.foreignObject,t.parentVnode=e.parentVnode,t}function c(e){try{return JSON.stringify(e,null,"    ")}catch(t){return String(e)}}var l=e("x-is-array"),v=e("../vnode/vnode.js"),f=e("../vnode/vtext.js"),p=e("../vnode/is-vnode"),d=e("../vnode/is-vtext"),h=e("../vnode/is-widget"),y=e("../vnode/is-vhook"),g=e("../vnode/is-thunk"),k=e("./parse-tag.js"),m=e("./hooks/soft-set-hook.js"),x=e("./hooks/ev-hook.js");t.exports=r},{"../vnode/is-thunk":25,"../vnode/is-vhook":26,"../vnode/is-vnode":27,"../vnode/is-vtext":28,"../vnode/is-widget":29,"../vnode/vnode.js":31,"../vnode/vtext.js":33,"./hooks/ev-hook.js":20,"./hooks/soft-set-hook.js":21,"./parse-tag.js":23,"x-is-array":12}],23:[function(e,t,n){"use strict";function r(e,t){if(!e)return"DIV";var n=!t.hasOwnProperty("id"),r=o(e,i),a=null;s.test(r[1])&&(a="DIV");var u,c,l,v;for(v=0;v<r.length;v++)c=r[v],c&&(l=c.charAt(0),a?"."===l?(u=u||[],u.push(c.substring(1,c.length))):"#"===l&&n&&(t.id=c.substring(1,c.length)):a=c);return u&&(t.className&&u.push(t.className),t.className=u.join(" ")),t.namespace?a:a.toUpperCase()}var o=e("browser-split"),i=/([\.#]?[a-zA-Z0-9\u007F-\uFFFF_:-]+)/,s=/^\.|#/;t.exports=r},{"browser-split":6}],24:[function(e,t,n){function r(e,t){var n=e,r=t;return u(t)&&(r=o(t,e)),u(e)&&(n=o(e,null)),{a:n,b:r}}function o(e,t){var n=e.vnode;if(n||(n=e.vnode=e.render(t)),!(i(n)||s(n)||a(n)))throw new Error("thunk did not return a valid node");return n}var i=e("./is-vnode"),s=e("./is-vtext"),a=e("./is-widget"),u=e("./is-thunk");t.exports=r},{"./is-thunk":25,"./is-vnode":27,"./is-vtext":28,"./is-widget":29}],25:[function(e,t,n){function r(e){return e&&"Thunk"===e.type}t.exports=r},{}],26:[function(e,t,n){function r(e){return e&&("function"==typeof e.hook&&!e.hasOwnProperty("hook")||"function"==typeof e.unhook&&!e.hasOwnProperty("unhook"))}t.exports=r},{}],27:[function(e,t,n){function r(e){return e&&"VirtualNode"===e.type&&e.version===o}var o=e("./version");t.exports=r},{"./version":30}],28:[function(e,t,n){function r(e){return e&&"VirtualText"===e.type&&e.version===o}var o=e("./version");t.exports=r},{"./version":30}],29:[function(e,t,n){function r(e){return e&&"Widget"===e.type}t.exports=r},{}],30:[function(e,t,n){t.exports="2"},{}],31:[function(e,t,n){function r(e,t,n,r,o){this.tagName=e,this.properties=t||c,this.children=n||l,this.key=null!=r?String(r):void 0,this.namespace="string"==typeof o?o:null;var v,f=n&&n.length||0,p=0,d=!1,h=!1,y=!1;for(var g in t)if(t.hasOwnProperty(g)){var k=t[g];u(k)&&k.unhook&&(v||(v={}),v[g]=k)}for(var m=0;f>m;m++){var x=n[m];i(x)?(p+=x.count||0,!d&&x.hasWidgets&&(d=!0),!h&&x.hasThunks&&(h=!0),y||!x.hooks&&!x.descendantHooks||(y=!0)):!d&&s(x)?"function"==typeof x.destroy&&(d=!0):!h&&a(x)&&(h=!0)}this.count=f+p,this.hasWidgets=d,this.hasThunks=h,this.hooks=v,this.descendantHooks=y}var o=e("./version"),i=e("./is-vnode"),s=e("./is-widget"),a=e("./is-thunk"),u=e("./is-vhook");t.exports=r;var c={},l=[];r.prototype.version=o,r.prototype.type="VirtualNode"},{"./is-thunk":25,"./is-vhook":26,"./is-vnode":27,"./is-widget":29,"./version":30}],32:[function(e,t,n){function r(e,t,n){this.type=Number(e),this.vNode=t,this.patch=n}var o=e("./version");r.NONE=0,r.VTEXT=1,r.VNODE=2,r.WIDGET=3,r.PROPS=4,r.ORDER=5,r.INSERT=6,r.REMOVE=7,r.THUNK=8,t.exports=r,r.prototype.version=o,r.prototype.type="VirtualPatch"},{"./version":30}],33:[function(e,t,n){function r(e){this.text=String(e)}var o=e("./version");t.exports=r,r.prototype.version=o,r.prototype.type="VirtualText"},{"./version":30}],34:[function(e,t,n){function r(e,t){var n;for(var a in e){a in t||(n=n||{},n[a]=void 0);var u=e[a],c=t[a];if(u!==c)if(i(u)&&i(c))if(o(c)!==o(u))n=n||{},n[a]=c;else if(s(c))n=n||{},n[a]=c;else{var l=r(u,c);l&&(n=n||{},n[a]=l)}else n=n||{},n[a]=c}for(var v in t)v in e||(n=n||{},n[v]=t[v]);return n}function o(e){return Object.getPrototypeOf?Object.getPrototypeOf(e):e.__proto__?e.__proto__:e.constructor?e.constructor.prototype:void 0}var i=e("is-object"),s=e("../vnode/is-vhook");t.exports=r},{"../vnode/is-vhook":26,"is-object":11}],35:[function(e,t,n){function r(e,t){var n={a:e};return o(e,t,n,0),n}function o(e,t,n,r){if(e!==t){var o=n[r],a=!1;if(w(e)||w(t))u(e,t,n,r);else if(null==t)x(e)||(s(e,n,r),o=n[r]),o=h(o,new g(g.REMOVE,e,t));else if(k(t))if(k(e))if(e.tagName===t.tagName&&e.namespace===t.namespace&&e.key===t.key){var c=_(e.properties,t.properties);c&&(o=h(o,new g(g.PROPS,e,c))),o=i(e,t,n,o,r)}else o=h(o,new g(g.VNODE,e,t)),a=!0;else o=h(o,new g(g.VNODE,e,t)),a=!0;else m(t)?m(e)?e.text!==t.text&&(o=h(o,new g(g.VTEXT,e,t))):(o=h(o,new g(g.VTEXT,e,t)),a=!0):x(t)&&(x(e)||(a=!0),o=h(o,new g(g.WIDGET,e,t)));o&&(n[r]=o),a&&s(e,n,r)}}function i(e,t,n,r,i){for(var s=e.children,a=f(s,t.children),u=a.children,c=s.length,l=u.length,v=c>l?c:l,p=0;v>p;p++){var d=s[p],y=u[p];i+=1,d?o(d,y,n,i):y&&(r=h(r,new g(g.INSERT,null,y))),k(d)&&d.count&&(i+=d.count)}return a.moves&&(r=h(r,new g(g.ORDER,e,a.moves))),r}function s(e,t,n){l(e,t,n),a(e,t,n)}function a(e,t,n){if(x(e))"function"==typeof e.destroy&&(t[n]=h(t[n],new g(g.REMOVE,e,null)));else if(k(e)&&(e.hasWidgets||e.hasThunks))for(var r=e.children,o=r.length,i=0;o>i;i++){var s=r[i];n+=1,a(s,t,n),k(s)&&s.count&&(n+=s.count)}else w(e)&&u(e,null,t,n)}function u(e,t,n,o){var i=b(e,t),s=r(i.a,i.b);c(s)&&(n[o]=new g(g.THUNK,null,s))}function c(e){for(var t in e)if("a"!==t)return!0;return!1}function l(e,t,n){if(k(e)){if(e.hooks&&(t[n]=h(t[n],new g(g.PROPS,e,v(e.hooks)))),e.descendantHooks||e.hasThunks)for(var r=e.children,o=r.length,i=0;o>i;i++){var s=r[i];n+=1,l(s,t,n),k(s)&&s.count&&(n+=s.count)}}else w(e)&&u(e,null,t,n)}function v(e){var t={};for(var n in e)t[n]=void 0;return t}function f(e,t){var n=d(t),r=n.keys,o=n.free;if(o.length===t.length)return{children:t,moves:null};var i=d(e),s=i.keys,a=i.free;if(a.length===e.length)return{children:t,moves:null};for(var u=[],c=0,l=o.length,v=0,f=0;f<e.length;f++){var h,y=e[f];y.key?r.hasOwnProperty(y.key)?(h=r[y.key],u.push(t[h])):(h=f-v++,u.push(null)):l>c?(h=o[c++],u.push(t[h])):(h=f-v++,u.push(null))}for(var g=c>=o.length?t.length:o[c],k=0;k<t.length;k++){var m=t[k];m.key?s.hasOwnProperty(m.key)||u.push(m):k>=g&&u.push(m)}for(var x,w=u.slice(),b=0,_=[],j=[],O=0;O<t.length;){var E=t[O];for(x=w[b];null===x&&w.length;)_.push(p(w,b,null)),x=w[b];x&&x.key===E.key?(b++,O++):E.key?(x&&x.key&&r[x.key]!==O+1?(_.push(p(w,b,x.key)),x=w[b],x&&x.key===E.key?b++:j.push({key:E.key,to:O})):j.push({key:E.key,to:O}),O++):x&&x.key&&_.push(p(w,b,x.key))}for(;b<w.length;)x=w[b],_.push(p(w,b,x&&x.key));return _.length!==v||j.length?{children:u,moves:{removes:_,inserts:j}}:{children:u,moves:null}}function p(e,t,n){return e.splice(t,1),{from:t,key:n}}function d(e){for(var t={},n=[],r=e.length,o=0;r>o;o++){var i=e[o];i.key?t[i.key]=o:n.push(o)}return{keys:t,free:n}}function h(e,t){return e?(y(e)?e.push(t):e=[e,t],e):t}var y=e("x-is-array"),g=e("../vnode/vpatch"),k=e("../vnode/is-vnode"),m=e("../vnode/is-vtext"),x=e("../vnode/is-widget"),w=e("../vnode/is-thunk"),b=e("../vnode/handle-thunk"),_=e("./diff-props");t.exports=r},{"../vnode/handle-thunk":24,"../vnode/is-thunk":25,"../vnode/is-vnode":27,"../vnode/is-vtext":28,"../vnode/is-widget":29,"../vnode/vpatch":32,"./diff-props":34,"x-is-array":12}],36:[function(e,t,n){function r(e){this.ws=e}function o(e){var t=new WebSocket(e),n=new r(t);return t.onopen=function(){n.emit("opened",n)},t.onclose=function(){n.emit("closed",n)},t.onmessage=function(e){var t=s(e.data);n.emit(t.event,t.data)},n}var i=e("emitter-component"),s=e("./parse");i(r.prototype),t.exports=o},{"./parse":40,"emitter-component":2}],37:[function(e,t,n){function r(e){return o("h1","Dashboard")}var o=e("virtual-dom/h");t.exports=r},{"virtual-dom/h":5}],38:[function(e,t,n){function r(){return{}}t.exports={state:r}},{}],39:[function(e,t,n){var r=(e("virtual-dom/diff"),e("virtual-dom/patch"),e("virtual-dom/create-element")),o=(e("virtual-dom/h"),e("./connection")),i=e("./data"),s=e("./dashboard"),a=i.state(),u=s(a),c=r(u);document.body.appendChild(c);var l=o("ws://localhost:8001/ws");l.on("opened",function(){console.log("opened")}),l.on("count",function(e){console.log("count",e)})},{"./connection":36,"./dashboard":37,"./data":38,"virtual-dom/create-element":3,"virtual-dom/diff":4,"virtual-dom/h":5,"virtual-dom/patch":13}],40:[function(e,t,n){function r(e){var t=e.match(/^([a-z-]+)@(.*)$/),n=null;try{var n=JSON.parse(t[2])}catch(r){}return{event:t[1],data:n}}t.exports=r},{}]},{},[39]);